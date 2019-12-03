package com.myetherwallet.mewwalletkit.core.data

import androidx.annotation.IntRange

/**
 * Created by BArtWell on 14.05.2019.
 */

enum class Bit {

    ZERO, ONE;

    companion object {

        fun get(value: Boolean) = if (value) ONE else ZERO

        fun get(@IntRange(from = 0, to = 1) value: Int) = values()[value]
    }

    override fun toString(): String {
        return if (this == ONE) "1" else "0"
    }
}