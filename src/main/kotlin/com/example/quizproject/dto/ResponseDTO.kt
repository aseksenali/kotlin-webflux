package com.example.quizproject.dto

sealed interface ResponseDTO: DTO {
    data class Solve(val answers: Set<AnswerDTO<*>>): ResponseDTO
}