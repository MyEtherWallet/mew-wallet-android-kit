package com.myetherwallet.mewwalletkit.core.data.rlp

import com.myetherwallet.mewwalletkit.core.extension.toByteArrayWithoutLeadingZeroByte
import java.math.BigInteger

/**
 * Created by BArtWell on 19.06.2019.
 */

internal data class RlpInt(private val value: Int) : Rlp, RlpLength {

    override fun rlpEncode(offset: Byte?): ByteArray? {
        return if (offset == null) {
            val array = BigInteger.valueOf(value.toLong()).toByteArrayWithoutLeadingZeroByte()
            RlpByteArray(array).rlpEncode()
        } else {
            rlpLengthEncode(offset)
        }
    }

    override fun rlpLengthEncode(offset: Byte): ByteArray? {
        if (value < 0) {
            return null
        }
        return if (value < 56) {
            byteArrayOf((value + offset).toByte())
        } else {
            RlpBigInteger(BigInteger.valueOf(value.toLong())).rlpLengthEncode(offset)
        }
    }

    override fun toString(): String {
        return value.toString()
    }
}