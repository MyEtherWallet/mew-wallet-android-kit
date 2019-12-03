package com.myetherwallet.mewwalletkit.core.util

import org.spongycastle.crypto.Digest
import org.spongycastle.crypto.digests.SHA256Digest
import org.spongycastle.crypto.digests.SHA512Digest
import org.spongycastle.crypto.generators.PKCS5S2ParametersGenerator
import org.spongycastle.crypto.params.KeyParameter

/**
 * Created by BArtWell on 16.05.2019.
 */

object PKCS5 {

    fun pbkdf2(passphrase: String, salt: ByteArray, iterations: Int, keyLength: Int, algorithm: Algorithm): ByteArray {
        val digest: Digest = when (algorithm) {
            Algorithm.PBKDF2WithHmacSHA256 -> {
                SHA256Digest()
            }
            Algorithm.PBKDF2WithHmacSHA512 -> {
                SHA512Digest()
            }
        }

        val gen = PKCS5S2ParametersGenerator(digest)
        gen.init(passphrase.toByteArray(), salt, iterations)
        return (gen.generateDerivedParameters(keyLength * 8) as KeyParameter).key
    }

    enum class Algorithm {
        PBKDF2WithHmacSHA256,
        PBKDF2WithHmacSHA512
    }
}