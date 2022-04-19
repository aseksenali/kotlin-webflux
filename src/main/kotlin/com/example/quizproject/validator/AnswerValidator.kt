package com.example.quizproject.validator

import com.example.quizproject.dto.AnswerDTO
import com.example.quizproject.dto.MultipleAnswerDTO
import com.example.quizproject.dto.SingleAnswerDTO
import org.springframework.validation.Errors
import org.springframework.validation.ValidationUtils
import org.springframework.validation.Validator

class AnswerValidator: Validator {
    override fun supports(clazz: Class<*>): Boolean {
        return AnswerDTO::class.java.isAssignableFrom(clazz)
    }

    override fun validate(target: Any, errors: Errors) {
        if (target !is AnswerDTO<*>)
            throw IllegalArgumentException("The parameter target should not be null and must be of type ${AnswerDTO::class.java}")

        ValidationUtils.rejectIfEmpty(errors, "questionNumber", "questionNumber.required")
        if (target.questionNumber <= 0) errors.rejectValue("questionNumber", "questionNumber.positive")
        when (target) {
            is SingleAnswerDTO -> {
                if (target.answer != null && target.answer.isBlank())
                    errors.rejectValue("answer", "answer.notBlank")
            }
            is MultipleAnswerDTO -> {
                if (target.answer.any { it.isBlank() })
                    errors.rejectValue("answer", "answer.notBlank")
            }
        }
    }

}