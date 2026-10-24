/**
 * Nguyen Dinh Lam
 * Email: kiminonawa1305@gmail.com
 * Phone number: +84 855354919
 * Create at: 12:52 PM-24/07/2025
 *  User: kimin
 **/

package com.lamnguyen.user.services.business.v1

import com.lamnguyen.user.events.InviteAddFriendEvent
import com.lamnguyen.user.exceptions.ApplicationException
import com.lamnguyen.user.exceptions.ExceptionEnum
import com.lamnguyen.user.models.InviteAddFriend
import com.lamnguyen.user.repositories.IInviteAddFriendRepository
import com.lamnguyen.user.services.business.IInviteAddFriendService
import com.lamnguyen.user.services.business.IRelationshipService
import com.lamnguyen.user.services.kafka.INotificationKafkaProducer
import com.lamnguyen.user.utils.helpers.KeyGenerator
import com.lamnguyen.user.utils.helpers.formatPhoneNumber
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2

@Service
class InviteAddFriendServiceImpl(
    val inviteAddFriendRepository: IInviteAddFriendRepository,
    val notificationKafkaProducer: INotificationKafkaProducer,
    val relationshipService: IRelationshipService,
) : IInviteAddFriendService {
    @Transactional(rollbackFor = [Exception::class])
    override fun sendRequest(phoneNumberSender: String, phoneNumberReceiver: String, message: String?): Mono<Void> {
        val phoneNumberSenderFormated = formatPhoneNumber(phoneNumberSender)
        val phoneNumberReceiverFormated = formatPhoneNumber(phoneNumberReceiver)
        if (phoneNumberSenderFormated == phoneNumberReceiverFormated) {
            return Mono.error(
                ApplicationException(
                    ExceptionEnum.INVITE_ADD_FRIEND_FAILED,
                    "The phone number that sent the friend request is the same as your phone number"
                )
            )
        }


        return inviteAddFriend(
            phoneNumberSenderFormated,
            phoneNumberReceiverFormated,
            message
        ).then()
    }

    private fun inviteAddFriend(
        phoneNumberSender: String,
        phoneNumberReceiver: String,
        message: String?,
    ): Mono<InviteAddFriend> {
        val data = InviteAddFriend().apply {
            this.phoneNumberReceiver = formatPhoneNumber(phoneNumberReceiver)
            this.phoneNumberSender = formatPhoneNumber(phoneNumberSender)
            this.message = message
        }

        return Mono.zip(
            existsInviteAddFriend(
                formatPhoneNumber(phoneNumberReceiver),
                formatPhoneNumber(phoneNumberSender)
            ), relationshipService.isStranger(data.phoneNumberSender, data.phoneNumberReceiver)
        )
            .flatMap { (existsInviteAddFriend, isStranger) ->
                if (existsInviteAddFriend) return@flatMap Mono.error(ApplicationException(ExceptionEnum.INVITE_EXISTS))
                if (!isStranger) return@flatMap Mono.error(ApplicationException(ExceptionEnum.ONLY_INVITE_WHEN_STRANGER))

                return@flatMap inviteAddFriendRepository.save(data)
                    .flatMap {
                        notificationKafkaProducer.sendNotification(InviteAddFriendEvent(phoneNumberSender))
                        Mono.just(it)
                    }
                    .onErrorResume {
                        Mono.error(ApplicationException(ExceptionEnum.INVITE_ADD_FRIEND_FAILED))
                    }.switchIfEmpty(Mono.error(ApplicationException(ExceptionEnum.USER_NOT_FOUND)))
            }
    }

    override fun findInviteAddFriend(
        phoneNumberSender: String,
        phoneNumberReceiver: String,
    ): Mono<InviteAddFriend> {
        return inviteAddFriendRepository.findInviteAddFriendAndDeletedIsFalse(
            formatPhoneNumber(phoneNumberSender),
            formatPhoneNumber(phoneNumberReceiver)
        )
    }

    override fun replyInvite(phoneNumberReceiver: String, id: Long, answer: Boolean): Mono<Void> {
        return inviteAddFriendRepository.findByIdAndDeletedIsFalseAndPhoneNumberReceiverEquals(
            id,
            formatPhoneNumber(phoneNumberReceiver)
        )
            .switchIfEmpty(Mono.error(ApplicationException(ExceptionEnum.INVITE_NOT_EXISTS)))
            .flatMap { invite ->
                invite.accepted = answer
                invite.deleted = true

                val flowAddFriend =
                    if (answer) relationshipService.addFriend(invite.phoneNumberSender, invite.phoneNumberReceiver)
                    else Mono.just(true)

                return@flatMap Mono.zip(
                    inviteAddFriendRepository.save(invite),
                    flowAddFriend
                )
            }
            .then()
    }

    override fun getAllInvite(phoneNumberReceiver: String): Flux<InviteAddFriend> {
        return inviteAddFriendRepository.findAllByPhoneNumberReceiverAndDeletedIsFalse(phoneNumberReceiver)
    }

    override fun getAllRequest(phoneNumberSender: String): Flux<InviteAddFriend> {
        return inviteAddFriendRepository.findAllByPhoneNumberSenderAndDeletedIsFalse(phoneNumberSender)
    }

    override fun existsInviteAddFriend(senderPhoneNumber: String, receiverPhoneNumber: String): Mono<Boolean> {
        return inviteAddFriendRepository.existsInviteAddFriendByPhoneNumberAndDeletedIsFalse(
            senderPhoneNumber,
            receiverPhoneNumber
        )
    }

    override fun removeInvite(phoneNumberSender: String, phoneNumberReceiver: String): Mono<Void> {
        return inviteAddFriendRepository.deleteAllByPhoneNumberSenderAndPhoneNumberReceiver(
            formatPhoneNumber(phoneNumberSender),
            formatPhoneNumber(phoneNumberReceiver)
        ).then()
    }
}