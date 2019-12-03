package com.myetherwallet.mewwalletkit.bip.bip44

import com.myetherwallet.mewwalletkit.bip.bip39.BIP39
import com.myetherwallet.mewwalletkit.bip.bip39.wordlist.BaseWordlist
import com.myetherwallet.mewwalletkit.bip.bip39.wordlist.EnglishWordlist
import com.myetherwallet.mewwalletkit.bip.bip44.exception.EmptyPrivateKeyException
import com.myetherwallet.mewwalletkit.bip.bip44.exception.EmptySeedException

/**
 * Created by BArtWell on 23.05.2019.
 */

class Wallet {

    val privateKey: PrivateKey

    constructor(seed: ByteArray, network: Network = Network.ETHEREUM) {
        privateKey = PrivateKey.createWithSeed(seed, network)
    }

    constructor(privateKey: PrivateKey) {
        this.privateKey = privateKey
    }

    fun derive(path: String, index: Int? = null): Wallet {
        var derivationPath = path.derivationPath()
        if (index != null) {
            derivationPath += DerivationNode.NON_HARDENED(index)
        }
        val derivedPrivateKey = this.privateKey.derived(derivationPath) ?: throw EmptyPrivateKeyException()
        return Wallet(derivedPrivateKey)
    }

    companion object {

        fun generate(bitsOfEntropy: Int = 256, language: BaseWordlist = EnglishWordlist(), network: Network = Network.ETHEREUM): Pair<BIP39, Wallet> {
            val bip39 = BIP39(bitsOfEntropy, language)
            val seed = bip39.seed() ?: throw EmptySeedException()
            val wallet = Wallet(seed, network)
            return Pair(bip39, wallet)
        }

        fun restore(mnemonic: List<String>, language: BaseWordlist = EnglishWordlist(), network: Network = Network.ETHEREUM): Pair<BIP39, Wallet> {
            val bip39 = BIP39(mnemonic, language)
            val seed = bip39.seed() ?: throw EmptySeedException()
            val wallet = Wallet(seed, network)
            return Pair(bip39, wallet)
        }
    }
}