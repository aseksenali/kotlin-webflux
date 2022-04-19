package com.example.quizproject.exception

data class ValidationException(val errors: Set<String>): Exception()