package com.myetherwallet.mewwalletkit.eip.eip712.data

import pm.gnosis.model.SolidityBase

/**
 * Created by BArtWell on 05.08.2021.
 */

class Literal712(override val typeName: String, val value: SolidityBase.Type?) : Type712(emptyList()) {

    override fun toString(): String {
        return "{\"typeName\":\"$typeName\",\"value\":\"$value\"}"
    }
}
