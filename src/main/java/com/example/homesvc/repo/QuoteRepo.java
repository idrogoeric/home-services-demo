package com.example.homesvc.repo;

import com.example.homesvc.domain.mongo.Quote;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuoteRepo extends MongoRepository<Quote, String> {
}
