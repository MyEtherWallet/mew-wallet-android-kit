package org.bitcoin

/**
 * Created by BArtWell on 26.06.2019.
 */

object Secp256k1Context {

    internal val context: Long

    init {
        System.loadLibrary("secp256k1")
        context = secp256k1_init_context()
    }

    private external fun secp256k1_init_context(): Long
}
