package com.myetherwallet.mewwalletkit.core.extension

import java.nio.ByteBuffer

/**
 * Created by BArtWell on 14.05.2019.
 */

fun Short.toByteArray(): ByteArray = ByteBuffer.allocate(Short.SIZE_BYTES).putShort(this).array()
