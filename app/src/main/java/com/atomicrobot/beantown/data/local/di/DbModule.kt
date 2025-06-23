package com.atomicrobot.beantown.data.local.di

import android.content.Context
import com.atomicrobot.beantown.data.local.JellyBeanDb
import org.koin.core.KoinApplication
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.atomicrobot.beantown.data.local")
class DbModule {

    @Single
    fun database(context: Context): JellyBeanDb = JellyBeanDb.buildDB(context = context, inMemory = false)
}

fun KoinApplication.dbModule(): KoinApplication = modules(DbModule().module)