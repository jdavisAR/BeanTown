package com.atomicrobot.beantown.data.network.ktor

import io.ktor.resources.Resource

@Resource(path = "/beans")
data class JellyBeanRequest(
    val pageIndex: Int,
    val pageSize: Int,
)