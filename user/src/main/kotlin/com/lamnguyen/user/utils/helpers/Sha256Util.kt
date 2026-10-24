/**
 * Nguyen Dinh Lam
 * Email: kiminonawa1305@gmail.com
 * Phone number: +84 855354919
 * Create at: 11:11 AM-25/07/2026
 *  User: ndlamdev
 **/

package com.lamnguyen.user.utils.helpers

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*

object Sha256Util {
    @Throws(Exception::class)
    fun sha256(input: String): String? {
        val md = MessageDigest.getInstance("SHA-256")

        val hash = md.digest(input.toByteArray(StandardCharsets.UTF_8))

        return HexFormat.of().formatHex(hash)
    }
}