package com.myetherwallet.mewwalletkit.core.data

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Created by BArtWell on 17.05.2019.
 */

class BitTest {

    @Test
    fun fromFalse() {
        assertEquals(Bit.ZERO, Bit.get(false))
    }

    @Test
    fun fromTrue() {
        assertEquals(Bit.ONE, Bit.get(true))
    }

    @Test
    fun fromZero() {
        assertEquals(Bit.ZERO, Bit.get(0))
    }

    @Test
    fun fromOne() {
        assertEquals(Bit.ONE, Bit.get(1))
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun fromTwo() {
        Bit.get(2)
    }

    @Test
    fun toStringZero() {
        assertEquals("0", Bit.ZERO.toString())
    }

    @Test
    fun toStringOne() {
        assertEquals("1", Bit.ONE.toString())
    }
}