package com.example.quizproject.dto

sealed interface QuizDTO: DTO {
    data class Create(
        val title: String,
        val questions: List<QuestionDTO<*>>
    ) : QuizDTO
}