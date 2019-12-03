package com.myetherwallet.mewwalletkit.core.data.rlp

import com.myetherwallet.mewwalletkit.core.extension.toHexString

/**
 * Created by BArtWell on 19.06.2019.
 */

internal data class RlpByteArray(private val value: ByteArray) : Rlp {

    override fun rlpEncode(offset: Byte?): ByteArray? {
        if (value.size == 1 && (value[0].toLong() and 0xffffffff) < 0x80) {
            return value
        }
        val length = RlpInt(value.size).rlpLengthEncode(0x80.toByte()) ?: return null
        return length + value
    }

    override fun toString(): String {
        return value.toHexString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as RlpByteArray
        if (!value.contentEquals(other.value)) return false
        return true
    }

    override fun hashCode(): Int {
        return value.contentHashCode()
    }
}
