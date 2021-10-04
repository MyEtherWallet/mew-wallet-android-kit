package com.myetherwallet.mewwalletkit.eip.eip712.data

import com.myetherwallet.mewwalletkit.core.extension.keccak256
import pm.gnosis.model.Solidity
import pm.gnosis.model.SolidityBase
import pm.gnosis.utils.hexToByteArray

/**
 * Created by BArtWell on 05.08.2021.
 */

sealed class Type712(protected val parameters: List<Parameter712>) {

    abstract val typeName: String

    protected fun encodeParameters(): ByteArray {
        return if (parameters.isEmpty()) {
            byteArrayOf()
        } else {
            parameters
                // Checkout for wrong structure
                // If parameter contains null value, remove it
                .filter { parameter -> parameter.value.parameters.find { it.value is Literal712 && it.value.value == null } == null }
                .map { (_, type) ->
                    when (type) {
                        is Struct712 -> type.hashStruct()
                        is Array712 -> type.hashStruct()
                        is Literal712 -> encodeSolidityType(type.value!!)
                        // Never be called
                        else -> throw IllegalStateException("Unknown type $type")
                    }
                }.reduce { acc, bytes -> acc + bytes }
        }
    }

    private fun encodeSolidityType(value: SolidityBase.Type): ByteArray = when (value) {
        // Solidity.String is also Solidity.Bytes
        is Solidity.Bytes -> (value.items).keccak256()
        is SolidityBase.Array<*> -> (value.items.map { encodeSolidityType(value = it) }.reduce { acc, bytes -> acc + bytes }).keccak256()
        is SolidityBase.Vector<*> -> (value.items.map { encodeSolidityType(value = it) }.reduce { acc, bytes -> acc + bytes }).keccak256()
        else -> value.encode().hexToByteArray()
    }
}
