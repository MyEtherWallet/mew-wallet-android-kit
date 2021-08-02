package com.myetherwallet.mewwalletkit.eip.eip155

import android.os.Parcelable
import com.myetherwallet.mewwalletkit.bip.bip44.Address
import com.myetherwallet.mewwalletkit.core.data.rlp.RlpTransaction
import com.myetherwallet.mewwalletkit.core.extension.hexToBigInteger
import com.myetherwallet.mewwalletkit.core.extension.keccak256
import kotlinx.android.parcel.Parcelize
import java.math.BigInteger

/**
 * Created by BArtWell on 13.06.2019.
 */

@Parcelize
class Transaction(
    var nonce: BigInteger = BigInteger.ZERO,
    var gasPrice: BigInteger = BigInteger.ZERO,
    var gasLimit: BigInteger = BigInteger.ZERO,
    var to: Address?,
    var value: BigInteger = BigInteger.ZERO,
    var data: ByteArray = ByteArray(0),
    var from: Address? = null,
    var signature: TransactionSignature? = null,
    var chainId: BigInteger? = null,
    var currency: TransactionCurrency? = null
) : Parcelable {

    constructor(
        nonce: ByteArray = byteArrayOf(0x00),
        gasPrice: ByteArray = byteArrayOf(0x00),
        gasLimit: ByteArray = byteArrayOf(0x00),
        to: Address?,
        value: ByteArray = byteArrayOf(0x00),
        data: ByteArray = ByteArray(0)
    ) : this(
        BigInteger(nonce),
        BigInteger(gasPrice),
        BigInteger(gasLimit),
        to,
        BigInteger(value),
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

    fun hash(chainID: BigInteger? = null, forSignature: Boolean = false): ByteArray? {
        return RlpTransaction(this).rlpData(chainID, forSignature).rlpEncode()?.keccak256()
    }

    override fun toString(): String {
        return "Transaction(nonce=$nonce, gasPrice=$gasPrice, gasLimit=$gasLimit, to=$to, value=$value, data=${data.contentToString()}, from=$from, signature=$signature, chainId=$chainId)"
    }
}
