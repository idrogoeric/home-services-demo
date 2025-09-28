package com.example.homesvc.api;

import com.example.homesvc.api.dto.AcceptQuoteRequest;
import com.example.homesvc.api.dto.AcceptQuoteResponse;
import com.example.homesvc.domain.mongo.Quote;
import com.example.homesvc.dto.QuoteRequest;
import com.example.homesvc.dto.QuoteResponse;
import com.example.homesvc.service.QuoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/quotes")
@RequiredArgsConstructor
public class QuoteController {
    private final QuoteService svc;
    @PostMapping
    public ResponseEntity<QuoteResponse> create(@RequestBody QuoteRequest q){
        return ResponseEntity.ok(svc.create(q));
    }
    @PostMapping
    public Quote get(@PathVariable String id){
        return svc.get(id);
    }

    @PostMapping("/{id}/send")
    public Quote send(@PathVariable String id) {
        return svc.send(id);
    }
    @PostMapping("/{id}/accept")
    public void accept(@PathVariable String id,
                       @Valid @RequestBody AcceptQuoteRequest req){
        svc.accept(id, req);
    }
    @PostMapping("/{id}/expire")
    public Quote expire(@PathVariable String id){
        return svc.expire(id);
    }
    @PostMapping("/{id}/cancel")
    public Quote cancel(@PathVariable String id){
        return svc.cancel(id);
    }

}
