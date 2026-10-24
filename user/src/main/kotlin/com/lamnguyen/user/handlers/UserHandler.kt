/**
 * Nguyen Dinh Lam
 * Email: kiminonawa1305@gmail.com
 * Phone number: +84 855354919
 * Create at: 7:29 PM-15/07/2025
 *  User: kimin
 **/

package com.lamnguyen.user.handlers

import com.lamnguyen.user.domain.request.InviteAddFriendRequest
import com.lamnguyen.user.domain.request.RegisInfoRequest
import com.lamnguyen.user.domain.request.ReplyInviteAddFriendRequest
import com.lamnguyen.user.services.business.IInviteAddFriendService
import com.lamnguyen.user.services.business.IUserService
import com.lamnguyen.user.utils.helpers.*
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.validation.Validator
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2

@Component
class UserHandler(
    val userService: IUserService,
    val inviteAddFriendService: IInviteAddFriendService,
    val validator: Validator,
) {

    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'USER_SEARCH_BY_PHONE_NUMBER')")
    fun getInfo(request: ServerRequest): Mono<ServerResponse?> {
        return ReactiveSecurityContextHolder.getContext()
            .flatMap { context ->
                userService.getInfo(context.authentication.name)
            }.responseOkWithBody()
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'USER_SEARCH_BY_PHONE_NUMBER')")
    fun registerInfo(request: ServerRequest): Mono<ServerResponse?> {
        return Mono.zip(
            request.bodyToMonoAndValidate<RegisInfoRequest>(validator),
            ReactiveSecurityContextHolder.getContext()
        )
            .flatMap { (data, context) ->
                userService.registerInfo(context.authentication.name, data)
            }.responseOkWithBody()
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'USER_SEARCH_BY_PHONE_NUMBER')")
    fun search(request: ServerRequest): Mono<ServerResponse?> {
        val data = request.queryParamWithDefaultValue("phone_number", "")
        return ReactiveSecurityContextHolder.getContext()
            .flatMap { context ->
                val phoneNumber = formatPhoneNumber(data, context.authentication.name, data)

                if (phoneNumber == context.authentication.name) {
                    return@flatMap ok(null)
                }
                val monoUser = userService.findFriendAndStrangerByPhoneNumber(context.authentication.name, phoneNumber)

                monoUser.collectList().responseOkWithBody()
            }
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'USER_ADD_FRIEND', 'ROLE_ADMIN')")
    fun addFriend(request: ServerRequest): Mono<ServerResponse?> {
        return request
            .bodyToMonoAndValidate<InviteAddFriendRequest>(validator)
            .flatMap {
                ReactiveSecurityContextHolder.getContext()
                    .flatMap { securityContext ->
                        inviteAddFriendService.sendRequest(
                            securityContext.authentication.name,
                            it.phoneNumber,
                            it.message
                        )
                    }
            }.responseOk()
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'USER_GET_ALL_FRIEND', 'ROLE_ADMIN')")
    fun getAllFriend(request: ServerRequest): Mono<ServerResponse?> {
        return ReactiveSecurityContextHolder.getContext()
            .flatMapMany { context ->
                userService.getAllFriend(context.authentication.name)
            }.collectList()
            .responseOkWithBody()
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'USER_REPLY_ADD_FRIEND', 'ROLE_ADMIN')")
    fun replyAddFriend(request: ServerRequest): Mono<ServerResponse?> {
        return request
            .bodyToMonoAndValidate<ReplyInviteAddFriendRequest>(validator)
            .flatMap {
                ReactiveSecurityContextHolder.getContext()
                    .flatMap { securityContext ->
                        inviteAddFriendService.replyInvite(securityContext.authentication.name, it.id!!, it.answer)
                    }
            }.responseOk()
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'USER_GET_ALL_INVITE', 'ROLE_ADMIN')")
    fun getAllInvite(request: ServerRequest): Mono<ServerResponse?> {
        return ReactiveSecurityContextHolder.getContext()
            .flatMapMany { securityContext ->
                inviteAddFriendService.getAllInvite(securityContext.authentication.name)
            }.collectList()
            .responseOkWithBody()
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'USER_GET_ALL_REQUEST_INVITE', 'ROLE_ADMIN')")
    fun getAllRequestInvite(request: ServerRequest): Mono<ServerResponse?> {
        return ReactiveSecurityContextHolder.getContext()
            .flatMapMany { securityContext ->
                inviteAddFriendService.getAllRequest(securityContext.authentication.name)
            }.collectList()
            .responseOkWithBody()
    }
}