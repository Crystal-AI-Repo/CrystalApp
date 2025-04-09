package com.lovelycatv.ai.crystalapp.controller

import com.lovelycatv.ai.crystalapp.GlobalConstants
import com.lovelycatv.ai.crystalapp.client.OAuth2Client
import com.lovelycatv.ai.crystalapp.common.Result
import com.lovelycatv.ai.crystalapp.common.retrofit.RetrofitApi
import com.lovelycatv.ai.crystalapp.config.CrystalAppSettings
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

/**
 * @author lovelycat
 * @since 2025-04-09 16:13
 * @version 1.0
 */
@RestController
@RequestMapping("/auth")
class AuthController(
    @Value("\${server.port}")
    private val serverPort: Int,
    private val crystalAppSettings: CrystalAppSettings
) {
    @PostMapping("/token")
    suspend fun getUserToken(@RequestParam("code") code: String): Result<*> {
        val client = RetrofitApi("http://127.0.0.1:$serverPort").getApi(OAuth2Client::class)

        return try {
            val result = client.getTokenByCode(
                authorization = "Basic " + Base64.getEncoder().encodeToString("${crystalAppSettings.oauth2.clientId}:${crystalAppSettings.oauth2.clientSecret}".toByteArray()),
                grantType = "authorization_code",
                redirectUri = GlobalConstants.getCustomFrontConsentConfirmPageUrl(crystalAppSettings.frontBaseUrl),
                code = code
            ).body()
            Result.success("", result)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.badRequest("Invalid consent code")
        }
    }
}