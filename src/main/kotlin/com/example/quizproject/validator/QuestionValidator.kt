package com.example.quizproject.validator

import com.example.quizproject.dto.MultipleQuestionDTO
import com.example.quizproject.dto.QuestionDTO
import com.example.quizproject.dto.SingleQuestionDTO
import org.springframework.util.StringUtils.hasText
import org.springframework.validation.Errors
import org.springframework.validation.ValidationUtils
import org.springframework.validation.Validator

class QuestionValidator : Validator {
    override fun supports(clazz: Class<*>): Boolean {
        return QuestionDTO::class.java.isAssignableFrom(clazz)
    }

    override fun validate(target: Any, errors: Errors) {
        if (target !is QuestionDTO<*>)
            throw IllegalArgumentException("The parameter obj should not be null and must be of type ${QuestionDTO::class.java}")
        ValidationUtils.rejectIfEmpty(errors, "number", "number.required")
        if (target.number <= 0) errors.rejectValue("number", "number.positive")
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", "title.required")

        if (target.answers.isEmpty() || target.answers.any { !hasText(it) })
            errors.rejectValue("answers", "answers.required")

        when (target) {
            is SingleQuestionDTO -> {
                if (target.correctAnswer == null || !hasText(target.correctAnswer))
                    errors.rejectValue("correctAnswer", "correctAnswer.required")
            }
            is MultipleQuestionDTO -> {
                if (target.correctAnswer.isNullOrEmpty() || target.correctAnswer?.any { !hasText(it) } == true)
                    errors.rejectValue("correctAnswer", "correctAnswer.required")
            }
        }
    }
}