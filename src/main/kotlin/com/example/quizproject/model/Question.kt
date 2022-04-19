package com.example.quizproject.model

import com.fasterxml.jackson.annotation.*

@JsonSubTypes(
    value = [
        JsonSubTypes.Type(value = SingleQuestion::class, name = "SingleQuestion"),
        JsonSubTypes.Type(value = MultipleQuestion::class, name = "MultipleQuestion")
    ]
)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonIgnoreProperties(ignoreUnknown = true)
sealed interface Question<T> {
    val number: Int
    val questionText: String
    val answers: Set<String>
    val correctAnswer: T?
}

data class SingleQuestion(
    override val number: Int,
    override val questionText: String,
    override val answers: Set<String>,
    @get:JsonIgnore @set:JsonProperty("correctAnswer") override var correctAnswer: String?
) : Question<String>

data class MultipleQuestion(
    override val number: Int,
    override val questionText: String,
    override val answers: Set<String>,
    @get:JsonIgnore @set:JsonProperty("correctAnswer") override var correctAnswer: Set<String>?
) : Question<Set<String>>