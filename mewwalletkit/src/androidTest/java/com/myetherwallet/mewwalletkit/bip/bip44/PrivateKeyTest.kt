package com.myetherwallet.mewwalletkit.bip.bip44

import org.junit.Assert
import org.junit.Test

/**
 * Created by BArtWell on 15.12.2020.
 */

class PrivateKeyTest {

    @Test
    fun stressPublicKey() {
        repeat(10) {
            val (biP39, wallet) = Wallet.generate()
            val privateKey = PrivateKey.createWithSeed(biP39.seed()!!, Network.ETHEREUM)
            Assert.assertNotNull(privateKey.publicKey()?.data())
        }
    }
}
