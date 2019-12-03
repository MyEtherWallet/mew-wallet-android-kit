package com.myetherwallet.mewwalletkit.core.data.rlp

/**
 * Created by BArtWell on 19.06.2019.
 */

internal interface Rlp {
    fun rlpEncode(offset: Byte? = null): ByteArray?

    override fun toString(): String
}