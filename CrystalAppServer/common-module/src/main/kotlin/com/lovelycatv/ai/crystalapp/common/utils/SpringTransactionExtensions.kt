package com.lovelycatv.ai.crystalapp.common.utils

import org.springframework.transaction.interceptor.TransactionAspectSupport

/**
 * @author lovelycat
 * @since 2025-04-14 21:28
 * @version 1.0
 */
class SpringTransactionExtensions private constructor()

fun transactionRollback() {
    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
}