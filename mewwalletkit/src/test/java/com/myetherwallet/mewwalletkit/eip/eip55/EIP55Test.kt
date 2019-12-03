package com.myetherwallet.mewwalletkit.eip.eip55

import com.myetherwallet.mewwalletkit.core.extension.eip55
import com.myetherwallet.mewwalletkit.core.extension.hexToByteArray
import org.junit.Assert
import org.junit.Test

/**
 * Created by BArtWell on 20.06.2019.
 */

class EIP55Test {

    private val testVectors = arrayOf(
        //All upper
        "52908400098527886E0F7030069857D2E4169EE7",
        "8617E340B3D01FA5F11F306F4090FD50E238070D",
        //All lower
        "de709f2102306220921060314715629080e2fb77",
        "27b1fdb04752bbc536007a920d24acb045561c26",
        //Normal
        "5aAeb6053F3E94C9b9A09f33669435E7Ef1BeAed",
        "fB6916095ca1df60bB79Ce92cE3Ea74c37c5d359",
        "dbF03B407c01E7cD3CBea99509d93f8DDDC8C6FB",
        "D1220A0cf47c7B9Be7A2E6BA89F429762e7b9aDb"
    )

    @Test
    fun `Should correctly format data`() {
        for (vector in testVectors) {
            val data = vector.toLowerCase().hexToByteArray()
            Assert.assertEquals(data.eip55(), vector)
        }
    }
}