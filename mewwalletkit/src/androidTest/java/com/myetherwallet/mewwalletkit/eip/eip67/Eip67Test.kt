package com.myetherwallet.mewwalletkit.eip.eip67

import com.myetherwallet.mewwalletkit.bip.bip44.Address
import com.myetherwallet.mewwalletkit.core.extension.addHexPrefix
import com.myetherwallet.mewwalletkit.core.extension.toHexString
import com.myetherwallet.mewwalletkit.eip.eip681.AbiFunction
import com.myetherwallet.mewwalletkit.eip.eip681.EipQrCodeParameter
import org.junit.Assert

import org.junit.Test
import pm.gnosis.model.Solidity
import java.math.BigInteger

/**
 * Created by BArtWell on 27.09.2021.
 */

class Eip67Test {

    private val testVectors = arrayOf(
        "ethereum:0xcccc00000000000000000000000000000000cccc?gas=100000&data=0xa9059cbb00000000000000000000000000000000000000000000000000000000deadbeef0000000000000000000000000000000000000000000000000000000000000005",
        "ethereum:0xcccc00000000000000000000000000000000cccc?gas=100000&function=transfer(address 0xeeee00000000000000000000000000000000eeee, uint 5)",
        "ethereum:0xeeee00000000000000000000000000000000eeee",
        "ethereum:0xcccc00000000000000000000000000000000cccc=100000&function=transfer(address 0xeeee00000000000000000000000000000000eeee, uint 5)",
        "ethereum:0xdeadbeef"
    )

    @Test
    fun shouldParseLink1() {
        val code = Eip67Code.create(testVectors[0])
        Assert.assertNotNull(code)
        Assert.assertNotNull(code!!.targetAddress)
        Assert.assertEquals(Address.createRaw("0xcccc00000000000000000000000000000000cccc"), code.targetAddress)
        Assert.assertNotNull(code.recipientAddress)
        Assert.assertEquals(Address.createRaw("0x00000000000000000000000000000000deadbeef"), code.recipientAddress)
        Assert.assertNull(code.value)
        Assert.assertEquals(BigInteger("5"), code.tokenValue)
        Assert.assertEquals(BigInteger("100000"), code.gasLimit)
        Assert.assertNotNull(code.data)
        Assert.assertEquals(
            "0xa9059cbb00000000000000000000000000000000000000000000000000000000deadbeef0000000000000000000000000000000000000000000000000000000000000005",
            code.data!!.toHexString().addHexPrefix()
        )
        Assert.assertEquals("transfer", code.functionName)
        Assert.assertEquals(
            code.function, AbiFunction(
                "transfer", listOf(
                    Pair("0", Solidity.Address::class.java.canonicalName!!),
                    Pair("1", Solidity.UInt256::class.java.canonicalName!!)
                ),
                emptyArray(),
                false,
                false
            )
        )
        Assert.assertArrayEquals(
            code.parameters.toTypedArray(), listOf(
                EipQrCodeParameter(Solidity.Address::class.java.canonicalName!!, Address.createRaw("0x00000000000000000000000000000000deadbeef")),
                EipQrCodeParameter(Solidity.UInt256::class.java.canonicalName!!, BigInteger("5"))
            ).toTypedArray()
        )
    }

    @Test
    fun shouldParseLink2() {
        val code = Eip67Code.create(testVectors[1])
        Assert.assertNotNull(code)
        Assert.assertNotNull(code!!.targetAddress)
        Assert.assertEquals(Address.createRaw("0xcccc00000000000000000000000000000000cccc"), code.targetAddress)
        Assert.assertNotNull(code.recipientAddress)
        Assert.assertEquals(Address.createRaw("0xeeee00000000000000000000000000000000eeee"), code.recipientAddress)
        Assert.assertNull(code.value)
        Assert.assertEquals(BigInteger("5"), code.tokenValue)
        Assert.assertEquals(BigInteger("100000"), code.gasLimit)
        Assert.assertNull(code.data)
        Assert.assertEquals("transfer", code.functionName)
        Assert.assertEquals(
            code.function, AbiFunction(
                "transfer", listOf(
                    Pair("0", Solidity.Address::class.java.canonicalName!!),
                    Pair("1", Solidity.UInt256::class.java.canonicalName!!)
                ),
                emptyArray(),
                false,
                false
            )
        )
        Assert.assertArrayEquals(
            code.parameters.toTypedArray(), listOf(
                EipQrCodeParameter(Solidity.Address::class.java.canonicalName!!, Address.createRaw("0xeeee00000000000000000000000000000000eeee")),
                EipQrCodeParameter(Solidity.UInt256::class.java.canonicalName!!, BigInteger("5"))
            ).toTypedArray()
        )
    }

    @Test
    fun shouldParseLink3() {
        val code = Eip67Code.create(testVectors[2])
        Assert.assertNotNull(code)
        Assert.assertNotNull(code!!.targetAddress)
        Assert.assertEquals(Address.createRaw("0xeeee00000000000000000000000000000000eeee"), code.targetAddress)
        Assert.assertNull(code.recipientAddress)
        Assert.assertNull(code.value)
        Assert.assertNull(code.tokenValue)
        Assert.assertNull(code.gasLimit)
        Assert.assertNull(code.functionName)
        Assert.assertNull(code.function)
        Assert.assertTrue(code.parameters.isEmpty())
    }

    @Test
    fun shouldParseLink4() {
        val code = Eip67Code.create(testVectors[3])
        Assert.assertNotNull(code)
    }

    @Test
    fun shouldParseLink5() {
        val code = Eip67Code.create(testVectors[4])
        Assert.assertNotNull(code)
    }
}
