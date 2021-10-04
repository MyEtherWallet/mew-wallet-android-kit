package com.myetherwallet.mewwalletkit.eip.eip681

import com.myetherwallet.mewwalletkit.bip.bip44.Address
import java.math.BigInteger

/**
 * Created by BArtWell on 15.09.2021.
 */

data class EipQrCodeParameter(
    val type: String,
    val value: Any
) {

    override fun equals(other: Any?): Boolean {
        var result = false
        if (other is EipQrCodeParameter) {
            result = when {
                this.value is Address && other.value is Address -> this.value == other.value && this.type == other.type
                this.value is BigInteger && other.value is BigInteger -> this.value == other.value && this.type == other.type
                this.value is String && other.value is String -> this.value == other.value && this.type == other.type
                this.value is ByteArray && other.value is ByteArray -> this.value == other.value && this.type == other.type
                this.value is Boolean && other.value is Boolean -> this.value == other.value && this.type == other.type
                else -> return false
            }
        }
        return result
    }
}
