package com.myetherwallet.mewwalletkit.core.extension

import com.myetherwallet.mewwalletkit.core.data.rlp.RlpBigInteger
import java.math.BigDecimal
import java.math.BigInteger

/**
 * Created by BArtWell on 04.07.2019.
 */

fun BigInteger.toByteArrayWithoutLeadingZeroByte(): ByteArray {
    var array = this.toByteArray()
    if (array[0].toInt() == 0) {
        val tmp = ByteArray(array.size - 1)
        System.arraycopy(array, 1, tmp, 0, tmp.size)
        array = tmp
    }
    return array
}

fun BigInteger.toHexString() = this.toByteArray().toHexString().addHexPrefix()

fun BigInteger.toHexStringWithoutLeadingZeroByte() = this.toByteArrayWithoutLeadingZeroByte().toHexString().addHexPrefix()

fun BigInteger.toHexStringWithoutStartZeros() = this.toString(16).addHexPrefix()

fun BigInteger.toTokenValue(decimals: Int = 18): BigDecimal = BigDecimal(this).divide(BigDecimal.TEN.pow(decimals))

internal fun BigInteger.toRlp() = RlpBigInteger(this)
