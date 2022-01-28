package com.myetherwallet.mewwalletkit.eip.eip2930

import com.myetherwallet.mewwalletkit.bip.bip44.Address
import com.myetherwallet.mewwalletkit.core.data.rlp.*
import com.myetherwallet.mewwalletkit.core.extension.*
import com.myetherwallet.mewwalletkit.eip.eip155.Transaction
import com.myetherwallet.mewwalletkit.eip.eip155.TransactionCurrency
import com.myetherwallet.mewwalletkit.eip.eip155.TransactionSignature
import kotlinx.parcelize.Parcelize
import java.math.BigInteger

/**
 * Created by BArtWell on 07.07.2021.
 */

@Parcelize
class Eip2930Transaction(
    override var nonce: BigInteger = BigInteger.ZERO,
    val gasPrice: BigInteger = BigInteger.ZERO,
    override var gasLimit: BigInteger = BigInteger.ZERO,
    override var to: Address?,
    override var value: BigInteger = BigInteger.ZERO,
    override var data: ByteArray = ByteArray(0),
    override var from: Address? = null,
    val accessList: Array<AccessList>?,
    override var chainId: BigInteger? = null,
    override var signature: TransactionSignature? = null,
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
    EIPTransactionType.EIP2930
) {

    constructor(
        nonce: ByteArray = byteArrayOf(0x00),
        gasPrice: ByteArray = byteArrayOf(0x00),
        gasLimit: ByteArray = byteArrayOf(0x00),
        to: Address?,
        value: ByteArray = byteArrayOf(0x00),
        data: ByteArray = ByteArray(0),
        from: Address? = null,
        accessList: Array<AccessList>?,
        chainId: ByteArray?
    ) : this(
        nonce.toBigInteger(),
        gasPrice.toBigInteger(),
        gasLimit.toBigInteger(),
        to,
        value.toBigInteger(),
        data,
        from,
        accessList,
        chainId?.toBigInteger()
    )

    constructor(
        nonce: String = "0x00",
        gasPrice: String = "0x00",
        gasLimit: String = "0x00",
        to: Address?,
        value: String = "0x00",
        data: ByteArray,
        from: Address? = null,
        accessList: Array<AccessList>?,
        chainId: ByteArray? = null
    ) : this(
        nonce.hexToBigInteger(),
        gasPrice.hexToBigInteger(),
        gasLimit.hexToBigInteger(),
        to,
        value.hexToBigInteger(),
        data,
        from,
        accessList,
        chainId?.toBigInteger()
    )

    override fun toString(): String {
        var description = "Transaction\n"
        description += "EIPType: " + eipType.name + "\n"
        description += "Nonce: " + nonce.toHexStringWithoutLeadingZeroByte() + "\n"
        description += "Gas price: " + gasPrice.toHexStringWithoutLeadingZeroByte() + "\n"
        description += "Gas Limit: " + gasLimit.toHexStringWithoutLeadingZeroByte() + "\n"
        description += "From: $from\n"
        description += "To: $to\n"
        description += "Value: " + value.toHexStringWithoutLeadingZeroByte() + "\n"
        description += "Data: " + data.toHexString() + "\n"
        description += "Access list: \n"
        accessList?.forEach {
            description += "address: " + it.address + "\n"
            for (slot in (it.slots ?: emptyArray())) {
                description += "slot: " + slot.toHexString() + "\n"
            }
        }

        description += "ChainId: " + chainId?.toHexStringWithoutLeadingZeroByte() + "\n"
        description += "Signature: $signature\n"
        description += "Hash: " + hash()?.toHexString()
        return description
    }

    // Creates and returns rlp array with order:
    // rlp([chainId, nonce, gasPrice, gasLimit, to, value, data, accessList, signatureYParity, signatureR, signatureS])
    override fun rlpData(chainId: BigInteger?, forSignature: Boolean): RlpArray {
        if (chainId == null) {
            return RlpArray()
        }

        val fields = mutableListOf<Rlp>(chainId.toRlp(), nonce.toRlp(), gasPrice.toRlp(), gasLimit.toRlp())
        to?.address?.let {
            fields.add(RlpString(it))
        } ?: fields.add(RlpString(""))

        fields.add(value.toRlp())
        fields.add(RlpByteArray(data))

        val list = accessList
            ?.filter { it.address != null && it.slots != null }
            ?: emptyList()
        fields.add(RlpArray(*list.toTypedArray()))

        if (signature != null && !forSignature) {
            val signatureYParity = signature!!.signatureYParity
            val r = RlpBigInteger(signature!!.r)
            val s = RlpBigInteger(signature!!.s)

            r.dataLength = RlpBigInteger(signature!!.r).dataLength
            signatureYParity.dataLength = signature!!.signatureYParity.dataLength
            s.dataLength = RlpBigInteger(signature!!.s).dataLength
            fields.add(signatureYParity)
            fields.add(r)
            fields.add(s)
        }
        return RlpArray(*fields.toTypedArray())
    }
}
