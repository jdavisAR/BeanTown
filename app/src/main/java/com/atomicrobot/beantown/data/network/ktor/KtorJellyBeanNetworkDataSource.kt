package com.atomicrobot.beantown.data.network.ktor

import com.atomicrobot.beantown.data.network.JellyBeanNetworkDataSource
import com.atomicrobot.beantown.data.network.model.NetworkJellyBeans
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.plugins.resources.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.appendIfNameAbsent
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single

/**
 * Implementation of [JellyBeanNetworkDataSource] based by the [HttpClient].
 */
@Single
class KtorJellyBeanNetworkDataSource(
    json: Json,
    logger: Logger,
) : JellyBeanNetworkDataSource {

    private val client: HttpClient = HttpClient(Android) {
        install(Resources)
        defaultRequest {
            url { url.takeFrom(baseJellyBeanUrl) }
            headers.appendIfNameAbsent(
                name = HttpHeaders.ContentType,
                value = ContentType.Application.Json.toString()
            )
        }
        install(ContentNegotiation) {
            json(json = json)
        }

        // Helpful for logging information about request
        install(Logging) {
            this.logger = logger
            level = LogLevel.INFO
            sanitizeHeader { header -> header == HttpHeaders.Authorization }
        }

        // Helpful for debug (logging) request round trip time info
        install(ResponseObserver) {
            onResponse { response ->
                logger.logHttpResponseRoundTrip(response)
            }
        }
    }

    override suspend fun getJellyBeans(
        pageIndex: Int,
        pageSize: Int
    ): NetworkJellyBeans = client.get(
        JellyBeanRequest(
            pageIndex = pageIndex,
            pageSize = pageSize,
        )
    )
        .let { resp ->
            when (resp.status.isSuccess()) {
                true -> resp.body<NetworkJellyBeans>()
                // Since we should always received beans we can treat empty as an error state
                false -> NetworkJellyBeans(
                    totalCount = 0,
                    pageSize = 0,
                    currentPage = 0,
                    totalPages = 0,
                    items = emptyList(),
                )
            }
        }
}

/**
 * Helper function for logging round-trip time for http request.
 */
private fun Logger.logHttpResponseRoundTrip(response: HttpResponse) {
    log(
        """
        RESPONSE: ${response.status}
        FROM: /${response.request.url.segments.last()}
        round-trip: ${response.responseTime.timestamp - response.requestTime.timestamp} ms
        """.trimIndent()
    )
}

const val baseJellyBeanUrl: String = "https://jellybellywikiapi.onrender.com/api/"
