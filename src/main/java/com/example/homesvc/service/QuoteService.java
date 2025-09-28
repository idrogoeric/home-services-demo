package com.example.homesvc.service;

import com.example.homesvc.api.dto.AcceptQuoteRequest;
import com.example.homesvc.domain.events.QuoteAcceptedEvent;
import com.example.homesvc.domain.mongo.Quote;
import com.example.homesvc.domain.mongo.QuoteStatus;
import com.example.homesvc.dto.QuoteRequest;
import com.example.homesvc.dto.QuoteResponse;
import com.example.homesvc.repo.QuoteRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class QuoteService {
    private final QuoteRepo repo;
    private final ApplicationEventPublisher events;
    private final BookingOrchestrator orchestrator;

    public QuoteResponse create(QuoteRequest req){
        var preview = orchestrator.quote(req);
        var q = Quote.builder()
                .customerId(req.userId)
                .currency("USD")
                .validUntil(Instant.now().plus(5, ChronoUnit.MINUTES))
                .status(QuoteStatus.SENT)
                .subtotal(preview.estimatedPrice)
                .tax(preview.taxes)
                .shipping(preview.surcharges)
                .grandTotal(preview.estimatedPrice
                        .add(preview.taxes)
                        .add(preview.surcharges))
                .build();
        q = repo.save(q);
        preview.quoteId = q.getId();
        return preview;
    }
    public Quote get(String id){
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Quote not found!"));
    }
    public Quote transition(Quote q, QuoteStatus target){
        if(!q.getStatus().canTransitionTo(target))
            throw new IllegalStateException("Invalid transition " +
                    q.getStatus() + " -> " + target);
        q.setStatus(target);
        return repo.save(q);
    }
    public Quote send(String id) {
        return transition(get(id), QuoteStatus.SENT);
    }

    public void accept(String id, AcceptQuoteRequest body){
        var q = get(id);
        if(q.getValidUntil() != null && q.getValidUntil().isBefore(Instant.now()))
            throw new IllegalStateException("Quote has expired and cannot be accepted");
        var accepted = transition(q, QuoteStatus.ACCEPTED);
        repo.save(accepted);

        events.publishEvent(new QuoteAcceptedEvent(
                accepted.getId(),
                accepted.getCustomerId(),
                body.providerId(),
                body.scheduleAt()));
    }
    public Quote expire(String id) {
        return transition(get(id), QuoteStatus.EXPIRED);
    }
    public Quote cancel(String id) {
        return transition(get(id), QuoteStatus.CANCELLED);
    }
}
