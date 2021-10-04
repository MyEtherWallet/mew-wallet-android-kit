package com.myetherwallet.mewwalletkit.eip.eip67

import com.myetherwallet.mewwalletkit.bip.bip44.Address
import com.myetherwallet.mewwalletkit.eip.eip681.*
import java.math.BigInteger

/**
 * Created by BArtWell on 24.09.2021.
 */

class Eip67Code private constructor(override var targetAddress: Address): EipQrCode {

    override var recipientAddress: Address? = null
    override var chainId: BigInteger? = null
    var type: EipQrCodeType = EipQrCodeType.PAY
    override var functionName: String? = null
    override var gasLimit: BigInteger? = null
    override var value: BigInteger? = null
    override var tokenValue: BigInteger? = null
    override var function: AbiFunction? = null
    override var parameters: MutableList<EipQrCodeParameter> = mutableListOf()
    override var data: ByteArray? = null

    companion object {

        fun create(targetAddress: Address)  = Eip67Code(targetAddress)

        fun create(data: ByteArray)  = Eip67CodeParser.parse(data)

        fun create(data: String)  = Eip67CodeParser.parse(data)
    }
}
