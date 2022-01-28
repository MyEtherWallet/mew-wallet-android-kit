package com.myetherwallet.mewwalletkit.eip.eip155

import com.myetherwallet.mewwalletkit.bip.bip44.Address
import com.myetherwallet.mewwalletkit.bip.bip44.Network
import com.myetherwallet.mewwalletkit.bip.bip44.PrivateKey
import com.myetherwallet.mewwalletkit.bip.bip44.PublicKey
import com.myetherwallet.mewwalletkit.core.data.rlp.RlpTransaction
import com.myetherwallet.mewwalletkit.core.extension.addHexPrefix
import com.myetherwallet.mewwalletkit.core.extension.hexToByteArray
import com.myetherwallet.mewwalletkit.core.extension.sign
import com.myetherwallet.mewwalletkit.core.extension.toHexString
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger

/**
 * Created by BArtWell on 20.06.2019.
 */

class LegacyTransactionTest {

    private val testVectors = arrayOf(
        TestVector(
            0,
            "0",
            "0",
            "0",
            "0x095e7baea6a6c7c4c2dfeb977efac326af552d87",
            "",
            "0",
            "0x48b55bfa915ac795c431978d8a6a992b628d557da5ff759b307d495a36649353",
            "0xefffd310ac743f371de3b9f7f9cb56c0b28ad43601b4ab949f53faa07bd2c804",
            "0x1b",
            "0xf85d80808094095e7baea6a6c7c4c2dfeb977efac326af552d8780801ba048b55bfa915ac795c431978d8a6a992b628d557da5ff759b307d495a36649353a0efffd310ac743f371de3b9f7f9cb56c0b28ad43601b4ab949f53faa07bd2c804",
            null,
            null
        ),
        TestVector(
            1,
            "0x03",
            "0x01",
            "0x5208",
            "0xb94f5374fce5edbc8e2a8697c15331677e6ebf0b",
            "0x",
            "0x0a",
            "0xfffffffffffffffffffffffffffffffebaaedce6af48a03bbfd25e8cd0364141",
            "0xfffffffffffffffffffffffffffffffebaaedce6af48a03bbfd25e8cd0364141",
            "0x1c",
            "0xf85f030182520894b94f5374fce5edbc8e2a8697c15331677e6ebf0b0a801ca0fffffffffffffffffffffffffffffffebaaedce6af48a03bbfd25e8cd0364141a0fffffffffffffffffffffffffffffffebaaedce6af48a03bbfd25e8cd0364141",
            null,
            null
        ),
        TestVector(
            2,
            "0x03",
            "0x01",
            "0x5208",
            "0xb94f5374fce5edbc8e2a8697c15331677e6ebf0b",
            "0x",
            "0x0a",
            "0x98ff921201554726367d2be8c804a7ff89ccf285ebc57dff8ae4c44b9c19ac4a",
            "0x1887321be575c8095f789dd4c743dfe42c1820f9231f98a962b210e3ac2452a3",
            "0x1c",
            "0xf85f030182520894b94f5374fce5edbc8e2a8697c15331677e6ebf0b0a801ca098ff921201554726367d2be8c804a7ff89ccf285ebc57dff8ae4c44b9c19ac4aa01887321be575c8095f789dd4c743dfe42c1820f9231f98a962b210e3ac2452a3",
            "58d79230fc81a042315da7d243272798e27cb40c",
            "1cbb233404f49e96cb795d0ea74f485eca2c41a216e0ce80694cef4dd7a45b50"
        ),
        TestVector(
            3,
            "0",
            "0x01",
            "0x5208",
            "0x095e7baea6a6c7c4c2dfeb977efac326af552d87",
            "",
            "0x0a",
            "0x48b55bfa915ac795c431978d8a6a992b628d557da5ff759b307d495a36649353",
            "0x1fffd310ac743f371de3b9f7f9cb56c0b28ad43601b4ab949f53faa07bd2c804",
            "0x1b",
            "0xf85f800182520894095e7baea6a6c7c4c2dfeb977efac326af552d870a801ba048b55bfa915ac795c431978d8a6a992b628d557da5ff759b307d495a36649353a01fffd310ac743f371de3b9f7f9cb56c0b28ad43601b4ab949f53faa07bd2c804",
            "963f4a0d8a11b758de8d5b99ab4ac898d6438ea6",
            "ecb3ece1b90ea15a2360b99abc98ae56bd6bec7d14d5ce16ca4e814b44e4438d"
        ),
        TestVector(
            4,
            "",
            "0x04a817c800",
            "0x5208",
            "0x3535353535353535353535353535353535353535",
            "",
            "",
            "0x044852b2a670ade5407e78fb2863c51de9fcb96542a07186fe3aeda6bb8a116d",
            "0x044852b2a670ade5407e78fb2863c51de9fcb96542a07186fe3aeda6bb8a116d",
            "0x25",
            "0xf864808504a817c800825208943535353535353535353535353535353535353535808025a0044852b2a670ade5407e78fb2863c51de9fcb96542a07186fe3aeda6bb8a116da0044852b2a670ade5407e78fb2863c51de9fcb96542a07186fe3aeda6bb8a116d",
            "f0f6f18bca1b28cd68e4357452947e021241e9ce",
            "b1e2188bc490908a78184e4818dca53684167507417fdb4c09c2d64d32a9896a"
        ),
        TestVector(
            5,
            "0x01",
            "0x04a817c801",
            "0xa410",
            "0x3535353535353535353535353535353535353535",
            "",
            "0x01",
            "0x489efdaa54c0f20c7adf612882df0950f5a951637e0307cdcb4c672f298b8bca",
            "0x489efdaa54c0f20c7adf612882df0950f5a951637e0307cdcb4c672f298b8bc6",
            "0x25",
            "0xf864018504a817c80182a410943535353535353535353535353535353535353535018025a0489efdaa54c0f20c7adf612882df0950f5a951637e0307cdcb4c672f298b8bcaa0489efdaa54c0f20c7adf612882df0950f5a951637e0307cdcb4c672f298b8bc6",
            "23ef145a395ea3fa3deb533b8a9e1b4c6c25d112",
            "e62703f43b6f10d42b520941898bf710ebb66dba9df81702702b6d9bf23fef1b"
        ),
        TestVector(
            6,
            "0x02",
            "0x04a817c802",
            "0xf618",
            "0x3535353535353535353535353535353535353535",
            "",
            "0x08",
            "0x2d7c5bef027816a800da1736444fb58a807ef4c9603b7848673f7e3a68eb14a5",
            "0x2d7c5bef027816a800da1736444fb58a807ef4c9603b7848673f7e3a68eb14a5",
            "0x25",
            "0xf864028504a817c80282f618943535353535353535353535353535353535353535088025a02d7c5bef027816a800da1736444fb58a807ef4c9603b7848673f7e3a68eb14a5a02d7c5bef027816a800da1736444fb58a807ef4c9603b7848673f7e3a68eb14a5",
            "2e485e0c23b4c3c542628a5f672eeab0ad4888be",
            "1f621d7d8804723ab6fec606e504cc893ad4fe4a545d45f499caaf16a61d86dd"
        ),
        TestVector(
            7,
            "0x03",
            "0x04a817c803",
            "0x014820",
            "0x3535353535353535353535353535353535353535",
            "",
            "0x1b",
            "0x2a80e1ef1d7842f27f2e6be0972bb708b9a135c38860dbe73c27c3486c34f4e0",
            "0x2a80e1ef1d7842f27f2e6be0972bb708b9a135c38860dbe73c27c3486c34f4de",
            "0x25",
            "0xf865038504a817c803830148209435353535353535353535353535353535353535351b8025a02a80e1ef1d7842f27f2e6be0972bb708b9a135c38860dbe73c27c3486c34f4e0a02a80e1ef1d7842f27f2e6be0972bb708b9a135c38860dbe73c27c3486c34f4de",
            "82a88539669a3fd524d669e858935de5e5410cf0",
            "99b6455776b1988840d0074c23772cb6b323eb32c5011e4a3a1d06d27b2eb425"
        ),
        TestVector(
            8,
            "0x04",
            "0x04a817c804",
            "0x019a28",
            "0x3535353535353535353535353535353535353535",
            "",
            "0x40",
            "0x13600b294191fc92924bb3ce4b969c1e7e2bab8f4c93c3fc6d0a51733df3c063",
            "0x13600b294191fc92924bb3ce4b969c1e7e2bab8f4c93c3fc6d0a51733df3c060",
            "0x25",
            "0xf865048504a817c80483019a28943535353535353535353535353535353535353535408025a013600b294191fc92924bb3ce4b969c1e7e2bab8f4c93c3fc6d0a51733df3c063a013600b294191fc92924bb3ce4b969c1e7e2bab8f4c93c3fc6d0a51733df3c060",
            "f9358f2538fd5ccfeb848b64a96b743fcc930554",
            "0b2b499d5a3e729bcc197e1a00f922d80890472299dd1c648988eb08b5b1ff0a"
        ),
        TestVector(
            9,
            "0x05",
            "0x04a817c805",
            "0x01ec30",
            "0x3535353535353535353535353535353535353535",
            "",
            "0x7d",
            "0x4eebf77a833b30520287ddd9478ff51abbdffa30aa90a8d655dba0e8a79ce0c1",
            "0x4eebf77a833b30520287ddd9478ff51abbdffa30aa90a8d655dba0e8a79ce0c1",
            "0x25",
            "0xf865058504a817c8058301ec309435353535353535353535353535353535353535357d8025a04eebf77a833b30520287ddd9478ff51abbdffa30aa90a8d655dba0e8a79ce0c1a04eebf77a833b30520287ddd9478ff51abbdffa30aa90a8d655dba0e8a79ce0c1",
            "a8f7aba377317440bc5b26198a363ad22af1f3a4",
            "99a214f26aaf2804d84367ac8f33ff74b3a94e68baf820668f3641819ced1216"
        ),
        TestVector(
            10,
            "0x06",
            "0x04a817c806",
            "0x023e38",
            "0x3535353535353535353535353535353535353535",
            "",
            "0xd8",
            "0x6455bf8ea6e7463a1046a0b52804526e119b4bf5136279614e0b1e8e296a4e2f",
            "0x6455bf8ea6e7463a1046a0b52804526e119b4bf5136279614e0b1e8e296a4e2d",
            "0x25",
            "0xf866068504a817c80683023e3894353535353535353535353535353535353535353581d88025a06455bf8ea6e7463a1046a0b52804526e119b4bf5136279614e0b1e8e296a4e2fa06455bf8ea6e7463a1046a0b52804526e119b4bf5136279614e0b1e8e296a4e2d",
            "f1f571dc362a0e5b2696b8e775f8491d3e50de35",
            "4ed0b4b20536cce62389c6b95ff6a517489b6045efdefeabb4ecf8707d99e15d"
        ),
        TestVector(
            11,
            "0x07",
            "0x04a817c807",
            "0x029040",
            "0x3535353535353535353535353535353535353535",
            "",
            "0x0157",
            "0x52f1a9b320cab38e5da8a8f97989383aab0a49165fc91c737310e4f7e9821021",
            "0x52f1a9b320cab38e5da8a8f97989383aab0a49165fc91c737310e4f7e9821021",
            "0x25",
            "0xf867078504a817c807830290409435353535353535353535353535353535353535358201578025a052f1a9b320cab38e5da8a8f97989383aab0a49165fc91c737310e4f7e9821021a052f1a9b320cab38e5da8a8f97989383aab0a49165fc91c737310e4f7e9821021",
            "d37922162ab7cea97c97a87551ed02c9a38b7332",
            "a40eb7000de852898a385a19312284bb06f6a9b5d8d03e0b8fb5df2f07f9fe94"
        ),
        TestVector(
            12,
            "0x08",
            "0x04a817c808",
            "0x02e248",
            "0x3535353535353535353535353535353535353535",
            "",
            "0x0200",
            "0x64b1702d9298fee62dfeccc57d322a463ad55ca201256d01f62b45b2e1c21c12",
            "0x64b1702d9298fee62dfeccc57d322a463ad55ca201256d01f62b45b2e1c21c10",
            "0x25",
            "0xf867088504a817c8088302e2489435353535353535353535353535353535353535358202008025a064b1702d9298fee62dfeccc57d322a463ad55ca201256d01f62b45b2e1c21c12a064b1702d9298fee62dfeccc57d322a463ad55ca201256d01f62b45b2e1c21c10",
            "9bddad43f934d313c2b79ca28a432dd2b7281029",
            "588df025c4c2d757d3e314bd3dfbfe352687324e6b8557ad1731585e96928aed"
        ),
        TestVector(
            13,
            "0x09",
            "0x04a817c809",
            "0x033450",
            "0x3535353535353535353535353535353535353535",
            "",
            "0x02d9",
            "0x52f8f61201b2b11a78d6e866abc9c3db2ae8631fa656bfe5cb53668255367afb",
            "0x52f8f61201b2b11a78d6e866abc9c3db2ae8631fa656bfe5cb53668255367afb",
            "0x25",
            "0xf867098504a817c809830334509435353535353535353535353535353535353535358202d98025a052f8f61201b2b11a78d6e866abc9c3db2ae8631fa656bfe5cb53668255367afba052f8f61201b2b11a78d6e866abc9c3db2ae8631fa656bfe5cb53668255367afb",
            "3c24d7329e92f84f08556ceb6df1cdb0104ca49f",
            "f39c7dac06a9f3abf09faf5e30439a349d3717611b3ed337cd52b0d192bc72da"
        ),
        TestVector(
            14,
            "0x0e",
            "",
            "0x0493e0",
            "",
            "0x60f2ff61000080610011600039610011565b6000f3",
            "",
            "0xa310f4d0b26207db76ba4e1e6e7cf1857ee3aa8559bcbc399a6b09bfea2d30b4",
            "0x6dff38c645a1486651a717ddf3daccb4fd9a630871ecea0758ddfcf2774f9bc6",
            "0x1c",
            "0xf8610e80830493e080809560f2ff61000080610011600039610011565b6000f31ca0a310f4d0b26207db76ba4e1e6e7cf1857ee3aa8559bcbc399a6b09bfea2d30b4a06dff38c645a1486651a717ddf3daccb4fd9a630871ecea0758ddfcf2774f9bc6",
            "874b54a8bd152966d63f706bae1ffeb0411921e5",
            "db38325f4c7a9917a611fd09694492c23b0ec357a68ab5cbf905fc9757b9919a"
        ),
        TestVector(
            15,
            "0x0f",
            "",
            "0x0493e0",
            "0x00000000000000000000000000000000000000c0",
            "0x646f6e6b6579",
            "",
            "0x9f00c6da4f2e4b5f3316e70c7669f9df71fa21d533afa63450065731132ba7b6",
            "0x3886c27a8b3515ab9e2e04492f8214718621421e92d3b6954d9e3fb409ead788",
            "0x1b",
            "0xf8660f80830493e09400000000000000000000000000000000000000c08086646f6e6b65791ba09f00c6da4f2e4b5f3316e70c7669f9df71fa21d533afa63450065731132ba7b6a03886c27a8b3515ab9e2e04492f8214718621421e92d3b6954d9e3fb409ead788",
            "874b54a8bd152966d63f706bae1ffeb0411921e5",
            "278608eba8465230d0552c8df9fbcc6fc35d2350f4feb0e49a399b2adab37e39"
        ),
        TestVector(
            16,
            "0x0f",
            "",
            "0x0493e0",
            "",
            "0x60f3ff61000080610011600039610011565b6000f3",
            "0x01",
            "0xaf2944b645e280a35789aacfce7e16a8d57b43a0076dfd564bdc0395e3576324",
            "0x5272a0013f198713128bb0e3da43bb03ec7224c5509d4cabbe39fd31c36b5786",
            "0x1b",
            "0xf8610f80830493e080019560f3ff61000080610011600039610011565b6000f31ba0af2944b645e280a35789aacfce7e16a8d57b43a0076dfd564bdc0395e3576324a05272a0013f198713128bb0e3da43bb03ec7224c5509d4cabbe39fd31c36b5786",
            "874b54a8bd152966d63f706bae1ffeb0411921e5",
            "d9a26fff8704b137b592b07b64a86eac555dc347c87ae7fe1d2fe824d12427e5"
        ),
        TestVector(
            17,
            "0x12",
            "",
            "0x0493e0",
            "0x0000000000000000000000000000000000000001",
            "0x6d6f6f7365",
            "0x00",
            "0x376d0277dd3a2a9229dbcb5530b532c7e4cb0f821e0ca27d9acb940970d500d8",
            "0x0ab2798f9d9c2f7551eb29d56878f8e342b45ca45f413b0fcba793d094f36f2b",
            "0x29",
            "0xf8651280830493e094000000000000000000000000000000000000000180856d6f6f736529a0376d0277dd3a2a9229dbcb5530b532c7e4cb0f821e0ca27d9acb940970d500d8a00ab2798f9d9c2f7551eb29d56878f8e342b45ca45f413b0fcba793d094f36f2b",
            "3c24d7329e92f84f08556ceb6df1cdb0104ca49f",
            null
        ),
        TestVector(
            18,
            "0x13",
            "",
            "0x0493e0",
            "0x0000000000000000000000000000000000000012",
            "0x6d6f6f7365",
            "0x00",
            "0xd0e340578f9d733986f6a55c5401953c90f7ccd46fe72d5588592dd9cbdf1e03",
            "0x01d8d63149bd986f363084ac064e8387850d80e5238cc16ed4d30fd0a5af7261",
            "0x2a",
            "0xf8651380830493e094000000000000000000000000000000000000001280856d6f6f73652aa0d0e340578f9d733986f6a55c5401953c90f7ccd46fe72d5588592dd9cbdf1e03a001d8d63149bd986f363084ac064e8387850d80e5238cc16ed4d30fd0a5af7261",
            "3c24d7329e92f84f08556ceb6df1cdb0104ca49f",
            null
        ),
        TestVector(
            19,
            "0x14",
            "",
            "0x0493e0",
            "0x0000000000000000000000000000000000000022",
            "0x6d6f6f7365",
            "0x00",
            "0x4bc84887af29d2b159ee290dee793bdba34079428f48699e9fc92a7e12d4aeaf",
            "0x63b9d78c5a36f96454fe2d5c1eb7c0264f6273afdc0ba28dd9a8f00eadaf7476",
            "0x2a",
            "0xf8651480830493e094000000000000000000000000000000000000002280856d6f6f73652aa04bc84887af29d2b159ee290dee793bdba34079428f48699e9fc92a7e12d4aeafa063b9d78c5a36f96454fe2d5c1eb7c0264f6273afdc0ba28dd9a8f00eadaf7476",
            "3c24d7329e92f84f08556ceb6df1cdb0104ca49f",
            null
        )/*,
        TestVector(
            20,
            "0",
            "0x09184e72a000",
            "0xf388",
            "",
            "0x",
            "0",
            "0x2c",
            "0x04",
            "0x1b",
            "0xd1808609184e72a00082f3888080801b2c04",
            "170ad78f26da62f591fa3fe3d54c30016167cbbf",
            "ba09edc1275a285fb27bfe82c4eea240a907a0dbaf9e55764b8f318c37d5974f"
        )*/
    )

    @Test
    fun signatureTests() {
        for (vector in testVectors) {
            val transaction = vector.transaction
            Assert.assertEquals("Invalid RLP data (id " + vector.id + ")", vector.rlp.toHexString(), transaction.rlpData().rlpEncode()?.toHexString())
            if (vector.hash != null) {
                Assert.assertArrayEquals("Invalid hash. id: (" + vector.id + ")", vector.hash, transaction.hash())
                val publicKeyData = transaction.signature?.recoverPublicKey(transaction)
                Assert.assertNotNull("Can't recover public key. id: (" + vector.id + ")", publicKeyData)
                val publicKey = PublicKey(publicKeyData!!, false, Network.ETHEREUM)
                Assert.assertNotNull(publicKey.address()?.address)
                val lowerCasedAddress = publicKey.address()?.address?.toLowerCase()
                Assert.assertEquals("Invalid sender. id: (" + vector.id + ")", vector.sender, lowerCasedAddress)
            }
        }
    }

    @Test
    fun shouldSignTransactionAndReturnsTheExpectedSignature() {
        val transaction = LegacyTransaction(
            "0x03",
            "0x3b9aca00",
            "0x7530",
            Address.createRaw("0xb414031Aa4838A69e27Cb2AE31E709Bcd674F0Cb"),
            "0x64",
            ByteArray(0)
        )
        transaction.chainId = BigInteger.valueOf(0x11)
        Assert.assertEquals(transaction.hash()?.toHexString()?.addHexPrefix(), "0x91e0ad336c23d84f757aa4cde2d0bb557daf5e1ca0a0b850b6431f3277fc167b")

        val privateKey = PrivateKey.createWithPrivateKey("3a0ce9a362c73439adb38c595e739539be1e34d19c5e9f04962c101c86bd7616".hexToByteArray(), Network.ETHEREUM)
        transaction.sign(privateKey)

        Assert.assertNotNull(transaction.signature)
        Assert.assertEquals(transaction.signature?.r?.toByteArray()?.toHexString(), "1fff9fa845437523b0a7f334b7d2a0ab14364a3581f898cd1bba3b5909465867")
        Assert.assertEquals(transaction.signature?.s?.toByteArray()?.toHexString(), "1415137f53eeddf0687e966f8d59984676d6d92ce88140765ed343db6936679e")
        Assert.assertEquals(transaction.signature?.v?.toByteArray()?.toHexString(), "45")
    }

    @Test
    fun shouldSignTransactionAndReturnTheExpectedSignature2() {
        val transaction = LegacyTransaction(
            "0x00", "0x106", "0x33450",
            Address("0x5c5220918B616E583515A7F42b6bE0c967664462"), "0xc8", ByteArray(0)
        )
        transaction.chainId = BigInteger.ONE
//            Assert.assertEquals(transaction.serialize()?.toHexString(), "e08082010683033450945c5220918b616e583515a7f42b6be0c96766446281c880")

        val privateKey = PrivateKey.createWithPrivateKey("009312d3c3a8ac6d00fb2df851e1cb0023becc00cc7a0083b0ae70f4bd0575ae".hexToByteArray(), Network.ETHEREUM)
        transaction.sign(privateKey)

        Assert.assertNotNull(transaction.signature)
        Assert.assertEquals(transaction.signature?.r?.toHexString(), "0xd87153e2fb484f21469785f5b6ab95cc5c3aba5a80487428b63024068633bda2")
        Assert.assertEquals(transaction.signature?.s?.toHexString(), "0x2421eb4be1a11ff6071881608f660047604a7f63883326588b4168a3491800")
        Assert.assertEquals(transaction.signature?.v?.toHexString(), "0x25")
//        Assert.assertEquals(
//            transaction.serialize()?.toHexString(),
//            "f8628082010683033450945c5220918b616e583515a7f42b6be0c96766446281c88025a0d87153e2fb484f21469785f5b6ab95cc5c3aba5a80487428b63024068633bda29f2421eb4be1a11ff6071881608f660047604a7f63883326588b4168a3491800"
//        )
        Assert.assertEquals(transaction.hash()?.toHexString(), "a048f58d4da25b8d91c2691cd3527074ef4ac52f520cb63c40b1fe50a2abf906")
    }

    class TestVector(
        val id: Int,
        nonce: String,
        gasPrice: String,
        gasLimit: String,
        to: String,
        data: String,
        value: String,
        r: String,
        s: String,
        v: String,
        rlp: String,
        sender: String?,
        hash: String?
    ) {

        val transaction = LegacyTransaction(nonce, gasPrice, gasLimit, Address.createRaw(to), value, data.hexToByteArray())
        val signature = TransactionSignature(r, s, v)
        val rlp: ByteArray
        val sender: String?
        val hash: ByteArray?

        init {
            transaction.signature = signature
            this.rlp = rlp.hexToByteArray()
            this.sender = sender?.addHexPrefix()?.toLowerCase()
            this.hash = hash?.let { hash.hexToByteArray() }
        }
    }
}