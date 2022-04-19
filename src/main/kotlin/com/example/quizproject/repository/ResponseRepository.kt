package com.example.quizproject.repository

import com.example.quizproject.model.Response
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.data.querydsl.ReactiveQuerydslPredicateExecutor
import java.util.*

interface ResponseRepository: ReactiveMongoRepository<Response, UUID>, ReactiveQuerydslPredicateExecutor<Response>