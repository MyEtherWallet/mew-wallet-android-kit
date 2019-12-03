package com.myetherwallet.mewwalletkit.core.util

import com.myetherwallet.mewwalletkit.core.extension.hexToByteArray
import org.junit.Assert
import org.junit.Test

/**
 * Created by BArtWell on 24.06.2019.
 */

class HMACTest {

    @Test
    fun authenticate() {
        val expected = "e8f32e723decf4051aefac8e2c93c9c5b214313817cdb01a1494b917c8436b35873dff81c02f525623fd1fe5167eac3a55a049de3d314bb42ee227ffed37d508".hexToByteArray()
        val actual = HMAC.authenticate(
            byteArrayOf(0x42, 0x69, 0x74, 0x63, 0x6F, 0x69, 0x6E, 0x20, 0x73, 0x65, 0x65, 0x64),
            HMAC.Algorithm.HmacSHA512,
            "000102030405060708090a0b0c0d0e0f".hexToByteArray()
        )
        Assert.assertArrayEquals(expected, actual)
    }
}
