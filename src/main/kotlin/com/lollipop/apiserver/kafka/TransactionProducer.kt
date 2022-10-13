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


class TransactionProducer(bootstrapServers: String?) {
    private val log: Logger = LoggerFactory.getLogger(TransactionProducer::class.java.name)

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
        props[ProducerConfig.ACKS_CONFIG] = "all"
        props[ProducerConfig.TRANSACTIONAL_ID_CONFIG] = "TransactionalSend"
        val senderOptions = SenderOptions.create<Int, String>(props)
        sender = KafkaSender.create(senderOptions)
        dateFormat = DateTimeFormatter.ofPattern("HH:mm:ss:SSS z dd MMM yyyy")
    }

    fun sendMessages(latch: CountDownLatch) {
        sender!!.sendTransactionally(
            Flux.just("10", "20", "30", "40")
                .map { Flux.just(
                    SenderRecord.create("transaction.kafka.1", null, null, it.toInt(), "$it-X", it),
                    SenderRecord.create("transaction.kafka.2", null, null, it.toInt() + 1, "$it-Y", it),
                )}
        )
            .concatMap { r -> r }
            .doOnError { e: Throwable? -> log.error("Send failed", e) }
            .doOnCancel{ close() }
            .subscribe { result ->
                val metadata = result.recordMetadata()
                val timestamp = Instant.ofEpochMilli(metadata.timestamp())
                println(String.format( "Message %s sent successfully, topic-partition=%s-%s offset=%s timestamp=%s\n",
                    result.correlationMetadata().toString(),
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