package com.myetherwallet.mewwalletkit.core.extension

import org.junit.Assert
import org.junit.Test

/**
 * Created by BArtWell on 26.06.2019.
 */

class ByteArrayKtTest2 {

    @Test
    fun secp256k1Verify() {
        val data = "3a0ce9a362c73439adb38c595e739539be1e34d19c5e9f04962c101c86bd7616"
        Assert.assertTrue(data.hexToByteArray().secp256k1Verify())
    }

    @Test
    fun secp256k1ParseSignature() {
        val data = "98ff921201554726367d2be8c804a7ff89ccf285ebc57dff8ae4c44b9c19ac4a1887321be575c8095f789dd4c743dfe42c1820f9231f98a962b210e3ac2452a301".hexToByteArray()
        val expected = "4aac199c4bc4e48aff7dc5eb85f2cc89ffa704c8e82b7d36264755011292ff98a35224ace310b262a9981f23f920182ce4df43c7d49d785f09c875e51b32871801".hexToByteArray()
        Assert.assertArrayEquals(expected, data.secp256k1ParseSignature())
    }

    @Test
    fun secp256k1RecoverableSign() {
        val data = "91e0ad336c23d84f757aa4cde2d0bb557daf5e1ca0a0b850b6431f3277fc167b".hexToByteArray()
        val privateKey = "3a0ce9a362c73439adb38c595e739539be1e34d19c5e9f04962c101c86bd7616".hexToByteArray()
        val expected = "67584609593bba1bcd98f881354a3614aba0d2b734f3a7b023754345a89fff1f9e673669db43d35e764081e82cd9d6764698598d6f967e68f0ddee537f13151400".hexToByteArray()
        Assert.assertArrayEquals(expected, data.secp256k1RecoverableSign(privateKey, false))
    }

    @Test
    fun secp256k1RecoverPublicKey() {
        val data = "91e0ad336c23d84f757aa4cde2d0bb557daf5e1ca0a0b850b6431f3277fc167b".hexToByteArray()
        val privateKey = "3a0ce9a362c73439adb38c595e739539be1e34d19c5e9f04962c101c86bd7616".hexToByteArray()
        val expected = "67584609593bba1bcd98f881354a3614aba0d2b734f3a7b023754345a89fff1f9e673669db43d35e764081e82cd9d6764698598d6f967e68f0ddee537f13151400".hexToByteArray()
        Assert.assertArrayEquals(expected, data.secp256k1RecoverableSign(privateKey, false))
    }

    @Test
    fun mewConnectSignatureTest() {
        // should sign personal message data correctly
        val testMessage = byteArrayOf(0xe2.toByte(), 0x45, 0x10, 0x93.toByte(), 0x83.toByte(), 0xa4.toByte(), 0x80.toByte(), 0xc0.toByte(), 0x86.toByte(), 0x80.toByte(), 0x18, 0xca.toByte(), 0x92.toByte(), 0x8c.toByte(), 0xbe.toByte(), 0xd5.toByte(), 0x77, 0x21, 0x39, 0xa4.toByte(), 0x31, 0x04, 0x7f, 0xef.toByte(), 0xb5.toByte(), 0xc5.toByte(), 0xf0.toByte(), 0x46, 0x59, 0x24, 0x65, 0x75)
        val testPrivateKey = byteArrayOf(0x41, 0xd1.toByte(), 0xc8.toByte(), 0xf0.toByte(), 0xd3.toByte(), 0x78, 0x1e, 0x50, 0xf8.toByte(), 0x49, 0x96.toByte(), 0xfc.toByte(), 0x65, 0x5e, 0xe5.toByte(), 0x64, 0x98.toByte(), 0x2a, 0x88.toByte(), 0xbe.toByte(), 0x8d.toByte(), 0xa4.toByte(), 0x95.toByte(), 0x2d, 0x9e.toByte(), 0xdd.toByte(), 0xb5.toByte(), 0x16, 0xdb.toByte(), 0x06, 0xd5.toByte(), 0x88.toByte())
        val testRecoverableSignature = byteArrayOf(0x35, 0x38, 0x1f, 0x1e, 0x79, 0x6d, 0x53, 0xf4.toByte(), 0x60, 0x47, 0xce.toByte(), 0xfc.toByte(), 0x8d.toByte(), 0x40, 0x0b, 0xb2.toByte(), 0xac.toByte(), 0x51, 0x7e, 0xcf.toByte(), 0xde.toByte(), 0x9b.toByte(), 0xb0.toByte(), 0xab.toByte(), 0x1f, 0x84.toByte(), 0x59, 0x22, 0x84.toByte(), 0xf3.toByte(), 0x08, 0x31, 0x02, 0xca.toByte(), 0x22, 0xb8.toByte(), 0xfb.toByte(), 0x2c, 0xe0.toByte(), 0x54, 0x82.toByte(), 0x01, 0x9d.toByte(), 0xf0.toByte(), 0x2c, 0xa9.toByte(), 0xfc.toByte(), 0x65, 0x26, 0x53, 0xdd.toByte(), 0x07, 0xb5.toByte(), 0xbe.toByte(), 0xdb.toByte(), 0x8c.toByte(), 0x48, 0x86.toByte(), 0x24, 0x1a, 0x86.toByte(), 0x77, 0xdd.toByte(), 0x41, 0x01)
        val testSerializedRecoverableSignature = byteArrayOf(0x31, 0x08, 0xf3.toByte(), 0x84.toByte(), 0x22, 0x59, 0x84.toByte(), 0x1f, 0xab.toByte(), 0xb0.toByte(), 0x9b.toByte(), 0xde.toByte(), 0xcf.toByte(), 0x7e, 0x51, 0xac.toByte(), 0xb2.toByte(), 0x0b, 0x40, 0x8d.toByte(), 0xfc.toByte(), 0xce.toByte(), 0x47, 0x60, 0xf4.toByte(), 0x53, 0x6d, 0x79, 0x1e, 0x1f, 0x38, 0x35, 0x41, 0xdd.toByte(), 0x77, 0x86.toByte(), 0x1a, 0x24, 0x86.toByte(), 0x48, 0x8c.toByte(), 0xdb.toByte(), 0xbe.toByte(), 0xb5.toByte(), 0x07, 0xdd.toByte(), 0x53, 0x26, 0x65, 0xfc.toByte(), 0xa9.toByte(), 0x2c, 0xf0.toByte(), 0x9d.toByte(), 0x01, 0x82.toByte(), 0x54, 0xe0.toByte(), 0x2c, 0xfb.toByte(), 0xb8.toByte(), 0x22, 0xca.toByte(), 0x02, 0x01)

        val recoverableSignature = testMessage.secp256k1RecoverableSign(testPrivateKey)
        Assert.assertArrayEquals(testRecoverableSignature, recoverableSignature)

        val serializedRecoverableSignature = recoverableSignature?.secp256k1SerializeSignature()
        Assert.assertArrayEquals(testSerializedRecoverableSignature, serializedRecoverableSignature)

//        siga: 3108f384 2259841f abb09bde cf7e51ac b20b408d fcce4760 f4536d79 1e1f3835 41dd7786 1a248648 8cdbbeb5 07dd5326 65fca92c f09d0182 54e02cfb b822ca02 00
//              3108f384 2259841f abb09bde cf7e51ac b20b408d fcce4760 f4536d79 1e1f3835 41dd7786 1a248648 8cdbbeb5 07dd5326 65fca92c f09d0182 54e02cfb b822ca02 01
        //          3108f3 84225984 1fabb09b decf7e51 acb20b40 8dfcce47 60f4536d 791e1f38 3541dd77 861a2486 488cdbbe b507dd53 2665fca9 2cf09d01 8254e02c fbb822ca 0201
//        result: 1c3108f3 84225984 1fabb09b decf7e51 acb20b40 8dfcce47 60f4536d 791e1f38 3541dd77 861a2486 488cdbbe b507dd53 2665fca9 2cf09d01 8254e02c fbb822ca 02
    }

    @Test
    fun jsonRpcTestsBytes() {
        // Should correctly hash personal message
        val testMessage = byteArrayOf(0x34, 0x31, 0x64, 0x31, 0x63, 0x38, 0x66, 0x30, 0x64, 0x33, 0x37, 0x38, 0x31, 0x65, 0x35, 0x30, 0x66, 0x38, 0x34, 0x39, 0x39, 0x36, 0x66, 0x63, 0x36, 0x35, 0x35, 0x65, 0x65, 0x35, 0x36, 0x34, 0x39, 0x38, 0x32, 0x61, 0x38, 0x38, 0x62, 0x65, 0x38, 0x64, 0x61, 0x34, 0x39, 0x35, 0x32, 0x64, 0x39, 0x65, 0x64, 0x64, 0x62, 0x35, 0x31, 0x36, 0x64, 0x62, 0x30, 0x36, 0x64, 0x35, 0x38, 0x38)
        val testHash = byteArrayOf(0xe2.toByte(), 0x45, 0x10, 0x93.toByte(), 0x83.toByte(), 0xa4.toByte(), 0x80.toByte(), 0xc0.toByte(), 0x86.toByte(), 0x80.toByte(), 0x18, 0xca.toByte(), 0x92.toByte(), 0x8c.toByte(), 0xbe.toByte(), 0xd5.toByte(), 0x77, 0x21, 0x39, 0xa4.toByte(), 0x31, 0x04, 0x7f, 0xef.toByte(), 0xb5.toByte(), 0xc5.toByte(), 0xf0.toByte(), 0x46, 0x59, 0x24, 0x65, 0x75)
        val hash = testMessage.hashPersonalMessage()
        Assert.assertArrayEquals(testHash, hash)
    }

    @Test
    fun jsonRpcTestsString() {
        val testMessage = "41d1c8f0d3781e50f84996fc655ee564982a88be8da4952d9eddb516db06d588"
        val testHash = byteArrayOf(0xe2.toByte(), 0x45, 0x10, 0x93.toByte(), 0x83.toByte(), 0xa4.toByte(), 0x80.toByte(), 0xc0.toByte(), 0x86.toByte(), 0x80.toByte(), 0x18, 0xca.toByte(), 0x92.toByte(), 0x8c.toByte(), 0xbe.toByte(), 0xd5.toByte(), 0x77, 0x21, 0x39, 0xa4.toByte(), 0x31, 0x04, 0x7f, 0xef.toByte(), 0xb5.toByte(), 0xc5.toByte(), 0xf0.toByte(), 0x46, 0x59, 0x24, 0x65, 0x75)
        val hash = testMessage.hashPersonalMessage()
        Assert.assertArrayEquals(testHash, hash)
    }
}
