/**
 * Nguyen Dinh Lam
 * Email: kiminonawa1305@gmail.com
 * Phone number: +84 855354919
 * Create at: 6:36 AM-11/07/2025
 *  User: kimin
 **/

package com.lamnguyen.user.utils.helpers

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.Validator
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono
import reactor.util.function.Tuple2

inline fun <reified T : Any> Validator.validate(data: T): Mono<T> {
    val errors = BeanPropertyBindingResult(this, T::class.java.name)
    this.validate(data, errors)
    if (!errors.hasErrors()) return Mono.just(data)
    val msg = errors.fieldErrors.associate { error -> error.field to (error.defaultMessage ?: "Invalid") }
    return Mono.error(
        ResponseStatusException(HttpStatus.BAD_REQUEST, ObjectMapper().writeValueAsString(msg))
    )
}

inline fun <reified T : Any> ServerRequest.bodyToMonoAndValidate(validator: Validator): Mono<T> {
    return bodyToMono(object : ParameterizedTypeReference<T>() {})
        .flatMap(validator::validate)
}


inline fun <reified T : Any> ServerRequest.bodyToMonoWithAuthAndValidate(validator: Validator): Mono<Tuple2<T, Authentication>> {
    return bodyToMono(object : ParameterizedTypeReference<T>() {})
        .flatMap(validator::validate)
        .zipWith(ReactiveSecurityContextHolder.getContext().map { it.authentication })
}
