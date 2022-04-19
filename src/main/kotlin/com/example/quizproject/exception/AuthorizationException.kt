package com.example.quizproject.exception

data class AuthorizationException(override val message: String): Exception(message)