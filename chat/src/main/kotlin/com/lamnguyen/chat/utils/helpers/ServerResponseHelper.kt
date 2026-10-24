/**
 * Nguyen Dinh Lam
 * Email: kiminonawa1305@gmail.com
 * Phone number: +84 855354919
 * Create at: 5:30 PM-10/07/2025
 *  chat: kimin
 **/

package com.lamnguyen.chat.utils.helpers

import com.fasterxml.jackson.databind.ObjectMapper
import com.lamnguyen.chat.domain.dto.ApiResponseError
import com.lamnguyen.chat.domain.dto.ApiResponseSuccess
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

fun <T : Any> ok(data: T?, message: String? = "Response Success!"): Mono<ServerResponse?> {
    return ServerResponse.ok().body(BodyInserters.fromValue(ApiResponseSuccess<Any>().apply {
        code = 200
        this.data = data
        this.message = message ?: "Response Success!"
    }))
}

inline fun <reified T : Any> error(
    ex: Throwable,
    code: Int?,
    message: String?,
    detail: T?,
): Mono<ServerResponse> {
    return ServerResponse.badRequest().body(BodyInserters.fromValue(ApiResponseError<T>().apply {
        this.code = code ?: 400
        error = message ?: ex.message
        this.detail = ObjectMapper().readValue(detail?.toString() ?: "{}", T::class.java)
        trace = ex.stackTrace
    }))
}

fun <T : Any> Mono<T>.responseOkWithBody(message: String? = "Response Success!"): Mono<ServerResponse?> {
    return this.flatMap { ok(it, message) }
}

fun <T : Any> Mono<T>.responseOk(message: String? = "Response Success!"): Mono<ServerResponse?> {
    return this.then(ok(null, message))
}