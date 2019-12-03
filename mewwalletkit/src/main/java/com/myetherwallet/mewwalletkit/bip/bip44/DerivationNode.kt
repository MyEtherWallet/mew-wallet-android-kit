package com.myetherwallet.mewwalletkit.bip.bip44

import com.myetherwallet.mewwalletkit.bip.bip44.exception.InvalidChildIndexException
import com.myetherwallet.mewwalletkit.bip.bip44.exception.InvalidPathException

/**
 * Created by BArtWell on 07.06.2019.
 */

private const val hardenedCharacter = "'"
private val hardenedCharacterSet = hardenedCharacter.toCharArray()
private const val hardenedEdge = 0x80000000.toInt()

sealed class DerivationNode(val index: Int) {

    class HARDENED(index: Int) : DerivationNode(index) {
        override fun toString() = "HARDENED($index)"
    }

    @Suppress("ClassName")
    class NON_HARDENED(index: Int) : DerivationNode(index) {
        override fun toString() = "NON_HARDENED($index)"
    }

    companion object {
        fun create(component: String): DerivationNode? {
            if (component.startsWith("m")) {
                return null
            }
            val index: Int
            try {
                index = Integer.valueOf(component.trim(*hardenedCharacterSet))
            } catch (e: Exception) {
                throw InvalidPathException()
            }
            if (hardenedEdge and index != 0) {
                throw InvalidChildIndexException()
            }
            return if (component.endsWith(hardenedCharacter)) {
                HARDENED(hardenedEdge or index)
            } else {
                NON_HARDENED(index)
            }
        }

        fun nodes(path: String): Array<DerivationNode> {
            val nodes = mutableListOf<DerivationNode>()
            val components = path.split("/")
            for (component in components) {
                val node = create(component)
                if (node != null) {
                    nodes.add(node)
                }
            }
            return nodes.toTypedArray()
        }

    }
}

fun String.derivationPath() = DerivationNode.nodes(this)
