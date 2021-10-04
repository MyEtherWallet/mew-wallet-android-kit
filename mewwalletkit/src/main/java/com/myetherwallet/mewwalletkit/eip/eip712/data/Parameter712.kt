package com.myetherwallet.mewwalletkit.eip.eip712.data

/**
 * Created by BArtWell on 05.08.2021.
 */

data class Parameter712(val name: String?, val value: Type712) {

    constructor(value: Type712) : this(null, value)

    override fun toString(): String {
        return "{" +
                "   \"name\":\"$name\"," +
                "   \"value\":$value" +
                "}"
    }
}
