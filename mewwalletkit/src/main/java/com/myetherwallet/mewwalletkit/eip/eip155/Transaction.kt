package com.myetherwallet.mewwalletkit.eip.eip155

import android.os.Parcelable
import com.myetherwallet.mewwalletkit.bip.bip44.Address
import com.myetherwallet.mewwalletkit.core.data.rlp.Rlp
import com.myetherwallet.mewwalletkit.core.data.rlp.RlpArray
import com.myetherwallet.mewwalletkit.core.data.rlp.RlpTransaction
import com.myetherwallet.mewwalletkit.core.extension.hexToBigInteger
import com.myetherwallet.mewwalletkit.core.extension.hexToByteArray
import com.myetherwallet.mewwalletkit.core.extension.keccak256
import java.math.BigInteger

/**
 * Created by BArtWell on 13.06.2019.
 */

abstract class Transaction(
    @Transient
    open var nonce: BigInteger = BigInteger.ZERO,
    @Transient
    open var gasLimit: BigInteger = BigInteger.ZERO,
    @Transient
    open var to: Address?,
    @Transient
    open var value: BigInteger = BigInteger.ZERO,
    @Transient
    open var data: ByteArray = ByteArray(0),
    @Transient
    open var from: Address? = null,
    @Transient
    open var signature: TransactionSignature? = null,
    @Transient
    open var chainId: BigInteger? = null,
    @Transient
    open var currency: TransactionCurrency? = null,
    open var eipType: EIPTransactionType = EIPTransactionType.LEGACY
) : Parcelable {

    constructor(
        nonce: ByteArray = byteArrayOf(0x00),
        gasLimit: ByteArray = byteArrayOf(0x00),
        to: Address?,
        value: ByteArray = byteArrayOf(0x00),
        data: ByteArray = ByteArray(0)
    ) : this(
        BigInteger(nonce),
        BigInteger(gasLimit),
        to,
        BigInteger(value),
        data
    )

    constructor(
        nonce: String = "0x00",
        gasLimit: String = "0x00",
        to: Address?,
        value: String = "0x00",
        data: ByteArray
    ) : this(
        nonce.hexToBigInteger(),
        gasLimit.hexToBigInteger(),
        to,
        value.hexToBigInteger(),
        data
    )

    fun serialize(): ByteArray? {
        val typeData = eipType.data
        val rlpData = rlpData(chainId, false).rlpEncode()
        return rlpData?.let {
            typeData + it
        }
    }

    fun hash(chainId: BigInteger? = null, forSignature: Boolean = false): ByteArray? {
        val typeData = eipType.data
        val rlpData = rlpData(chainId, forSignature).rlpEncode()
        return rlpData
            ?.let { typeData + it }
            ?.keccak256()
    }

    override fun toString(): String {
        return "Transaction(nonce=$nonce, gasLimit=$gasLimit, to=$to, value=$value, data=${data.contentToString()}, from=$from, signature=$signature, chainId=$chainId, eipType=$eipType)"
    }

    enum class EIPTransactionType(val data: ByteArray) {
        EIP2930("0x01".hexToByteArray()),
        EIP1559("0x02".hexToByteArray()),
        LEGACY("0x".hexToByteArray())
    }

    internal open fun rlpData(chainId: BigInteger? = null, forSignature: Boolean = false): RlpArray {
        throw IllegalAccessError("Please override it in the subclass if you want to have rlp encoded values")
    }
}
