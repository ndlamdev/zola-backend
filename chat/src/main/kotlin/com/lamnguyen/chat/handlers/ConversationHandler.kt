/**
 * Nguyen Dinh Lam
 * Email: kiminonawa1305@gmail.com
 * Phone number: +84 855354919
 * Create at: 5:05 PM-28/07/2025
 *  User: kimin
 **/

package com.lamnguyen.chat.handlers

import com.lamnguyen.chat.domain.requests.CreateConversationRequest
import com.lamnguyen.chat.exceptions.ApplicationException
import com.lamnguyen.chat.exceptions.ExceptionEnum
import com.lamnguyen.chat.services.business.IConversationService
import com.lamnguyen.chat.utils.helpers.bodyToMonoWithAuthAndValidate
import com.lamnguyen.chat.utils.helpers.handleMonoWithAuthentication
import com.lamnguyen.chat.utils.helpers.ok
import com.lamnguyen.chat.utils.helpers.responseOk
import com.lamnguyen.chat.utils.helpers.responseOkWithBody
import com.lamnguyen.chat.utils.helpers.validate
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.validation.Validator
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2

@Component
class ConversationHandler(
    val conversationService: IConversationService,
    val validator: Validator,
) {
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER', 'USER_CREATE_ROOM_CHAT')")
    fun createConversation(request: ServerRequest): Mono<ServerResponse?> {
        return request.bodyToMonoWithAuthAndValidate<CreateConversationRequest>(validator)
            .flatMap { (body, authentication) ->
                conversationService.createConversation(authentication.name, body)
            }
            .switchIfEmpty { Mono.error(ApplicationException(ExceptionEnum.REQUIRED_PAYLOAD)) }
            .responseOk()
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER', 'USER_GET_ALL_ROOM_CHAT')")
    fun getAllConversation(request: ServerRequest): Mono<ServerResponse?> {
        return request.handleMonoWithAuthentication { authentication ->
            conversationService.getAllDetailConversation(
                authentication.name
            )
        }.responseOkWithBody()
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER', 'USER_GET_ALL_ROOM_CHAT')")
    fun getConversationBySoftId(request: ServerRequest): Mono<ServerResponse?> {
        val softId = request.pathVariable("softId")
        return conversationService.getConversationBySoftId(softId).responseOkWithBody()
    }
}

