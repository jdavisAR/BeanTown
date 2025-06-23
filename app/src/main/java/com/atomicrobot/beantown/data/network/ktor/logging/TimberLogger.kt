package com.atomicrobot.beantown.data.network.ktor.logging

import io.ktor.client.plugins.logging.Logger
import timber.log.Timber

/**
 * [Timber] based logger for [io.ktor.client.HttpClient].
 */
object TimberLogger: Logger {

    val tree = Timber.Forest.tag("KtorLogger")

    override fun log(message: String) = tree.v(message)
}