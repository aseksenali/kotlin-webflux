package com.example.quizproject.config

import com.example.quizproject.validator.AnswerValidator
import com.example.quizproject.validator.QuestionValidator
import com.example.quizproject.validator.QuizValidator
import com.example.quizproject.validator.ResponseValidator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ValidationConfig {
    @Bean
    fun questionValidator(): QuestionValidator = QuestionValidator()

    @Bean
    fun quizValidator(questionValidator: QuestionValidator) = QuizValidator(questionValidator)

    @Bean
    fun answerValidator(): AnswerValidator = AnswerValidator()

    @Bean
    fun responseValidator(answerValidator: AnswerValidator): ResponseValidator = ResponseValidator(answerValidator)
}