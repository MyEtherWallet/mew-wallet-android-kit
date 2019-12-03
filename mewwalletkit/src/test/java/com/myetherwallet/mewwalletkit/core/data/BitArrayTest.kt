package com.myetherwallet.mewwalletkit.core.data

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Created by BArtWell on 17.05.2019.
 */
class BitArrayTest {

    //0b01010101_11111111_10101010
    private val bitsLong = listOf(
        Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE,
        Bit.ONE, Bit.ONE, Bit.ONE, Bit.ONE, Bit.ONE, Bit.ONE, Bit.ONE, Bit.ONE,
        Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO
    )
    //0b00001101
    private val bitsShort = listOf(Bit.ONE, Bit.ONE, Bit.ZERO, Bit.ONE)

    @Test
    fun fromBits() {
        assertEquals(24, BitArray(bitsLong).size)
        assertEquals(4, BitArray(bitsShort).size)
    }

    @Test
    fun fromByte() {
        assertEquals(8, BitArray(0b00001101.toByte()).size)
    }

    @Test
    fun slice() {
        val bitArray = BitArray(bitsLong).slice(8 until 16)
        assertEquals(8, bitArray.size)
        for (i in 0 until 8) {
            assertEquals(bitsLong[i + 8], bitArray.get(i))
        }
    }

    @Test
    fun toBytes() {
        val bytesLong = BitArray(bitsLong).toBytes()
        assertEquals(0b01010101.toByte(), bytesLong[0])
        assertEquals(0b11111111.toByte(), bytesLong[1])
        assertEquals(0b10101010.toByte(), bytesLong[2])
        val bytesShort = BitArray(bitsShort).toBytes()
        assertEquals(0b00001101.toByte(), bytesShort[0])
    }

    @Test
    fun toByte() {
        val byte1 = 0b01010101.toByte()
        assertEquals(byte1, BitArray(byte1).toByte())
        val byte2 = 0b00001101.toByte()
        assertEquals(byte2, BitArray(byte2).toByte())
    }

    @Test
    fun iterator() {
        val first = BitArray(bitsShort).iterator().next()
        assertEquals(bitsShort[0], first)
    }
}