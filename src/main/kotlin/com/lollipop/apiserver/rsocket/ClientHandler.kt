package com.lollipop.apiserver.rsocket

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.lollipop.apiserver.api.service.EmployeesService
import com.lollipop.apiserver.db.mySql.entity.ChattingRoom
import com.lollipop.apiserver.db.mySql.entity.Employees
import com.lollipop.apiserver.kafka.SampleProducer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.annotation.ConnectMapping
import org.springframework.messaging.rsocket.retrieveMono
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch

@Controller
class ClientHandler {

    @Autowired
    lateinit var employeesService: EmployeesService

    @ConnectMapping
    fun onConnect(requester: RSocketRequester) {
        requester.rsocket()
            ?.onClose()
            ?.doFirst {}
            ?.doOnError {}
            ?.doFinally {}
            ?.subscribe()
    }

    @MessageMapping("requestresponse")
    fun requestResponse(requester: RSocketRequester): Mono<String> {
        return Mono.just("asdf")
    }

    @MessageMapping("fireandforget")
    fun fireAndForgetTest(): Mono<Void> {
        return Mono.empty()
    }

    @MessageMapping("requeststream")
    fun requestStreamTest() : Flux<Employees> {
        var curtime = System.currentTimeMillis()
        return employeesService.findByLastName("Facello")
    }

    @MessageMapping("requeststream2")
    fun requestStreamTest2() : Flux<MutableMap<String, Any>?> {
        var curtime = System.currentTimeMillis()
        var result = employeesService.coroutineSelectJoin()
        return result
    }

    @MessageMapping("list")
    @Payload(required = true)
    fun listTest(requester: RSocketRequester, payload : MutableMap<String, String>): Flux<String> {
        return Flux.fromIterable(payload.keys)
            .flatMap { Flux.just(it, it, it, it) }
    }

    @MessageMapping("channel")
    @Payload(required = true)
    fun channelTest(requester: RSocketRequester, payload : Flux<Employees>): Flux<String> {
        return payload
            .flatMap { Flux.just("${it.firstName} ${it.lastName}") }
    }

    @MessageMapping("kafka.sample")
    fun kafkaSample(requester: RSocketRequester): Mono<Void> {
        val producer = SampleProducer("13.209.68.27:9092,3.35.26.142:9092")
        producer.sendMessages("bation.kafka.4", 10, CountDownLatch(5))
        producer.close()

        Flux.just("asdf","zxcv", "qawer")
            .subscribeOn(Schedulers.newSingle("single1"))
            .subscribe()

        return Mono.empty()
    }
}