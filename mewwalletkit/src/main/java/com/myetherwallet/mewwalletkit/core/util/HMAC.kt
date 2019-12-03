package com.myetherwallet.mewwalletkit.core.util

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * Created by BArtWell on 07.06.2019.
 */
object HMAC {

    fun authenticate(key: ByteArray, algorithm: Algorithm, message: ByteArray): ByteArray {
        val mac = Mac.getInstance(algorithm.name)
        mac.init(SecretKeySpec(key, algorithm.name))
        return mac.doFinal(message)
    }

    enum class Algorithm {
        HmacSHA1, HmacSHA256, HmacSHA512
    }
}