package com.myetherwallet.mewwalletkit.bip.bip39

import com.myetherwallet.mewwalletkit.bip.bip39.exception.*
import com.myetherwallet.mewwalletkit.bip.bip39.wordlist.BaseWordlist
import com.myetherwallet.mewwalletkit.bip.bip39.wordlist.EnglishWordlist
import com.myetherwallet.mewwalletkit.core.data.Bit
import com.myetherwallet.mewwalletkit.core.data.BitArray
import com.myetherwallet.mewwalletkit.core.extension.sha256
import com.myetherwallet.mewwalletkit.core.extension.toBits
import com.myetherwallet.mewwalletkit.core.extension.toByteArray
import com.myetherwallet.mewwalletkit.core.util.PKCS5
import java.security.SecureRandom
import java.text.Normalizer

/**
 * Created by BArtWell on 10.05.2019.
 */

private const val BIP39Salt = "mnemonic"

class BIP39 {

    private var inputEntropy: ByteArray? = null
    val entropy: ByteArray? by lazy {
        inputEntropy?.let {
            return@lazy it
        }
        inputMnemonic?.let {
            try {
                toEntropy(it)
            } catch (e: Exception) {
                null
            }
        }
    }

    private var inputMnemonic: List<String>? = null
    val mnemonic: List<String>? by lazy {
        inputMnemonic?.let {
            return@lazy it
        }
        inputEntropy?.let {
            try {
                toMnemonic(it)
            } catch (e: Exception) {
                null
            }
        }
    }

    private var language: BaseWordlist

    constructor(bitsOfEntropy: Int = 256, language: BaseWordlist = EnglishWordlist()) {
        this.language = language
        inputEntropy = generateEntropy(bitsOfEntropy)
    }

    constructor(mnemonic: List<String>, language: BaseWordlist = EnglishWordlist()) {
        this.language = language
        inputMnemonic = mnemonic
    }

    constructor(entropy: ByteArray, language: BaseWordlist = EnglishWordlist()) {
        this.language = language
        inputEntropy = entropy
    }

    fun seed(password: String = ""): ByteArray? {
        mnemonic?.let {
            if (it.isNotEmpty()) {
                return seed(it, password)
            }
        }
        throw InvalidMnemonicException()
    }

    private fun seed(mnemonic: List<String>, password: String = ""): ByteArray? {
        if (entropy == null) {
            throw InvalidEntropyException()
        }
        val mnemonicData = Normalizer.normalize(mnemonic.joinToString(" "), Normalizer.Form.NFKD)
        if (mnemonicData.isEmpty()) {
            throw InvalidMnemonicException()
        }
        val salt = BIP39Salt + password
        val saltData = Normalizer.normalize(salt, Normalizer.Form.NFKD).toByteArray()
        if (saltData.isEmpty()) {
            throw InvalidSaltException()
        }
        return PKCS5.pbkdf2(mnemonicData, saltData, 2048, 64, PKCS5.Algorithm.PBKDF2WithHmacSHA512)
    }

    private fun generateEntropy(bitsOfEntropy: Int = 256): ByteArray {
        if (bitsOfEntropy < 128 || bitsOfEntropy > 256 || bitsOfEntropy % 32 != 0) {
            throw InvalidBitsOfEntropyException()
        }
        val random = SecureRandom()
        val entropy = ByteArray(bitsOfEntropy / 8)
        random.nextBytes(entropy)
        return entropy
    }

    fun toMnemonic(entropy: ByteArray): List<String> {
        if (entropy.size < 16 || entropy.size > 32 || entropy.size % 4 != 0) {
            throw InvalidEntropyException()
        }
        val checksum = entropy.sha256()
        val checksumBits = entropy.size * 8 / 32
        val fullEntropy = mutableListOf<Byte>()
        fullEntropy.addAll(entropy.toList())
        val checksumPart = checksum.slice(0 until (checksumBits + 7) / 8)
        fullEntropy.addAll(checksumPart)
        val wordList = mutableListOf<String>()
        for (i in 0 until fullEntropy.size * 8 / 11) {
            val position = i * 11
            val index = fullEntropy.toByteArray().toBits(position until position + 11)
            if (index == null || language.words.count() < index) {
                throw InvalidEntropyException()
            }
            val word = language.words[index]
            wordList.add(word)
        }
        return wordList
    }

    fun toEntropy(mnemonic: List<String>): ByteArray {
        if (mnemonic.count() < 12 && mnemonic.count() % 3 != 0) {
            throw InvalidMnemonicException()
        }

        val bitsList = mutableListOf<Bit>()
        for (word in mnemonic) {
            val index = language.words.indexOf(word)
            if (index == -1) {
                throw InvalidMnemonicException()
            }

            val positionBits = index.toShort().toByteArray().flatMap { BitArray(it) }

            if (positionBits.count() != 16) {
                throw InvalidMnemonicException()
            }

            bitsList.addAll(positionBits.slice(5..15))
        }
        if (bitsList.count() % 33 != 0) {
            throw InvalidMnemonicException()
        }

        val bits = BitArray(bitsList)
        val entropyBits = bits.slice(0 until (bits.count() - bits.count() / 33))
        val checksumBits = bits.slice((bits.count() - bits.count() / 33) until bits.count())

        val entropy = entropyBits.toBytes()

        val checksum = entropy.sha256().toBits(0, checksumBits.count()) ?: throw InvalidChecksumException()

        if (checksumBits.toByte() != checksum.toByte()) {
            throw InvalidChecksumException()
        }

        return entropy
    }
}