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
                if (target.questions.size > 10) errors.rejectValue("questions", "questions.tooMany")
                if (target
                        .questions
                        .groupingBy { it.number }
                        .eachCount()
                        .any { it.value != 1 })
                    errors.rejectValue("questions", "questions.incorrectIndex")
                val max = target.questions.maxOf { it.number }
                if (target.questions.size < max)
                    errors.rejectValue("questions", "questions.incorrectIndex")
                target.questions.forEachIndexed { index, questionDTO ->
                    errors.pushNestedPath("questions[$index]")
                    ValidationUtils.invokeValidator(questionValidator, questionDTO, errors)
                    errors.popNestedPath()
                }
            }
        }
    }
}