package com.myetherwallet.mewwalletkit.eip.eip712

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.myetherwallet.mewwalletkit.core.extension.keccak256
import com.myetherwallet.mewwalletkit.eip.eip712.data.Struct712
import java.io.InputStream

/**
 * Created by BArtWell on 22.02.2021.
 */

private const val TAG = "Eip712Utils"

object Eip712Utils {

    fun getHash(json: String): ByteArray {
        val unescaped = unescapeJson(json).toString()
        val adapter = object : Eip712JsonAdapter {
            override fun parse(typedDataJson: String): Eip712JsonAdapter.Result {
                return Gson().fromJson(typedDataJson, Eip712JsonAdapter.Result::class.java)
            }

            override fun parse(inputStream: InputStream): Eip712JsonAdapter.Result {
                TODO("Not yet implemented")
            }
        }
        val eip712JsonParser = Eip712JsonParser(adapter)
        val domainWithMessage = eip712JsonParser.parseMessage(unescaped)
        return typedDataHash(domainWithMessage.message, domainWithMessage.domain)
    }

    private fun typedDataHash(message: Struct712, domain: Struct712): ByteArray {
        return (byteArrayOf(0x19, 0x1) + domain.hashStruct() + message.hashStruct()).keccak256()
    }

    private fun unescapeJson(escapedJsonString: String) = try {
        JsonParser.parseString(escapedJsonString).asJsonObject
    } catch (e: Exception) {
        val unescaped = JsonParser.parseString(escapedJsonString).asString
        JsonParser.parseString(unescaped).asJsonObject
    }
}
