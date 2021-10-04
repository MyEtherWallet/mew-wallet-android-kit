package com.myetherwallet.mewwalletkit.eip.eip67

/**
 * Created by BArtWell on 24.09.2021.
 */

enum class Eip67Group(val id: String) {
    TARGET("target"),
    PARAMETERS("parameters");

    override fun toString(): String {
        return id
    }
}
