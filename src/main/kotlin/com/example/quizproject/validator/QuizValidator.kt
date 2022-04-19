package com.example.quizproject.validator

import com.example.quizproject.dto.QuizDTO
import org.springframework.validation.Errors
import org.springframework.validation.ValidationUtils
import org.springframework.validation.Validator

class QuizValidator(private val questionValidator: QuestionValidator): Validator {
    override fun supports(clazz: Class<*>): Boolean {
        return QuizDTO::class.java.isAssignableFrom(clazz)
    }

    override fun validate(target: Any, errors: Errors) {
        if (target !is QuizDTO)
            throw IllegalArgumentException("The parameter obj should not be null and must be of type ${QuizDTO::class.java}")

        when (target) {
            is QuizDTO.Create -> {
                ValidationUtils.rejectIfEmpty(errors, "title", "title.required")
                if (target.questions.isEmpty()) errors.rejectValue("questions", "questions.required")
                target.questions.forEachIndexed { index, questionDTO ->
                    errors.pushNestedPath("questions[$index]")
                    ValidationUtils.invokeValidator(questionValidator, questionDTO, errors)
                    errors.popNestedPath()
                }
            }
        }
    }
}