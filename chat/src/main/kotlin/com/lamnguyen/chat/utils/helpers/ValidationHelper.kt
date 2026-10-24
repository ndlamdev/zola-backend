/**
 * Nguyen Dinh Lam
 * Email: kiminonawa1305@gmail.com
 * Phone number: +84 855354919
 * Create at: 6:36 AM-11/07/2025
 *  User: kimin
 **/

package com.lamnguyen.chat.utils.helpers

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.cglib.core.internal.Function
import org.springframework.http.HttpStatus
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.Validator
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono

inline fun <reified T : Any> Validator.validate(data: T): Mono<T> {
    val errors = BeanPropertyBindingResult(this, T::class.java.name)
    this.validate(data, errors)
    if (!errors.hasErrors()) return Mono.just(data)
    val msg = errors.fieldErrors.associate { error -> error.field to (error.defaultMessage ?: "Invalid") }
    return Mono.error(
        ResponseStatusException(HttpStatus.BAD_REQUEST, ObjectMapper().writeValueAsString(msg))
    )
}

inline fun <reified T : Any, R : Any> Validator.validate(data: T, next: Function<T, Mono<R>>): Mono<R> {
    val errors = BeanPropertyBindingResult(this, T::class.java.name)
    this.validate(data, errors)
    if (!errors.hasErrors()) return next.apply(data)
    val msg = errors
        .fieldErrors
        .associate { error -> error.field to (error.defaultMessage ?: "Invalid") }
        .plus(Pair("Error data", errors.globalError?.defaultMessage ?: "Error data"))
    return Mono.error(
        ResponseStatusException(HttpStatus.BAD_REQUEST, ObjectMapper().writeValueAsString(msg))
    )
}