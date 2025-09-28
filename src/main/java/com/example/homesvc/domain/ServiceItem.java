package com.example.homesvc.domain;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class ServiceItem {
    private String serviceId;
    private int quantity;
    private BigDecimal pricePerUnit;
    private BigDecimal discount;
    private BigDecimal totalCost;
}
