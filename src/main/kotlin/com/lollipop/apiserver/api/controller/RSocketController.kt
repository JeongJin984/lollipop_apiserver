package com.lollipop.apiserver.api.controller

import kotlinx.coroutines.flow.Flow
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.retrieveFlow
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class RSocketController (
    val rSocketRequester: RSocketRequester
        ) {

    @GetMapping("/test/rsocket")
    fun test() : Flow<String> {
        return rSocketRequester
            .route("currentMarketData")
            .data("result")
            .retrieveFlow()
    }
}