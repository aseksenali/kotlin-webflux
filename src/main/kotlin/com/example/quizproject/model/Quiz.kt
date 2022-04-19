package com.example.quizproject.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Transient
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.util.*

@Document
data class Quiz(
    @Transient
    @JsonIgnore
    override var _id: UUID,
    var title: String,
    var questions: List<Question<*>>,
    var creatorId: UUID,
    @CreatedDate
    var timeOfCreation: LocalDateTime? = null
): BasePersistable<UUID>(_id)