package com.example.quizproject.router

import com.example.quizproject.exception.*
import com.example.quizproject.handler.Handler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait
import org.springframework.web.reactive.function.server.coRouter
import org.springframework.web.reactive.function.server.json

@Configuration
class RouterConfiguration {
    private final val logger: Logger = LoggerFactory.getLogger(RouterConfiguration::class.java)

    @Bean
    fun router(handler: Handler) = coRouter {
        GET("/my/responses", handler::allSolutionsForUser)
        GET("/my/quizzes", handler::allQuizzesForUser)
        GET("/{id}", handler::find)
        GET("/{id}/statistics", handler::allSubmittedResponsesForUser)
        GET("", handler::all)
        POST("/{id}", handler::solve)
        POST("", handler::create)
        DELETE("/{id}", handler::delete)
        onError<AuthorizationException> { e, _ ->
            status(HttpStatus.FORBIDDEN)
                .json()
                .bodyValueAndAwait(e.localizedMessage)
        }
        onError<DatabaseRecordNotFoundException> { _, _ -> notFound().buildAndAwait() }
        onError<IncorrectAnswerFormatException> { e, _ -> badRequest().json().bodyValueAndAwait(e.localizedMessage) }
        onError<OAuth2AttributeNotFoundException> { _, _ -> status(HttpStatus.FORBIDDEN).buildAndAwait() }
        onError<QuestionNotFoundException> { e, _ -> badRequest().json().bodyValueAndAwait(e.localizedMessage) }
        onError<RequestBodyNotFoundException> { _, _ -> badRequest().buildAndAwait() }
        onError<ResponseAlreadyExistsException> { e, _ -> badRequest().json().bodyValueAndAwait(e.localizedMessage) }
        onError<ValidationException> { e, _ ->
            e as ValidationException
            badRequest().json().bodyValueAndAwait(e.errors)
        }
        onError<Exception> { e, _ ->
            logger.error("Internal server error occurred", e)
            status(HttpStatus.INTERNAL_SERVER_ERROR)
                .bodyValueAndAwait("Internal server error occurred")
        }
    }
}