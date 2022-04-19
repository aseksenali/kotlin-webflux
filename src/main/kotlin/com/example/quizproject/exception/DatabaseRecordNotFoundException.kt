package com.example.quizproject.exception

import java.util.*

data class DatabaseRecordNotFoundException(val id: UUID): Exception("Record with id $id was not found")