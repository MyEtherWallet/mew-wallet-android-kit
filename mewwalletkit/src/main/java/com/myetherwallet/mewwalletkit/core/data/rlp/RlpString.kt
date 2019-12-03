package com.myetherwallet.mewwalletkit.core.data.rlp

import com.myetherwallet.mewwalletkit.core.extension.hexToByteArray
import com.myetherwallet.mewwalletkit.core.extension.isHex

/**
 * Created by BArtWell on 19.06.2019.
 */

internal class RlpString(private val value: String) : Rlp {

    override fun rlpEncode(offset: Byte?): ByteArray? {
        return if (value.isHex()) {
            RlpByteArray(value.hexToByteArray()).rlpEncode()
        } else {
            RlpByteArray(value.toByteArray()).rlpEncode()
        }
    }

    override fun toString() = value
}
