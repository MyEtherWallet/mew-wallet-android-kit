package com.myetherwallet.mewwalletkit.eip.eip67

import com.myetherwallet.mewwalletkit.bip.bip44.Address
import com.myetherwallet.mewwalletkit.core.extension.addHexPrefix
import com.myetherwallet.mewwalletkit.core.extension.toHexString
import pm.gnosis.model.Solidity
import java.math.BigInteger

/**
 * Created by BArtWell on 24.09.2021.
 */

private const val FUNCTION_MEMORY_USAGE = 32

object AbiDecoder {

    fun decode(types: Array<Pair<String, String>>, data: ByteArray): Array<Any>? {
        val params = types.map { element -> element.second }.toTypedArray()
        return decode(params, data)
    }

    fun decode(types: Array<String>, data: ByteArray): Array<Any>? {
        val toReturn = mutableListOf<Any>()
        var consumed = 0
        for (type in types) {
            val (valueUnwrapped, consumedUnwrapped) = decodeSingleType(type, data, consumed)
            if (valueUnwrapped == null || consumedUnwrapped == null) {
                return null
            }
            toReturn.add(valueUnwrapped)
            consumed += consumedUnwrapped
        }
        if (toReturn.size != types.size) {
            return null
        }
        return toReturn.toTypedArray()
    }

    private fun decodeSingleType(type: String, data: ByteArray, pointer: Int = 0): Pair<Any?, Int?> {
        val (elementItself, nextElementPointer) = followTheData(data, pointer)
        if (elementItself == null || nextElementPointer == null) {
            return Pair(null, null)
        }
        when(type) {
            Solidity.UInt256::class.java.canonicalName ->
                if (elementItself.size >= 32) {
                    val mod = BigInteger.ONE.shl(256)
                            val dataSlice = elementItself.copyOfRange(0, 32)
                    val v = BigInteger (dataSlice) % mod
                    return Pair(v, 32)
                }
                Solidity.Address::class.java.canonicalName ->
                    if (elementItself.size >= 32) {
                        val dataSlice = elementItself.copyOfRange(12, 32)
                        val address = Address.createRaw(dataSlice.toHexString().addHexPrefix())
                        return Pair(address, 32)
                    }
        }
        return Pair(null, null)
    }

    private fun followTheData(data: ByteArray, pointer: Int = 0): Pair<ByteArray?, Int?> {
        if (data.size < pointer + FUNCTION_MEMORY_USAGE) {
            return Pair(null, null)
        }
        val elementItself = data.copyOfRange(pointer, pointer + FUNCTION_MEMORY_USAGE)
        val nextElement = pointer + FUNCTION_MEMORY_USAGE
        return Pair(elementItself, nextElement)
    }
}
