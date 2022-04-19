package com.example.quizproject.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonSubTypes(
    value = [
        JsonSubTypes.Type(value = SingleAnswer::class, name = "SingleAnswer"),
        JsonSubTypes.Type(value = MultipleAnswer::class, name = "MultipleAnswer")
    ]
)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonIgnoreProperties(ignoreUnknown = true)
sealed interface Answer<T> {
    val question: Question<T>
    var score: Double
    val answer: T?
}

data class SingleAnswer(
    override val question: SingleQuestion,
    override val answer: String?
) : Answer<String> {
    override var score: Double = answer?.let {
        if (answer == question.correctAnswer) 1.0 else -1.0
    } ?: 0.0
}

data class MultipleAnswer(
    override val question: MultipleQuestion,
    override val answer: Set<String>
) : Answer<Set<String>> {
    override var score: Double = answer.groupingBy {
            question.correctAnswer!!.contains(it.trim())
        }
            .eachCount()
            .map {
                when (it.key) {
                    true -> it.value.toDouble() / question.correctAnswer!!.size
                    false -> -it.value.toDouble() / (answer.size - question.correctAnswer!!.size)
                }
            }
            .sum()
}