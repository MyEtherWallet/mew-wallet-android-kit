package com.myetherwallet.mewwalletkit.eip.eip681

/**
 * Created by BArtWell on 16.09.2021.
 */

enum class Eip681Group(val id: String) {
    TYPE("type"),
    TARGET("target"),
    CHAIN_ID("chainID"),
    FUNCTION_NAME("functionName"),
    PARAMETERS("parameters");

    override fun toString(): String {
        return id
    }
}
