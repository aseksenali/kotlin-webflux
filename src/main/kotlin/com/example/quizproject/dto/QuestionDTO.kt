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
    val questionText: String
    val answers: List<String>
    val correctAnswer: T?
    @JsonIgnore
    fun toModel(): Question<T>
}

data class SingleQuestionDTO(
    override val number: Int,
    override val questionText: String,
    override val answers: List<String>,
    @get:JsonIgnore @set:JsonProperty("correctAnswer") override var correctAnswer: String?

) : QuestionDTO<String> {
    override fun toModel(): Question<String> {
        return SingleQuestion(number, questionText, answers, correctAnswer)
    }
}

data class MultipleQuestionDTO(
    override val number: Int,
    override val questionText: String,
    override val answers: List<String>,
    @get:JsonIgnore @set:JsonProperty("correctAnswer") override var correctAnswer: Set<String>?
) : QuestionDTO<Set<String>> {
    override fun toModel(): Question<Set<String>> {
        return MultipleQuestion(number, questionText, answers, correctAnswer)
    }
}