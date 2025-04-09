package com.lovelycatv.ai.crystalapp.common.utils

object UrlUtils {
    fun getHostFromUrl(url: String): String {
        val protocol = if (url.contains("http://") || url.contains("https://")) {
            val http = url.indexOf("http://")
            val https = url.indexOf("https://")
            if (http >= 0)
                (http + 7) to "http://"
            else (https + 8) to "https://"
        } else 0 to "http://"

        return protocol.second + url.slice(protocol.first..<url.length).run {
            this.slice(0..<this.indexOf("/"))
        }
    }
}