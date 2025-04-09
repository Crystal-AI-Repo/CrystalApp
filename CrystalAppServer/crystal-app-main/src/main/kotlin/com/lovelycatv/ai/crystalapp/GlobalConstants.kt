package com.lovelycatv.ai.crystalapp

object GlobalConstants {
    fun getCustomFrontLoginPageUrl(baseUrl: String): String {
        return "${baseUrl}/login"
    }

    fun getCustomFrontConsentPageUrl(baseUrl: String): String {
        return "${baseUrl}/consent"
    }

    fun getCustomFrontConsentConfirmPageUrl(baseUrl: String): String {
        return "${baseUrl}/auth/consent"
    }
}