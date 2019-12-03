package com.myetherwallet.mewwalletkit.bip.bip44

import com.myetherwallet.mewwalletkit.core.extension.*
import org.bitcoin.NativeSecp256k1
import java.nio.ByteOrder

/**
 * Created by BArtWell on 10.06.2019.
 */

private const val PUBLIC_KEY_COMPRESSED_SIZE = 33
private const val PUBLIC_KEY_DECOMPRESSED_SIZE = 65

class PublicKey : Key {

    private val raw: ByteArray
    private val chainCode: ByteArray
    private val depth: Byte
    private val fingerprint: ByteArray
    private val index: Int
    private val network: Network

    constructor(
        privateKey: ByteArray,
        compressed: Boolean = false,
        chainCode: ByteArray,
        depth: Byte,
        fingerprint: ByteArray,
        index: Int,
        network: Network
    ) {
        val publicKey = NativeSecp256k1.computePublicKey(privateKey)!!
        raw = NativeSecp256k1.serializePublicKey(publicKey, compressed)!!
        this.chainCode = chainCode
        this.depth = depth
        this.fingerprint = fingerprint
        this.index = index
        this.network = network
    }

    constructor(publicKey: ByteArray, compressed: Boolean = false, network: Network) {
        this.raw = publicKey
        this.chainCode = ByteArray(0)
        this.depth = 0
        this.fingerprint = ByteArray(0)
        this.index = 0
        this.network = network
    }

    override fun string() = raw.toHexString()

    override fun extended(): String? {
        val alphabet = network.alphabet() ?: return null
        var extendedKey = ByteArray(0)
        extendedKey += network.publicKeyPrefix().toByteArray(ByteOrder.LITTLE_ENDIAN)
        extendedKey += depth.toByteArray(ByteOrder.BIG_ENDIAN)
        extendedKey += fingerprint
        extendedKey += index.toByteArray(ByteOrder.BIG_ENDIAN)
        extendedKey += chainCode
        extendedKey += raw
        val checksum = extendedKey.sha256().sha256().prefix(4)
        extendedKey += checksum
        return extendedKey.encodeBase58String(alphabet)
    }

    override fun data() = raw

    override fun address(): Address? {
        when (network) {
            Network.BITCOIN, Network.LITECOIN -> {
                if (raw.size != PUBLIC_KEY_COMPRESSED_SIZE) {
                    return null
                }
                val alphabet = network.alphabet() ?: return null
                val prefix = byteArrayOf(network.publicKeyHash())
                val publicKey = raw
                val payload = publicKey.sha256().ripemd160()
                val checksum = (prefix + payload).sha256().sha256().prefix(4)
                val data = prefix + payload + checksum
                val stringAddress = data.encodeBase58String(alphabet) ?: return null
                return Address.createRaw(stringAddress)
            }
            else -> {
                if (raw.size != PUBLIC_KEY_DECOMPRESSED_SIZE) {
                    return null
                }
                val publicKey = raw
                val formattedData = (network.addressPrefix().hexToByteArray() + publicKey).drop(1).toByteArray()
                val addressData = formattedData.keccak256().takeLast(20).toByteArray()
                val eip55 = addressData.eip55() ?: return null
                return Address.create(eip55, network.addressPrefix())
            }
        }
    }
}
