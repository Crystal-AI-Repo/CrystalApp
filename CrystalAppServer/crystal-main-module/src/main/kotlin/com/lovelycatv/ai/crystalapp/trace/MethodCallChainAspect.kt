package com.lovelycatv.ai.crystalapp.trace

import com.lovelycatv.ai.crystalapp.common.utils.logger
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class MethodCallChainAspect {

    private val logger = logger()

    @Around("execution(* com.lovelycatv.ai.crystalapp.controller..*.*(..)) || execution(* com.lovelycatv.ai.crystalapp.service..*.*(..))")
    @Throws(Throwable::class)
    fun logMethodCallChain(joinPoint: ProceedingJoinPoint): Any? {
        val className = joinPoint.target.javaClass.simpleName
        val methodName = joinPoint.signature.name
        val methodSignature = "$className.$methodName()"

        CallChainContext.setUUID()
        CallChainContext.addMethod(className, methodName)

        val isControllerMethod = joinPoint.signature.declaringTypeName.contains("Controller")
        val logPrefix = if (isControllerMethod) "" else "  "

        try {
            val inTime = System.nanoTime()
            logger.info("$logPrefix → Entering $methodSignature")
            val result = joinPoint.proceed()
            val costTime = System.nanoTime() - inTime
            logger.info("$logPrefix ← Exiting $methodSignature, cost ${costTime / 1000000}ms (${costTime}ns)")
            return result
        } catch (e: Exception) {
            logger.error("$logPrefix ⚠ Error in $methodSignature: ${e.message}")
            throw e
        } finally {
            if (isControllerMethod) {
                val fullCallChain = CallChainContext.getCurrentCallChain().reversed()
                logger.info("Call Chain (Entry: $methodSignature): $fullCallChain")
            }

            CallChainContext.removeMethod()

            if (isControllerMethod) {
                CallChainContext.clear()
            }
        }
    }
}