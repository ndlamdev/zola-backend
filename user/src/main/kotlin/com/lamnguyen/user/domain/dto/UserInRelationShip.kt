/**
 * Nguyen Dinh Lam
 * Email: kiminonawa1305@gmail.com
 * Phone number: +84 855354919
 * Create at: 2:09 PM-08/08/2025
 *  User: kimin
 **/

package com.lamnguyen.user.domain.dto

import com.lamnguyen.user.models.User
import com.lamnguyen.user.utils.enums.RelationshipStatus

class UserInRelationShip : User() {
    var displayName: String? = null
    var relationShipStatus: RelationshipStatus = RelationshipStatus.SELF
}