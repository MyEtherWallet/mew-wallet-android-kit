package com.myetherwallet.mewwalletkit.core.extension

import org.junit.Assert
import org.junit.Test

/**
 * Created by BArtWell on 21.05.2019.
 */
class ByteArrayKtTest {

    // length == 3 bytes/24 bits
    val data = byteArrayOf(0b00001111.toByte(), 0b00110011.toByte(), 0b11001100.toByte())

    @Test
    fun toBits() {
        Assert.assertNull(data.toBits(250, 20))
        Assert.assertNull(data.toBits(-1, 10))
        Assert.assertNull(data.toBits(0, -1))

        Assert.assertEquals(0b0000, data.toBits(0, 4))
        Assert.assertEquals(0b0000, data.toBits(0 until 4))
        Assert.assertEquals(0b00001111, data.toBits(0, 8))
        Assert.assertEquals(0b00001111, data.toBits(0 until 8))
        Assert.assertEquals(0b00001111_0011, data.toBits(0, 12))
        Assert.assertEquals(0b00001111_0011, data.toBits(0 until 12))
        Assert.assertEquals(0b10011_110011, data.toBits(11, 11))
        Assert.assertEquals(0b10011_110011, data.toBits(11 until 22))
        Assert.assertEquals(0b00001111_00110011_11001100, data.toBits(0, 24))
        Assert.assertEquals(0b00001111_00110011_11001100, data.toBits(0 until 24))
        Assert.assertEquals(0b11001100, data.toBits(16, 8))
        Assert.assertEquals(0b11001100, data.toBits(16 until 24))
    }

    @Test
    fun sha256() {
        val data = "00000000000000000000000000000000".hexToByteArray()
        val expected = "374708fff7719dd5979ec875d56cd2286f6d3cf7ec317a3b25632aab28ec37bb".hexToByteArray()
        Assert.assertArrayEquals(expected, data.sha256())
    }

    @Test
    fun md5() {
        val data = "test".toByteArray()
        val expected = "098f6bcd4621d373cade4e832627b4f6".hexToByteArray()
        Assert.assertArrayEquals(expected, data.md5())
    }

    @Test
    fun `Base58 tests`() {
        val alphabet = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"
        val testVectors = arrayOf(
            Base58TestVector("", ""),
            Base58TestVector("61", "2g"),
            Base58TestVector("626262", "a3gV"),
            Base58TestVector("636363", "aPEr"),
            Base58TestVector("73696d706c792061206c6f6e6720737472696e67", "2cFupjhnEsSn59qHXstmK2ffpLv2"),
            Base58TestVector("00eb15231dfceb60925886b67d065299925915aeb172c06647", "1NS17iag9jJgTHD1VXjvLCEnZuQ3rJDE9L"),
            Base58TestVector("516b6fcd0f", "ABnLTmg"),
            Base58TestVector("bf4f89001e670274dd", "3SEo3LWLoPntC"),
            Base58TestVector("572e4794", "3EFU7m"),
            Base58TestVector("ecac89cad93923c02321", "EJDM8drfXA6uyA"),
            Base58TestVector("10c8511e", "Rt5zm"),
            Base58TestVector("00000000000000000000", "1111111111"),
            Base58TestVector(
                "000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f202122232425262728292a2b2c2d2e2f303132333435363738393a3b3c3d3e3f404142434445464748494a4b4c4d4e4f505152535455565758595a5b5c5d5e5f606162636465666768696a6b6c6d6e6f707172737475767778797a7b7c7d7e7f808182838485868788898a8b8c8d8e8f909192939495969798999a9b9c9d9e9fa0a1a2a3a4a5a6a7a8a9aaabacadaeafb0b1b2b3b4b5b6b7b8b9babbbcbdbebfc0c1c2c3c4c5c6c7c8c9cacbcccdcecfd0d1d2d3d4d5d6d7d8d9dadbdcdddedfe0e1e2e3e4e5e6e7e8e9eaebecedeeeff0f1f2f3f4f5f6f7f8f9fafbfcfdfeff",
                "1cWB5HCBdLjAuqGGReWE3R3CguuwSjw6RHn39s2yuDRTS5NsBgNiFpWgAnEx6VQi8csexkgYw3mdYrMHr8x9i7aEwP8kZ7vccXWqKDvGv3u1GxFKPuAkn8JCPPGDMf3vMMnbzm6Nh9zh1gcNsMvH3ZNLmP5fSG6DGbbi2tuwMWPthr4boWwCxf7ewSgNQeacyozhKDDQQ1qL5fQFUW52QKUZDZ5fw3KXNQJMcNTcaB723LchjeKun7MuGW5qyCBZYzA1KjofN1gYBV3NqyhQJ3Ns746GNuf9N2pQPmHz4xpnSrrfCvy6TVVz5d4PdrjeshsWQwpZsZGzvbdAdN8MKV5QsBDY"
            ),
            Base58TestVector("000111d38e5fc9071ffcd20b4a763cc9ae4f252bb4e48fd66a835e252ada93ff480d6dd43dc62a641155a5", "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz")
        )

        // Should encode correctly
        for (vector in testVectors) {
            val encoded = vector.decoded.encodeBase58String(alphabet)
            Assert.assertEquals(vector.encoded, encoded)
        }

        // Should decode correctly
        for (vector in testVectors) {
            val decoded = vector.encoded.decodeBase58(alphabet)
            Assert.assertArrayEquals("Decode error (" + vector.encoded + ")", vector.decoded, decoded)
        }
    }

    private data class Base58TestVector(
        private val decodedString: String,
        val encoded: String
    ) {
        val decoded: ByteArray by lazy { decodedString.hexToByteArray() }
    }
}