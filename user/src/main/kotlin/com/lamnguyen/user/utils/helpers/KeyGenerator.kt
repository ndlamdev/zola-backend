/**
 * Nguyen Dinh Lam
 * Email: kiminonawa1305@gmail.com
 * Phone number: +84 855354919
 * Create at: 9:19 AM-24/10/2026
 *  User: ndlamdev
 **/

package com.lamnguyen.user.utils.helpers

import com.lamnguyen.user.exceptions.ApplicationException
import com.lamnguyen.user.exceptions.ExceptionEnum

object KeyGenerator {
    fun generateKeyRelationship(phoneA: String?, phoneB: String?): String {
        if (phoneA.isNullOrBlank() || phoneB.isNullOrBlank()) throw ApplicationException(ExceptionEnum.ERROR_FORMAT_PHONE_NUMBER)

        val compare = phoneA.compareTo(phoneB)
        if (compare == 0) throw ApplicationException(ExceptionEnum.SAME_PHONE_NUMBER)
        val rawKey = if (compare > 0) "${phoneA}-${phoneB}"
        else "${phoneB}-${phoneA}"

        return Sha256Util.sha256(rawKey) ?: throw ApplicationException(ExceptionEnum.GenerateKeyRelationshipFailure)
    }
}