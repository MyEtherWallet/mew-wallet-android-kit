package com.myetherwallet.mewwalletkit.eip.eip712.data

import com.myetherwallet.mewwalletkit.core.extension.keccak256
import pm.gnosis.model.Solidity

/**
 * Created by BArtWell on 05.08.2021.
 */

private val SOLIDITY_TYPES = Solidity.types.keys

class Struct712(override val typeName: String, parameters: List<Parameter712>) : Type712(parameters) {

    private val typeHash by lazy { (encodeType().joinToString(separator = "").toByteArray(charset = Charsets.UTF_8)).keccak256() }

    fun hashStruct() = (typeHash + encodeParameters()).keccak256()

    private fun encodeType(): List<String> {
        val encodedStruct = parameters.joinToString(separator = ",", prefix = "$typeName(", postfix = ")",
            transform = { (name, type) -> "${type.typeName} $name" })
        val structParams = parameters
            .asSequence()
            .filter { (_, type) -> !SOLIDITY_TYPES.contains(type.typeName) }
            .mapNotNull { (_, type) -> (type as? Struct712)?.encodeType() }
            .flatten()
            .distinct()
            .sorted()
            .toList()
        return listOf(encodedStruct) + structParams
    }

    override fun toString(): String {
        return "{" +
                "\"typeName\":$typeName," +
                "\"parameters\":[\n" +
                "   " + parameters.joinToString(",\n    ") + "" +
                "]" +
                "}"
    }
}
