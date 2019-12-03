package com.myetherwallet.mewwalletkit.core.extension

import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Created by BArtWell on 12.06.2019.
 */

fun Byte.toByteArray(byteOrder: ByteOrder = ByteOrder.nativeOrder()): ByteArray =
    ByteBuffer
        .allocate(Byte.SIZE_BYTES)
        .put(this)
        .order(byteOrder)
        .array()