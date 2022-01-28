package com.myetherwallet.mewwalletkit.eip.eip155

import android.os.Parcelable
import com.myetherwallet.mewwalletkit.core.data.rlp.RlpBigInteger
import com.myetherwallet.mewwalletkit.core.extension.*
import com.myetherwallet.mewwalletkit.eip.eip155.exception.InvalidSignatureException
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.math.BigInteger

/**
 * Created by BArtWell on 13.06.2019.
 */

@Parcelize
class TransactionSignature(
    internal var rBytes: ByteArray,
    internal var sBytes: ByteArray,
    internal var v: BigInteger,
    private var chainId: BigInteger = BigInteger.ZERO
) : Parcelable {

    internal lateinit var signatureYParity: RlpBigInteger
        private set

    @IgnoredOnParcel
    internal val r: BigInteger by lazy {
        rBytes.toBigInteger()
    }

    @IgnoredOnParcel
    internal val s: BigInteger by lazy {
        sBytes.toBigInteger()
    }

    val inferredChainID: BigInteger?
        get() = if (r == BigInteger.ZERO && s == BigInteger.ZERO) {
            v
        } else if (v == BigInteger.valueOf(27) || v == BigInteger.valueOf(28)) {
            null
        } else {
            (v - BigInteger.valueOf(35)) / BigInteger.valueOf(2)
        }

    constructor(signature: ByteArray, chainID: BigInteger) : this(
        signature.copyOfRange(0, 32),
        signature.copyOfRange(32, 64),
        BigInteger.valueOf(signature[64].toLong()) + BigInteger.valueOf(35) + chainID + chainID,
        chainID
    ) {
        if (signature.size != 65) {
            throw InvalidSignatureException()
        }
        signatureYParity = BigInteger(byteArrayOf(signature[64])).toRlp()
    }

    constructor(r: String, s: String, v: String, chainID: BigInteger = BigInteger.ZERO) : this(
        r.hexToByteArray(),
        s.hexToByteArray(),
        v.hexToBigInteger(),
        chainID
    )

    fun recoverPublicKey(transaction: Transaction): ByteArray? {
        if (this.r == BigInteger.ZERO || this.s == BigInteger.ZERO) {
            return null
        }
        val inferredChainID = this.inferredChainID
        val normalizedV = when {
            this.chainId != BigInteger.ZERO -> this.v - BigInteger.valueOf(35) - BigInteger.valueOf(2) * this.chainId
            inferredChainID != null -> this.v - BigInteger.valueOf(35) - BigInteger.valueOf(2) * inferredChainID
            else -> this.v - BigInteger.valueOf(27)
        }
        val rByteArray = this.rBytes.reversedArray()
        val sByteArray = this.sBytes.reversedArray()

        val vByteArray = if (normalizedV == BigInteger.ZERO) {
            byteArrayOf(0x00)
        } else {
            normalizedV.toByteArrayWithoutLeadingZeroByte()
        }
        val signature = rByteArray + sByteArray + vByteArray
        val hash = transaction.hash(inferredChainID, true) ?: return null
        return signature.secp256k1RecoverPublicKey(hash, false)
    }

    override fun toString() = "TransactionSignature(r=${r.toHexStringWithoutLeadingZeroByte()}, s=${s.toHexStringWithoutLeadingZeroByte()}, v=${v.toHexStringWithoutLeadingZeroByte()}, chainID=$chainId)"
}
