package com.example.homesvc.service;

import com.example.homesvc.af.ComponentsFactory;
import com.example.homesvc.domain.enums.BookingEvent;
import com.example.homesvc.domain.enums.BookingStatus;
import com.example.homesvc.domain.enums.UserTier;
import com.example.homesvc.domain.mongo.Booking;
import com.example.homesvc.domain.records.Result;
import com.example.homesvc.dto.*;
import com.example.homesvc.infra.mongo.SequenceService;
import com.example.homesvc.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class BookingOrchestrator {
  private final UserRepo users;
  private final ProviderRepo providers;
  private final BookingRepo bookings;
  private final PricingService pricing;
  private final ProviderMatchingService providerMatching;
  private final PaymentService payments;
  private final NotificationService notify;
  private final BookingStateMachine stateMachine;
  private final ComponentsFactory factory;
  private final SequenceService sequenceService;

  private final QuoteService quoteService;
  private final BookingService bookingService;

  public QuoteResponse quote(QuoteRequest req){
    var user = users.findById(req.userId)
            .orElseThrow();
    UserTier tier = req.tierOverride != null ? req.tierOverride : user.getTier();
    var calc = pricing.estimate(req.region,req.serviceType,req.urgent,
            tier, req.voucherCode,req.desiredAt);

    var provider = providerMatching.suggestProviders(req.region,req.serviceType,req.extra);

    return QuoteResponse.builder()
                    .estimatedPrice(calc.estimate())
                    .taxes(calc.taxes())
                    .surcharges(calc.surcharges())
                    .matchingNotes(calc.notes())
                    .suggestedProviderIds(provider.providerIds())
                            .quoteAt(LocalDateTime.now())
            .build();
  }
  public BookingView create(CreateBookingRequest bookReq){
    var user = users.findById(bookReq.userId).orElseThrow();
    var calc = pricing
            .estimate(bookReq.region, bookReq.serviceType, bookReq.highPriority, user.getTier(),
            bookReq.voucherCode, bookReq.scheduledAt);
    BigDecimal amount = calc.estimate();
    String providerId = bookReq.preferredProviderId;
    if(providerId == null){
      var match = providerMatching.suggestProviders(bookReq.region,
              bookReq.serviceType,
              Map.of("algo","BALANCED"));
      providerId = match.providerIds().isEmpty() ? null : match.providerIds().get(0);
    }
    if (providerId == null){
      throw new ResponseStatusException(NOT_FOUND,
              "No providers available for %s in %s".formatted(bookReq.serviceType, bookReq.region));
    }
    //var payOld = payments.charge(bookReq.paymentMethod, amount);
    Result pay = factory.paymentGateway().capture(amount, factory.currency(),
            user.getId());
    //BookingStatus status =
        //    pay.success()? BookingStatus.CONFIRMED : BookingStatus.FAILED_PAYMENT;
    //NEW: Always start as QUOTED; state machine decides the next status.
    Long id = sequenceService.next("booking");
    var booking = new Booking();
            booking.setNumber(id);/*seq.incrementAndGet()*/
            booking.userId = user.getId();
            booking.setProviderId(providerId);
            booking.setServiceType(bookReq.serviceType);
            booking.setRegion(bookReq.region);
            booking.setScheduledAt(bookReq.scheduledAt);
            booking.setStatus(BookingStatus.QUOTED);
            booking.setQuotedPrice(amount);
            booking.setFinalPrice(null);
            booking.setNotes("init");//pay.code());
    //bookings.save(booking);
//NEW: Transition based on payment result
    if(pay.success()){
      stateMachine.apply(booking, BookingEvent.PAYMENT_AUTHORIZED);// confirmed
      booking.setFinalPrice(amount);
      booking.setNotes(pay.code());
    } else{
      stateMachine.apply(booking, BookingEvent.PAYMENT_FAILED);
      booking.setNotes(pay.code());
    }
    bookings.save(booking);
    notify.sendBookingConfirmation(user.getId(), booking.getId(), providerId);
    var v = new BookingView();
    v.setId(booking.getNumber());
    v.setUserId(booking.getUserId());
    v.setProviderId(booking.getProviderId());
    v.setServiceType(booking.getServiceType());
    v.setRegion(booking.getRegion());
    v.setScheduledAt(booking.getScheduledAt());
    v.setStatus(booking.getStatus());
    v.setQuotedPrice(booking.getQuotedPrice());
    v.setFinalPrice(booking.getFinalPrice());
    v.setNotes(booking.getNotes());
    return v;
  }
  /*public Optional<BookingView> get(String id){
    return bookings.findById(id)
            .map(b -> {
              var v = new BookingView();
              v.id = b.getNumber();
              v.userId=b.getUserId();
              v.providerId=b.getProviderId();
              v.serviceType=b.getServiceType();
              v.region=b.getRegion();
      v.scheduledAt=b.getScheduledAt();
      v.status=b.getStatus();
      v.quotedPrice=b.getQuotedPrice();
      v.finalPrice=b.getFinalPrice();
      v.notes=b.getNotes();
      return v;});
  }*/
  /*public void createBookingFromQuote(String quoteId){
    bookingService.createFromQuoteId(quoteId);
  }*/
  public BookingView start(String id) {
    var b = bookings.findById(id)
            .orElseThrow();
    stateMachine.apply(b, BookingEvent.START_WORK);
    bookings.save(b);
    return toView(b);
  }
  public BookingView complete(String id){
    var b = bookings.findById(id).orElseThrow();
    stateMachine.apply(b, BookingEvent.COMPLETE_WORK); // IN_PROGRESS -> COMPLETED
    bookings.save(b);
    return toView(b);
  }

  public BookingView cancel(String id){
    var b = bookings.findById(id).orElseThrow();
    stateMachine.apply(b, BookingEvent.CANCEL); // Many states -> CANCELLED (if allowed)
    bookings.save(b);
    return toView(b);
  }
  private BookingView toView(Booking b){
    var v = new BookingView();
    v.setId(b.getNumber());
    v.setUserId(b.getUserId());
    v.setProviderId(b.getProviderId());
    v.setServiceType(b.getServiceType());
    v.setRegion(b.getRegion());
    v.setScheduledAt(b.getScheduledAt());
    v.setStatus(b.getStatus());
    v.setQuotedPrice(b.getQuotedPrice());
    v.setFinalPrice(b.getFinalPrice());
    v.setNotes(b.getNotes());
    return v;
  }


}
