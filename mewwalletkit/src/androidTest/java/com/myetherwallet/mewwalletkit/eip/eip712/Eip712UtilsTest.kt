package com.myetherwallet.mewwalletkit.eip.eip712

import com.myetherwallet.mewwalletkit.core.extension.*
import org.junit.Assert
import org.junit.Test

/**
 * Created by BArtWell on 09.04.2021.
 */

class Eip712UtilsTest {

    private val typedMessage = """
        {
   "types":{
      "EIP712Domain":[
         {
            "name":"name",
            "type":"string"
         },
         {
            "name":"version",
            "type":"string"
         },
         {
            "name":"chainId",
            "type":"uint256"
         },
         {
            "name":"verifyingContract",
            "type":"address"
         }
      ],
      "Person":[
         {
            "name":"name",
            "type":"string"
         },
         {
            "name":"wallet",
            "type":"address"
         }
      ],
      "Mail":[
         {
            "name":"from",
            "type":"Person"
         },
         {
            "name":"to",
            "type":"Person"
         },
         {
            "name":"contents",
            "type":"string"
         }
      ]
   },
   "primaryType":"Mail",
   "domain":{
      "name":"Ether Mail",
      "version":"1",
      "chainId":1,
      "verifyingContract":"0xCcCCccccCCCCcCCCCCCcCcCccCcCCCcCcccccccC"
   },
   "message":{
      "from":{
         "name":"Cow",
         "wallet":"0xCD2a3d9F938E13CD947Ec05AbC7FE734Df8DD826"
      },
      "to":{
         "name":"Bob",
         "wallet":"0xbBbBBBBbbBBBbbbBbbBbbbbBBbBbbbbBbBbbBBbB"
      },
      "contents":"Hello, Bob!"
   }
}
            """

    private val typedMessageWithBytesV3 = """
        {
   "types":{
      "EIP712Domain":[
         {
            "name":"name",
            "type":"string"
         },
         {
            "name":"version",
            "type":"string"
         },
         {
            "name":"chainId",
            "type":"uint256"
         },
         {
            "name":"verifyingContract",
            "type":"address"
         }
      ],
      "Bet":[
         {
            "name":"roundId",
            "type":"uint32"
         },
         {
            "name":"gameType",
            "type":"uint8"
         },
         {
            "name":"number",
            "type":"uint256"
         },
         {
            "name":"value",
            "type":"uint256"
         },
         {
            "name":"balance",
            "type":"int256"
         },
         {
            "name":"serverHash",
            "type":"bytes32"
         },
         {
            "name":"userHash",
            "type":"bytes32"
         },
         {
            "name":"gameId",
            "type":"uint256"
         }
      ]
   },
   "primaryType":"Bet",
   "domain":{
      "name":"Dicether",
      "version":"2",
      "chainId":1,
      "verifyingContract":"0xaEc1F783B29Aab2727d7C374Aa55483fe299feFa"
   },
   "message":{
        "roundId": 1,
        "gameType": 4,
        "num": 1,
        "value": "320000000000000",
        "balance": "-640000000000000",
        "serverHash": "0x4ed3c2d4c6acd062a3a61add7ecdb2fcfd988d944ba18e52a0b0d912d7a43cf4",
        "userHash": "0x6901562dd98a823e76140dc8728eca225174406eaa6bf0da7b0ab67f6f93de4d",
        "gameId": 2393,
        "number": 1
   }
}
            """

    private val typedMessageV4 = """
        {
   "types":{
      "EIP712Domain":[
         {
            "name":"name",
            "type":"string"
         },
         {
            "name":"version",
            "type":"string"
         },
         {
            "name":"chainId",
            "type":"uint256"
         },
         {
            "name":"verifyingContract",
            "type":"address"
         }
      ],
      "Person":[
         {
            "name":"name",
            "type":"string"
         },
         {
            "name":"wallets",
            "type":"address[]"
         }
      ],
      "Mail":[
         {
            "name":"from",
            "type":"Person"
         },
         {
            "name":"to",
            "type":"Person[]"
         },
         {
            "name":"contents",
            "type":"string"
         }
      ],
      "Group":[
         {
            "name":"name",
            "type":"string"
         },
         {
            "name":"members",
            "type":"Person[]"
         }
      ]
   },
   "primaryType":"Mail",
   "domain":{
      "name":"Ether Mail",
      "version":"1",
      "chainId":1,
      "verifyingContract":"0xCcCCccccCCCCcCCCCCCcCcCccCcCCCcCcccccccC"
   },
   "message":{
            "from": {
                "name": "Cow",
                "wallets": [
                    "0xCD2a3d9F938E13CD947Ec05AbC7FE734Df8DD826",
                    "0xDeaDbeefdEAdbeefdEadbEEFdeadbeEFdEaDbeeF"
                ]
            },
            "to": [
                {
                    "name": "Bob",
                    "wallets": [
                        "0xbBbBBBBbbBBBbbbBbbBbbbbBBbBbbbbBbBbbBBbB",
                        "0xB0BdaBea57B0BDABeA57b0bdABEA57b0BDabEa57",
                        "0xB0B0b0b0b0b0B000000000000000000000000000"
                    ]
                }
            ],
            "contents": "Hello, Bob!"
        }
}
            """

    private val typedMessageCorrupted = """       
{
   "types":{
      "EIP712Domain":[
         {
            "name":"name",
            "type":"string"
         },
         {
            "name":"version",
            "type":"string"
         },
         {
            "name":"chainId",
            "type":"uint256"
         },
         {
            "name":"verifyingContract",
            "type":"address"
         }
      ],
      "Person":[
         {
            "name":"name",
            "type":"string"
         },
         {
            "name":"wallet",
            "type":"address"
         }
      ],
      "Mail":[
         {
            "name":"from",
            "type":"Person"
         },
         {
            "name":"to",
            "type":"Person"
         },
         {
            "name":"contents",
            "type":"string"
         }
      ]
   },
   "primaryType":"Mail",
   "domain":{
      "name":"Ether Mail",
      "version":"1",
      "chainId":1,
      "verifyingContract":"0xCcCCccccCCCCcCCCCCCcCcCccCcCCCcCcccccccC"
   },
   "message":{
      "sender":{
         "name":"Cow",
         "wallet":"0xCD2a3d9F938E13CD947Ec05AbC7FE734Df8DD826"
      },
      "recipient":{
         "name":"Bob",
         "wallet":"0xbBbBBBBbbBBBbbbBbbBbbbbBBbBbbbbBbBbbBBbB"
      },
      "contents":"Hello, Bob!"
   }
}                     
    """

    val rarible1 = """
        {
          "types": {
            "EIP712Domain": [
              {
                "type": "string",
                "name": "name"
              },
              {
                "type": "string",
                "name": "version"
              },
              {
                "type": "uint256",
                "name": "chainId"
              },
              {
                "type": "address",
                "name": "verifyingContract"
              }
            ],
            "Part": [
              {
                "name": "account",
                "type": "address"
              },
              {
                "name": "value",
                "type": "uint96"
              }
            ],
            "Mint721": [
              {
                "name": "tokenId",
                "type": "uint256"
              },
              {
                "name": "tokenURI",
                "type": "string"
              },
              {
                "name": "creators",
                "type": "Part[]"
              },
              {
                "name": "royalties",
                "type": "Part[]"
              }
            ]
          },
          "domain": {
            "name": "Mint721",
            "version": "1",
            "chainId": 3,
            "verifyingContract": "0xB0EA149212Eb707a1E5FC1D2d3fD318a8d94cf05"
          },
          "primaryType": "Mint721",
          "message": {
            "tokenId": "16931694090904938262317193072096647983503975066464051000845489401260150882311",
            "uri": "/ipfs/QmWLsBu6nS4ovaHbGAXprD1qEssJu4r5taQfB74sCG51tp",
            "creators": [
              {
                "account": "0x256eFfCeA2ab308D31e318728D2615545171d85B",
                "value": "10000"
              }
            ],
            "royalties": [],
            "tokenURI": "/ipfs/QmWLsBu6nS4ovaHbGAXprD1qEssJu4r5taQfB74sCG51tp"
          }
        }
    """

    @Suppress("MaxLineLength")
    val rarible2 = """
       {
  "types": {
    "EIP712Domain": [
      {
        "type": "string",
        "name": "name"
      },
      {
        "type": "string",
        "name": "version"
      },
      {
        "type": "uint256",
        "name": "chainId"
      },
      {
        "type": "address",
        "name": "verifyingContract"
      }
    ],
    "AssetType": [
      {
        "name": "assetClass",
        "type": "bytes4"
      },
      {
        "name": "data",
        "type": "bytes"
      }
    ],
    "Asset": [
      {
        "name": "assetType",
        "type": "AssetType"
      },
      {
        "name": "value",
        "type": "uint256"
      }
    ],
    "Order": [
      {
        "name": "maker",
        "type": "address"
      },
      {
        "name": "makeAsset",
        "type": "Asset"
      },
      {
        "name": "taker",
        "type": "address"
      },
      {
        "name": "takeAsset",
        "type": "Asset"
      },
      {
        "name": "salt",
        "type": "uint256"
      },
      {
        "name": "start",
        "type": "uint256"
      },
      {
        "name": "end",
        "type": "uint256"
      },
      {
        "name": "dataType",
        "type": "bytes4"
      },
      {
        "name": "data",
        "type": "bytes"
      }
    ]
  },
  "domain": {
    "name": "Exchange",
    "version": "2",
    "chainId": 3,
    "verifyingContract": "0x33aef288c093bf7b36fbe15c3190e616a993b0ad"
  },
  "primaryType": "Order",
  "message": {
    "maker": "0x19d2a55f2bd362a9e09f674b722782329f63f3fb",
    "makeAsset": {
      "assetType": {
        "assetClass": "0xd8f960c1",
        "data": "0x000000000000000000000000b0ea149212eb707a1e5fc1d2d3fd318a8d94cf05000000000000000000000000000000000000000000000000000000000000004019d2a55f2bd362a9e09f674b722782329f63f3fb00000000000000000000000400000000000000000000000000000000000000000000000000000000000000a000000000000000000000000000000000000000000000000000000000000001000000000000000000000000000000000000000000000000000000000000000160000000000000000000000000000000000000000000000000000000000000018000000000000000000000000000000000000000000000000000000000000000342f697066732f516d574c734275366e53346f7661486247415870724431714573734a753472357461516642373473434735317470000000000000000000000000000000000000000000000000000000000000000000000000000000000000000100000000000000000000000019d2a55f2bd362a9e09f674b722782329f63f3fb0000000000000000000000000000000000000000000000000000000000002710000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000010000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000004191d1c7799f97886dcfa79f97d3372494c032fd3dd9917521383331a641635a174ec897403f9674ae25cd61ab5764ef3542e5e2249d4d9c8a8ec4a3da0b5216d21c00000000000000000000000000000000000000000000000000000000000000"
      },
      "value": "1"
    },
    "taker": "0x0000000000000000000000000000000000000000",
    "takeAsset": {
      "assetType": {
        "assetClass": "0xaaaebeba",
        "data": "0x"
      },
      "value": "10000000000000000"
    },
    "salt": "9253",
    "start": 0,
    "end": 0,
    "dataType": "0x4c234266",
    "data": "0x0000000000000000000000000000000000000000000000000000000000000040000000000000000000000000000000000000000000000000000000000000006000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"
  }
} 
    """

    @Test
    fun shouldCorrectlyHashTheTypedMessage() {
        Assert.assertEquals("0xbe609aee343fb3c4b28e1df9e632fca64fcfaede20f02e86244efddf30957bd2", Eip712Utils.getHash(typedMessage).toHexString().addHexPrefix())
    }

    @Test
    fun shouldCorrectlySignTheTypedMessage() {
        val expected = "0x4355c47d63924e8a72e509b65029052eb6c299d53a04e167c5775fd466751c9d07299936d304c153f6443dfa05f40ff007d72911b6f72307f996231605b915621c"
        val privateKey = "cow".toByteArray().keccak256()
        val signature = hashAndSign(typedMessage, privateKey)
        Assert.assertEquals(expected, signature)
    }

    @Test
    fun testRarible1() {
        val expected = "0xfbd15209f31f35061a6fa7fdfe541e5335b0d1d2367815c7e67caaeb4e6969483490718a48f7e899f326724ccb8351e9b5cbf1827083375f6f43d82fc3d4b0821b"
        val privateKey = "cow".toByteArray().keccak256()
        val signature = hashAndSign(rarible1, privateKey)
        Assert.assertEquals(expected, signature)
    }

    @Test
    fun testRarible2() {
        val expected = "0x3de0bc4f4300910765af3103d37a34fd35058fbd920ba43d0e40b9630bf49d5f24f844b1027022a91d4e24eab6d091c8ec3578a783589b4833d58bc26ea88fc41b"
        val privateKey = "cow".toByteArray().keccak256()
        val signature = hashAndSign(rarible2, privateKey)
        Assert.assertEquals(expected, signature)
    }

    @Test
    fun shouldCorrectlySignTheTypedMessageWithBytes() {
        val expected = "0xaf453442075953aced4d54ccf5773a486fd03d9c5a853a1163594209ba1c637409acf82e335e464487c375c4438e1bfc56725fd039255d4ddf33f7fcdafebe931b"
        val privateKey = "0x5a2ca5de56191208ba8f8d230c29fa2b0d93226743eb00f2fb7a33c9b3305edf"
        val signature = hashAndSign(typedMessageWithBytesV3, privateKey)
        Assert.assertEquals(expected, signature)
    }

    @Test
    fun shouldCorrectlySignTheTypedCorruptedMessage() {
        val expected = "0x68eb5fdd7e3fe79c2efcc75878f602b1a487c8d5c59bfaf3ced9422987f0f6854dbc7f72bc352e4176fd76bec935ec2e371318c73e855727ef2f36c3aeab6d921b"
        val privateKey = "0x5a2ca5de56191208ba8f8d230c29fa2b0d93226743eb00f2fb7a33c9b3305edf"
        val signature = hashAndSign(typedMessageCorrupted, privateKey)
        Assert.assertEquals(expected, signature)
    }

    @Test
    fun shouldCorrectlySignTheTypedMessageV4() {
        val expected = "0x65cbd956f2fae28a601bebc9b906cea0191744bd4c4247bcd27cd08f8eb6b71c78efdf7a31dc9abee78f492292721f362d296cf86b4538e07b51303b67f749061b"
        val privateKey = "cow".toByteArray().keccak256()
        val signature = hashAndSign(typedMessageV4, privateKey)
        Assert.assertEquals(expected, signature)
    }

    private fun hashAndSign(json: String, privateKeyHex: String) = hashAndSign(json, privateKeyHex.hexToByteArray())

    private fun hashAndSign(json: String, privateKey: ByteArray): String {
        val signature = Eip712Utils.getHash(json).secp256k1RecoverableSign(privateKey)
        val serialized = signature?.secp256k1SerializeSignature()!!
        val rs = serialized.copyOfRange(0, 64)
        val v = (serialized[64] + 27).toByte()
        val normalized = rs + v
        return normalized.toHexString().addHexPrefix()
    }
}
