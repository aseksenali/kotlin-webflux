package com.example.quizproject.repository

import com.example.quizproject.model.Quiz
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import java.util.*

interface QuizRepository: ReactiveMongoRepository<Quiz, UUID> {
    fun findAllByCreatorId(creatorId: UUID): Flux<Quiz>
}