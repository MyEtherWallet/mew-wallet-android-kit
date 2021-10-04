package com.myetherwallet.mewwalletkit.eip.eip681

import com.myetherwallet.mewwalletkit.bip.bip44.Address
import java.math.BigInteger

/**
 * Created by BArtWell on 15.09.2021.
 */

interface EipQrCode {

    val chainId: BigInteger?
    val targetAddress: Address
    val recipientAddress: Address?
    val value: BigInteger?
    val tokenValue: BigInteger?
    val gasLimit: BigInteger?
    val data: ByteArray?
    val functionName: String?
    val function: AbiFunction?
    val parameters: MutableList<EipQrCodeParameter>
}
