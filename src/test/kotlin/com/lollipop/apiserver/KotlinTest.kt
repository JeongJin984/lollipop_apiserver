package com.lollipop.apiserver

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.reactivestreams.Publisher
import org.reactivestreams.Subscription
import reactor.core.publisher.*
import reactor.core.scheduler.Schedulers
import reactor.test.StepVerifier
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport
import java.time.Duration
import java.util.concurrent.atomic.AtomicLong
import java.util.function.Consumer
import kotlin.properties.Delegates
import kotlin.reflect.KProperty


class KotlinTest {

    @Test
    @DisplayName("subscribe")
    fun propertyListener_test() {
        open class PropertyChangeAware {

            protected val propertyChangeSupport = PropertyChangeSupport(this)

            fun addPropertyChangeListener(listener: PropertyChangeListener) {
                propertyChangeSupport.addPropertyChangeListener(listener)
            }

            fun removePropertyChangeListener(listener: PropertyChangeListener)
            {
                propertyChangeSupport.removePropertyChangeListener(listener)
            }
        }

        class Person(val name: String, age: Int, message: String) : PropertyChangeAware() {
            private val observer = {
                    property: KProperty<*>,
                    oldValue: Any,
                    newValue: Any -> propertyChangeSupport.firePropertyChange(property.name, oldValue, newValue)
            }

            var age: Int by Delegates.observable(age, observer)
            var message: String by Delegates.observable(message, observer)
        }

        val person = Person("asdf", 25, "qwerqwerqwer")
        person.addPropertyChangeListener(PropertyChangeListener { event ->
            println("Property [${event.propertyName}] changed " + "from [${event.oldValue}] to [${event.newValue}]")
        })

        person.age = 28
        person.message = "zxcvzxcvzxcv"
    }

    internal object CommonUtilsk {
        var startTime: Long = 0
        fun exampleStart() {
            startTime = System.currentTimeMillis()
        }

        fun sleep(mills: Int) {
            try {
                Thread.sleep(mills.toLong())
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

        }
        fun getThreadName(): String {
            var threadName = Thread.currentThread().name
            if (threadName.length > 30) {
                threadName = threadName.substring(0, 30) + "..."
            }
            return threadName
        }
    }

    class MySub : BaseSubscriber<String>()

    @Test
    fun pubsub_test() {
        Flux.range(1, 10)
            .log()
            .take(3)
            .subscribe()
    }

    @Test
    fun concatMap_test() {
        val items : List<String> = mutableListOf("a", "b", "c", "d", "e", "f");

        Flux.fromIterable(items)
            .publishOn(Schedulers.boundedElastic())
            .concatMap { s ->
                Flux    // Publisher
                    .just(s + "x")
                    .doOnNext { println(it) }
                    .delaySubscription(Duration.ofSeconds(2))
            }

            .blockLast()

        Flux.fromIterable(items)
            .publishOn(Schedulers.boundedElastic())
            .flatMap { s ->
                Flux    // Publisher
                    .just(s + "x")
                    .doOnNext { println(it) }
                    .delaySubscription(Duration.ofSeconds(2))
            }
            .blockLast()
    }



    @Test
    fun testAppendBoomError() {
        fun appendBoomError(source: Flux<String>): Flux<String> {
            return source.concatWith(Mono.error(IllegalArgumentException("boom")))
        }

        StepVerifier.create(
            appendBoomError(Flux.just("thing1", "thing2"))
        )
            .expectNext("thing1")
            .expectNext("thing2")
            .expectErrorMessage("boom")
            .verify()
    }

    class SampleSubscriber<T> : BaseSubscriber<T>() {
        override fun hookOnSubscribe(subscription: Subscription) {
            request(100)
        }

        override fun hookOnNext(value: T) {
            println("${System.currentTimeMillis()} : ${Thread.currentThread()} : $value")
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

    class CharacterCreator {
        var consumer: Consumer<List<Char?>>? = null
        fun createCharacterSequence(): Flux<Char?> {
            return Flux.create { sink: FluxSink<Char?> ->
                consumer =
                    Consumer { items: List<Char?> ->
                        items.forEach { t: Char? -> sink.next(t!!) }
                    }
            }
        }
    }

    class CharacterGenerator {
        var consumer: Consumer<List<Char?>>? = null
        fun createCharacterSequence(): Flux<Char?> {
            return Flux.generate {
                consumer =
                    Consumer { items: List<Char?> ->
                        items.forEach { t: Char? -> it.next(t!!) }
                    }
            }
        }
    }

    @Test
    fun subscriberTest() {
        val characterCreator = CharacterCreator()
        val producerThread1 = Thread {
            characterCreator.consumer!!.accept(
                ('a'..'z').toList()
            )
        }
        val producerThread2 = Thread {
            characterCreator.consumer!!.accept(
                ('a'..'z').toList()
            )
        }

        val consolidated: MutableList<Char?> = ArrayList()
        characterCreator.createCharacterSequence().subscribe {
            consolidated.add(it)
        }

        producerThread1.start();
        producerThread2.start();
        producerThread1.join();
        producerThread2.join();

        println(consolidated)
    }

    @Test
    fun subscriberTest2() {
        val characterGenerator = CharacterGenerator()

        val producerThread1 = Thread {
            characterGenerator.consumer!!.accept(
                ('a'..'z').toList()
            )
        }
        val producerThread2 = Thread {
            characterGenerator.consumer!!.accept(
                ('a'..'z').toList()
            )
        }

        val consolidated: MutableList<Char?> = ArrayList()
        characterGenerator.createCharacterSequence().subscribe {
            consolidated.add(it)
        }

        producerThread1.start();
        producerThread2.start();
        producerThread1.join();
        producerThread2.join();

        println(consolidated)
    }

    private fun processOrFallback(source: Mono<String>, fallback: Publisher<String>) : Flux<String> {
        return source
            .flatMapMany { Flux.fromIterable(it.split(" ")) }
            .switchIfEmpty(fallback);
    }

    @Test
    fun testSplitPathIsUsed() {
        StepVerifier.create(
            processOrFallback(
                Mono.just("just a phrase with tabs!"),
                Mono.just("EMPTY_PHRASE")
            )
        )
            .expectNext("just", "a", "phrase", "with", "tabs!")
            .verifyComplete()
    }

    @Test
    fun testEmptyPathIsUsed() {
        StepVerifier.create(processOrFallback(Mono.empty(), Mono.just("EMPTY_PHRASE")))
            .expectNext("EMPTY_PHRASE")
            .verifyComplete()
    }

    @Test
    fun haha() {
        Flux.just(1,2,3,4,5,6,7,8,9)
            .doOnRequest { println(it) }
            .subscribe()
    }

}