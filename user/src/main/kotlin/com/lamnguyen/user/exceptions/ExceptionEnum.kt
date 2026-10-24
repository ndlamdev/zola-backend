/**
 * Nguyen Dinh Lam
 * Email: kiminonawa1305@gmail.com
 * Phone number: +84 855354919
 * Create at: 6:41 AM-10/07/2025
 *  User: kimin
 **/

package com.lamnguyen.user.exceptions

enum class ExceptionEnum(val code: Int, val message: String) {
    CREATE_USER_FAILED(3001, "Create User Failed"),
    UNAUTHENTICATED(3002, "Unauthenticated"),
    INVITE_ADD_FRIEND_FAILED(3003, "Add Friend Failed"),
    USER_NOT_FOUND(3004, "User Not Found"),
    INVITE_EXISTS(3005, "Invite Exists"),
    ERROR_FORMAT_PHONE_NUMBER(3006, "Invalid phone number!"),
    INVITE_NOT_EXISTS(3007, "Invite Not Exists"),
    SAME_PHONE_NUMBER(3008, "Same phone number"),
    GenerateKeyRelationshipFailure(3009, "Generate key relation ship failure"),
    ONLY_INVITE_WHEN_STRANGER(3010, "Only invite when relationship is stranger"),
}