package com.example.homesvc.service;

import com.example.homesvc.domain.enums.BookingEvent;
import com.example.homesvc.domain.enums.BookingStatus;
import com.example.homesvc.domain.mongo.Booking;
import com.example.homesvc.service.state.BookingState;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class BookingStateMachine {
    private final Map<BookingStatus, BookingState> states =
            new EnumMap<>(BookingStatus.class);
    public BookingStateMachine(List<BookingState> impls){
        for(var s : impls)
            states.put(s.status(), s);
    }
    public void apply(Booking booking, BookingEvent event){
        var current = states.get(booking.getStatus());
        if(current == null) throw new IllegalStateException("No BookingState for " +
                booking.getStatus());
        var next = current.next(event);
        booking.setStatus(next);
    }

}
