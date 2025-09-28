package com.example.homesvc.api;

import com.example.homesvc.dto.BookingView;
import com.example.homesvc.dto.CreateBookingRequest;
import com.example.homesvc.service.BookingOrchestrator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
  private final BookingOrchestrator orchestrator;
  private static final Logger log = LoggerFactory.getLogger(BookingController.class);
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BookingView> create(@Valid @RequestBody CreateBookingRequest req){
    log.info("POST /api/bookings userId={} providerId={} scheduledAt={}",
            req.getUserId(), req.preferredProviderId, req.getScheduledAt());
    BookingView view = orchestrator.create(req);
    URI location = URI.create("api/bookings/" + view.getId());
    return ResponseEntity.created(location).body(view);
  }
  /*@GetMapping("/{id}")
  public Optional<BookingView> get(@PathVariable String id){
    return orchestrator.get(id);
  }*/
  @PostMapping("/{id}/start")
  public BookingView start(@PathVariable String id){
    return orchestrator.start(id);
  }
  @PostMapping("/{id}/complete")
  public BookingView complete(@PathVariable String id){
    return orchestrator.complete(id);
  }
  @PostMapping("/{id}/cancel")
  public BookingView cancel(@PathVariable String id){
    return orchestrator.cancel(id);
  }
}
