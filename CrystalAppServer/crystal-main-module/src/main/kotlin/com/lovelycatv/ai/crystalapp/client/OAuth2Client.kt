package com.lovelycatv.ai.crystalapp.client

import retrofit2.Response
import retrofit2.http.*

/**
 * @author lovelycat
 * @since 2025-04-09 16:16
 * @version 1.0
 */
interface OAuth2Client {
    @POST("/oauth2/token")
    @FormUrlEncoded
    suspend fun getTokenByCode(
        @Header("Authorization")
        authorization: String,
        @Field("grant_type")
        grantType: String,
        @Field("redirect_uri")
        redirectUri: String,
        @Field("code")
        code: String,
    ): Response<Map<String, Any?>>
}