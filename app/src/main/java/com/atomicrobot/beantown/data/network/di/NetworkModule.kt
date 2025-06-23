package com.atomicrobot.beantown.data.network.di

import com.atomicrobot.beantown.data.network.NetworkUtils
import com.atomicrobot.beantown.data.network.ktor.logging.TimberLogger
import io.ktor.client.plugins.logging.Logger
import kotlinx.serialization.json.Json
import org.koin.core.KoinApplication
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.atomicrobot.beantown.data.network")
class NetworkModule {

    @Single
    fun json(): Json = NetworkUtils.json

    @Single
    fun logger(): Logger = TimberLogger
}

fun KoinApplication.networkModule(): KoinApplication = modules(NetworkModule().module)