package com.myetherwallet.mewwalletkit.eip.eip2930

import android.os.Parcelable
import com.myetherwallet.mewwalletkit.bip.bip44.Address
import com.myetherwallet.mewwalletkit.core.data.rlp.Rlp
import com.myetherwallet.mewwalletkit.core.data.rlp.RlpArray
import com.myetherwallet.mewwalletkit.core.data.rlp.RlpByteArray
import com.myetherwallet.mewwalletkit.core.data.rlp.RlpString
import kotlinx.parcelize.Parcelize

/**
 * Created by BArtWell on 02.07.2021.
 */

typealias StorageSlot = ByteArray

@Parcelize
data class AccessList(
    val address: Address?,
    val slots: Array<StorageSlot>?
) : Rlp, Parcelable {

    companion object {
        val EMPTY = AccessList(null, null)
    }

    override fun rlpEncode(offset: Byte?): ByteArray? {
        if (address == null || slots == null) {
            return null
        }
        val slotsRlp = slots.map { RlpByteArray(it) }.toTypedArray()
        val rlp = RlpArray(RlpString(address.address), RlpArray(*slotsRlp))
        return rlp.rlpEncode(offset)
    }
}