package com.atomicrobot.beantown.data.network

import kotlinx.serialization.json.Json

object NetworkUtils {
    
    val json: Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        // Settings Below copied from default JsonConfig
        encodeDefaults = true
        isLenient = true
    }
}