package com.myetherwallet.mewwalletkit.core.data.rlp

import com.myetherwallet.mewwalletkit.eip.eip155.Transaction
import java.math.BigInteger

/**
 * Created by BArtWell on 19.06.2019.
 */

internal class RlpTransaction(private val value: Transaction) : Rlp {

    override fun rlpEncode(offset: Byte?): ByteArray? {
        return rlpData().rlpEncode()
    }

    internal fun rlpData(chainID: BigInteger? = null, forSignature: Boolean = false): Rlp {
        val fields = mutableListOf<Rlp>(RlpBigInteger(value.nonce), RlpBigInteger(value.gasPrice), RlpBigInteger(value.gasLimit))
        val address = value.to?.address
        if (address == null) {
            fields.add(RlpString(""))
        } else {
            fields.add(RlpString(address))
        }
        fields.add(RlpBigInteger(value.value))
        fields.add(RlpByteArray(value.data))

        val signature = value.signature
        val checkedChainID = chainID ?: value.chainId
        if (signature != null && !forSignature) {
            fields.add(RlpBigInteger(signature.v, 1))
            fields.add(RlpBigInteger(signature.r, 32))
            fields.add(RlpBigInteger(signature.s, 32))
        } else if (checkedChainID != null) {
            fields.add(RlpBigInteger(checkedChainID))
            fields.add(RlpInt(0))
            fields.add(RlpInt(0))
        }
        return RlpArray(*fields.toTypedArray())
    }

    override fun toString(): String {
        return value.toString()
    }
}