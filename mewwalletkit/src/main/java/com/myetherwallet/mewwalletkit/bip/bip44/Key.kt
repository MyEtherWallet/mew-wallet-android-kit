package com.myetherwallet.mewwalletkit.bip.bip44

/**
 * Created by BArtWell on 23.05.2019.
 */

interface Key {

    fun string(): String?

    fun extended(): String?

    fun data(): ByteArray

    fun address(): Address?
}