package com.example.quizproject.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonSubTypes(
    value = [JsonSubTypes.Type(
        value = SingleAnswerDTO::class,
        name = "SingleAnswer"
    ), JsonSubTypes.Type(value = MultipleAnswerDTO::class, name = "MultipleAnswer")]
)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonIgnoreProperties(ignoreUnknown = true)
sealed interface AnswerDTO<T>: DTO {
    val questionNumber: Int
    val answer: T
}

data class SingleAnswerDTO(override val questionNumber: Int, override val answer: String) :
    AnswerDTO<String>

data class MultipleAnswerDTO(override val questionNumber: Int, override val answer: Set<String>) :
    AnswerDTO<Set<String>>