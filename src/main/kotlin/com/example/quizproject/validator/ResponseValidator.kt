package com.example.quizproject.validator

import com.example.quizproject.dto.ResponseDTO
import org.springframework.validation.Errors
import org.springframework.validation.ValidationUtils
import org.springframework.validation.Validator

class ResponseValidator(private val answerValidator: AnswerValidator): Validator {
    override fun supports(clazz: Class<*>): Boolean {
        return ResponseDTO::class.java.isAssignableFrom(clazz)
    }

    override fun validate(target: Any, errors: Errors) {
        if (target !is ResponseDTO)
            throw IllegalArgumentException("The parameter obj should not be null and must be of type ${ResponseDTO::class.java}")

        when (target) {
            is ResponseDTO.Solve -> {
                if (target
                        .answers
                        .groupingBy { it.questionNumber }
                        .eachCount()
                        .any { it.value != 1 }) {
                    errors.rejectValue("answers", "answers.incorrectAmount")
                }
                target.answers.forEachIndexed { index, answerDTO ->
                    errors.pushNestedPath("answers[$index]")
                    ValidationUtils.invokeValidator(answerValidator, answerDTO, errors)
                    errors.popNestedPath()
                }
            }
        }
    }
}