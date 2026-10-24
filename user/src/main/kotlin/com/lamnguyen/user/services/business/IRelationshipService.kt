/**
 * Nguyen Dinh Lam
 * Email: kiminonawa1305@gmail.com
 * Phone number: +84 855354919
 * Create at: 4:55 PM-30/07/2025
 *  User: kimin
 **/

package com.lamnguyen.user.services.business

import com.lamnguyen.user.models.Relationship
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface IRelationshipService {
    fun getAllFriend(phoneNumber: String): Flux<Relationship>
    fun addFriend(phoneNumberSender: String, phoneNumberReceiver: String): Mono<Relationship>
    fun isFriend(phoneA: String, phoneB: String): Mono<Boolean>
    fun isStranger(phoneA: String, phoneB: String): Mono<Boolean>
}