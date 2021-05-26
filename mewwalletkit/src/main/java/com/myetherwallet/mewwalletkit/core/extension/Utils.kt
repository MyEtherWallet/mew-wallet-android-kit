package com.myetherwallet.mewwalletkit.core.extension

/**
 * Created by BArtWell on 18.03.2021.
 */

inline fun <reified T : Enum<T>> enumValueOfOrNull(name: String): T? {
    return try {
        enumValueOf<T>(name)
    } catch (e: IllegalArgumentException) {
        null
    }
}
