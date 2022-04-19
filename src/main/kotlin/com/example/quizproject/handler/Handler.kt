package com.example.quizproject.handler

import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

interface Handler {
    suspend fun all(request: ServerRequest): ServerResponse
    suspend fun find(request: ServerRequest): ServerResponse
    suspend fun create(request: ServerRequest): ServerResponse
    suspend fun delete(request: ServerRequest): ServerResponse
    suspend fun solve(request: ServerRequest): ServerResponse
    suspend fun allQuizzesForUser(request: ServerRequest): ServerResponse
    suspend fun allSolutionsForUser(request: ServerRequest): ServerResponse
    suspend fun allSubmittedResponsesForUser(request: ServerRequest): ServerResponse
}