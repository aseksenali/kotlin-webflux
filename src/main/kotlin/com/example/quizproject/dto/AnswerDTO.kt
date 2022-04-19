package com.example.quizproject.dto

import com.example.quizproject.exception.IncorrectAnswerFormatException
import com.example.quizproject.exception.QuestionNotFoundException
import com.example.quizproject.model.*
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.util.*

@JsonSubTypes(
    value = [JsonSubTypes.Type(
        value = SingleAnswerDTO::class,
        name = "SingleAnswer"
    ), JsonSubTypes.Type(value = MultipleAnswerDTO::class, name = "MultipleAnswer")]
)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonIgnoreProperties(ignoreUnknown = true)
sealed interface AnswerDTO<T> : DTO {
    val questionNumber: Int
    val answer: T?

    @JsonIgnore
    fun toModel(quiz: Quiz, userId: UUID): Answer<T>

    @JsonIgnore
    fun getQuestion(quiz: Quiz): Question<T> {
        return quiz.questions.filterIsInstance<Question<T>>().find { question ->
            question.number == questionNumber
        } ?: throw QuestionNotFoundException(questionNumber)
    }
}

data class SingleAnswerDTO(
    override val questionNumber: Int,
    override val answer: String?
) : AnswerDTO<String> {
    override fun toModel(quiz: Quiz, userId: UUID): Answer<String> {
        val question = getQuestion(quiz)
        if (question !is SingleQuestion) throw IncorrectAnswerFormatException()
        return SingleAnswer(question, userId, answer)
    }
}

data class MultipleAnswerDTO(override val questionNumber: Int, override val answer: Set<String>) :
    AnswerDTO<Set<String>> {
    override fun toModel(quiz: Quiz, userId: UUID): Answer<Set<String>> {
        val question = getQuestion(quiz)
        if (question !is MultipleQuestion) throw IncorrectAnswerFormatException()
        return MultipleAnswer(question, userId, answer)
    }
}