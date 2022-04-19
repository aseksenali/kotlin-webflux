package com.example.quizproject.dto

import com.example.quizproject.model.MultipleQuestion
import com.example.quizproject.model.Question
import com.example.quizproject.model.SingleQuestion
import com.fasterxml.jackson.annotation.*

@JsonSubTypes(
    value = [
        JsonSubTypes.Type(value = SingleQuestionDTO::class, name = "SingleQuestion"),
        JsonSubTypes.Type(value = MultipleQuestionDTO::class, name = "MultipleQuestion")
    ]
)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonIgnoreProperties(ignoreUnknown = true)
sealed interface QuestionDTO<T>: DTO {
    val number: Int
    val title: String
    val answers: Set<String>
    val correctAnswer: T?
    @JsonIgnore
    fun toModel(): Question<T>
}

data class SingleQuestionDTO(
    override val number: Int,
    override val title: String,
    override val answers: Set<String>,
    @get:JsonIgnore @set:JsonProperty("correctAnswer") override var correctAnswer: String?

) : QuestionDTO<String> {
    override fun toModel(): Question<String> {
        return SingleQuestion(number, title, answers, correctAnswer)
    }
}

data class MultipleQuestionDTO(
    override val number: Int,
    override val title: String,
    override val answers: Set<String>,
    @get:JsonIgnore @set:JsonProperty("correctAnswer") override var correctAnswer: Set<String>?
) : QuestionDTO<Set<String>> {
    override fun toModel(): Question<Set<String>> {
        return MultipleQuestion(number, title, answers, correctAnswer)
    }
}