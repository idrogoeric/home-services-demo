package com.example.homesvc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuoteResponse {
  public String quoteId;
  public BigDecimal estimatedPrice;
  public BigDecimal taxes;
  public BigDecimal surcharges;
  public String pricingNotes;
  public List<String> suggestedProviderIds;
  public String matchingNotes;
  public LocalDateTime quoteAt;
  //public QuoteResponse(){}
  /*public QuoteResponse(BigDecimal e, BigDecimal t, BigDecimal s, String pn, List<String> p, String mn, LocalDateTime qa){
    this.estimatedPrice = e;
    this.taxes = t;
    this.surcharges = s;
    this.pricingNotes = pn;
    this.suggestedProviderIds = p;
    this.matchingNotes = mn;
    this.quoteAt = qa;
  }*/
}
