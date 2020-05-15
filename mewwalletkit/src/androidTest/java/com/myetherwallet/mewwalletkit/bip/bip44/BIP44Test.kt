package com.myetherwallet.mewwalletkit.bip.bip44

import com.myetherwallet.mewwalletkit.core.extension.hexToByteArray
import com.myetherwallet.mewwalletkit.core.extension.toHexString
import org.junit.Assert
import org.junit.Test

/**
 * Created by BArtWell on 20.06.2019.
 */

class BIP44Test {

    private val testVectors = arrayOf(
        TestVector(
            "m",
            "72c0aae51ca3493f4190d004fac29497d31758038f9190ec8d140f95c93dcb578ead9bc0fff34f60dfc6d6c3101610a9fc6e93a4fdc6b1fedcbd9bb356c4683b",
            "xprv9s21ZrQH143K3WP92kxn8S141XX9XSZQzAC6gZ686hDL5A9uA5wY7wPZKjp8LiPqETNTrt6ngLaDTgZveMxYurRM9mQ1tmNoh2AMBqakHDF",
            "xpub661MyMwAqRbcFzTc8nVnVZwnZZMdvuHGMP7hUwVjf2kJwxV3hdFnfji3B2Xjp6jJ6Zfx7ei1BpZzTmNZvUqsmG8rdQLUD1n8UATRyEvmA5g"
        ),
        TestVector(
            "m/1000'",
            "72c0aae51ca3493f4190d004fac29497d31758038f9190ec8d140f95c93dcb578ead9bc0fff34f60dfc6d6c3101610a9fc6e93a4fdc6b1fedcbd9bb356c4683b",
            "xprv9usAGYdEMShKs6kjQmAbo43jEdb22isLotnD8XCPj5rZZsoinuBQfUWFZXTHftjdaXGR5MacwZQZdpzpwCtriGF9WZ9SsxC9sot7a2xtKMU",
            "xpub68rWg4A8BpFd5aqCWnhcABzTnfRWSBbCB7hovuc1HRPYSg8sLSVfDGpjQqk2rdyQwYPtw46ay8bzqNLF5GDUpYGEeqkBmoExiqSViVPmaSY"
        ),
        TestVector(
            "m/1000'/60'",
            "72c0aae51ca3493f4190d004fac29497d31758038f9190ec8d140f95c93dcb578ead9bc0fff34f60dfc6d6c3101610a9fc6e93a4fdc6b1fedcbd9bb356c4683b",
            "xprv9wzFYWHzddGewMeorS1xGfhqTs1TLfX4qZdYJrptezxYgGH3giRQzGnqXT6jpWZExdMT91o9wQLTXY3meRe9r2rFsxkXzYMvzDmzsQ6QaUo",
            "xpub6Aybx1ptTzpx9qjGxTYxdoea1tqwk8EvCnZ97FEWDLVXZ4cCEFjfY57KNjGkwjsz6pKLd8M4H7Gzy74v1EKrFuP9Mi2Wfr1DFwdWxhQ8wXK"
        ),
        TestVector(
            "m/1000'/60'/0'",
            "72c0aae51ca3493f4190d004fac29497d31758038f9190ec8d140f95c93dcb578ead9bc0fff34f60dfc6d6c3101610a9fc6e93a4fdc6b1fedcbd9bb356c4683b",
            "xprv9yemBFqGA3yNjVSDCzLBCFNb6Z8JN3b3pt8Mq6dQGDtgQiHHNfr4LSyKqa1o1VpmMLiQ6xiDVVC7rdWGwb2aVijiU6nkHp51ZRwLF8WN5RK",
            "xpub6Ce7amN9zRXfwyWgK1sBZPKKeaxnmWJuC73xdV31pZRfHWcRvDAJtFHogsqsF35zEUULtEdMBdYx5eDxNKRexzkKad9qKZz1LQ4rMPCwVDf"
        ),
        TestVector(
            "m/1000'/60'/0'/0",
            "72c0aae51ca3493f4190d004fac29497d31758038f9190ec8d140f95c93dcb578ead9bc0fff34f60dfc6d6c3101610a9fc6e93a4fdc6b1fedcbd9bb356c4683b",
            "xprvA1HVK3jGEwMBsUNQXaMnZcvbSA65RqLhsJH6H3SzUiCGWGUE4unAd7DqkE79v96hKii4nZCjAyLCB5nr2QSGYHZR86b8tWQFSuoEhfVgjay",
            "xpub6EGqiZGA5JuV5xSsdbtnvksKzBvZqJ4ZEXCh5Rrc33jFP4oNcT6RAuYKbTdjBq1criMSCa7eu1jyLnN5uXJ2zm3vUit2wpveMq3epPSG1sg"
        ),
        TestVector(
            "m/44'/60'",
            "72c0aae51ca3493f4190d004fac29497d31758038f9190ec8d140f95c93dcb578ead9bc0fff34f60dfc6d6c3101610a9fc6e93a4fdc6b1fedcbd9bb356c4683b",
            "xprv9woLTiR2TEd4XwEVbifW6neheGyDAp6Cz4rJKayujkRtaEKnUHaop6SwHSK6F2XJRmVizLV1y7q28Rh4eNRfcyLaxYrSRuQwzJBfhre2TeC",
            "xpub6AngsDwvHcBMkRJxhkCWTvbSCJohaGp4MHmu7yPXJ5xsT2ew1pu4MtmR8ipHbgqA7GBSbP1cL5mBgFj148fvP71BoUq9CgKSDrukELE1N2x"
        ),
        TestVector(
            "m/44'/60'/0'",
            "72c0aae51ca3493f4190d004fac29497d31758038f9190ec8d140f95c93dcb578ead9bc0fff34f60dfc6d6c3101610a9fc6e93a4fdc6b1fedcbd9bb356c4683b",
            "xprv9yAR5B1VhzoKEBewsGwP5hjSsAw6t8B7H3yyn4EeTwqgCMWKWqQSBtDb7vbpfKzaMjjwLpCEuoJmpVgNn8ebmpzCJxdtuyyeDxUr6WPcPLm",
            "xpub6C9mUgYPYNMcSfjQyJUPSqgBRCmbHatxeGuaaSeG2HNf59qU4NigjgY4yCkBSEJv49MdsZiTWsJLks95zFHgdanvWyNNKj2ogzebMysJDZr"
        ),
        TestVector(
            "m/44'/60'/0'/0",
            "72c0aae51ca3493f4190d004fac29497d31758038f9190ec8d140f95c93dcb578ead9bc0fff34f60dfc6d6c3101610a9fc6e93a4fdc6b1fedcbd9bb356c4683b",
            "xprvA1EfRiGuppNDAKbgn2M3XNFR2wKm9TXwyigok4LfSnKTYHtBghTTiBPDC6Ue8QsN1uAqAUjAxnFBsVW88B3p7K2tmoEec4oxbFUheVAmgRH",
            "xpub6EE1qDoofBvWNog9t3t3tWC9ayAFYvFoLwcQYSkH17rSR6DLEEmiFyhh3M7EjUEueb1nmHLDvVrEbK4rGGREXFAamNnvot2AdT7NHhUiS6R"
        ),
        TestVector(
            "m/44'",
            "72c0aae51ca3493f4190d004fac29497d31758038f9190ec8d140f95c93dcb578ead9bc0fff34f60dfc6d6c3101610a9fc6e93a4fdc6b1fedcbd9bb356c4683b",
            "xprv9usAGYdEMSgbqwf1wnTbiEG7SnenhfqiYE2EuFoXQjA6fm4vrHQZAu9JU6whjaeLSraaBRhLDaSRVTaKdbeaKuzKh4J5fwvwDYgiWoUo5ob",
            "xpub68rWg4A8BpEu4RjV3ozc5NCqzpVH78ZZuSwqheD8y4h5YZQ5PpioihTnKNRR6ZhExYyCwEHAuRKXFzS7f7XnzAd6WysipnL6WnszkCr3BWt"
        ),
        TestVector(
            "m/44'/60'",
            "72c0aae51ca3493f4190d004fac29497d31758038f9190ec8d140f95c93dcb578ead9bc0fff34f60dfc6d6c3101610a9fc6e93a4fdc6b1fedcbd9bb356c4683b",
            "xprv9woLTiR2TEd4XwEVbifW6neheGyDAp6Cz4rJKayujkRtaEKnUHaop6SwHSK6F2XJRmVizLV1y7q28Rh4eNRfcyLaxYrSRuQwzJBfhre2TeC",
            "xpub6AngsDwvHcBMkRJxhkCWTvbSCJohaGp4MHmu7yPXJ5xsT2ew1pu4MtmR8ipHbgqA7GBSbP1cL5mBgFj148fvP71BoUq9CgKSDrukELE1N2x"
        ),
        TestVector(
            "m/44'/60'/0'",
            "72c0aae51ca3493f4190d004fac29497d31758038f9190ec8d140f95c93dcb578ead9bc0fff34f60dfc6d6c3101610a9fc6e93a4fdc6b1fedcbd9bb356c4683b",
            "xprv9yAR5B1VhzoKEBewsGwP5hjSsAw6t8B7H3yyn4EeTwqgCMWKWqQSBtDb7vbpfKzaMjjwLpCEuoJmpVgNn8ebmpzCJxdtuyyeDxUr6WPcPLm",
            "xpub6C9mUgYPYNMcSfjQyJUPSqgBRCmbHatxeGuaaSeG2HNf59qU4NigjgY4yCkBSEJv49MdsZiTWsJLks95zFHgdanvWyNNKj2ogzebMysJDZr"
        ),
        TestVector(
            "m/44'/60'/0'/0",
            "72c0aae51ca3493f4190d004fac29497d31758038f9190ec8d140f95c93dcb578ead9bc0fff34f60dfc6d6c3101610a9fc6e93a4fdc6b1fedcbd9bb356c4683b",
            "xprvA1EfRiGuppNDAKbgn2M3XNFR2wKm9TXwyigok4LfSnKTYHtBghTTiBPDC6Ue8QsN1uAqAUjAxnFBsVW88B3p7K2tmoEec4oxbFUheVAmgRH",
            "xpub6EE1qDoofBvWNog9t3t3tWC9ayAFYvFoLwcQYSkH17rSR6DLEEmiFyhh3M7EjUEueb1nmHLDvVrEbK4rGGREXFAamNnvot2AdT7NHhUiS6R"
        ),
        TestVector(
            "m",
            "000102030405060708090a0b0c0d0e0f",
            "xprv9s21ZrQH143K3QTDL4LXw2F7HEK3wJUD2nW2nRk4stbPy6cq3jPPqjiChkVvvNKmPGJxWUtg6LnF5kejMRNNU3TGtRBeJgk33yuGBxrMPHi",
            "xpub661MyMwAqRbcFtXgS5sYJABqqG9YLmC4Q1Rdap9gSE8NqtwybGhePY2gZ29ESFjqJoCu1Rupje8YtGqsefD265TMg7usUDFdp6W1EGMcet8"
        ),
        TestVector(
            "m/0'",
            "000102030405060708090a0b0c0d0e0f",
            "xprv9uHRZZhk6KAJC1avXpDAp4MDc3sQKNxDiPvvkX8Br5ngLNv1TxvUxt4cV1rGL5hj6KCesnDYUhd7oWgT11eZG7XnxHrnYeSvkzY7d2bhkJ7",
            "xpub68Gmy5EdvgibQVfPdqkBBCHxA5htiqg55crXYuXoQRKfDBFA1WEjWgP6LHhwBZeNK1VTsfTFUHCdrfp1bgwQ9xv5ski8PX9rL2dZXvgGDnw"
        ),
        TestVector(
            "m/0'/1",
            "000102030405060708090a0b0c0d0e0f",
            "xprv9wTYmMFdV23N2TdNG573QoEsfRrWKQgWeibmLntzniatZvR9BmLnvSxqu53Kw1UmYPxLgboyZQaXwTCg8MSY3H2EU4pWcQDnRnrVA1xe8fs",
            "xpub6ASuArnXKPbfEwhqN6e3mwBcDTgzisQN1wXN9BJcM47sSikHjJf3UFHKkNAWbWMiGj7Wf5uMash7SyYq527Hqck2AxYysAA7xmALppuCkwQ"
        ),
        TestVector(
            "m/0'/1/2'",
            "000102030405060708090a0b0c0d0e0f",
            "xprv9z4pot5VBttmtdRTWfWQmoH1taj2axGVzFqSb8C9xaxKymcFzXBDptWmT7FwuEzG3ryjH4ktypQSAewRiNMjANTtpgP4mLTj34bhnZX7UiM",
            "xpub6D4BDPcP2GT577Vvch3R8wDkScZWzQzMMUm3PWbmWvVJrZwQY4VUNgqFJPMM3No2dFDFGTsxxpG5uJh7n7epu4trkrX7x7DogT5Uv6fcLW5"
        ),
        TestVector(
            "m/0'/1/2'/2",
            "000102030405060708090a0b0c0d0e0f",
            "xprvA2JDeKCSNNZky6uBCviVfJSKyQ1mDYahRjijr5idH2WwLsEd4Hsb2Tyh8RfQMuPh7f7RtyzTtdrbdqqsunu5Mm3wDvUAKRHSC34sJ7in334",
            "xpub6FHa3pjLCk84BayeJxFW2SP4XRrFd1JYnxeLeU8EqN3vDfZmbqBqaGJAyiLjTAwm6ZLRQUMv1ZACTj37sR62cfN7fe5JnJ7dh8zL4fiyLHV"
        ),
        TestVector(
            "m/0'/1/2'/2/1000000000",
            "000102030405060708090a0b0c0d0e0f",
            "xprvA41z7zogVVwxVSgdKUHDy1SKmdb533PjDz7J6N6mV6uS3ze1ai8FHa8kmHScGpWmj4WggLyQjgPie1rFSruoUihUZREPSL39UNdE3BBDu76",
            "xpub6H1LXWLaKsWFhvm6RVpEL9P4KfRZSW7abD2ttkWP3SSQvnyA8FSVqNTEcYFgJS2UaFcxupHiYkro49S8yGasTvXEYBVPamhGW6cFJodrTHy"
        ),
        TestVector(
            "m",
            "fffcf9f6f3f0edeae7e4e1dedbd8d5d2cfccc9c6c3c0bdbab7b4b1aeaba8a5a29f9c999693908d8a8784817e7b7875726f6c696663605d5a5754514e4b484542",
            "xprv9s21ZrQH143K31xYSDQpPDxsXRTUcvj2iNHm5NUtrGiGG5e2DtALGdso3pGz6ssrdK4PFmM8NSpSBHNqPqm55Qn3LqFtT2emdEXVYsCzC2U",
            "xpub661MyMwAqRbcFW31YEwpkMuc5THy2PSt5bDMsktWQcFF8syAmRUapSCGu8ED9W6oDMSgv6Zz8idoc4a6mr8BDzTJY47LJhkJ8UB7WEGuduB"
        ),
        TestVector(
            "m/0",
            "fffcf9f6f3f0edeae7e4e1dedbd8d5d2cfccc9c6c3c0bdbab7b4b1aeaba8a5a29f9c999693908d8a8784817e7b7875726f6c696663605d5a5754514e4b484542",
            "xprv9vHkqa6EV4sPZHYqZznhT2NPtPCjKuDKGY38FBWLvgaDx45zo9WQRUT3dKYnjwih2yJD9mkrocEZXo1ex8G81dwSM1fwqWpWkeS3v86pgKt",
            "xpub69H7F5d8KSRgmmdJg2KhpAK8SR3DjMwAdkxj3ZuxV27CprR9LgpeyGmXUbC6wb7ERfvrnKZjXoUmmDznezpbZb7ap6r1D3tgFxHmwMkQTPH"
        ),
        TestVector(
            "m/0/2147483647'",
            "fffcf9f6f3f0edeae7e4e1dedbd8d5d2cfccc9c6c3c0bdbab7b4b1aeaba8a5a29f9c999693908d8a8784817e7b7875726f6c696663605d5a5754514e4b484542",
            "xprv9wSp6B7kry3Vj9m1zSnLvN3xH8RdsPP1Mh7fAaR7aRLcQMKTR2vidYEeEg2mUCTAwCd6vnxVrcjfy2kRgVsFawNzmjuHc2YmYRmagcEPdU9",
            "xpub6ASAVgeehLbnwdqV6UKMHVzgqAG8Gr6riv3Fxxpj8ksbH9ebxaEyBLZ85ySDhKiLDBrQSARLq1uNRts8RuJiHjaDMBU4Zn9h8LZNnBC5y4a"
        ),
        TestVector(
            "m/0/2147483647'/1",
            "fffcf9f6f3f0edeae7e4e1dedbd8d5d2cfccc9c6c3c0bdbab7b4b1aeaba8a5a29f9c999693908d8a8784817e7b7875726f6c696663605d5a5754514e4b484542",
            "xprv9zFnWC6h2cLgpmSA46vutJzBcfJ8yaJGg8cX1e5StJh45BBciYTRXSd25UEPVuesF9yog62tGAQtHjXajPPdbRCHuWS6T8XA2ECKADdw4Ef",
            "xpub6DF8uhdarytz3FWdA8TvFSvvAh8dP3283MY7p2V4SeE2wyWmG5mg5EwVvmdMVCQcoNJxGoWaU9DCWh89LojfZ537wTfunKau47EL2dhHKon"
        ),
        TestVector(
            "m/0/2147483647'/1/2147483646'",
            "fffcf9f6f3f0edeae7e4e1dedbd8d5d2cfccc9c6c3c0bdbab7b4b1aeaba8a5a29f9c999693908d8a8784817e7b7875726f6c696663605d5a5754514e4b484542",
            "xprvA1RpRA33e1JQ7ifknakTFpgNXPmW2YvmhqLQYMmrj4xJXXWYpDPS3xz7iAxn8L39njGVyuoseXzU6rcxFLJ8HFsTjSyQbLYnMpCqE2VbFWc",
            "xpub6ERApfZwUNrhLCkDtcHTcxd75RbzS1ed54G1LkBUHQVHQKqhMkhgbmJbZRkrgZw4koxb5JaHWkY4ALHY2grBGRjaDMzQLcgJvLJuZZvRcEL"
        ),
        TestVector(
            "m/0/2147483647'/1/2147483646'/2",
            "fffcf9f6f3f0edeae7e4e1dedbd8d5d2cfccc9c6c3c0bdbab7b4b1aeaba8a5a29f9c999693908d8a8784817e7b7875726f6c696663605d5a5754514e4b484542",
            "xprvA2nrNbFZABcdryreWet9Ea4LvTJcGsqrMzxHx98MMrotbir7yrKCEXw7nadnHM8Dq38EGfSh6dqA9QWTyefMLEcBYJUuekgW4BYPJcr9E7j",
            "xpub6FnCn6nSzZAw5Tw7cgR9bi15UV96gLZhjDstkXXxvCLsUXBGXPdSnLFbdpq8p9HmGsApME5hQTZ3emM2rnY5agb9rXpVGyy3bdW6EEgAtqt"
        ),
        TestVector(
            "m",
            "4b381541583be4423346c643850da4b320e46a87ae3d2a4e6da11eba819cd4acba45d239319ac14f863b8d5ab5a0d0c64d2e8a1e7d1457df2e5a3c51c73235be",
            "xprv9s21ZrQH143K25QhxbucbDDuQ4naNntJRi4KUfWT7xo4EKsHt2QJDu7KXp1A3u7Bi1j8ph3EGsZ9Xvz9dGuVrtHHs7pXeTzjuxBrCmmhgC6",
            "xpub661MyMwAqRbcEZVB4dScxMAdx6d4nFc9nvyvH3v4gJL378CSRZiYmhRoP7mBy6gSPSCYk6SzXPTf3ND1cZAceL7SfJ1Z3GC8vBgp2epUt13"
        ),
        TestVector(
            "m/0'",
            "4b381541583be4423346c643850da4b320e46a87ae3d2a4e6da11eba819cd4acba45d239319ac14f863b8d5ab5a0d0c64d2e8a1e7d1457df2e5a3c51c73235be",
            "xprv9uPDJpEQgRQfDcW7BkF7eTya6RPxXeJCqCJGHuCJ4GiRVLzkTXBAJMu2qaMWPrS7AANYqdq6vcBcBUdJCVVFceUvJFjaPdGZ2y9WACViL4L",
            "xpub68NZiKmJWnxxS6aaHmn81bvJeTESw724CRDs6HbuccFQN9Ku14VQrADWgqbhhTHBaohPX4CjNLf9fq9MYo6oDaPPLPxSb7gwQN3ih19Zm4Y"
        )
    )

    @Test
    fun shouldDeriveCorrectPrivateKeys() {
        for (vector in testVectors) {
            println("Path " + vector.path)
            println("Seed " + vector.seedHex)

            val masterKey = PrivateKey.createWithSeed(vector.seed, Network.BITCOIN)
            val derivationNodes = vector.path.derivationPath()

            val derivedPrivateKey = masterKey.derived(derivationNodes)
            val derivedPublicKey = derivedPrivateKey?.publicKey(true)

            Assert.assertEquals(
                "Derivation path: (" + vector.path + "), seed: (" + vector.seed.toHexString() + ")",
                derivedPrivateKey?.extended(),
                vector.privateKey
            )
            Assert.assertEquals(
                "Derivation path: (" + vector.path + "), seed: (" + vector.seed.toHexString() + ")",
                derivedPublicKey?.extended(),
                vector.publicKey
            )
        }
    }

    private data class TestVector(
        val path: String,
        val seedHex: String,
        val privateKey: String,
        val publicKey: String
    ) {
        val seed: ByteArray by lazy { seedHex.hexToByteArray() }
    }
}