/**
 * Nguyen Dinh Lam
 * Email: kiminonawa1305@gmail.com
 * Phone number: +84 855354919
 * Create at: 2:35 PM-08/08/2025
 *  User: kimin
 **/

package com.lamnguyen.user.mappers

import com.lamnguyen.user.domain.dto.UserInRelationShip
import com.lamnguyen.user.domain.request.RegisInfoRequest
import com.lamnguyen.user.models.Relationship
import com.lamnguyen.user.models.User
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Named
import org.mapstruct.TargetPropertyName

@Mapper(componentModel = "spring")
interface IUserMapper {
    @Mapping(target = "displayName", source = "fullName")
    fun toUserInRelationShip(user: User): UserInRelationShip

    @Mapping(
        target = "displayName",
        expression = "java(user.getPhoneNumber().equals(relationship.getPhoneA()) ? relationship.getDisplayNameB() : relationship.getDisplayNameA())"
    )
    @Mapping(target = "relationShipStatus", source = "relationship.status")
    @Mapping(target = "createdAt", source = "user.createdAt")
    @Mapping(target = "updatedAt", source = "user.updatedAt")
    @Mapping(target = "createdBy", source = "user.createdBy")
    @Mapping(target = "updatedBy", source = "user.updatedBy")
    @Mapping(target = "locked", source = "user.locked")
    @Mapping(target = "deleted", source = "user.deleted")
    fun toUserInRelationShip(user: User, relationship: Relationship): UserInRelationShip

    fun toUserInRelationShip(user: User, displayName: String): UserInRelationShip

    @Mapping(target = "avatar", source = "avatarUrl")
    fun toEntity(data: RegisInfoRequest): User
}