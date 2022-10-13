package com.lollipop.apiserver.kafka

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.IntegerSerializer
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderOptions
import reactor.kafka.sender.SenderRecord
import reactor.kafka.sender.SenderResult
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.concurrent.CountDownLatch


class SampleProducer(bootstrapServers: String?) {
    private val log: Logger = LoggerFactory.getLogger(SampleProducer::class.java.name)

    private val BOOTSTRAP_SERVERS = "localhost:9092"
    private val TOPIC = "demo-topic"

    private var sender: KafkaSender<Int, String>? = null
    private var dateFormat: DateTimeFormatter? = null

    init {
        val props: MutableMap<String, Any> = HashMap()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers ?: BOOTSTRAP_SERVERS
        props[ProducerConfig.CLIENT_ID_CONFIG] = "sample-producer"
        props[ProducerConfig.ACKS_CONFIG] = "all"
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = IntegerSerializer::class.java
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        val senderOptions = SenderOptions.create<Int, String>(props)
        sender = KafkaSender.create(senderOptions)
        dateFormat = DateTimeFormatter.ofPattern("HH:mm:ss:SSS z dd MMM yyyy")
    }

    @Throws(InterruptedException::class)
    fun sendMessages(topic: String?, count: Int, latch: CountDownLatch) {
        sender!!.send(Flux.range(1, count)
            .map { i: Int ->
                SenderRecord.create(
                    ProducerRecord(topic, i, "Message_$i"),
                    i
                )
            })
            .doOnError { e: Throwable? -> log.error("Send failed", e) }
            .subscribe { r: SenderResult<Int> ->
                val metadata = r.recordMetadata()
                val timestamp = Instant.ofEpochMilli(metadata.timestamp())
                println(String.format( "Message %s sent successfully, topic-partition=%s-%s offset=%s timestamp=%s\n",
                    r.correlationMetadata().toString(),
                    metadata.topic().toString(),
                    metadata.partition().toString(),
                    metadata.offset().toString(),
                    timestamp.toString()
                ))
                latch.countDown()
            }
    }

    fun test() {
        Flux.just("asdf", "zxcv").subscribe()
    }

    fun close() {
        sender!!.close()
    }
}