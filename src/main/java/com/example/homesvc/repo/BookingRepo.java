package com.example.homesvc.repo;

import com.example.homesvc.domain.mongo.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookingRepo extends MongoRepository<Booking, String> {
}
