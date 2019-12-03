package com.myetherwallet.mewwalletkit.core.util

import com.myetherwallet.mewwalletkit.core.extension.hexToByteArray
import org.junit.Assert
import org.junit.Test

/**
 * Created by BArtWell on 20.05.2019.
 */

class PKCS5Test {

    @Test
    fun pbkdf2() {
        val passphrase = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about"
        val salt = "6d6e656d6f6e69635452455a4f52".hexToByteArray()
        val expected = ("c55257c360c07c72029aebc1b53c05ed0362ada38ead3e3e9efa3708e5349553" +
                "1f09a6987599d18264c1e1c92f2cf141630c7a3c4ab7c81b2f001698e7463b04")
            .hexToByteArray()
        val result = PKCS5.pbkdf2(passphrase, salt, 2048, 64, PKCS5.Algorithm.PBKDF2WithHmacSHA512)
        Assert.assertArrayEquals(expected, result)
    }
}