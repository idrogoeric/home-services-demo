package com.example.homesvc.domain.mongo;

import com.example.homesvc.domain.ServiceItem;
import com.example.homesvc.domain.enums.BookingStatus;
import com.example.homesvc.domain.enums.Region;
import com.example.homesvc.domain.enums.ServiceType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Document("bookings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    private String id;
    @Indexed(unique = true)
    private Long number;
    @Indexed
    public String userId;
    @Indexed
    private String providerId;
    private ServiceType serviceType;
    private Region region;
    private LocalDateTime scheduledAt;
    private BookingStatus status;
    private BigDecimal quotedPrice;
    private BigDecimal finalPrice;
    private String notes;

    private String quoteId;
    private String customerId;
    private List<ServiceItem> items;
}
