package com.atomicrobot.beantown

import android.app.Application
import com.atomicrobot.beantown.data.local.di.dbModule
import com.atomicrobot.beantown.data.network.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.ksp.generated.defaultModule
import timber.log.Timber

class BeanTownApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        startKoin {
            androidContext(this@BeanTownApplication)
            defaultModule()
                .networkModule()
                .dbModule()
        }
    }
}