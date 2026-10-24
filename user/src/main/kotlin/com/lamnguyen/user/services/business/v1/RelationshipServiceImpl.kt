/**
 * Nguyen Dinh Lam
 * Email: kiminonawa1305@gmail.com
 * Phone number: +84 855354919
 * Create at: 4:55 PM-30/07/2025
 *  User: kimin
 **/

package com.lamnguyen.user.services.business.v1

import com.lamnguyen.user.models.Relationship
import com.lamnguyen.user.repositories.IRelationshipRepository
import com.lamnguyen.user.repositories.IUserRepository
import com.lamnguyen.user.services.business.IRelationshipService
import com.lamnguyen.user.utils.enums.RelationshipStatus
import com.lamnguyen.user.utils.helpers.KeyGenerator
import com.lamnguyen.user.utils.helpers.formatPhoneNumber
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class RelationshipServiceImpl(
    val relationshipRepository: IRelationshipRepository,
    val userRepository: IUserRepository,
) : IRelationshipService {
    override fun getAllFriend(phoneNumber: String): Flux<Relationship> {
        return relationshipRepository.findAllRelationship(
            formatPhoneNumber(phoneNumber),
            RelationshipStatus.FRIEND
        )
    }

    @Transactional
    override fun addFriend(
        phoneNumberSender: String,
        phoneNumberReceiver: String,
    ): Mono<Relationship> {
        val key = KeyGenerator.generateKeyRelationship(phoneNumberSender, phoneNumberReceiver)
        return relationshipRepository
            .existsBySoftIdAndStatus(
                key,
                RelationshipStatus.FRIEND
            )
            .filter { !it }
            .flatMap {
                Mono.zip(
                    userRepository.findUserByPhoneNumber(phoneNumberSender),
                    userRepository.findUserByPhoneNumber(phoneNumberReceiver)
                )
            }.flatMap {
                val sender = it.t1
                val receiver = it.t2
                relationshipRepository.save(Relationship().apply {
                    softId = key
                    phoneA = phoneNumberSender
                    displayNameA = sender.fullName
                    phoneB = phoneNumberReceiver
                    displayNameB = receiver.fullName
                    status = RelationshipStatus.FRIEND
                })
            }
    }

    override fun isFriend(
        phoneA: String,
        phoneB: String,
    ): Mono<Boolean> {
        val key = KeyGenerator.generateKeyRelationship(phoneA, phoneB)
        return relationshipRepository.existsBySoftIdAndStatus(
            key,
            RelationshipStatus.FRIEND
        )
    }

    override fun isStranger(phoneA: String, phoneB: String): Mono<Boolean> {
        val key = KeyGenerator.generateKeyRelationship(phoneA, phoneB)
        return relationshipRepository.findBySoftId(key)
            .map { it.status == RelationshipStatus.STRANGER }
            .switchIfEmpty(Mono.just(true))
    }
}