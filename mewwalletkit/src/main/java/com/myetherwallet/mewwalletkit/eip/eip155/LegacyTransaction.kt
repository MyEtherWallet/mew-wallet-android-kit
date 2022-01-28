package com.myetherwallet.mewwalletkit.eip.eip155

import android.os.Parcelable
import com.myetherwallet.mewwalletkit.bip.bip44.Address
import com.myetherwallet.mewwalletkit.core.data.rlp.*
import com.myetherwallet.mewwalletkit.core.extension.hexToBigInteger
import com.myetherwallet.mewwalletkit.core.extension.toBigInteger
import com.myetherwallet.mewwalletkit.core.extension.toRlp
import kotlinx.parcelize.Parcelize
import java.math.BigInteger

/**
 * Created by BArtWell on 15.06.2021.
 */

@Parcelize
open class LegacyTransaction(
    override var nonce: BigInteger = BigInteger.ZERO,
    var gasPrice: BigInteger = BigInteger.ZERO,
    override var gasLimit: BigInteger = BigInteger.ZERO,
    override var to: Address?,
    override var value: BigInteger = BigInteger.ZERO,
    override var data: ByteArray = byteArrayOf(),
    override var from: Address? = null,
    override var signature: TransactionSignature? = null,
    override var chainId: BigInteger? = null,
    override var currency: TransactionCurrency? = null
) : Transaction(
    nonce,
    gasLimit,
    to,
    value,
    data,
    from,
    signature,
    chainId,
    currency,
    EIPTransactionType.LEGACY
), Parcelable {

    constructor(
        nonce: ByteArray = byteArrayOf(0x00),
        gasPrice: ByteArray = byteArrayOf(0x00),
        gasLimit: ByteArray = byteArrayOf(0x00),
        to: Address?,
        value: ByteArray = byteArrayOf(0x00),
        data: ByteArray = ByteArray(0)
    ) : this(
        nonce.toBigInteger(),
        gasPrice.toBigInteger(),
        gasLimit.toBigInteger(),
        to,
        value.toBigInteger(),
        data
    )

    constructor(
        nonce: String = "0x00",
        gasPrice: String = "0x00",
        gasLimit: String = "0x00",
        to: Address?,
        value: String = "0x00",
        data: ByteArray
    ) : this(
        nonce.hexToBigInteger(),
        gasPrice.hexToBigInteger(),
        gasLimit.hexToBigInteger(),
        to,
        value.hexToBigInteger(),
        data
    )

    override fun rlpData(chainId: BigInteger?, forSignature: Boolean): RlpArray {
        val fields = mutableListOf<Rlp>(nonce.toRlp(), gasPrice.toRlp(), gasLimit.toRlp())
        to?.address?.let {
            fields.add(RlpString(it))
        } ?: fields.add(RlpString(""))

        fields.add(value.toRlp())
        fields.add(RlpByteArray(data))
        val finalChainId = chainId ?: this.chainId

        if (signature != null && !forSignature) {
            val v = RlpBigInteger(signature!!.v)
            val r = RlpBigInteger(signature!!.r)
            val s = RlpBigInteger(signature!!.s)
            r.dataLength = RlpBigInteger(signature!!.r).dataLength
            v.dataLength = RlpBigInteger(signature!!.v).dataLength
            s.dataLength = RlpBigInteger(signature!!.s).dataLength
            fields.add(v)
            fields.add(r)
            fields.add(s)
        } else if (finalChainId != null) {
            fields.add(finalChainId.toRlp())
            fields.add(RlpBigInteger(BigInteger.ZERO))
            fields.add(RlpBigInteger(BigInteger.ZERO))
        }
        return RlpArray(*fields.toTypedArray())
    }
}
