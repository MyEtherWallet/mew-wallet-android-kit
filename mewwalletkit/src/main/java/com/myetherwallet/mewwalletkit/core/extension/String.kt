package com.myetherwallet.mewwalletkit.core.extension

import java.math.BigDecimal
import java.math.BigInteger
import java.util.*

/**
 * Created by BArtWell on 21.05.2019.
 */

private const val PREFIX = "0x"

fun String.hexToByteArray(): ByteArray {
    var source = this.removeHexPrefix()
    var length = source.length
    if (length == 1) {
        source = "0$source"
        length = 2
    }
    if (length % 2 == 0) {
        val result = ByteArray(length / 2)
        for (i in 0 until length step 2) {
            result[i / 2] = Integer.parseInt(source.substring(i, i + 2), 16).toByte()
        }
        return result
    } else {
        throw IllegalStateException("Wrong string length")
    }
}

fun String.toBigIntegerExponential() = BigDecimal(this).toBigInteger()

fun String.hexToBigInteger(): BigInteger {
    val source = this.removePrefix("-").removeHexPrefix()
    if (source == "" || source == "0") {
        return BigInteger.ZERO
    }
    return BigInteger(source, 16).let {
        if (this.startsWith("-")) it.negate() else it
    }
}

fun String.isHex(checkPrefix: Boolean = false) = (!checkPrefix || this.startsWith(PREFIX)) && this.removeHexPrefix().isAllCharsHex()

private fun String.isAllCharsHex(): Boolean {
    for (char in this.uppercase(Locale.US).toCharArray()) {
        if (char != '0' &&
            char != '1' &&
            char != '2' &&
            char != '3' &&
            char != '4' &&
            char != '5' &&
            char != '6' &&
            char != '7' &&
            char != '8' &&
            char != '9' &&
            char != 'A' &&
            char != 'B' &&
            char != 'C' &&
            char != 'D' &&
            char != 'E' &&
            char != 'F'
        ) {
            return false
        }
    }
    return true
}

fun String.hasHexPrefix() = this.startsWith(PREFIX)

fun String.removeHexPrefix() = this.removePrefix(PREFIX)

fun String.addHexPrefix(): String {
    if (!this.startsWith(PREFIX)) {
        return PREFIX + this
    }
    return this
}

fun String.eip55(): String? {
    val hasHexPrefix = this.hasHexPrefix()
    val address = this.lowercase(Locale.US).removeHexPrefix()
    val hash = address.toByteArray().keccak256().toHexString()

    var eip55 = address
        .zip(hash)
        .map { (addressChar, hashChar) ->
            if (addressChar == '0' || addressChar == '1' || addressChar == '2' || addressChar == '3' || addressChar == '4' || addressChar == '5' || addressChar == '6' || addressChar == '7' || addressChar == '8' || addressChar == '9') {
                addressChar.toString()
            } else if (hashChar == '8' || hashChar == '9' || hashChar == 'a' || hashChar == 'b' || hashChar == 'c' || hashChar == 'd' || hashChar == 'e' || hashChar == 'f') {
                addressChar.toString().uppercase(Locale.US)
            } else {
                addressChar.toString().lowercase(Locale.US)
            }
        }
        .joinToString("")
    if (hasHexPrefix) {
        eip55 = eip55.addHexPrefix()
    }
    return eip55
}

fun String.decodeBase58(alphabet: String): ByteArray? {
    if (this.isEmpty()) {
        return ByteArray(0)
    }
    val alphabetBytes = alphabet.toByteArray()
    var result = BigInteger.ZERO
    var j = BigInteger.ONE
    val radix = BigInteger.valueOf(alphabetBytes.size.toLong())

    val byteString = this.toByteArray()
    val byteStringReversed = byteString.reversedArray()

    for (char in byteStringReversed) {
        val index = alphabetBytes.indexOf(char)
        if (index == -1) {
            return null
        }
        result += j * BigInteger.valueOf(index.toLong())
        j *= radix
    }

    val bytes = result.toByteArrayWithoutLeadingZeroByte()
    var prefixData = ByteArray(0)

    for (i in 0 until byteString.takeWhile { it == alphabetBytes[0] }.size) {
        prefixData += 0x00.toByte()
    }
    return prefixData + bytes
}

fun String.hashPersonalMessage() = this.toByteArray().hashPersonalMessage()
