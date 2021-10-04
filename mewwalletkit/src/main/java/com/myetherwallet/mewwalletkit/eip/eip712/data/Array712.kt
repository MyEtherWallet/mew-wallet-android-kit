package com.myetherwallet.mewwalletkit.eip.eip712.data

import com.myetherwallet.mewwalletkit.core.extension.keccak256

/**
 * Created by BArtWell on 05.08.2021.
 */

class Array712(override val typeName: String, parameters: List<Parameter712>) : Type712(parameters) {

    fun hashStruct() = encodeParameters().keccak256()

    override fun toString(): String {
        return "{" +
                "\"typeName\":$typeName," +
                "\"parameters\":[\n" +
                "   " + parameters.joinToString(",\n    ") + "" +
                "]" +
                "}"
    }
}
