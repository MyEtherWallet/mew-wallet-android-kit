package com.myetherwallet.mewwalletkit.core.util

/**
 * Created by BArtWell on 14.05.2019.
 */

class BitReader(input: ByteArray) {

    private val bytes = input.clone()
    private var position = 0

    fun get(range: IntRange): Int {
        reset()
        skip(range.first)
        return get(range.count())
    }

    fun get(count: Int): Int {
        var bitValue = 0
        for (i in 0 until count) {
            bitValue = bitValue shl 1
            bitValue = bitValue or getBit(position)
            position++
        }

        return bitValue
    }

    fun skip(count: Int) {
        position += count
    }

    fun reset() {
        position = 0
    }

    private fun getBit(position: Int): Int {
        val currentByte = 0xFF and bytes[position / 8].toInt()
        val bitIndex = position % 8
        return 0x01 and (currentByte shr 7 - bitIndex)
    }
}