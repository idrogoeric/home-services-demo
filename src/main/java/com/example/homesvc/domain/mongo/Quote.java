package com.example.homesvc.domain.mongo;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Document("quotes")
@Data
@Builder
public class Quote {
    @Id
    private String id;
    private String customerId;
    private List<QuoteLine> lines;
    private String currency;
    private Instant validUntil;
    private QuoteStatus status = QuoteStatus.DRAFT;

    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal shipping;
    private BigDecimal grandTotal;

    @Version
    Long version;
    @CreatedDate
    Instant createdAt;
    @LastModifiedDate
    Instant updatedAt;

    private String acceptedBookingId;
}
