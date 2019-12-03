package org.bitcoin

/**
 * Created by BArtWell on 26.06.2019.
 */

internal object NativeSecp256k1Util {

    @Throws(AssertFailException::class)
    fun assertEquals(`val`: Int, val2: Int, message: String) {
        if (`val` != val2)
            throw AssertFailException("FAIL: $message")
    }

    @Throws(AssertFailException::class)
    fun assertEquals(`val`: Boolean, val2: Boolean, message: String) {
        if (`val` != val2)
            throw AssertFailException("FAIL: $message")
        else
            println("PASS: $message")
    }

    @Throws(AssertFailException::class)
    fun assertEquals(`val`: String, val2: String, message: String) {
        if (`val` != val2)
            throw AssertFailException("FAIL: $message")
        else
            println("PASS: $message")
    }

    internal class AssertFailException(message: String) : Exception(message)
}
