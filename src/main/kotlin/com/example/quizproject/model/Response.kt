package com.example.quizproject.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Transient
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.util.*

@Document
data class Response(
    @Transient
    @JsonIgnore
    override var _id: UUID,
    var user: UUID,
    @set:JsonIgnore
    var quiz: Quiz,
    var givenAnswers: List<Answer<*>>,
    @CreatedDate
    var timeOfCreation: LocalDateTime? = null,
    var score: Double = givenAnswers.sumOf { it.score },
) : BasePersistable<UUID>(_id)