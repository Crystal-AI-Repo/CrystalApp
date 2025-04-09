package com.lovelycatv.auth.misc

import com.lovelycatv.ai.crystalapp.common.utils.logger
import org.springframework.security.core.context.DeferredSecurityContext
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolderStrategy
import java.util.function.Supplier

/**
 * @author lovelycat
 * @since 2025-04-06 21:57
 * @version 1.0
 */
class SupplierDeferredSecurityContext(
    private val supplier: Supplier<SecurityContext?>,
    private val strategy: SecurityContextHolderStrategy
) : DeferredSecurityContext {

    private val logger = logger()


    private var securityContext: SecurityContext? = null

    private var missingContext: Boolean = false


    /**
     * Gets a result.
     *
     * @return a result
     */
    override fun get(): SecurityContext {
        init()
        return this.securityContext!!
    }

    override fun isGenerated(): Boolean {
        init()
        return this.missingContext
    }

    private fun init() {
        if (this.securityContext != null) {
            return
        }

        this.securityContext = this.supplier.get()
        this.missingContext = (this.securityContext == null)
        if (this.missingContext) {
            this.securityContext = this.strategy.createEmptyContext()
            logger.info("Created %s".format(this.securityContext))
        }
    }
}