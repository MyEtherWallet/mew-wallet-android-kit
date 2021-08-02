package org.bitcoin

import org.bitcoin.NativeSecp256k1Util.AssertFailException
import org.bitcoin.NativeSecp256k1Util.assertEquals
import java.math.BigInteger
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.locks.ReentrantReadWriteLock

object NativeSecp256k1 {

    private val rwl = ReentrantReadWriteLock()
    private val r = rwl.readLock()
    private val w = rwl.writeLock()
    private val nativeECDSABuffer = ThreadLocal<ByteBuffer>()

    /**
     * Verifies the given secp256k1 signature in native code.
     *
     * @param data      The data which was signed, must be exactly 32 bytes
     * @param signature The signature
     * @param pub       The public key which did the signing
     */
    fun verify(data: ByteArray, signature: ByteArray, pub: ByteArray): Boolean {
        if (data.size != 32 || signature.size > 520 || pub.size > 520) {
            throw IllegalArgumentException()
        }

        var byteBuff = nativeECDSABuffer.get()
        if (byteBuff == null || byteBuff.capacity() < 520) {
            byteBuff = ByteBuffer.allocateDirect(520)
            byteBuff!!.order(ByteOrder.nativeOrder())
            nativeECDSABuffer.set(byteBuff)
        }
        byteBuff.rewind()
        byteBuff.put(data)
        byteBuff.put(signature)
        byteBuff.put(pub)

        r.lock()
        try {
            return secp256k1_ecdsa_verify(byteBuff, Secp256k1Context.context, signature.size, pub.size) == 1
        } finally {
            r.unlock()
        }
    }

    /**
     * libsecp256k1 Create an ECDSA signature.
     *
     * @param data Message hash, 32 bytes
     * @param sec  Secret key, 32 bytes
     *
     * @return byte array of signature
     */
    @Throws(AssertFailException::class)
    fun sign(data: ByteArray, sec: ByteArray): ByteArray {
        if (data.size != 32 || sec.size > 32) {
            throw IllegalArgumentException()
        }

        var byteBuff = nativeECDSABuffer.get()
        if (byteBuff == null || byteBuff.capacity() < 32 + 32) {
            byteBuff = ByteBuffer.allocateDirect(32 + 32)
            byteBuff!!.order(ByteOrder.nativeOrder())
            nativeECDSABuffer.set(byteBuff)
        }
        byteBuff.rewind()
        byteBuff.put(data)
        byteBuff.put(sec)

        val retByteArray: Array<ByteArray>

        r.lock()
        try {
            retByteArray = secp256k1_ecdsa_sign(byteBuff, Secp256k1Context.context)
        } finally {
            r.unlock()
        }

        val sigArr = retByteArray[0]
        val sigLen = BigInteger(byteArrayOf(retByteArray[1][0])).toInt()
        val retVal = BigInteger(byteArrayOf(retByteArray[1][1])).toInt()

        assertEquals(sigArr.size, sigLen, "Got bad signature length.")

        return if (retVal == 0) ByteArray(0) else sigArr
    }

    /**
     * libsecp256k1 Seckey Verify - returns 1 if valid, 0 if invalid
     *
     * @param seckey ECDSA Secret key, 32 bytes
     */
    fun secKeyVerify(seckey: ByteArray): Boolean {
        if (seckey.size != 32) {
            throw IllegalArgumentException()
        }

        var byteBuff = nativeECDSABuffer.get()
        if (byteBuff == null || byteBuff.capacity() < seckey.size) {
            byteBuff = ByteBuffer.allocateDirect(seckey.size)
            byteBuff!!.order(ByteOrder.nativeOrder())
            nativeECDSABuffer.set(byteBuff)
        }
        byteBuff.rewind()
        byteBuff.put(seckey)

        r.lock()
        try {
            return secp256k1_ec_seckey_verify(byteBuff, Secp256k1Context.context) == 1
        } finally {
            r.unlock()
        }
    }


    /**
     * libsecp256k1 Compute Pubkey - computes public key from secret key
     *
     * @param seckey ECDSA Secret key, 32 bytes
     *
     * @return  pubkey ECDSA Public key, 33 or 65 bytes
     */
    @Throws(AssertFailException::class)
    fun computePublicKey(seckey: ByteArray): ByteArray? {
        if (seckey.size != 32) {
            throw IllegalArgumentException()
        }

        var byteBuff = nativeECDSABuffer.get()
        if (byteBuff == null || byteBuff.capacity() < seckey.size) {
            byteBuff = ByteBuffer.allocateDirect(seckey.size)
            byteBuff!!.order(ByteOrder.nativeOrder())
            nativeECDSABuffer.set(byteBuff)
        }
        byteBuff.rewind()
        byteBuff.put(seckey)

        r.lock()
        try {
            return secp256k1_ec_pubkey_create(byteBuff, Secp256k1Context.context)
        } finally {
            r.unlock()
        }
    }

    /**
     * libsecp256k1 Cleanup - This destroys the secp256k1 context object
     * This should be called at the end of the program for proper cleanup of the context.
     */
    @Synchronized
    fun cleanup() {
        w.lock()
        try {
            secp256k1_destroy_context(Secp256k1Context.context)
        } finally {
            w.unlock()
        }
    }

    fun cloneContext(): Long {
        r.lock()
        try {
            return secp256k1_ctx_clone(Secp256k1Context.context)
        } finally {
            r.unlock()
        }
    }

    /**
     * libsecp256k1 PrivKey Tweak-Mul - Tweak privkey by multiplying to it
     *
     * @param tweak  some bytes to tweak with
     * @param privkey 32-byte seckey
     */
    @Throws(AssertFailException::class)
    fun privKeyTweakMul(privkey: ByteArray, tweak: ByteArray): ByteArray {
        if (privkey.size != 32) {
            throw IllegalArgumentException()
        }

        var byteBuff = nativeECDSABuffer.get()
        if (byteBuff == null || byteBuff.capacity() < privkey.size + tweak.size) {
            byteBuff = ByteBuffer.allocateDirect(privkey.size + tweak.size)
            byteBuff!!.order(ByteOrder.nativeOrder())
            nativeECDSABuffer.set(byteBuff)
        }
        byteBuff.rewind()
        byteBuff.put(privkey)
        byteBuff.put(tweak)

        val retByteArray: Array<ByteArray>
        r.lock()
        try {
            retByteArray = secp256k1_privkey_tweak_mul(byteBuff, Secp256k1Context.context)
        } finally {
            r.unlock()
        }

        val privArr = retByteArray[0]

        val privLen = BigInteger(byteArrayOf(retByteArray[1][0])).toInt() and 0xFF
        val retVal = BigInteger(byteArrayOf(retByteArray[1][1])).toInt()

        assertEquals(privArr.size, privLen, "Got bad pubkey length.")

        assertEquals(retVal, 1, "Failed return value check.")

        return privArr
    }

    /**
     * libsecp256k1 PrivKey Tweak-Add - Tweak privkey by adding to it
     *
     * @param tweak  some bytes to tweak with
     * @param privkey 32-byte seckey
     */
    @Throws(AssertFailException::class)
    fun privKeyTweakAdd(privkey: ByteArray, tweak: ByteArray): ByteArray {
        if (privkey.size != 32) {
            throw IllegalArgumentException()
        }

        var byteBuff = nativeECDSABuffer.get()
        if (byteBuff == null || byteBuff.capacity() < privkey.size + tweak.size) {
            byteBuff = ByteBuffer.allocateDirect(privkey.size + tweak.size)
            byteBuff!!.order(ByteOrder.nativeOrder())
            nativeECDSABuffer.set(byteBuff)
        }
        byteBuff.rewind()
        byteBuff.put(privkey)
        byteBuff.put(tweak)

        val retByteArray: Array<ByteArray>
        r.lock()
        try {
            retByteArray = secp256k1_privkey_tweak_add(byteBuff, Secp256k1Context.context)
        } finally {
            r.unlock()
        }

        val privArr = retByteArray[0]

        val privLen = BigInteger(byteArrayOf(retByteArray[1][0])).toInt() and 0xFF
        val retVal = BigInteger(byteArrayOf(retByteArray[1][1])).toInt()

        assertEquals(privArr.size, privLen, "Got bad pubkey length.")

        assertEquals(retVal, 1, "Failed return value check.")

        return privArr
    }

    /**
     * libsecp256k1 PubKey Tweak-Add - Tweak pubkey by adding to it
     *
     * @param tweak  some bytes to tweak with
     * @param pubkey 32-byte seckey
     */
    @Throws(AssertFailException::class)
    fun pubKeyTweakAdd(pubkey: ByteArray, tweak: ByteArray): ByteArray {
        if (pubkey.size != 33 && pubkey.size != 65) {
            throw IllegalArgumentException()
        }

        var byteBuff = nativeECDSABuffer.get()
        if (byteBuff == null || byteBuff.capacity() < pubkey.size + tweak.size) {
            byteBuff = ByteBuffer.allocateDirect(pubkey.size + tweak.size)
            byteBuff!!.order(ByteOrder.nativeOrder())
            nativeECDSABuffer.set(byteBuff)
        }
        byteBuff.rewind()
        byteBuff.put(pubkey)
        byteBuff.put(tweak)

        val retByteArray: Array<ByteArray>
        r.lock()
        try {
            retByteArray = secp256k1_pubkey_tweak_add(byteBuff, Secp256k1Context.context, pubkey.size)
        } finally {
            r.unlock()
        }

        val pubArr = retByteArray[0]

        val pubLen = BigInteger(byteArrayOf(retByteArray[1][0])).toInt() and 0xFF
        val retVal = BigInteger(byteArrayOf(retByteArray[1][1])).toInt()

        assertEquals(pubArr.size, pubLen, "Got bad pubkey length.")

        assertEquals(retVal, 1, "Failed return value check.")

        return pubArr
    }

    /**
     * libsecp256k1 PubKey Tweak-Mul - Tweak pubkey by multiplying to it
     *
     * @param tweak  some bytes to tweak with
     * @param pubkey 32-byte seckey
     */
    @Throws(AssertFailException::class)
    fun pubKeyTweakMul(pubkey: ByteArray, tweak: ByteArray): ByteArray {
        if (pubkey.size != 33 && pubkey.size != 65) {
            throw IllegalArgumentException()
        }

        var byteBuff = nativeECDSABuffer.get()
        if (byteBuff == null || byteBuff.capacity() < pubkey.size + tweak.size) {
            byteBuff = ByteBuffer.allocateDirect(pubkey.size + tweak.size)
            byteBuff!!.order(ByteOrder.nativeOrder())
            nativeECDSABuffer.set(byteBuff)
        }
        byteBuff.rewind()
        byteBuff.put(pubkey)
        byteBuff.put(tweak)

        val retByteArray: Array<ByteArray>
        r.lock()
        try {
            retByteArray = secp256k1_pubkey_tweak_mul(byteBuff, Secp256k1Context.context, pubkey.size)
        } finally {
            r.unlock()
        }

        val pubArr = retByteArray[0]

        val pubLen = BigInteger(byteArrayOf(retByteArray[1][0])).toInt() and 0xFF
        val retVal = BigInteger(byteArrayOf(retByteArray[1][1])).toInt()

        assertEquals(pubArr.size, pubLen, "Got bad pubkey length.")

        assertEquals(retVal, 1, "Failed return value check.")

        return pubArr
    }

    /**
     * libsecp256k1 create ECDH secret - constant time ECDH calculation
     *
     * @param seckey byte array of secret key used in exponentiaion
     * @param pubkey byte array of public key used in exponentiaion
     */
    @Throws(AssertFailException::class)
    fun createECDHSecret(seckey: ByteArray, pubkey: ByteArray): ByteArray {
        if (seckey.size > 32 || pubkey.size > 65) {
            throw IllegalArgumentException()
        }

        var byteBuff = nativeECDSABuffer.get()
        if (byteBuff == null || byteBuff.capacity() < 32 + pubkey.size) {
            byteBuff = ByteBuffer.allocateDirect(32 + pubkey.size)
            byteBuff!!.order(ByteOrder.nativeOrder())
            nativeECDSABuffer.set(byteBuff)
        }
        byteBuff.rewind()
        byteBuff.put(seckey)
        byteBuff.put(pubkey)

        val retByteArray: Array<ByteArray>
        r.lock()
        try {
            retByteArray = secp256k1_ecdh(byteBuff, Secp256k1Context.context, pubkey.size)
        } finally {
            r.unlock()
        }

        val resArr = retByteArray[0]
        val retVal = BigInteger(byteArrayOf(retByteArray[1][0])).toInt()

        assertEquals(resArr.size, 32, "Got bad result length.")
        assertEquals(retVal, 1, "Failed return value check.")

        return resArr
    }

    /**
     * libsecp256k1 randomize - updates the context randomization
     *
     * @param seed 32-byte random seed
     */
    @Synchronized
    fun randomize(seed: ByteArray): Boolean {
        if (seed.size != 32) {
            throw IllegalArgumentException()
        }

        var byteBuff = nativeECDSABuffer.get()
        if (byteBuff == null || byteBuff.capacity() < seed.size) {
            byteBuff = ByteBuffer.allocateDirect(seed.size)
            byteBuff!!.order(ByteOrder.nativeOrder())
            nativeECDSABuffer.set(byteBuff)
        }
        byteBuff.rewind()
        byteBuff.put(seed)

        w.lock()
        try {
            return secp256k1_context_randomize(byteBuff, Secp256k1Context.context) == 1
        } finally {
            w.unlock()
        }
    }

    fun parseEcdsaRecoverableSignatureCompact(sign: ByteArray, recId: Int): ByteArray {
        if (sign.size != 64) {
            throw IllegalArgumentException()
        }

        var byteBuff = nativeECDSABuffer.get()
        if (byteBuff == null || byteBuff.capacity() < sign.size) {
            byteBuff = ByteBuffer.allocateDirect(sign.size)
            byteBuff!!.order(ByteOrder.nativeOrder())
            nativeECDSABuffer.set(byteBuff)
        }
        byteBuff.rewind()
        byteBuff.put(sign)

        r.lock()
        try {
            return secp256k1_ecdsa_recoverable_signature_parse_compact(byteBuff, recId, Secp256k1Context.context)
        } finally {
            r.unlock()
        }
    }

    fun parsePublicKey(pubkey: ByteArray): ByteArray {
        if (pubkey.size > 65) {
            throw IllegalArgumentException()
        }

        var byteBuff = nativeECDSABuffer.get()
        if (byteBuff == null || byteBuff.capacity() <  pubkey.size) {
            byteBuff = ByteBuffer.allocateDirect( pubkey.size)
            byteBuff!!.order(ByteOrder.nativeOrder())
            nativeECDSABuffer.set(byteBuff)
        }
        byteBuff.rewind()
        byteBuff.put(pubkey)

        val retByteArray: Array<ByteArray>
        r.lock()
        try {
            retByteArray = secp256k1_ec_pubkey_parse(byteBuff, Secp256k1Context.context, pubkey.size)
        } finally {
            r.unlock()
        }

        val resArr = retByteArray[0]
        val retVal = BigInteger(byteArrayOf(retByteArray[1][0])).toInt()

        assertEquals(resArr.size, 32, "Got bad result length.")
        assertEquals(retVal, 1, "Failed return value check.")

        return resArr
    }

    fun createRecoverableEcdsaSignature(data: ByteArray, privateKey: ByteArray, extraEntropy: ByteArray?): ByteArray {
        if (data.size != 32 || privateKey.size != 32) {
            throw IllegalArgumentException()
        }
        var byteBuff = nativeECDSABuffer.get()
        if (byteBuff == null || byteBuff.capacity() < data.size) {
            byteBuff = ByteBuffer.allocateDirect(data.size)
            byteBuff!!.order(ByteOrder.nativeOrder())
            nativeECDSABuffer.set(byteBuff)
        }
        byteBuff.rewind()
        byteBuff.put(data)

        r.lock()
        try {
            return secp256k1_ecdsa_sign_recoverable(byteBuff, privateKey, extraEntropy, Secp256k1Context.context)
        } finally {
            r.unlock()
        }
    }

    fun recoverEcdsaPublicKeyFromSignature(signature: ByteArray, hash: ByteArray): ByteArray? {
        if (signature.size != 65 || hash.size != 32) {
            throw IllegalArgumentException()
        }
        var byteBuff = nativeECDSABuffer.get()
        if (byteBuff == null || byteBuff.capacity() < signature.size) {
            byteBuff = ByteBuffer.allocateDirect(signature.size)
            byteBuff!!.order(ByteOrder.nativeOrder())
            nativeECDSABuffer.set(byteBuff)
        }
        byteBuff.rewind()
        byteBuff.put(signature)

        r.lock()
        try {
            return secp256k1_ecdsa_recover(Secp256k1Context.context, byteBuff, hash)
        } finally {
            r.unlock()
        }
    }

    fun serializeCompactEcdsaRecoverableSignature(signature: ByteArray): Pair<Byte, ByteArray>? {
        if (signature.size != 65) {
            throw IllegalArgumentException()
        }
        var byteBuff = nativeECDSABuffer.get()
        if (byteBuff == null || byteBuff.capacity() < signature.size) {
            byteBuff = ByteBuffer.allocateDirect(signature.size)
            byteBuff!!.order(ByteOrder.nativeOrder())
            nativeECDSABuffer.set(byteBuff)
        }
        byteBuff.rewind()
        byteBuff.put(signature)

        val retByteArray: Array<ByteArray>?
        r.lock()
        try {
            retByteArray = secp256k1_ecdsa_recoverable_signature_serialize_compact(byteBuff, Secp256k1Context.context)
        } finally {
            r.unlock()
        }

        return retByteArray?.let { Pair(it[1][0], it[0]) }
    }

    /**
     * libsecp256k1 Serialize public key
     *
     * @param publicKey ECDSA Public key, 64 bytes
     * @param compressed Is result should be compressed or not
     *
     * @return  pubkey ECDSA serialized public key, 33 or 65 bytes
     */
    @Throws(AssertFailException::class)
    fun serializePublicKey(publicKey: ByteArray, compressed: Boolean): ByteArray? {
        if (publicKey.size != 64) {
            throw IllegalArgumentException()
        }

        var byteBuff = nativeECDSABuffer.get()
        if (byteBuff == null || byteBuff.capacity() < publicKey.size) {
            byteBuff = ByteBuffer.allocateDirect(publicKey.size)
            byteBuff!!.order(ByteOrder.nativeOrder())
            nativeECDSABuffer.set(byteBuff)
        }
        byteBuff.rewind()
        byteBuff.put(publicKey)

        r.lock()
        try {
            return secp256k1_ec_pubkey_serialize(byteBuff, compressed, Secp256k1Context.context)
        } finally {
            r.unlock()
        }
    }

    private external fun secp256k1_ctx_clone(context: Long): Long

    private external fun secp256k1_context_randomize(byteBuff: ByteBuffer, context: Long): Int

    private external fun secp256k1_privkey_tweak_add(byteBuff: ByteBuffer, context: Long): Array<ByteArray>

    private external fun secp256k1_privkey_tweak_mul(byteBuff: ByteBuffer, context: Long): Array<ByteArray>

    private external fun secp256k1_pubkey_tweak_add(byteBuff: ByteBuffer, context: Long, pubLen: Int): Array<ByteArray>

    private external fun secp256k1_pubkey_tweak_mul(byteBuff: ByteBuffer, context: Long, pubLen: Int): Array<ByteArray>

    private external fun secp256k1_destroy_context(context: Long)

    private external fun secp256k1_ecdsa_verify(byteBuff: ByteBuffer, context: Long, sigLen: Int, pubLen: Int): Int

    private external fun secp256k1_ecdsa_sign(byteBuff: ByteBuffer, context: Long): Array<ByteArray>

    private external fun secp256k1_ec_seckey_verify(byteBuff: ByteBuffer, context: Long): Int

    private external fun secp256k1_ec_pubkey_create(byteBuff: ByteBuffer, context: Long): ByteArray?

    private external fun secp256k1_ec_pubkey_parse(byteBuff: ByteBuffer, context: Long, inputLen: Int): Array<ByteArray>

    private external fun secp256k1_ecdh(byteBuff: ByteBuffer, context: Long, inputLen: Int): Array<ByteArray>

    private external fun secp256k1_ecdsa_recoverable_signature_parse_compact(byteBuff: ByteBuffer, recId: Int, context: Long): ByteArray

    private external fun secp256k1_ecdsa_sign_recoverable(data: ByteBuffer, privateKey: ByteArray, extraEntropy: ByteArray?, context: Long): ByteArray

    private external fun secp256k1_ecdsa_recover(context: Long, signature: ByteBuffer, hash: ByteArray): ByteArray?

    private external fun secp256k1_ecdsa_recoverable_signature_serialize_compact(signature: ByteBuffer, context: Long): Array<ByteArray>?

    private external fun secp256k1_ec_pubkey_serialize(byteBuff: ByteBuffer, compressed: Boolean, context: Long): ByteArray?
}
