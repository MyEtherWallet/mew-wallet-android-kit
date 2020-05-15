package com.myetherwallet.mewwalletkit.bip.bip44

import org.junit.Assert
import org.junit.Test

/**
 * Created by BArtWell on 13.03.2020.
 */

class WalletTest {

    @Test
    fun generateRestoreDerive() {
        for (i in 0 until 5000) {
            val (biP39Generated, walletGenerated) = Wallet.generate()
            Assert.assertNotNull(biP39Generated.mnemonic)
            Assert.assertTrue(biP39Generated.mnemonic!!.isNotEmpty())
            val mnemonic = biP39Generated.mnemonic!!.joinToString(" ")
            val (biP39Restored, walletRestored) = Wallet.restore(biP39Generated.mnemonic!!.toList())
            Assert.assertNotNull("mnemonic=$mnemonic", biP39Restored)
            Assert.assertNotNull("mnemonic=$mnemonic", walletRestored)
            for (index in 0 until 10) {
                val derived = walletRestored.derive(Network.ANONYMIZED_ID.path, index)
                Assert.assertNotNull("index=$index, mnemonic=${mnemonic}", derived.privateKey.address())
            }
        }
    }
}
