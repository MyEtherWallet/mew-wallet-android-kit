package com.myetherwallet.mewwalletkit.eip.eip712

import com.myetherwallet.mewwalletkit.core.extension.hexToBigInteger
import com.myetherwallet.mewwalletkit.core.extension.hexToByteArray
import com.myetherwallet.mewwalletkit.eip.eip712.data.Array712
import com.myetherwallet.mewwalletkit.eip.eip712.data.Literal712
import com.myetherwallet.mewwalletkit.eip.eip712.data.Struct712
import com.myetherwallet.mewwalletkit.eip.eip712.data.Parameter712
import pm.gnosis.model.Solidity
import java.io.InputStream
import java.math.BigDecimal
import java.math.BigInteger

private const val EIP712_DOMAIN_TYPE = "EIP712Domain"
private val SOLIDITY_TYPES = Solidity.types.keys

class Eip712JsonParser(private val jsonAdapter: Eip712JsonAdapter) {

    fun parseMessage(rawJson: String): DomainWithMessage = parseMessage(jsonAdapter.parse(rawJson))

    fun parseMessage(inputStream: InputStream): DomainWithMessage = parseMessage(jsonAdapter.parse(inputStream))

    private fun parseMessage(adapterResult: Eip712JsonAdapter.Result): DomainWithMessage {
        val domain = buildStruct712(EIP712_DOMAIN_TYPE, adapterResult.domain, adapterResult.types)
        val message = buildStruct712(adapterResult.primaryType, adapterResult.message, adapterResult.types)
        return DomainWithMessage(domain, message)
    }

    private fun buildStruct712(
        typeName: String,
        values: Map<String, Any>,
        typeSpec: Map<String, List<Eip712JsonAdapter.Parameter>>
    ): Struct712 {
        val params = typeSpec[typeName] ?: throw IllegalArgumentException("TypedDate does not contain type $typeName")
        val innerParams = params.map { typeParam ->
            val type712 = when {
                typeSpec.contains(typeParam.type) -> {
                    buildStruct712(
                        typeName = typeParam.type,
                        values = if (values.containsKey(typeParam.name)) {
                            values[typeParam.name] as Map<String, Any>
                        } else {
                            emptyMap()
                        },
                        typeSpec = typeSpec
                    )
                }
                typeParam.type.endsWith("]") -> {
                    buildArray(typeParam, values, typeSpec)
                }
                else -> {
                    buildLiteral(typeParam, values)
                }
            }
            Parameter712(name = typeParam.name, value = type712)
        }
        return Struct712(typeName = typeName, parameters = innerParams)
    }

    private fun buildLiteral(typeParam: Eip712JsonAdapter.Parameter, values: Map<String, Any>): Literal712 {
        // TODO this doesn't check size constraints ie.: having uint8 with a value greater than 8 bits
        val type = typeParam.type
        return if (values.containsKey(typeParam.name)) {
            val rawValue = values[typeParam.name]!!
            buildLiteral(typeParam.name, type, rawValue)
        } else {
            Literal712(typeName = type, value = null)
        }
    }

    private fun buildArray(typeParam: Eip712JsonAdapter.Parameter, values: Map<String, Any>, typeSpec: Map<String, List<Eip712JsonAdapter.Parameter>>): Array712 {
        val itemType = typeParam.type.replace("[]", "")
        return if (typeSpec.contains(itemType)) {
            Array712(
                typeParam.type,
                (values[typeParam.name] as List<Map<String, Any>>).map { value ->
                    Parameter712(buildStruct712(itemType, value, typeSpec))
                }
            )
        } else {
            Array712(
                typeParam.type,
                (values[typeParam.name] as List<Any>).withIndex().map { (index, value) ->
                    Parameter712(buildLiteral("typeParam.name[$index]", itemType, value))
                }
            )
        }
    }

    private fun buildLiteral(name: String, type: String, rawValue: Any): Literal712 {
        if (!SOLIDITY_TYPES.contains(type)) throw IllegalArgumentException("Property with name $name has invalid Solidity type $type")
        val bivrostType = when {
            type.startsWith(prefix = "uint") -> readNumber(rawNumber = rawValue, creator = { Solidity.UInt256(it) })
            type.startsWith(prefix = "int") -> readNumber(rawNumber = rawValue, creator = { Solidity.Int256(it) })
            type == "bytes" -> Solidity.Bytes(rawValue.toString().hexToByteArray())
            type == "string" -> Solidity.String(rawValue.toString())
            type.startsWith(prefix = "bytes") -> Solidity.Bytes32(rawValue.toString().hexToByteArray())
            type == "bool" -> readBool(rawBool = rawValue)
            type == "address" -> readNumber(rawNumber = rawValue, creator = { Solidity.Address(it) })
            else -> throw IllegalArgumentException("Unknown literal type $type")
        }
        return Literal712(typeName = type, value = bivrostType)
    }

    private fun <T> readNumber(rawNumber: Any, creator: (BigInteger) -> T): T =
        when (rawNumber) {
            is Number -> creator(BigDecimal(rawNumber.toString()).exactNumber())
            is String -> {
                if (rawNumber.startsWith(prefix = "0x")) creator(rawNumber.hexToBigInteger())
                else creator(BigDecimal(rawNumber).exactNumber())
            }
            else -> throw IllegalArgumentException("Value $rawNumber is neither a Number nor String")
        }

    private fun readBool(rawBool: Any): Solidity.Bool =
        if (rawBool is Boolean) Solidity.Bool(rawBool)
        else if (rawBool.toString().equals("true", ignoreCase = true) || rawBool.toString().equals("false", ignoreCase = true))
            Solidity.Bool(rawBool.toString().equals("true", ignoreCase = true))
        else throw java.lang.IllegalArgumentException("Value $rawBool is not a Boolean")

    private fun BigDecimal.exactNumber() = try {
        toBigIntegerExact()
    } catch (e: Exception) {
        throw IllegalArgumentException("Value ${toString()} is a decimal (not supported)")
    }

    data class DomainWithMessage(val domain: Struct712, val message: Struct712)
}
