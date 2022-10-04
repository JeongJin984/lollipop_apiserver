package com.lollipop.apiserver.rsocket

import com.lollipop.apiserver.api.service.EmployeesService
import com.lollipop.apiserver.db.mySql.entity.Employees
import org.reactivestreams.Subscription
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Controller
import reactor.core.publisher.BaseSubscriber
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration

@Controller
class ClientHandler {

    @Autowired
    lateinit var employeesService: EmployeesService

    @MessageMapping("requestresponse")
    fun requestResponse(): Mono<String> {
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
            .doOnNext {
                println("emp No: " + it.empNo + " / " + (System.currentTimeMillis() - curtime))
                curtime = System.currentTimeMillis()
            }
    }

    @MessageMapping("channel")
    @Payload(required = true)
    fun channelTest(payload : Flux<Employees>): Flux<String> {
        return payload
            .flatMap { Flux.just("${it.firstName} ${it.lastName}") }
    }
}