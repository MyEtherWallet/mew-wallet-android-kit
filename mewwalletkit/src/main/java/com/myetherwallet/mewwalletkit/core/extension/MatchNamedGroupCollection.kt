package com.myetherwallet.mewwalletkit.core.extension

import com.myetherwallet.mewwalletkit.eip.eip681.Eip681Group

/**
 * Created by BArtWell on 24.09.2021.
 */

// UnsupportedOperationException: Retrieving groups by name is not supported on this platform.
// private operator fun MatchNamedGroupCollection.get(group: Eip681Groups) = this[group.toString()]?.value
// So using .ordinal instead of direct access

inline operator fun <reified T : Enum<T>> MatchNamedGroupCollection.get(group: T) = this.get(group.ordinal + 1)?.value
