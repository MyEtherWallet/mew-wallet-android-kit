package com.myetherwallet.mewwalletkit.core.extension

import com.myetherwallet.mewwalletkit.core.util.BitReader
import org.bitcoin.NativeSecp256k1
import org.spongycastle.crypto.digests.RIPEMD160Digest
import org.spongycastle.jcajce.provider.digest.Keccak
import java.math.BigInteger
import java.security.MessageDigest
import java.security.SecureRandom
import kotlin.experimental.or
import kotlin.experimental.xor

/**
 * Created by BArtWell on 13.05.2019.
 */

fun ByteArray.toBits(position: Int, length: Int): Int? {
    return toBits(position until position + length)
}

fun ByteArray.toBits(range: IntRange): Int? {
    if (range.start < 0 || range.start > range.last || range.last >= this.size * 8) {
        return null
    }
    val reader = BitReader(this)
    return reader.get(range)
}

fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }

fun ByteArray.toBigInteger(): BigInteger = if (this.isEmpty()) BigInteger.ZERO else BigInteger(1, this)

fun ByteArray.prefix(count: Int) = this.copyOfRange(0, count)

fun ByteArray.padLeft(length: Int, byte: Byte = 0x00): ByteArray {
    return if (length > this.size) {
        val data = ByteArray(length - this.size)
        data.fill(byte)
        data + this
    } else {
        this
    }
}

fun ByteArray.padRight(length: Int, byte: Byte = 0x00): ByteArray {
    return if (length > this.size) {
        val data = ByteArray(length - this.size)
        data.fill(byte)
        this + data
    } else {
        this
    }
}


fun ByteArray.sha256() = sha("SHA-256")

fun ByteArray.sha512() = sha("SHA-512")

fun ByteArray.md5(): ByteArray {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(this))
        .toString(16)
        .padStart(32, '0')
        .hexToByteArray()
}

private fun ByteArray.sha(algorithm: String): ByteArray {
    val messageDigest = MessageDigest.getInstance(algorithm)
    messageDigest.update(this)
    return messageDigest.digest()
}

fun ByteArray.keccak256(): ByteArray {
    val digest = Keccak.Digest256()
    digest.update(this)
    return digest.digest()
}

fun ByteArray.ripemd160(): ByteArray {
    val data = this.sha256()
    val digest = RIPEMD160Digest()
    digest.update(data, 0, data.size)
    val output = ByteArray(digest.digestSize)
    digest.doFinal(output, 0)
    return output
}

fun ByteArray.eip55() = this.toHexString().eip55()

fun ByteArray.encodeBase58(alphabet: String): ByteArray? {
    if (this.isEmpty()) {
        return ByteArray(0)
    }
    val radix = BigInteger.valueOf(alphabet.length.toLong())
    val extraZero = ByteArray(this.size + 1)
    extraZero[0] = 0
    var result = ""
    System.arraycopy(this, 0, extraZero, 1, this.size)
    var value = BigInteger(extraZero)
    while (value.compareTo(BigInteger.ZERO) == 1) {
        val (quotient, modulus) = value.divideAndRemainder(radix)
        value = quotient
        val c = alphabet[modulus.toInt()]
        result += c
    }
    result = String(StringBuffer(result).reverse())
    var i = 0
    while (i < this.size && this[i].toInt() == 0) {
        result = alphabet[0] + result
        i++
    }
    return result.toByteArray()
}

fun ByteArray.encodeBase58String(alphabet: String): String? {
    val data = this.encodeBase58(alphabet) ?: return null
    return String(data)
}

fun ByteArray.secp256k1Verify() = NativeSecp256k1.secKeyVerify(this)

fun ByteArray.secp256k1RecoverableSign(privateKey: ByteArray, addExtraEntropy: Boolean = false): ByteArray? {
    if (this.size != 32 || privateKey.size != 32) {
        return null
    }
    val extraEntropy = if (addExtraEntropy) {
        val bytes = ByteArray(32)
        SecureRandom().nextBytes(bytes)
        bytes
    } else {
        null
    }
    return NativeSecp256k1.createRecoverableEcdsaSignature(this, privateKey, extraEntropy)
}

fun ByteArray.secp256k1SerializeSignature(): ByteArray? {
    if (this.size != 65) {
        return null
    }
    var (v, serialized) = NativeSecp256k1.serializeCompactEcdsaRecoverableSignature(this) ?: return null
    serialized += when (v) {
        0.toByte() -> byteArrayOf(0x00)
        1.toByte() -> byteArrayOf(0x01)
        else -> return null
    }
    return serialized
}

fun ByteArray.secp256k1ParseSignature(): ByteArray? {
    if (this.size != 65) {
        return null
    }
    val serialized = this.copyOfRange(0, 64)
    val v = this[64].toInt()
    return NativeSecp256k1.parseEcdsaRecoverableSignatureCompact(serialized, v)
}

fun ByteArray.parsePublicKey(): ByteArray {
    return NativeSecp256k1.parsePublicKey(this)
}

fun ByteArray.secp256k1RecoverPublicKey(hash: ByteArray, compressed: Boolean): ByteArray? {
    if (this.size != 65 || hash.size != 32) {
        return null
    }
    val publicKey = NativeSecp256k1.recoverEcdsaPublicKeyFromSignature(this, hash)
    return NativeSecp256k1.serializePublicKey(publicKey!!, compressed)
}

fun ByteArray.secureCompare(rhs: ByteArray): Boolean {
    if (this.size != rhs.size) {
        return false
    }
    var difference = 0x00.toByte()
    for (i in 0 until this.size) {
        difference = difference or (this[i] xor rhs[i])
    }
    return difference == 0x00.toByte()
}

fun ByteArray.hashPersonalMessage(): ByteArray {
    var prefix = "\u0019Ethereum Signed Message:\n"
    prefix += this.size.toString()
    val prefixData = prefix.toByteArray()
    val data = prefixData + this
    return data.keccak256()
}

fun ByteArray?.isNullOrEmpty() = this == null || this.isEmpty()
