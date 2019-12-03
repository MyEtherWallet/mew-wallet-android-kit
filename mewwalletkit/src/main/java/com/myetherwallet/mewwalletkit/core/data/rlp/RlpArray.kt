package com.myetherwallet.mewwalletkit.core.data.rlp

/**
 * Created by BArtWell on 19.06.2019.
 */

internal class RlpArray(private vararg val value: Rlp) : Rlp {

    override fun rlpEncode(offset: Byte?): ByteArray? {
        var data = ByteArray(0)
        for (item in value) {
            val encoded = item.rlpEncode(null) ?: return null
            data += encoded
        }
        val length = RlpInt(data.size).rlpLengthEncode(0xc0.toByte()) ?: return null
        return length + data
    }

    override fun toString() = value.contentToString()
}
