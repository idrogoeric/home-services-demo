package com.example.homesvc.domain.events;

import java.time.LocalDateTime;

public record QuoteAcceptedEvent(
        String quoteId,
        String customerId,
        String providerId,
        LocalDateTime scheduleAt
) {}
