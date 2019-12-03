package com.myetherwallet.mewwalletkit.bip.bip44

import com.myetherwallet.mewwalletkit.core.extension.hexToByteArray
import org.junit.Assert
import org.junit.Test

/**
 * Created by BArtWell on 03.07.2019.
 */

class PublicKeyTest {

    @Test
    fun createFromPrivateKey() {
        val publicKey = PublicKey(
            "e8f32e723decf4051aefac8e2c93c9c5b214313817cdb01a1494b917c8436b35".hexToByteArray(),
            true,
            "873dff81c02f525623fd1fe5167eac3a55a049de3d314bb42ee227ffed37d508".hexToByteArray(),
            0,
            "00000000".hexToByteArray(),
            0,
            Network.BITCOIN
        )
        val expected = "0339a36013301597daef41fbe593a02cc513d0b55527ec2df1050e2e8ff49c85c2".hexToByteArray()
        Assert.assertArrayEquals(expected, publicKey.data())
    }

    @Test
    fun shouldReturnCorrectPublicAddress() {
        val privateKey = PrivateKey.createWithPrivateKey("0x58d23b55bc9cdce1f18c2500f40ff4ab7245df9a89505e9b1fa4851f623d241d".hexToByteArray(), Network.ETHEREUM)
        val publicKey = privateKey.publicKey(false)
        Assert.assertEquals(publicKey?.address()?.address, "0xdC544d1AA88Ff8bbd2F2AeC754B1F1e99e1812fd")
    }
}