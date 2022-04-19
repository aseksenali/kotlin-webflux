package com.example.quizproject.exception

import java.util.*

data class ResponseAlreadyExistsException(val id: UUID, val userId: UUID): Exception("There is already a record with user id: $userId and id $id")