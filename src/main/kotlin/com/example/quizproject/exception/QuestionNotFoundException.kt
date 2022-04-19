package com.example.quizproject.exception

data class QuestionNotFoundException(val number: Int): Exception("Question with number $number does not found")