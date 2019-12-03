package com.myetherwallet.mewwalletkit.core.extension

import org.junit.Assert
import org.junit.Test

/**
 * Created by BArtWell on 20.05.2019.
 */

class ShortKtTest {

    @Test
    fun toByteArray() {
        Assert.assertArrayEquals(byteArrayOf(7.toByte(), 247.toByte()), 2039.toShort().toByteArray())
    }
}