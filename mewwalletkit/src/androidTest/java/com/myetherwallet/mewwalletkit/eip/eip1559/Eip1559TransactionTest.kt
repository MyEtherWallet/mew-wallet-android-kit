package com.myetherwallet.mewwalletkit.eip.eip1559

import com.myetherwallet.mewwalletkit.bip.bip44.Address
import com.myetherwallet.mewwalletkit.bip.bip44.Network
import com.myetherwallet.mewwalletkit.bip.bip44.PrivateKey
import com.myetherwallet.mewwalletkit.core.data.rlp.RlpByteArray
import com.myetherwallet.mewwalletkit.core.extension.*
import com.myetherwallet.mewwalletkit.eip.eip2930.AccessList
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger

/**
 * Created by BArtWell on 08.07.2021.
 */

class Eip1559TransactionTest {

    private val testVectors = arrayOf(
        // https://github.com/ethereumjs/ethereumjs-monorepo/blob/ef29902092473b78bd79f80f68047d6f2c38c71b/packages/tx/test/json/eip1559.json
        TestVector(
            0,
            BigInteger.valueOf(819),
            BigInteger.valueOf(43203529),
            BigInteger.valueOf(75853),
            BigInteger.valueOf(121212),
            BigInteger.valueOf(35552),
            "0x000000000000000000000000000000000000aaaa",
            null,
            "0xb87302f870821e8e8203338301284d8301d97c828ae094000000000000000000000000000000000000aaaa8402933bc980c080a0d067f2167008c59219652f91cbf8788dbca5f771f6e91e2b7657e85b78b472e0a01d305af9fe81fdba43f0dfcd400ed24dcace0a7e30d4e85701dcaaf484cd079e",
            "0xa3bf78ff247cad934aa5fb13e05f11e59c93511523ff8a622e59c3a34700e5c8",
            BigInteger.valueOf(7822),
            "0x8f2a55949038a9610f50fb23b5883af3b4ecb3c3bb792cbcefbd1542c692be63"
        ),
        TestVector(
            1,
            BigInteger.valueOf(353),
            BigInteger.valueOf(61901619),
            BigInteger.valueOf(38850),
            BigInteger.valueOf(136295),
            BigInteger.valueOf(32593),
            "0x000000000000000000000000000000000000aaaa",
            null,
            "0xb87202f86f821e8e8201618297c283021467827f5194000000000000000000000000000000000000aaaa8403b08b3380c001a004205678f13fc1d7b10feea6513dfa76e24418b6e08097d95c6f93a3b64e536da01d45ad61bfece43862de2cce7d2575a8c1d08c26c87f3cb5f26d7b0be54a91b2",
            null,
            BigInteger.valueOf(7822),
            "0x8f2a55949038a9610f50fb23b5883af3b4ecb3c3bb792cbcefbd1542c692be63"
        ),
        TestVector(
            2,
            BigInteger.valueOf(985),
            BigInteger.valueOf(32531825),
            BigInteger.valueOf(66377),
            BigInteger.valueOf(136097),
            BigInteger.valueOf(68541),
            "0x000000000000000000000000000000000000aaaa",
            null,
            "0xb87402f871821e8e8203d983010349830213a183010bbd94000000000000000000000000000000000000aaaa8401f0657180c001a0e6d23c5ecc0f388a4d41a9523c3ad171e98083a61288c340b968dfe0903a6741a07319e8d54a4fd8f0c081d3ce093d869f9df598481991c8534bfd8f2c163884b4",
            null,
            BigInteger.valueOf(7822),
            "0x8f2a55949038a9610f50fb23b5883af3b4ecb3c3bb792cbcefbd1542c692be63"
        ),
        TestVector(
            3,
            BigInteger.valueOf(623),
            BigInteger.valueOf(21649799),
            BigInteger.valueOf(74140),
            BigInteger.valueOf(81173),
            BigInteger.valueOf(57725),
            "0x000000000000000000000000000000000000aaaa",
            null,
            "0xb87302f870821e8e82026f8301219c83013d1582e17d94000000000000000000000000000000000000aaaa84014a598780c080a0b107893e4590e177e244493a3f2b59d63619ddd82772105d8c96b7fc85ff683da07dc9b9dc054d2f69f9cc540688f6c0da3b54dd8bd8323f06d0ca26b546a480f1",
            null,
            BigInteger.valueOf(7822),
            "0x8f2a55949038a9610f50fb23b5883af3b4ecb3c3bb792cbcefbd1542c692be63"
        ),
        TestVector(
            4,
            BigInteger.valueOf(972),
            BigInteger.valueOf(94563383),
            BigInteger.valueOf(42798),
            BigInteger.valueOf(103466),
            BigInteger.valueOf(65254),
            "0x000000000000000000000000000000000000aaaa",
            null,
            "0xb87202f86f821e8e8203cc82a72e8301942a82fee694000000000000000000000000000000000000aaaa8405a2ec3780c080a02596bfedd3101f7e3c881eec060f83c60617fb21173ff41a560e02c9c23f02f4a01ef1f7930d31081c06dfd2176046a99442a88678638a33eb80afa8d2d3389ee0",
            null,
            BigInteger.valueOf(7822),
            "0x8f2a55949038a9610f50fb23b5883af3b4ecb3c3bb792cbcefbd1542c692be63"
        ),
        TestVector(
            5,
            BigInteger.valueOf(588),
            BigInteger.valueOf(99359647),
            BigInteger.valueOf(87890),
            BigInteger.valueOf(130273),
            BigInteger.valueOf(37274),
            "0x000000000000000000000000000000000000aaaa",
            null,
            "0xb87302f870821e8e82024c830157528301fce182919a94000000000000000000000000000000000000aaaa8405ec1b9f80c001a0404a06f2a9ba9d444e59f03e1b4ce46c4d7127342aa287a6e9a910b26ff6cc89a04af28549efdfb3e746938d01c92785e9852eb58c48d6f35cc99961e88f56603a",
            null,
            BigInteger.valueOf(7822),
            "0x8f2a55949038a9610f50fb23b5883af3b4ecb3c3bb792cbcefbd1542c692be63"
        ),
        TestVector(
            6,
            BigInteger.valueOf(900),
            BigInteger.valueOf(30402257),
            BigInteger.valueOf(8714),
            BigInteger.valueOf(112705),
            BigInteger.valueOf(76053),
            "0x000000000000000000000000000000000000aaaa",
            null,
            "0xb87302f870821e8e82038482220a8301b8418301291594000000000000000000000000000000000000aaaa8401cfe6d180c080a06e1bc9e4645de8bdad2c60424539c2dd93aacbc06ae94e440629b133a99e9ba7a03d23fdfdd9934c77fe5385a6b4b6c69e84cdec7814a19d81e648b5852ee5f7eb",
            null,
            BigInteger.valueOf(7822),
            "0x8f2a55949038a9610f50fb23b5883af3b4ecb3c3bb792cbcefbd1542c692be63"
        ),
        TestVector(
            7,
            BigInteger.valueOf(709),
            BigInteger.valueOf(6478043),
            BigInteger.valueOf(86252),
            BigInteger.valueOf(94636),
            BigInteger.valueOf(28335),
            "0x000000000000000000000000000000000000aaaa",
            null,
            "0xb87202f86f821e8e8202c5830150ec830171ac826eaf94000000000000000000000000000000000000aaaa8362d8db80c001a09880d133cd636f8c8dce32e2c43494b9f2dc000eee8dc61931e09f866ed04b46a00e44d5c503c7f08b36c8b062c32a963f3dd20a71e95e3d903bfbe7f4da47cd47",
            null,
            BigInteger.valueOf(7822),
            "0x8f2a55949038a9610f50fb23b5883af3b4ecb3c3bb792cbcefbd1542c692be63"
        ),
        TestVector(
            8,
            BigInteger.valueOf(939),
            BigInteger.valueOf(2782905),
            BigInteger.valueOf(45216),
            BigInteger.valueOf(91648),
            BigInteger.valueOf(45047),
            "0x000000000000000000000000000000000000aaaa",
            null,
            "0xb87102f86e821e8e8203ab82b0a08301660082aff794000000000000000000000000000000000000aaaa832a76b980c001a0a0e7d9b07c7ec68f24788a554f993b99d59a3f08439cc6c942f62ea0341f2aeba078122b9957c146f7c87811bd58a7e9916ca8c07dabcf875b97bbdef79cb9c197",
            null,
            BigInteger.valueOf(7822),
            "0x8f2a55949038a9610f50fb23b5883af3b4ecb3c3bb792cbcefbd1542c692be63"
        ),
        TestVector(
            9,
            BigInteger.valueOf(119),
            BigInteger.valueOf(65456115),
            BigInteger.valueOf(24721),
            BigInteger.valueOf(107729),
            BigInteger.valueOf(62341),
            "0x000000000000000000000000000000000000aaaa",
            null,
            "0xb87002f86d821e8e778260918301a4d182f38594000000000000000000000000000000000000aaaa8403e6c7f380c001a0e38e239770801eb995498c821e55ac9c19ae09184bb27b34be105e02fe3b7615a067c64ca6bd6d9107e85fd581ab2e70a14b637eb36505bb40861ef9d6e800cab6",
            null,
            BigInteger.valueOf(7822),
            "0x8f2a55949038a9610f50fb23b5883af3b4ecb3c3bb792cbcefbd1542c692be63"
        ),
        TestVector(
            10,
            "0x77",
            "0x3E6C7F3",
            "0x6091",
            "0x1A4D1",
            "0xF385",
            "0x000000000000000000000000000000000000aaaa",
            null,
            "0xb87002f86d821e8e778260918301a4d182f38594000000000000000000000000000000000000aaaa8403e6c7f380c001a0e38e239770801eb995498c821e55ac9c19ae09184bb27b34be105e02fe3b7615a067c64ca6bd6d9107e85fd581ab2e70a14b637eb36505bb40861ef9d6e800cab6",
            null,
            "0x1E8E".hexToByteArray(),
            "0x8f2a55949038a9610f50fb23b5883af3b4ecb3c3bb792cbcefbd1542c692be63"
        )
    )

    @Test
    fun shouldSignTransactionAndReturnsTheExpectedSignature() {
        for (vector in testVectors) {
            val transaction = vector.transaction
            val privateKey = PrivateKey.createWithPrivateKey(vector.pkBytes, Network.ETHEREUM)
            transaction.sign(privateKey)
            Assert.assertNotNull(transaction.signature)
            val serialized = RlpByteArray(transaction.serialize()!!).rlpEncode()
            Assert.assertEquals("signed transaction failed (${vector.id})", vector.signedTransactionRlp, serialized?.toHexString()?.addHexPrefix())
            val signedHash = transaction.hash(BigInteger.valueOf(7822), false)
            vector.signedHash?.let {
                Assert.assertEquals("signed hash failed (${vector.id})", it, signedHash?.toHexString()?.addHexPrefix())
            }
        }
    }

    @Test
    fun testRealTransaction() {
        val pk = PrivateKey.createWithPrivateKey("e3046226c9d5f6b7fda49b00123fe96ab2fac359315f27169b5c984059b3540f".hexToByteArray(), Network.ETHEREUM)
        val transaction = Eip1559Transaction(
            "0x0",
            "0x07",
            "0x07",
            "0x5208",
            pk.address(),
            "0x16345785D8A0000",
            ByteArray(0),
            pk.address(),
            null,
            null,
            "0x7b".hexToByteArray()
        )
        transaction.sign(pk)
        val serialized = transaction.serialize()
        Assert.assertEquals(
            serialized?.toHexString(),
            "02f86a7b8007078252089461fac28c810253ea3a42ebbb1a5cf8687765e4ee88016345785d8a000080c001a01f75fb4ae321bb55c0e7569d940ce133e775fb4494500ac6de9aeb16f45f20d1a05bf9b98c50422e3ada79a6920b1007bc36a1c36d178dbc1b44dfbd97fc56cf13"
        )
        val signedHash = transaction.hash(transaction.chainId, false)
        Assert.assertEquals(signedHash?.toHexString(), "be76a9ae424fe7a42da7db0f42db0ced82929710d9c19cfdaa7e18c634e6ae54")
    }

    @Test
    fun testRealTransactionWithAccessList() {
        val pk = PrivateKey.createWithPrivateKey("e3046226c9d5f6b7fda49b00123fe96ab2fac359315f27169b5c984059b3540f".hexToByteArray(), Network.ETHEREUM)
        val transaction = Eip1559Transaction(
            "0x01",
            "0x07",
            "0x07",
            "0x62d4",
            pk.address(),
            "0x16345785D8A0000",
            ByteArray(0),
            pk.address(),
            arrayOf(AccessList(pk.address(), arrayOf("0x0000000000000000000000000000000000000000000000000000000000000000".hexToByteArray()))),
            null,
            "0x7b".hexToByteArray()
        )
        transaction.sign(pk)
        val serialized = transaction.serialize()
        Assert.assertEquals(
            "02f8a37b0107078262d49461fac28c810253ea3a42ebbb1a5cf8687765e4ee88016345785d8a000080f838f79461fac28c810253ea3a42ebbb1a5cf8687765e4eee1a0000000000000000000000000000000000000000000000000000000000000000001a0f76cef537ee6b5a2143e4ed62debc41c19e996365ceb8b7a9ec7d7db8f83c001a01fbe06e8a4e052015af13b985047925b9f97bf145c7e7adb96f051af7c2551f0",
            serialized?.toHexString()
        )
        val signedHash = transaction.hash(transaction.chainId, false)
        Assert.assertEquals(signedHash?.toHexString()?.addHexPrefix(), "0xc30b28761a4344c27f7ef7f5e3572c941111d32602a3c75e924f02fd6942bc04")
    }

    data class TestVector(
        val id: Int,
        val nonce: BigInteger,
        val value: BigInteger,
        val maxPriorityFeePerGas: BigInteger,
        val maxFeePerGas: BigInteger,
        val gasLimit: BigInteger,
        val to: String,
        val accessList: Array<AccessList>? = null,
        val signedTransactionRlp: String,
        val signedHash: String?,
        val chainId: BigInteger,
        val pk: String
    ) {

        val transaction: Eip1559Transaction
        val signedTransactionRlpBytes: ByteArray
        val signedHashBytes: ByteArray
        val pkBytes: ByteArray

        init {
            pkBytes = pk.hexToByteArray()
            val privateKey = PrivateKey.createWithPrivateKey(pkBytes, Network.ETHEREUM)
            transaction = Eip1559Transaction(
                nonce,
                maxPriorityFeePerGas,
                maxFeePerGas,
                gasLimit,
                Address(to),
                value,
                ByteArray(0),
                privateKey.address(),
                accessList,
                null,
                chainId
            )
            signedTransactionRlpBytes = signedTransactionRlp.hexToByteArray()
            signedHashBytes = signedHash?.hexToByteArray() ?: ByteArray(0)
        }

        constructor(
            id: Int,
            nonce: String,
            value: String,
            maxPriorityFeePerGas: String,
            maxFeePerGas: String,
            gasLimit: String,
            to: String,
            accessList: Array<AccessList>? = null,
            signedTransactionRlp: String,
            signedHash: String?,
            chainId: ByteArray,
            pk: String
        ) : this(
            id,
            nonce.hexToBigInteger(),
            value.hexToBigInteger(),
            maxPriorityFeePerGas.hexToBigInteger(),
            maxFeePerGas.hexToBigInteger(),
            gasLimit.hexToBigInteger(),
            to,
            accessList,
            signedTransactionRlp,
            signedHash,
            chainId.toBigInteger(),
            pk
        )
    }
}
