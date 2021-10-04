package com.myetherwallet.mewwalletkit.eip.eip681

import com.myetherwallet.mewwalletkit.bip.bip44.Address
import java.math.BigInteger

/**
 * Created by BArtWell on 15.09.2021.
 */

class Eip681Code private constructor(override var targetAddress: Address): EipQrCode {

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

        fun create(targetAddress: Address)  = Eip681Code(targetAddress)

        fun create(data: ByteArray)  = Eip681CodeParser.parse(data)

        fun create(data: String)  = Eip681CodeParser.parse(data)
    }
}

