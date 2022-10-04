package com.lollipop.apiserver.api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.lollipop.apiserver.api.service.EmployeesService
import com.lollipop.apiserver.db.mySql.entity.Employees
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.retrieveFlux
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink
import reactor.core.publisher.Mono
import reactor.core.publisher.SynchronousSink
import reactor.core.scheduler.Schedulers
import java.time.Duration
import java.util.concurrent.atomic.AtomicLong


@RestController
class HelloController (
    private val employeesService : EmployeesService,
    private val template : ReactiveStringRedisTemplate,
    private val rSocketRequester: RSocketRequester
) {
    @GetMapping("/hello")
    fun hello() : Flux<String> {
        return Flux.just("hello", "qwer", "zxcv")
    }

    @GetMapping("/hello1")
    fun hello1() : Flux<String> {
        return Flux
            .just("a", "b", "c", "d", "e")
            .flatMap { s ->
                Flux.just(s + "x")
                    .delaySubscription(Duration.ofSeconds(2))
            }
    }

    @GetMapping("/hello2")
    fun hello2() : Flux<String> {
        return Flux
            .just("a", "b", "c", "d", "e")
            .concatMap { s ->
                Flux.just(s + "x")
                    .delaySubscription(Duration.ofSeconds(2))
            }
    }

    @GetMapping("/redis/find")
    fun redisCache() : Mono<String> {
        return template.opsForValue().get("customer1")
    }

    @GetMapping("/redis/cache")
    suspend fun redisCacheGet() : Employees? {
        val objectMapper = ObjectMapper();
        val customer : Employees? = employeesService.coroutineSelect()
        val str : String = objectMapper.writeValueAsString(customer);
        template.opsForValue().append("customer1", str)
            .subscribe()
        return customer
    }

    @GetMapping("/db")
    fun redisCacheFlux(): Flux<Employees?> {
         return employeesService.findAll()
    }

    @GetMapping("/db/get1")
    suspend fun get1(): Employees? {
        return employeesService.coroutineSelect()
    }

    @GetMapping("/db/get2")
    fun get2(): Mono<Employees?> {
        return employeesService.findById(10001)
    }

    @GetMapping("/db/get3")
    fun get3(): Flux<Employees> {
        return employeesService.findByLastName("Facello")
    }

    @GetMapping("/db/insert")
    suspend fun insert(): Employees?  {
        return employeesService.insert()
    }

    @GetMapping("/rsocket")
    fun test(): Mono<String> {
        return rSocketRequester
            .route("test2")
            .retrieveFlux<String>()
            .delayElements(Duration.ofMillis(1))
            .filter { it == "20" }
            .next()
    }

    @GetMapping("/rest")
    fun test2(): Mono<String> {
        val client = WebClient.create()

        return client.get()
            .uri("http://localhost:8081/test3")
            .retrieve()
            .bodyToFlux(String::class.java)
            .filter { it == "20"}
            .doOnNext { println(it) }
            .next()
    }

    @GetMapping("/hihi")
    fun hihi() : Flux<String?> {
        return Flux.generate(
            { AtomicLong() }
        ) { state: AtomicLong, sink: SynchronousSink<String?> ->
            val i = state.getAndIncrement()
            sink.next("3 x $i = ${3*i}")
            if (i == 10L) sink.complete()
            state
        }
    }

    interface MyEventListener<T> {
        fun onDataChunk(chunk: List<T>)
        fun processComplete()
    }

    class EventProcessor {
        private var listener: MyEventListener<String>? = null

        fun register(listener: MyEventListener<String>) {
            this.listener = listener
        }

        fun process() {
            IntRange(1,20)
                .forEach {
                    try {
                        Thread.sleep(1000)
                    } catch (e : InterruptedException) {
                        e.printStackTrace()
                    }
                    listener!!.onDataChunk(listOf(it.toString()))
                    listener!!.processComplete()
                }
        }
    }

    @GetMapping("/hihi2")
    fun hihi2() : Flux<String> {
        val myEventProcessor = EventProcessor()

        return Flux.create { sink ->
            run {
                myEventProcessor.register(
                    object : MyEventListener<String> {
                        override fun onDataChunk(chunk: List<String>) {
                            for (s in chunk) {
                                sink.next(s)
                            }
                        }

                        override fun processComplete() {
                            sink.complete()
                        }
                    }
                );
            }
        }
    }
}