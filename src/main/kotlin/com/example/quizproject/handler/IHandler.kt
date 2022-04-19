package com.example.quizproject.handler

import com.example.quizproject.dto.*
import com.example.quizproject.exception.*
import com.example.quizproject.model.*
import com.example.quizproject.repository.QuizRepository
import com.example.quizproject.repository.ResponseRepository
import com.example.quizproject.validator.QuizValidator
import com.example.quizproject.validator.ResponseValidator
import kotlinx.coroutines.flow.toSet
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils.hasText
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.web.reactive.function.server.*
import java.util.*

@Component
class IHandler(
    private val quizRepository: QuizRepository,
    private val responseRepository: ResponseRepository,
    private val quizValidator: QuizValidator,
    private val responseValidator: ResponseValidator
) : Handler {
    private suspend fun getUserIdFromRequest(request: ServerRequest): UUID {
        val bearerTokenAuthentication = request.awaitPrincipal() as? BearerTokenAuthentication
            ?: throw ClassCastException("Cannot cast to BearerTokenAuthentication")
        val oauth2Principal = bearerTokenAuthentication.principal as? OAuth2AuthenticatedPrincipal
            ?: throw ClassCastException("Cannot cast to OAuth2AuthenticationPrincipal")
        val userId =
            oauth2Principal.getAttribute<String>("user_id") ?: throw OAuth2AttributeNotFoundException("user_id")
        return UUID.fromString(userId)
    }

    override suspend fun all(request: ServerRequest): ServerResponse {
        val quizzes = quizRepository.findAll().asFlow()
        return ServerResponse
            .ok()
            .json()
            .bodyAndAwait(quizzes)
    }

    override suspend fun find(request: ServerRequest): ServerResponse {
        val id = UUID.fromString(request.pathVariable("id"))
        val quiz = quizRepository.findById(id).awaitSingle() ?: throw DatabaseRecordNotFoundException(id)
        return ServerResponse
            .ok()
            .json()
            .bodyValueAndAwait(quiz)
    }

    override suspend fun create(request: ServerRequest): ServerResponse {
        val userId = getUserIdFromRequest(request)
        val quizDTO: QuizDTO.Create = request.awaitBodyOrNull() ?: throw RequestBodyNotFoundException()
        validate(quizDTO)
        val quiz = quizRepository.save(
            Quiz(
                UUID.randomUUID(),
                quizDTO.title,
                quizDTO.questions.map { it.toModel() },
                userId
            ).also { it.markNew() }
        ).awaitSingle()
        return ServerResponse
            .ok()
            .json()
            .bodyValueAndAwait(quiz)
    }

    override suspend fun delete(request: ServerRequest): ServerResponse {
        val userId = getUserIdFromRequest(request)
        val id = UUID.fromString(request.pathVariable("id"))
        val quiz = quizRepository.findById(id).awaitSingleOrNull() ?: throw DatabaseRecordNotFoundException(id)
        if (quiz.creatorId != userId) throw AuthorizationException("Not authorized for this action")
        quizRepository.deleteById(id).awaitSingleOrNull()
        return ServerResponse
            .ok()
            .json()
            .buildAndAwait()
    }

    override suspend fun solve(request: ServerRequest): ServerResponse {
        val userId = getUserIdFromRequest(request)
        val id = UUID.fromString(request.pathVariable("id"))
        val existingResponse = responseRepository.findOne(
            QResponse.response.quiz._id.eq(id)
                .and(QResponse.response.user.eq(userId))
        ).awaitSingleOrNull()
        if (existingResponse != null) throw ResponseAlreadyExistsException(id, userId)
        val quiz = quizRepository.findById(id).awaitSingleOrNull() ?: throw DatabaseRecordNotFoundException(id)
        if (quiz.creatorId == userId) throw AuthorizationException("Operation is not possible")
        val responseDTO: ResponseDTO.Solve = request.awaitBodyOrNull() ?: throw RequestBodyNotFoundException()
        validate(responseDTO)
        val answers = responseDTO.answers.map { it.toModel(quiz, userId) }
        val response = responseRepository.save(
            Response(
                UUID.randomUUID(),
                userId,
                quiz,
                answers
            ).also { it.markNew() }
        ).awaitSingle()
        return ServerResponse
            .ok()
            .json()
            .bodyValueAndAwait(response)
    }

    override suspend fun allSolutionsForUser(request: ServerRequest): ServerResponse {
        val userId = getUserIdFromRequest(request)
        val responses = responseRepository.findAll(QResponse.response.user.eq(userId)).asFlow()
        return ServerResponse
            .ok()
            .json()
            .bodyAndAwait(responses)
    }

    override suspend fun allSubmittedResponsesForUser(request: ServerRequest): ServerResponse {
        val userId = getUserIdFromRequest(request)
        val id = UUID.fromString(request.pathVariable("id"))
        val quiz = quizRepository.findById(id).awaitSingleOrNull() ?: throw DatabaseRecordNotFoundException(id)
        if (quiz.creatorId != userId) throw AuthorizationException("Not authorized for this operation")
        val responses = responseRepository
            .findAll(
                QResponse.response.quiz._id.eq(quiz._id)
                    .and(QResponse.response.quiz.creatorId.eq(userId))
            )
            .asFlow()
            .toSet()
        val statistics = Statistics(
            quiz,
            responses
        )
        return ServerResponse
            .ok()
            .json()
            .bodyValueAndAwait(statistics)
    }

    override suspend fun allQuizzesForUser(request: ServerRequest): ServerResponse {
        val userId = getUserIdFromRequest(request)
        val quizzes = quizRepository.findAllByCreatorId(userId).asFlow()
        return ServerResponse
            .ok()
            .json()
            .bodyAndAwait(quizzes)
    }

    private fun <T : DTO> validate(target: T) {
        val validator = when (target) {
            is QuizDTO -> quizValidator
            is ResponseDTO -> responseValidator
            else -> throw IllegalArgumentException()
        }
        val errors = BeanPropertyBindingResult(target, "target")
        validator.validate(target, errors)
        if (errors.hasErrors()) {
            throw ValidationException(errors.fieldErrors.map {
                "Error in field ${it.field}${if (hasText(it.rejectedValue.toString())) " for value ${it.rejectedValue}" else ""} with code ${it.code}"
            }.toSet())
        }
    }
}