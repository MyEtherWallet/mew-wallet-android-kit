package com.myetherwallet.mewwalletkit.core.data.rlp

import com.myetherwallet.mewwalletkit.core.extension.padLeft
import com.myetherwallet.mewwalletkit.core.extension.toByteArrayWithoutLeadingZeroByte
import java.math.BigInteger

/**
 * Created by BArtWell on 19.06.2019.
 */

internal class RlpBigInteger(private val value: BigInteger) : Rlp, RlpLength {

    constructor(value: BigInteger, length: Int) : this(value) {
        dataLength = length
    }

    var dataLength: Int? = null
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
        if (dataLength == null) {
            dataLength = value.toByteArrayWithoutLeadingZeroByte().size
        }
        return BigInteger.valueOf((dataLength!! + offset + 55).toLong()).toByteArray() +
                value.toByteArrayWithoutLeadingZeroByte().padLeft(dataLength!!)
    }

    override fun toString(): String {
        return value.toString()
    }
}
