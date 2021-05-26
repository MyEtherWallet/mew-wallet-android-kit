package com.myetherwallet.mewwalletkit.bip.bip44

import java.math.BigInteger

/**
 * Created by BArtWell on 22.05.2019.
 */

@Suppress("ClassName")
sealed class Network(val title: String, val path: String, val chainId: BigInteger, val symbol: String = "") {

    constructor(title: String, path: String, chainId: Int, symbol: String = "") : this(title, path, chainId.toBigInteger(), symbol)

    object BITCOIN : Network("Bitcoin", "m/44'/0'/0'/0", 0, "btc")
    object LITECOIN : Network("Litecoin", "m/44'/2'/0'/0", 2)
    object SINGULAR_DTV : Network("SingularDTV", "m/0'/0'/0'", 0)
    object ROPSTEN : Network("Ropsten", "m/44'/1'/0'/0", 3, "rop")
    object EXPANSE : Network("Expanse", "m/44'/40'/0'/0", 40)
    object LEDGER_LIVE_ETHEREUM : Network("Ethereum - Ledger Live", "m/44'/60'", 60)
    object KEEPKEY_ETHEREUM : Network("Ethereum", "m/44'/60'", 60)
    object LEDGER_ETHEREUM : Network("Ethereum", "m/44'/60'/0", 60)
    object ETHEREUM : Network("Ethereum", "m/44'/60'/0'/0", 1, "eth")
    object LEDGER_ETHEREUM_CLASSIC : Network("Ethereum Classic", "m/44'/60'/160720'/0", 60)
    object LEDGER_ETHEREUM_CLASSIC_VINTAGE : Network("Ethereum Classic MEW Vintage", "m/44'/60'/160720'/0'", 60)
    object LEDGER_LIVE_ETHEREUM_CLASSIC : Network("Ethereum Classic - Ledger Live", "m/44'/61'", 44)
    object KEEPKEY_ETHEREUM_CLASSIC : Network("Ethereum Classic", "m/44'/61'", 44)
    object ETHEREUM_CLASSIC : Network("Ethereum Classic", "m/44'/61'/0'/0", 61)
    object MIX_BLOCKCHAIN : Network("Mix Blockchain", "m/44'/76'/0'/0", 76)
    object UBIQ : Network("Ubiq", "m/44'/108'/0'/0", 108)
    object RSK_MAINNET : Network("RSK Mainnet", "m/44'/137'/0'/0", 137)
    object ELLAISM : Network("Ellaism", "m/44'/163'/0'/0", 163)
    object PIRL : Network("PIRL", "m/44'/164'/0'/0", 164)
    object MUSICOIN : Network("Musicoin", "m/44'/184'/0'/0", 184)
    object CALLISTO : Network("Callisto", "m/44'/820'/0'/0", 820)
    object TOMO_CHAIN : Network("TomoChain", "m/44'/889'/0'/0", 889)
    object THUNDERCORE : Network("ThunderCore", "m/44'/1001'/0'/0", 1001)
    object ETHEREUM_SOCIAL : Network("Ethereum Social", "m/44'/1128'/0'/0", 1128)
    object ATHEIOS : Network("Atheios", "m/44'/1620'/0'/0", 1620)
    object ETHER_GEM : Network("EtherGem", "m/44'/1987'/0'/0", 1987)
    object EOS_CLASSIC : Network("EOS Classic", "m/44'/2018'/0'/0", 2018)
    object GO_CHAIN : Network("GoChain", "m/44'/6060'/0'/0", 6060)
    object ETHER_SOCIAL_NETWORK : Network("EtherSocial Network", "m/44'/31102'/0'/0", 31102)
    object RSK_TESTNET : Network("RSK Testnet", "m/44'/37310'/0'/0", 37310)
    object AKROMA : Network("Akroma", "m/44'/200625'/0'/0", 200625)
    object IOLITE : Network("Iolite", "m/44'/1171337'/0'/0", 1171337)
    object ETHER1 : Network("Ether-1", "m/44'/1313114'/0'/0", 1313114)
    object GOERLI : Network("Goerli", "m/44'/60'/0'/0", 5)
    class CUSTOM(title: String, path: String, chainId: Int) : Network(title, path, chainId)

    object ANONYMIZED_ID : Network("Ethereum", "m/1000'/60'/0'/0", 1)

    fun wifPrefix() =
        when (this) {
            BITCOIN -> "0x80"
            LITECOIN -> "0xB0"
            else -> null
        }

    fun publicKeyHash() =
        when (this) {
            BITCOIN -> 0x00
            LITECOIN -> 0x30
            else -> 0x00
        }.toByte()

    fun addressPrefix() =
        when (this) {
            BITCOIN -> ""
            ETHEREUM, ROPSTEN -> "0x"
            else -> "0x"
        }

    fun alphabet() =
        when (this) {
            BITCOIN -> "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"
            else -> null
        }

    fun privateKeyPrefix() =
        when (this) {
            BITCOIN -> 0x0488ADE4
            ROPSTEN -> 0x04358394
            else -> 0
        }

    fun publicKeyPrefix() =
        when (this) {
            BITCOIN -> 0x0488b21e
            ROPSTEN -> 0x043587cf
            else -> 0
        }

    fun publicKeyCompressed() =
        when (this) {
            BITCOIN, LITECOIN -> true
            ETHEREUM, ROPSTEN -> false
            else -> false
        }

    companion object {
        fun findByChaidId(chainId: BigInteger?): Network? {
            for (sealedSubclass in Network::class.sealedSubclasses) {
                if (sealedSubclass is Network && sealedSubclass.chainId == chainId) {
                    return sealedSubclass
                }
            }
            return null
        }
    }
}
