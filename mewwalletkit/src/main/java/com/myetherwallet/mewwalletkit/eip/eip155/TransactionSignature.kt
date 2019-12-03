package com.myetherwallet.mewwalletkit.eip.eip155

import android.os.Parcelable
import com.myetherwallet.mewwalletkit.core.extension.hexToBigInteger
import com.myetherwallet.mewwalletkit.core.extension.padLeft
import com.myetherwallet.mewwalletkit.core.extension.secp256k1RecoverPublicKey
import com.myetherwallet.mewwalletkit.core.extension.toByteArrayWithoutLeadingZeroByte
import com.myetherwallet.mewwalletkit.eip.eip155.exception.InvalidSignatureException
import kotlinx.android.parcel.Parcelize
import java.math.BigInteger

/**
 * Created by BArtWell on 13.06.2019.
 */

@Parcelize
class TransactionSignature(
    internal var r: BigInteger,
    internal var s: BigInteger,
    internal var v: BigInteger,
    private var chainID: BigInteger = BigInteger.ZERO
) : Parcelable {

    val inferredChainID: BigInteger?
        get() = if (r == BigInteger.ZERO && s == BigInteger.ZERO) {
            v
        } else if (v == BigInteger.valueOf(27) || v == BigInteger.valueOf(28)) {
            null
        } else {
            (v - BigInteger.valueOf(35)) / BigInteger.valueOf(2)
        }

    constructor(signature: ByteArray, chainID: BigInteger) : this(
        BigInteger(signature.copyOfRange(0, 32)),
        BigInteger(signature.copyOfRange(32, 64)),
        BigInteger.valueOf(signature[64].toLong()) + BigInteger.valueOf(35) + chainID + chainID,
        chainID
    ) {
        if (signature.size != 65) {
            throw InvalidSignatureException()
        }
    }

    constructor(r: String, s: String, v: String, chainID: BigInteger = BigInteger.ZERO) : this(
        r.hexToBigInteger(),
        s.hexToBigInteger(),
        v.hexToBigInteger(),
        chainID
    )

    fun recoverPublicKey(transaction: Transaction): ByteArray? {
        if (this.r == BigInteger.ZERO || this.s == BigInteger.ZERO) {
            return null
        }
        val inferredChainID = this.inferredChainID
        val normalizedV = when {
            this.chainID != BigInteger.ZERO -> this.v - BigInteger.valueOf(35) - BigInteger.valueOf(2) * this.chainID
            inferredChainID != null -> this.v - BigInteger.valueOf(35) - BigInteger.valueOf(2) * inferredChainID
            else -> this.v - BigInteger.valueOf(27)
        }
        val rByteArray = this.r.toByteArrayWithoutLeadingZeroByte().padLeft(32).reversedArray()
        val sByteArray = this.s.toByteArrayWithoutLeadingZeroByte().padLeft(32).reversedArray()

        val vByteArray = if (normalizedV == BigInteger.ZERO) {
            byteArrayOf(0x00)
        } else {
            normalizedV.toByteArrayWithoutLeadingZeroByte()
        }
        val signature = rByteArray + sByteArray + vByteArray
        val hash = transaction.hash(inferredChainID, true) ?: return null
        return signature.secp256k1RecoverPublicKey(hash, false) ?: return null
    }
}