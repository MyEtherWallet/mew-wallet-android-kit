package com.myetherwallet.mewwalletkit.eip.eip67

import android.net.Uri
import com.myetherwallet.mewwalletkit.bip.bip44.Address
import com.myetherwallet.mewwalletkit.core.extension.*
import com.myetherwallet.mewwalletkit.eip.eip681.AbiFunction
import com.myetherwallet.mewwalletkit.eip.eip681.EipQrCodeParameter
import pm.gnosis.model.Solidity
import java.math.BigInteger
import java.net.URLDecoder

/**
 * Created by BArtWell on 24.09.2021.
 */

object Eip67CodeParser {

    fun parse(data: ByteArray): Eip67Code? {
        val string = String(data)
        return parse(string)
    }

    fun parse(string: String): Eip67Code? {
        val encoding = URLDecoder.decode(string, "UTF-8")
        val matcher = Regex("^ethereum:(?<${Eip67Group.TARGET}>[0-9a-zA-Z.]+)?[?]?(?<${Eip67Group.PARAMETERS}>.+)?$")
        val matches = matcher.find(encoding)

        val match = matches!!.groups as MatchNamedGroupCollection

        val target = match[Eip67Group.TARGET] ?: return null
        val targetAddress = Address.createRaw(target)

        val code = Eip67Code.create(targetAddress)

        val inputs = mutableListOf<Pair<String, String>>()

        var parameters = match[Eip67Group.PARAMETERS]
        if (parameters == null) {
            code.function = buildFunction(code, inputs)
            return code
        }
        parameters = "?$parameters"

        val components = Uri.parse(parameters)
        if (components == null) {
            code.function = buildFunction(code, inputs)
            return code
        }

        val queryItems = components.queryParameterNames
        if (queryItems.isEmpty()) {
            code.function = buildFunction(code, inputs)
            return code
        }

        var inputNumber = 0

        for (comp in queryItems) {
            when (comp) {
                "value" -> {
                    components.getQueryParameter(comp)?.let {
                        code.value = it.toBigIntegerExponential()
                    } ?: return null
                }
                "gas", "gasLimit" -> {
                    components.getQueryParameter(comp)?.let {
                        code.gasLimit = it.toBigIntegerExponential()
                    } ?: return null
                }
                "data" -> {
                    components.getQueryParameter(comp)?.let {
                        if (!it.hasHexPrefix()) {
                            return null
                        }
                        val data = it.hexToByteArray()
                        code.data = data

                        val function = AbiFunction(
                            AbiFunction.Method.ERC20_TRANSFER.methodName,
                            listOf(
                                Pair("0", Solidity.Address::class.java.canonicalName!!),
                                Pair("1", Solidity.UInt256::class.java.canonicalName!!),
                            ),
                            arrayOf(Solidity.Bool::class.java.canonicalName!!),
                            false,
                            false
                        )
                        val decoded = function.decodeInputData(data)
                        decoded?.let { functionInputs ->
                            val to = functionInputs["0"]
                            val amount = functionInputs["1"]
                            if (to != null && to is Address) {
                                code.parameters.add(EipQrCodeParameter(Solidity.Address::class.java.canonicalName!!, to))
                                inputs.add(Pair("0", Solidity.Address::class.java.canonicalName!!))
                            }
                            if (amount != null && amount is BigInteger) {
                                code.parameters.add(EipQrCodeParameter(Solidity.UInt256::class.java.canonicalName!!, amount))
                                inputs.add(Pair("1", Solidity.UInt256::class.java.canonicalName!!))
                            }
                            code.functionName = AbiFunction.Method.ERC20_TRANSFER.methodName
                        }
                    }
                }
                "gasPrice" -> {
                }
                "function" -> {
                    val value = components.getQueryParameter(comp)
                    if (value != null) {
                        val preFirstIndex = value.indexOf("(")
                        val lastIndex = value.lastIndexOf(")")
                        if (preFirstIndex == -1 || lastIndex == -1) {
                            code.functionName = value
                            continue
                        }

                        val functionParameters = "?" + value.substring(preFirstIndex + 1, lastIndex)
                            .replace(", ", "&")
                            .replace(",", "&")
                            .replace(" ", "=")

                        code.functionName = value.substring(0, preFirstIndex)

                        val functionComponents = Uri.parse(functionParameters)
                        val functionQueryItems = functionComponents.queryParameterNames

                        for (functionComp in functionQueryItems) {
                            val inputType = parseTypeString(functionComp)
                            if (inputType != null) {
                                val functionValue = functionComponents.getQueryParameter(functionComp) ?: continue
                                var nativeValue: Any?

                                when {
                                    inputType == Solidity.Address::class.java.canonicalName -> nativeValue = Address.createRaw(functionValue)
                                    inputType.contains("Int") || inputType.contains("UInt") -> {
                                        nativeValue = if (functionValue.hasHexPrefix()) {
                                            functionValue.hexToBigInteger()
                                        } else {
                                            functionValue.toBigIntegerExponential()
                                        }
                                    }
                                    inputType == Solidity.String::class.java.canonicalName -> nativeValue = functionValue
                                    inputType == Solidity.Bytes::class.java.canonicalName -> {
                                        nativeValue = if (functionValue.hasHexPrefix()) {
                                            functionValue.hexToByteArray()
                                        } else {
                                            functionValue.toByteArray()
                                        }
                                    }
                                    inputType == Solidity.Bool::class.java.canonicalName -> {
                                        nativeValue = when (functionValue.lowercase()) {
                                            "true", "1" -> true
                                            "false", "0" -> false
                                            else -> true
                                        }
                                    }
                                    else -> continue
                                }
                                if (nativeValue == null) {
                                    return null
                                }

                                inputs.add(Pair(inputNumber.toString(), inputType))
                                code.parameters.add(EipQrCodeParameter(inputType, nativeValue))
                                inputNumber += 1
                            }
                        }
                    } else {
                        return null
                    }
                }
                else -> continue
            }
        }

        code.function = buildFunction(code, inputs)
        checkForTransfer(code)
        return code
    }

    private fun parseTypeString(input: String?): String? {
        input?.let {
            val inputType = Solidity.aliases.getOrElse(it) { it }
            Solidity.types[inputType]?.let {
                return it
            }
        }
        return null
    }

    private fun buildFunction(code: Eip67Code, inputs: List<Pair<String, String>>): AbiFunction? {
        if (code.functionName == null) {
            return null
        }
        return AbiFunction(code.functionName!!, inputs, emptyArray(), false, code.value != null)
    }

    private fun checkForTransfer(code: Eip67Code) {
        if (code.function?.functionName == AbiFunction.Method.ERC20_TRANSFER.methodName) {
            for (parameter in code.parameters) {
                when {
                    parameter.type == Solidity.Address::class.java.canonicalName && parameter.value is Address -> code.recipientAddress = parameter.value
                    parameter.type == Solidity.UInt256::class.java.canonicalName -> code.tokenValue = parameter.value as BigInteger
                    else -> break
                }
            }
        }
    }
}
