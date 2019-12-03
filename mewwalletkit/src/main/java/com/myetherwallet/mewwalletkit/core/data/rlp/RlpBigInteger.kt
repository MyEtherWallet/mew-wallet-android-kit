package com.myetherwallet.mewwalletkit.core.data.rlp

import com.myetherwallet.mewwalletkit.core.extension.toByteArrayWithoutLeadingZeroByte
import java.math.BigInteger

/**
 * Created by BArtWell on 19.06.2019.
 */

internal class RlpBigInteger(private val value: BigInteger) : Rlp, RlpLength {

    private val rlpLengthMax = BigInteger.valueOf(1) shl 256

    override fun rlpEncode(offset: Byte?): ByteArray? {
        return if (offset == null) {
            if (value == BigInteger.ZERO) {
                RlpByteArray(ByteArray(0)).rlpEncode()
            } else {
                RlpByteArray(value.toByteArrayWithoutLeadingZeroByte()).rlpEncode()
            }
        } else {
            rlpLengthEncode(offset)
        }
    }

    override fun rlpLengthEncode(offset: Byte): ByteArray? {
        if (value >= rlpLengthMax) {
            return null
        }
        return BigInteger.valueOf((value.toByteArrayWithoutLeadingZeroByte().size + offset + 55).toLong()).toByteArray() + value.toByteArrayWithoutLeadingZeroByte()
    }

    override fun toString(): String {
        return value.toString()
    }
}
