package com.example.homesvc.domain.mongo;

public enum QuoteStatus {
    DRAFT,
    SENT,
    ACCEPTED,
    EXPIRED,
    CANCELLED;
    public boolean canTransitionTo(QuoteStatus target){
        return switch (this){
            case DRAFT -> target == SENT || target == CANCELLED;
            case SENT -> target == ACCEPTED || target == EXPIRED || target == CANCELLED;
            default -> false;
        };
    }
}
