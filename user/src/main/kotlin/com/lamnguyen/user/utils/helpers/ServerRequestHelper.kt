/**
 * Nguyen Dinh Lam
 * Email: kiminonawa1305@gmail.com
 * Phone number: +84 855354919
 * Create at: 6:20 AM-21/06/2026
 *  User: ndlamdev
 **/

package com.lamnguyen.user.utils.helpers

import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.validation.Validator
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.function.Tuple2

fun ServerRequest.queryParamWithDefaultValue(name: String, defaultValue: String): String {
    return this.queryParam(name).orElse(defaultValue) ?: defaultValue
}

fun <R> ServerRequest.handleMonoWithAuthentication(action: (auth: Authentication) -> Mono<R>): Mono<R> {
    return ReactiveSecurityContextHolder.getContext()
        .flatMap { context ->
            action(context.authentication)
        }
}

fun <R> ServerRequest.handleFluxWithAuthentication(action: (auth: Authentication) -> Flux<R>): Flux<R> {
    return ReactiveSecurityContextHolder.getContext()
        .flatMapMany { context ->
            action(context.authentication)
        }
}

inline fun <reified T : Any> ServerRequest.bodyToMonoAndValidate(validator: Validator): Mono<T> {
    return this.bodyToMono<T>()
        .flatMap(validator::validate)
}

inline fun <reified T : Any> ServerRequest.bodyToMonoWithAuthAndValidate(validator: Validator): Mono<Tuple2<T, Authentication>> {
    return this.bodyToMonoAndValidate<T>(validator)
        .zipWith(
            ReactiveSecurityContextHolder.getContext().map { it.authentication }.switchIfEmpty(
                Mono.error(
                    AccessDeniedException("Authentication not found")
                )
            )
        )
}
