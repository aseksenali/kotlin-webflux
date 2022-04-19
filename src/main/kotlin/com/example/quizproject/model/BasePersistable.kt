package com.example.quizproject.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable

abstract class BasePersistable<ID>(
    @Id open var _id: ID
): Persistable<ID> {
    @Transient
    private var isNew = false

    override fun getId() = _id

    @JsonIgnore
    override fun isNew() = isNew

    fun markNew() {
        isNew = true
    }
}