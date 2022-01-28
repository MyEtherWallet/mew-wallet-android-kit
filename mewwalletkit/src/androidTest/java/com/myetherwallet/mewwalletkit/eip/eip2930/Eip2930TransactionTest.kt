package com.myetherwallet.mewwalletkit.eip.eip2930

import com.myetherwallet.mewwalletkit.bip.bip44.Address
import com.myetherwallet.mewwalletkit.bip.bip44.Network
import com.myetherwallet.mewwalletkit.bip.bip44.PrivateKey
import com.myetherwallet.mewwalletkit.core.extension.hexToByteArray
import com.myetherwallet.mewwalletkit.core.extension.sign
import org.junit.Assert
import org.junit.Test

/**
 * Created by BArtWell on 14.07.2021.
 */

class Eip2930TransactionTest {

    private val testVectors = arrayOf(
        TestVector(
            0,
            "0x00",
            "0x01",
            "0x3b9aca00",
            "0x62d4",
            "0xdf0a88b2b68c673713a8ec826003676f272e3573",
            ByteArray(0),
            arrayOf(AccessList(Address("0x0000000000000000000000000000000000001337"), arrayOf("0x0000000000000000000000000000000000000000000000000000000000000000".hexToByteArray()))),
            null,
            null,
            "0x01f8a587796f6c6f76337880843b9aca008262d494df0a88b2b68c673713a8ec826003676f272e35730180f838f7940000000000000000000000000000000000001337e1a0000000000000000000000000000000000000000000000000000000000000000080a0294ac94077b35057971e6b4b06dfdf55a6fbed819133a6c1d31e187f1bca938da00be950468ba1c25a5cb50e9f6d8aa13c8cd21f24ba909402775b262ac76d374d",
            "0xbbd570a3c6acc9bb7da0d5c0322fe4ea2a300db80226f7df4fef39b2d6649eec",
            "0x796f6c6f763378",
            "0xfad9c8855b740a0b7ed4c221dbad0f33a83a49cad6b3fe8d5817ac83d38b6a19"
        ),
        TestVector(
            1,
            "0x",
            "0x",
            "0x",
            "0x",
            "0x0101010101010101010101010101010101010101",
            "0x010200".hexToByteArray(),
            arrayOf(AccessList(Address("0x0101010101010101010101010101010101010101"), arrayOf("0x0101010101010101010101010101010101010101010101010101010101010101".hexToByteArray()))),
            "0x01f858018080809401010101010101010101010101010101010101018083010200f838f7940101010101010101010101010101010101010101e1a00101010101010101010101010101010101010101010101010101010101010101",
            "0x78528e2724aa359c58c13e43a7c467eb721ce8d410c2a12ee62943a3aaefb60b",
            null,
            null,
            "0x01",
            "0x4646464646464646464646464646464646464646464646464646464646464646"
        )
    )

    @Test
    fun shouldSignTransactionAndReturnsTheExpectedSignature() {
        for (vector in testVectors) {
            val transaction = vector.transaction
            val privateKey = PrivateKey.createWithPrivateKey(vector.pkBytes, Network.ETHEREUM)

            if (vector.unsignedTransactionRlpBytes != null) {
                Assert.assertArrayEquals(vector.unsignedTransactionRlpBytes, transaction.serialize())
            }

            if (vector.unsignedHashBytes != null) {
                Assert.assertArrayEquals(vector.unsignedHashBytes, transaction.hash(transaction.chainId, true))
            }

            transaction.sign(privateKey)
            Assert.assertNotNull(transaction.signature)

            vector.signedTransactionRlpBytes?.let {
                val serialized = transaction.serialize()
                Assert.assertArrayEquals(it, serialized)
            }

            val signedHash = transaction.hash(transaction.chainId, false)
            vector.signedHashBytes?.let {
                Assert.assertArrayEquals(it, signedHash)
            }
        }
    }

    data class TestVector(
        val id: Int,
        val nonce: String,
        val value: String,
        val gasPrice: String,
        val gasLimit: String,
        val to: String,
        val data: ByteArray = ByteArray(0),
        val accessList: Array<AccessList>? = null,
        val unsignedTransactionRlp: String? = null,
        val unsignedHash: String? = null,
        val signedTransactionRlp: String? = null,
        val signedHash: String? = null,
        val chainId: String,
        val pk: String
    ) {

        val pkBytes = pk.hexToByteArray()
        val transaction: Eip2930Transaction
        val unsignedTransactionRlpBytes = unsignedTransactionRlp?.hexToByteArray()
        val unsignedHashBytes = unsignedHash?.hexToByteArray()
        val signedTransactionRlpBytes = signedTransactionRlp?.hexToByteArray()
        val signedHashBytes = signedHash?.hexToByteArray()

        init {
            val privateKey = PrivateKey.createWithPrivateKey(pkBytes, Network.ETHEREUM)
            transaction = Eip2930Transaction(
                nonce,
                gasPrice,
                gasLimit,
                Address(to),
                value,
                data,
                privateKey.address(),
                accessList ?: arrayOf(AccessList.EMPTY),
                chainId.hexToByteArray()
            )
        }
    }
}