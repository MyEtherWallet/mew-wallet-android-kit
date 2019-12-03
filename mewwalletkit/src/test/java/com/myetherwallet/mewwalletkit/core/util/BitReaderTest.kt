package com.myetherwallet.mewwalletkit.core.util

import org.junit.Assert
import org.junit.Test

/**
 * Created by BArtWell on 20.05.2019.
 */

class BitReaderTest {

    private val data = byteArrayOf(
        0b00001111.toByte(),
        0b00110011.toByte(),
        0b11001100.toByte()
    )

    @Test
    fun getCount() {
        Assert.assertEquals(0b0000, BitReader(data).get(4))
        Assert.assertEquals(0b00001111, BitReader(data).get(8))
        Assert.assertEquals(0b00001111_0011, BitReader(data).get(12))
    }

    @Test
    fun getRange() {
        Assert.assertEquals(0b10011_110011, BitReader(data).get(11 until 22))
        Assert.assertEquals(0b00001111_00110011_11001100, BitReader(data).get(0 until 24))
        Assert.assertEquals(0b11001100, BitReader(data).get(16 until 24))
    }

    @Test
    fun skip() {
        val bitReader = BitReader(data)
        bitReader.skip(8)
        Assert.assertEquals(0b00110011, bitReader.get(8))
        bitReader.skip(2)
        Assert.assertEquals(0b001100, bitReader.get(6))
    }

    @Test
    fun reset() {
        val bitReader = BitReader(data)
        bitReader.skip(16)
        bitReader.reset()
        Assert.assertEquals(0b00001111, bitReader.get(8))
    }
}