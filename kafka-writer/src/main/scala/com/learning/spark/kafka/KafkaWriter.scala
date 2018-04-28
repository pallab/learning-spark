package com.learning.spark.kafka

import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

class KafkaWriter(topic: String) {
  println(s"Created Kaftka")
  private val jsonProps = new Properties()
  jsonProps.put("bootstrap.servers", "localhost:9092")
  jsonProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  jsonProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  private val producer = new KafkaProducer[String, String](jsonProps)

  def send(v : String) = producer.send(new ProducerRecord(topic, v))

  def close = producer.close()
}
