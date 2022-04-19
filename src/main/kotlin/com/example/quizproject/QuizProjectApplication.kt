package com.example.quizproject

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories
import org.springframework.web.reactive.config.EnableWebFlux

@SpringBootApplication(exclude = [MongoAutoConfiguration::class])
@EnableWebFlux
@EnableReactiveMongoRepositories
@EnableReactiveMongoAuditing
class QuizProjectApplication

fun main(args: Array<String>) {
    runApplication<QuizProjectApplication>(*args)
}
