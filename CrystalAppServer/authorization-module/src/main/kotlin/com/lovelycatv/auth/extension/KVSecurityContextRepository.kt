package com.lovelycatv.auth.extension

import com.lovelycatv.ai.crystalapp.common.data.kv.ExpiringKeyValueStore
import com.lovelycatv.auth.AuthGlobalConstants
import com.lovelycatv.auth.misc.SupplierDeferredSecurityContext
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.DeferredSecurityContext
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.context.HttpRequestResponseHolder
import org.springframework.security.web.context.SecurityContextRepository
import org.springframework.util.ObjectUtils


/**
 * @author lovelycat
 * @since 2025-04-06 22:02
 * @version 1.0
 */
class KVSecurityContextRepository(
    private val keyValueStore: ExpiringKeyValueStore<String, SecurityContext>
) : SecurityContextRepository {
    private val securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy()

    @Deprecated("please use the loadDeferredContext()")
    override fun loadContext(requestResponseHolder: HttpRequestResponseHolder): SecurityContext {
        throw UnsupportedOperationException("Method deprecated.");
    }

    override fun saveContext(context: SecurityContext, request: HttpServletRequest, response: HttpServletResponse) {
        val nonce = getNonce(request)
        if (ObjectUtils.isEmpty(nonce)) {
            return
        }

        val emptyContext = this.securityContextHolderStrategy.createEmptyContext()
        if (emptyContext.equals(context)) {
            keyValueStore.remove((AuthGlobalConstants.SECURITY_CONTEXT_PREFIX_KEY + nonce))
        } else {
            keyValueStore.set((AuthGlobalConstants.SECURITY_CONTEXT_PREFIX_KEY + nonce), context, AuthGlobalConstants.DEFAULT_TIMEOUT_SECONDS * 1000L)
        }
    }

    override fun containsContext(request: HttpServletRequest): Boolean {
        val nonce = getNonce(request)
        if (ObjectUtils.isEmpty(nonce)) {
            return false
        }

        return keyValueStore.get((AuthGlobalConstants.SECURITY_CONTEXT_PREFIX_KEY + nonce)) != null
    }

    override fun loadDeferredContext(request: HttpServletRequest?): DeferredSecurityContext {
        return SupplierDeferredSecurityContext({ readSecurityContextFromRedis(request) }, this.securityContextHolderStrategy)
    }

    private fun readSecurityContextFromRedis(request: HttpServletRequest?): SecurityContext? {
        if (request == null) {
            return null
        }

        val nonce = getNonce(request)
        if (ObjectUtils.isEmpty(nonce)) {
            return null
        }

        return keyValueStore.get((AuthGlobalConstants.SECURITY_CONTEXT_PREFIX_KEY + nonce))
    }

    private fun getNonce(request: HttpServletRequest): String? {
        var nonce = request.getHeader(AuthGlobalConstants.NONCE_HEADER_NAME)
        if (ObjectUtils.isEmpty(nonce)) {
            nonce = request.getParameter(AuthGlobalConstants.NONCE_HEADER_NAME)
            val session = request.getSession(false)
            if (ObjectUtils.isEmpty(nonce) && session != null) {
                nonce = session.id
            }
        }
        return nonce
    }
}