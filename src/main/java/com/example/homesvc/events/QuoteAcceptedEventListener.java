package com.example.homesvc.events;

import com.example.homesvc.domain.events.QuoteAcceptedEvent;
import com.example.homesvc.domain.mongo.Quote;
import com.example.homesvc.dto.CreateBookingRequest;
import com.example.homesvc.service.BookingOrchestrator;
import com.example.homesvc.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuoteAcceptedEventListener {
    private static final Logger log = LoggerFactory.getLogger(QuoteAcceptedEventListener.class);

    private final MongoTemplate mongo;
    private final BookingOrchestrator orchestrator;
    @EventListener
    public void on(QuoteAcceptedEvent e){
        var q =  mongo.findById(e.quoteId(), Quote.class);
        if(q == null) {
            log.warn("Quote {} not found", e.quoteId());
            return;
        }

        var req = new CreateBookingRequest();
        req.setUserId(e.customerId());
        req.preferredProviderId = e.providerId();
        req.setScheduledAt(e.scheduleAt());

        var booking = orchestrator.create(req);
        var query = Query.query(Criteria.where("_id").is(e.quoteId())
                .and("acceptedBookingId").is(null));

        var update = new Update().set("acceptedBookingId", booking.getId());
        var res = mongo.updateFirst(query, update, Quote.class);

        if(res.getModifiedCount() == 0){
            log.info("Quote {} already linked to a booking; bookingId={}", e.quoteId(), q.getAcceptedBookingId());
        }
    }
}
