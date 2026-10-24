/**
 * Nguyen Dinh Lam
 * Email: kiminonawa1305@gmail.com
 * Phone number: +84 855354919
 * Create at: 5:09 AM-15/07/2025
 *  User: kimin
 **/

package com.lamnguyen.user.repositories

import com.lamnguyen.user.domain.dto.UserInRelationShip
import com.lamnguyen.user.models.User
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface IUserRepository : R2dbcRepository<User, String> {
    fun findUserByPhoneNumber(phoneNumber: String): Mono<User>

    @Query(
        """
        SELECT rs.status as relation_ship_status,
            users.*
        FROM users
                 LEFT JOIN relationships rs
                           ON rs.other_phone = users.phone_number
                               AND rs.owner_phone = :ownerPhoneNumber
        WHERE (
            (rs.other_phone IS NOT NULL AND users.phone_number LIKE CONCAT('%', :phoneNumber, '%')) -- Nếu là bạn thì tìm theo like
                OR
            (rs.other_phone IS NULL AND users.phone_number = :phoneNumber) -- Nếu không phải là bạn thì tìm chính xác
            )
          AND users.phone_number != :ownerPhoneNumber
          AND rs.status IN ('FRIEND', 'STRANGER');
    """
    )
    fun findFriendAndStrangerByPhoneNumber(ownerPhoneNumber: String, phoneNumber: String): Flux<UserInRelationShip>

    @Query(
        """
        SELECT rs.status as relation_ship_status,
            users.*
        FROM users
                 LEFT JOIN relationships rs
                           ON rs.other_phone = users.phone_number
                               AND rs.owner_phone = :ownerPhoneNumber
        WHERE users.phone_number IN (:phoneNumbers)
          AND rs.status IN ('FRIEND', 'STRANGER');
    """
    )
    fun findFriendAndStrangerByPhoneNumber(ownerPhoneNumber: String, phoneNumbers: List<String>): Flux<UserInRelationShip>
}
