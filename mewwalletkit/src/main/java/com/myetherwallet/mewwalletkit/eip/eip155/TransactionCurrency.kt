package com.myetherwallet.mewwalletkit.eip.eip155

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by BArtWell on 31.08.2020.
 */

@Parcelize
class TransactionCurrency(
    val symbol: String?,
    val decimals: Int?,
    val address: String?
) : Parcelable
