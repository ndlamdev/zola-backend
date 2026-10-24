/**
 * Nguyen Dinh Lam
 * Email: kiminonawa1305@gmail.com
 * Phone number: +84 855354919
 * Create at: 4:57 PM-30/07/2025
 *  User: kimin
 **/

package com.lamnguyen.user.repositories

import com.lamnguyen.user.models.Relationship
import com.lamnguyen.user.utils.enums.RelationshipStatus
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface IRelationshipRepository : R2dbcRepository<Relationship, String> {
    fun existsBySoftIdAndStatus(id: String, status: RelationshipStatus): Mono<Boolean>

    @Query(
        """
        SELECT *
        FROM relationships
        WHERE status = :status
          AND (phone_a = :viewer OR phone_b = :viewer)
    """
    )
    fun findAllRelationship(
        viewer: String,
        status: RelationshipStatus
    ): Flux<Relationship>

    fun findBySoftId(softId: String): Mono<Relationship>
}