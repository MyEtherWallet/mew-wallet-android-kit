package com.myetherwallet.mewwalletkit.bip.bip44

import org.junit.Assert
import org.junit.Test

/**
 * Created by BArtWell on 13.03.2020.
 */

class WalletTest {

    @Test
    fun generateRestoreDerive() {
        for (i in 0 until 10) {
            val (biP39Generated, walletGenerated) = Wallet.generate()
            Assert.assertNotNull(biP39Generated.mnemonic)
            Assert.assertTrue(biP39Generated.mnemonic!!.isNotEmpty())
            val mnemonicList = biP39Generated.mnemonic!!
            val mnemonicString = mnemonicList.joinToString(" ")
            val (biP39Restored, walletRestored) = Wallet.restore(mnemonicList)
            Assert.assertNotNull("mnemonic=$mnemonicString", biP39Restored)
            Assert.assertNotNull("mnemonic=$mnemonicString", walletRestored)
            Assert.assertEquals("Generated and restored address are not equals", walletGenerated.privateKey.address()?.address, walletRestored.privateKey.address()?.address)
            for (index in 0 until 10) {
                val derived = walletRestored.derive(Network.ANONYMIZED_ID.path, index)
                Assert.assertNotNull("index=$index, mnemonic=${mnemonicString}", derived.privateKey.address())
            }
        }
    }
}
