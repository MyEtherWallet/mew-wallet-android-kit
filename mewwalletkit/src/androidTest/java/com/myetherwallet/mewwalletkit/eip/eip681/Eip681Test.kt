package com.myetherwallet.mewwalletkit.eip.eip681

import com.myetherwallet.mewwalletkit.bip.bip44.Address
import org.junit.Assert
import org.junit.Test
import pm.gnosis.model.Solidity
import java.math.BigInteger

/**
 * Created by BArtWell on 21.09.2021.
 */

class Eip681Test {

    private val testVectors = arrayOf(
        "ethereum:pay-0xcccc00000000000000000000000000000000cccc@123/transfer?address=0xeeee00000000000000000000000000000000eeee&uint256=1.23e2&uint120=1e15",
        "ethereum:pay-0xcccc00000000000000000000000000000000cccc/transfer?address=0xeeee00000000000000000000000000000000eeee&uint256=1.23e2",
        "ethereum:pay-0xcccc00000000000000000000000000000000cccc/transfer?address=0xeeee00000000000000000000000000000000eeee",
        "ethereum:pay-0xcccc00000000000000000000000000000000cccc/transfer",
        "ethereum:pay-/transfer?address=0xeeee00000000000000000000000000000000eeee",
        "ethereum:custom-/atransfer?address=0xeeee00000000000000000000000000000000eeee",
        "ethereum:custom-0xcccc00000000000000000000000000000000cccc/atransfer?address=0xeeee00000000000000000000000000000000eeee",
        "ethereum:custom-0xcccc00000000000000000000000000000000cccc/atransfer",
        "ethereum:pay-0xcccc00000000000000000000000000000000cccc/atransfer",
        "ethereum:0xeeee00000000000000000000000000000000eeee",
        "ethereum:0xeeee00000000000000000000000000000000eeee?value=1.23e20",
        "ethereum:0xcccc00000000000000000000000000000000cccc@123/customfunction?key=value&key2=value2",
        "ethereum:@123/customfunction?key=value&key2=value2",
        "ethereum:/customfunction?key=value&key2=value2",
        "ethereum:?key=value&key2=value2"
    )

    @Test
    fun shouldParseLink1() {
        val code = Eip681Code.create(testVectors[0])
        Assert.assertNotNull(code)
        Assert.assertEquals(code!!.type, EipQrCodeType.PAY)
        Assert.assertNotNull(code.targetAddress)
        Assert.assertEquals(code.targetAddress, Address.createRaw("0xcccc00000000000000000000000000000000cccc"))
        Assert.assertEquals(code.chainId, BigInteger.valueOf(123))
        Assert.assertEquals(code.recipientAddress, Address.createRaw("0xeeee00000000000000000000000000000000eeee"))
        Assert.assertNull(code.value)
        Assert.assertNull(code.value)
        Assert.assertEquals(code.tokenValue, BigInteger.valueOf(123))
        Assert.assertNull(code.gasLimit)
        Assert.assertEquals(code.functionName, "transfer")
        Assert.assertEquals(
            code.function, AbiFunction(
                "transfer", listOf(
                    Pair("0", Solidity.Address::class.java.canonicalName!!),
                    Pair("1", Solidity.UInt256::class.java.canonicalName!!),
                    Pair("2", Solidity.UInt120::class.java.canonicalName!!)
                ),
                emptyArray(),
                false, false
            )
        )
        Assert.assertArrayEquals(
            code.parameters.toTypedArray(), listOf(
                EipQrCodeParameter(Solidity.Address::class.java.canonicalName!!, Address.createRaw("0xeeee00000000000000000000000000000000eeee")),
                EipQrCodeParameter(Solidity.UInt256::class.java.canonicalName!!, BigInteger("123")),
                EipQrCodeParameter(Solidity.UInt120::class.java.canonicalName!!, BigInteger("1000000000000000"))
            ).toTypedArray()
        )
    }

    @Test
    fun shouldParseLink2() {
        val code = Eip681Code.create(testVectors[1])
        Assert.assertNotNull(code)
        Assert.assertEquals(code!!.type, EipQrCodeType.PAY)
        Assert.assertNotNull(code.targetAddress)
        Assert.assertEquals(code.targetAddress, Address.createRaw("0xcccc00000000000000000000000000000000cccc"))
        Assert.assertNull(code.chainId)
        Assert.assertEquals(code.recipientAddress, Address.createRaw("0xeeee00000000000000000000000000000000eeee"))
        Assert.assertNull(code.value)
        Assert.assertEquals(code.tokenValue, BigInteger.valueOf(123))
        Assert.assertNull(code.gasLimit)
        Assert.assertEquals(code.functionName, "transfer")
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
                EipQrCodeParameter(Solidity.UInt256::class.java.canonicalName!!, BigInteger("123"))
            ).toTypedArray()
        )
    }

    @Test
    fun shouldParseLink3() {
        val code = Eip681Code.create(testVectors[2])
        Assert.assertNotNull(code)
        Assert.assertEquals(code!!.type, EipQrCodeType.PAY)
        Assert.assertNotNull(code.targetAddress)
        Assert.assertEquals(code.targetAddress, Address.createRaw("0xcccc00000000000000000000000000000000cccc"))
        Assert.assertNull(code.chainId)
        Assert.assertEquals(code.recipientAddress, Address.createRaw("0xeeee00000000000000000000000000000000eeee"))
        Assert.assertNull(code.value)
        Assert.assertNull(code.tokenValue)
        Assert.assertNull(code.gasLimit)
        Assert.assertEquals(code.functionName, "transfer")
        Assert.assertEquals(
            code.function, AbiFunction(
                "transfer", listOf(
                    Pair("0", Solidity.Address::class.java.canonicalName!!)
                ),
                emptyArray(),
                false,
                false
            )
        )
        Assert.assertArrayEquals(
            code.parameters.toTypedArray(), listOf(
                EipQrCodeParameter(Solidity.Address::class.java.canonicalName!!, Address.createRaw("0xeeee00000000000000000000000000000000eeee"))
            ).toTypedArray()
        )
    }

    @Test
    fun shouldParseLink4() {
        val code = Eip681Code.create(testVectors[3])
        Assert.assertNotNull(code)
        Assert.assertEquals(code!!.type, EipQrCodeType.PAY)
        Assert.assertNotNull(code.targetAddress)
        Assert.assertEquals(code.targetAddress, Address.createRaw("0xcccc00000000000000000000000000000000cccc"))
        Assert.assertNull(code.chainId)
        Assert.assertNull(code.recipientAddress)
        Assert.assertNull(code.value)
        Assert.assertNull(code.tokenValue)
        Assert.assertNull(code.gasLimit)
        Assert.assertEquals(code.functionName, "transfer")
        Assert.assertEquals(
            code.function, AbiFunction(
                "transfer", emptyList(),
                emptyArray(),
                false,
                false
            )
        )
        Assert.assertTrue(code.parameters.isEmpty())
    }

    @Test
    fun shouldParseLink5() {
        val code = Eip681Code.create(testVectors[4])
        Assert.assertNull(code)
    }

    @Test
    fun shouldParseLink6() {
        val code = Eip681Code.create(testVectors[5])
        Assert.assertNull(code)
    }

    @Test
    fun shouldParseLink7() {
        val code = Eip681Code.create(testVectors[6])
        Assert.assertNull(code)
    }

    @Test
    fun shouldParseLink8() {
        val code = Eip681Code.create(testVectors[7])
        Assert.assertNull(code)
    }

    @Test
    fun shouldParseLink9() {
        val code = Eip681Code.create(testVectors[8])
        Assert.assertNotNull(code)
        Assert.assertEquals(code!!.type, EipQrCodeType.PAY)
        Assert.assertNotNull(code.targetAddress)
        Assert.assertEquals(code.targetAddress, Address.createRaw("0xcccc00000000000000000000000000000000cccc"))
        Assert.assertNull(code.chainId)
        Assert.assertNull(code.recipientAddress)
        Assert.assertNull(code.value)
        Assert.assertNull(code.tokenValue)
        Assert.assertNull(code.gasLimit)
        Assert.assertEquals(code.functionName, "atransfer")
        Assert.assertNotNull(code.function)
        Assert.assertTrue(code.parameters.isEmpty())
    }

    @Test
    fun shouldParseLink10() {
        val code = Eip681Code.create(testVectors[9])
        Assert.assertNotNull(code)
        Assert.assertEquals(code!!.type, EipQrCodeType.PAY)
        Assert.assertNotNull(code.targetAddress)
        Assert.assertEquals(code.targetAddress, Address.createRaw("0xeeee00000000000000000000000000000000eeee"))
        Assert.assertNull(code.chainId)
        Assert.assertNull(code.recipientAddress)
        Assert.assertNull(code.value)
        Assert.assertNull(code.tokenValue)
        Assert.assertNull(code.gasLimit)
        Assert.assertNull(code.functionName)
        Assert.assertNull(code.function)
        Assert.assertTrue(code.parameters.isEmpty())
    }

    @Test
    fun shouldParseLink11() {
        val code = Eip681Code.create(testVectors[10])
        Assert.assertNotNull(code)
        Assert.assertEquals(code!!.type, EipQrCodeType.PAY)
        Assert.assertNotNull(code.targetAddress)
        Assert.assertEquals(code.targetAddress, Address.createRaw("0xeeee00000000000000000000000000000000eeee"))
        Assert.assertNull(code.chainId)
        Assert.assertNull(code.recipientAddress)
        Assert.assertEquals(code.value, BigInteger("123000000000000000000"))
        Assert.assertNull(code.tokenValue)
        Assert.assertNull(code.gasLimit)
        Assert.assertNull(code.functionName)
        Assert.assertNull(code.function)
        Assert.assertTrue(code.parameters.isEmpty())
    }

    @Test
    fun shouldParseLink12() {
        val code = Eip681Code.create(testVectors[11])
        Assert.assertNotNull(code)
        Assert.assertEquals(code!!.type, EipQrCodeType.PAY)
        Assert.assertNotNull(code.targetAddress)
        Assert.assertEquals(code.targetAddress, Address.createRaw("0xcccc00000000000000000000000000000000cccc"))
        Assert.assertEquals(code.chainId, BigInteger.valueOf(123))
        Assert.assertNull(code.recipientAddress)
        Assert.assertNull(code.value)
        Assert.assertNull(code.tokenValue)
        Assert.assertNull(code.gasLimit)
        Assert.assertEquals(code.functionName, "customfunction")
        Assert.assertEquals(
            code.function, AbiFunction(
                "customfunction",
                emptyList(),
                emptyArray(),
                false,
                false
            )
        )
        Assert.assertTrue(code.parameters.isEmpty())
    }

    @Test
    fun shouldParseLink13() {
        val code = Eip681Code.create(testVectors[12])
        Assert.assertNull(code)
    }

    @Test
    fun shouldParseLink14() {
        val code = Eip681Code.create(testVectors[13])
        Assert.assertNull(code)
    }

    @Test
    fun shouldParseLink15() {
        val code = Eip681Code.create(testVectors[14])
        Assert.assertNull(code)
    }
}
