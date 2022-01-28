package com.myetherwallet.mewwalletkit.bip.bip44

import com.myetherwallet.mewwalletkit.bip.bip44.exception.InvalidDataException
import com.myetherwallet.mewwalletkit.core.extension.*
import com.myetherwallet.mewwalletkit.core.util.HMAC
import java.math.BigInteger
import java.nio.ByteOrder


/**
 * Created by BArtWell on 23.05.2019.
 */

// "Bitcoin seed"
private val HMAC_KEY_DATA = byteArrayOf(0x42, 0x69, 0x74, 0x63, 0x6F, 0x69, 0x6E, 0x20, 0x73, 0x65, 0x65, 0x64)

class PrivateKey private constructor(
    private val rawPrivateKey: ByteArray,
    private val chainCode: ByteArray,
    private val depth: Byte,
    private val fingerprint: ByteArray,
    private val index: Int,
    internal val network: Network
) : Key {

    companion object {
        fun createWithSeed(seed: ByteArray, network: Network): PrivateKey {
            val output = HMAC.authenticate(HMAC_KEY_DATA, HMAC.Algorithm.HmacSHA512, seed)
            if (output.count() != 64) {
                throw InvalidDataException()
            }
            return PrivateKey(
                output.copyOfRange(0, 32),
                output.copyOfRange(32, 64),
                0,
                byteArrayOf(0x00, 0x00, 0x00, 0x00),
                0,
                network
            )
        }

        fun createWithPrivateKey(privateKey: ByteArray, network: Network) =
            PrivateKey(
                privateKey,
                ByteArray(0),
                0,
                byteArrayOf(0x00, 0x00, 0x00, 0x00),
                0,
                network
            )
    }

    fun derived(nodes: Array<DerivationNode>): PrivateKey? {
        if (nodes.isEmpty()) {
            return this
        }

        val node = nodes.first()

        val derivingIndex: Int
        val derivedPrivateKeyData: ByteArray
        val derivedChainCode: ByteArray

        val publicKeyData = publicKey(true)?.data() ?: return null

        var data = ByteArray(0)
        if (node is DerivationNode.HARDENED) {
            data += byteArrayOf(0x00)
            data += rawPrivateKey
        } else {
            data += publicKeyData
        }

        derivingIndex = node.index
        data += derivingIndex.toByteArray()

        val digest: ByteArray
        try {
            digest = HMAC.authenticate(chainCode, HMAC.Algorithm.HmacSHA512, data)
        } catch (e: Exception) {
            return null
        }

        val factor = BigInteger(1, digest.copyOfRange(0, 32))
        val curveOrder = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141".hexToBigInteger()

        val rawKey = BigInteger(1, rawPrivateKey)
        val calculatedKey = ((factor + rawKey) % curveOrder)

        derivedPrivateKeyData = calculatedKey.toByteArrayWithoutLeadingZeroByte().padLeft(32)

        derivedChainCode = digest.copyOfRange(32, 64)

        val fingerprint = publicKeyData.ripemd160().prefix(4)
        val derivedPrivateKey = PrivateKey(
            derivedPrivateKeyData, derivedChainCode,
            (depth + 1).toByte(), fingerprint, derivingIndex, network
        )

        if (nodes.count() > 1) {
            return derivedPrivateKey.derived(nodes.copyOfRange(1, nodes.count()))
        }

        return derivedPrivateKey
    }

    private fun derive(node: DerivationNode): PrivateKey? = null

    fun publicKey(compressed: Boolean? = null): PublicKey? {
        try {
            return PublicKey(rawPrivateKey, compressed ?: network.publicKeyCompressed(), chainCode, depth, fingerprint, index, network)
        } catch (e: Exception) {
            return null
        }
    }

    override fun string(): String? {
        val wifPrefix = network.wifPrefix()
        val alphabet = network.alphabet()
        if (wifPrefix == null || alphabet == null) {
            return rawPrivateKey.toHexString()
        }
        var data = ByteArray(0)
        data += wifPrefix.toByteArray()
        data += rawPrivateKey
        data += byteArrayOf(0x01)
        data += data.sha256().sha256().prefix(4)
        return data.encodeBase58String(alphabet)
    }

    override fun extended(): String? {
        val alphabet = network.alphabet() ?: return null
        var extendedKey = ByteArray(0)
        extendedKey += network.privateKeyPrefix().toByteArray(ByteOrder.LITTLE_ENDIAN)
        extendedKey += depth.toByteArray(ByteOrder.BIG_ENDIAN)
        extendedKey += fingerprint
        extendedKey += index.toByteArray(ByteOrder.BIG_ENDIAN)
        extendedKey += chainCode
        extendedKey += byteArrayOf(0x00)
        extendedKey += rawPrivateKey
        val checksum = extendedKey.sha256().sha256().prefix(4)
        extendedKey += checksum
        return extendedKey.encodeBase58String(alphabet)
    }

    override fun data() = rawPrivateKey

    override fun address() = publicKey()?.address()
}