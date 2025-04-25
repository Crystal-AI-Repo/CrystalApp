package com.lovelycatv.ai.crystalapp.trace

import java.util.UUID

object CallChainContext {
    private val uuid = ThreadLocal.withInitial { "" }
    private val callStack = ThreadLocal.withInitial { ArrayDeque<String>() }

    fun setUUID() {
        if (uuid.get().isBlank()) {
            uuid.set(UUID.randomUUID().toString())
        }
    }

    fun addMethod(className: String, methodName: String) {
        callStack.get().addFirst("$className.$methodName()")
    }

    fun removeMethod() {
        callStack.get().removeFirst()
    }

    fun getCurrentCallChain(): List<String> {
        return callStack.get().toList()
    }

    fun clear() {
        uuid.remove()
        callStack.remove()
    }
}