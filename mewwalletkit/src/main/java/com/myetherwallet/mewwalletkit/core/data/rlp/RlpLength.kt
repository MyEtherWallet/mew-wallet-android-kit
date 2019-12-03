package com.myetherwallet.mewwalletkit.core.data.rlp

/**
 * Created by BArtWell on 19.06.2019.
 */

internal interface RlpLength {
    fun rlpLengthEncode(offset: Byte): ByteArray?
}