/**
 * Nguyen Dinh Lam
 * Email: kiminonawa1305@gmail.com
 * Phone number: +84 855354919
 * Create at: 5:15 PM-28/07/2025
 *  User: kimin
 **/

package com.lamnguyen.chat.services.business

import com.lamnguyen.chat.domain.dto.ConversationDto
import com.lamnguyen.chat.domain.requests.CreateConversationRequest
import com.lamnguyen.chat.entities.Conversation
import reactor.core.publisher.Mono

interface IConversationService {
    fun createConversation(admin: String, createConversationRequest: CreateConversationRequest): Mono<Conversation>
    fun createConversation(admin: String, users: List<String>, createdBy: String? = "System"): Mono<Conversation>
    fun removeConversation(id: String): Mono<Void>
    fun getAllDetailConversation(phoneNumber: String): Mono<List<ConversationDto>>
    fun existConversationById(conversationId: String): Mono<Boolean>
    fun findById(conversationId: String): Mono<Conversation>
    fun getConversationInfo(conversationId: String): Mono<ConversationDto>
    fun getConversationInfo(ownerPhone: String, users: List<String>): Mono<ConversationDto>
    fun getConversationBySoftId(softId: String): Mono<ConversationDto>
}