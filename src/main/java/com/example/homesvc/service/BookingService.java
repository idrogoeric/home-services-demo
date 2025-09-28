package com.example.homesvc.service;

import com.example.homesvc.domain.ServiceItem;
import com.example.homesvc.domain.enums.BookingStatus;
import com.example.homesvc.domain.mongo.Booking;
import com.example.homesvc.domain.mongo.Quote;
import com.example.homesvc.domain.mongo.QuoteLine;
import com.example.homesvc.repo.BookingRepo;
import com.example.homesvc.repo.QuoteRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final QuoteRepo quoteRepo;
    private final BookingRepo bookingRepo;
    /*public Booking createFromQuoteId(String quoteId){
        Quote quote = quoteRepo.findById(quoteId)
                .orElseThrow(() -> new IllegalArgumentException("Quote not found"));

        var b = Booking.builder()
                .quoteId(quote.getId())
                .customerId(quote.getCustomerId())
                .status(BookingStatus.PENDING)
                .items(convertQuoteLinesToBookingItems(quote.getLines()))
                .build();
        return bookingRepo.save(b);
    }*/
    public List<ServiceItem> convertQuoteLinesToBookingItems(List<QuoteLine> quoteLines){
        return quoteLines.stream()
                .map(line -> ServiceItem.builder()
                        .serviceId(line.getSku())
                        .quantity(line.getQty())
                        .pricePerUnit(line.getUnitPrice())
                        .discount(line.getDiscount())
                        .totalCost(line.getTotal())
                        .build())
                .collect(Collectors.toList());
    }
}
