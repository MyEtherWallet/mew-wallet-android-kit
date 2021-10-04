package com.myetherwallet.mewwalletkit.eip.eip681

import android.net.Uri
import com.myetherwallet.mewwalletkit.bip.bip44.Address
import com.myetherwallet.mewwalletkit.core.extension.*
import pm.gnosis.model.Solidity
import java.math.BigInteger
import java.net.URLDecoder

/**
 * Created by BArtWell on 16.09.2021.
 */

object Eip681CodeParser {

    fun parse(data: ByteArray): Eip681Code? {
        val string = String(data)
        return parse(string)
    }

    fun parse(string: String): Eip681Code? {
        val encoding = URLDecoder.decode(string, "UTF-8")
        val matcher = Regex("^ethereum:(?>(?<${Eip681Group.TYPE}>[^-]*)-)?(?<${Eip681Group.TARGET}>[0-9a-zA-Z.]+)?(?>@(?<${Eip681Group.CHAIN_ID}>[0-9]+))?/?(?>(?<${Eip681Group.FUNCTION_NAME}>[^?\n]+))?[?]?(?<${Eip681Group.PARAMETERS}>.+)?$")
        val matches = matcher.find(encoding)

        val match = matches!!.groups as MatchNamedGroupCollection

        val target = match[Eip681Group.TARGET] ?: return null
        val targetAddress = Address.createRaw(target)

        val code = Eip681Code.create(targetAddress)

        val type = match[Eip681Group.TYPE]?.let {
            try {
                EipQrCodeType.valueOf(it.uppercase())
            } catch (e: IllegalArgumentException) {
                return null
            }
        } ?: EipQrCodeType.PAY
        code.type = type

        match[Eip681Group.CHAIN_ID]?.let {
            code.chainId = BigInteger(it)
        }

        code.functionName = match[Eip681Group.FUNCTION_NAME]

        val inputs = mutableListOf<Pair<String, String>>()

        var parameters = match[Eip681Group.PARAMETERS]
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
            val inputType = parseTypeString(comp)
            if (inputType != null) {
                val value = components.getQueryParameter(comp) ?: continue
                var nativeValue: Any? = null
                when {
                    inputType == Solidity.Address::class.java.canonicalName -> nativeValue = Address.createRaw(value)
                    inputType.contains("Int") || inputType.contains("UInt") -> {
                        nativeValue = if (value.hasHexPrefix()) {
                            value.hexToBigInteger()
                        } else {
                            value.toBigIntegerExponential()
                        }
                    }
                    inputType == Solidity.String::class.java.canonicalName -> nativeValue = value
                    inputType == Solidity.Bytes::class.java.canonicalName -> {
                        nativeValue = if (value.hasHexPrefix()) {
                            value.hexToByteArray()
                        } else {
                            value.toByteArray()
                        }
                    }
                    inputType == Solidity.Bool::class.java.canonicalName -> {
                        nativeValue = when (value.lowercase()) {
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
            } else {
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
                    "gasPrice" -> {
                    }
                    else -> continue
                }
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

    private fun buildFunction(code: Eip681Code, inputs: List<Pair<String, String>>): AbiFunction? {
        if (code.functionName == null) {
            return null
        }
        return AbiFunction(code.functionName!!, inputs, emptyArray(), false, code.value != null)
    }

    private fun checkForTransfer(code: Eip681Code) {
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
