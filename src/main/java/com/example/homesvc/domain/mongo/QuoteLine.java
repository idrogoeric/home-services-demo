package com.example.homesvc.domain.mongo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class QuoteLine {
    private String sku;
    private int qty;
    private BigDecimal unitPrice;
    private BigDecimal discount;
    private BigDecimal net;
    private BigDecimal tax;
    private BigDecimal total;
}
