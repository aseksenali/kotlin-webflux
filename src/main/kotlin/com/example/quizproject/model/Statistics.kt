package com.example.quizproject.model

data class Statistics(
    val quiz: Quiz,
    val responses: Set<Response>,
    val averageScore: Double = responses.map { it.score }.average(),
    val submitAmount: Int = responses.count()
)