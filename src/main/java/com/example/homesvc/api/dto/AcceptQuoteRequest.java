package com.example.homesvc.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AcceptQuoteRequest(
        @NotBlank String providerId,
        @NotNull LocalDateTime scheduleAt) {

}
