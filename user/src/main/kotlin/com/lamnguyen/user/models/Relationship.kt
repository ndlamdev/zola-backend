/**
 * Nguyen Dinh Lam
 * Email: kiminonawa1305@gmail.com
 * Phone number: +84 855354919
 * Create at: 12:31 PM-24/07/2025
 *  User: kimin
 **/

package com.lamnguyen.user.models

import com.lamnguyen.user.utils.enums.RelationshipStatus
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("relationships")
class Relationship : BaseEntity() {
    @Id
    var id: Long? = null
    var softId: String? = null
    var status: RelationshipStatus = RelationshipStatus.STRANGER
    var phoneA: String = ""
    var displayNameA: String? = null
    var phoneB: String = ""
    var displayNameB: String? = null
}