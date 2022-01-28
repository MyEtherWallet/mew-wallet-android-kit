package com.myetherwallet.mewwalletkit.core.data.rlp

import com.myetherwallet.mewwalletkit.eip.eip155.Transaction
import java.math.BigInteger

/**
 * Created by BArtWell on 19.06.2019.
 */

internal class RlpTransaction(private val value: Transaction) : Rlp {

    override fun rlpEncode(offset: Byte?): ByteArray? {
        return value.rlpData().rlpEncode()
    }

    override fun toString(): String {
        return value.toString()
    }
}
