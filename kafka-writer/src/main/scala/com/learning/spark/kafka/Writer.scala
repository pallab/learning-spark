package com.learning.spark.kafka

import org.apache.spark.sql.SparkSession


object Writer extends App{
  case class Tweet(msg: String)

  val session = SparkSession.builder().appName("TweetWriter").master("local[2]").getOrCreate()

  import session.implicits._

  val sqlContext = session.sqlContext

  val tweets = sqlContext.read.json("tweets").select( "msg").filter(!_.anyNull).toJSON
  tweets.take(2).foreach(println)
  println(tweets.count())
  val rs = tweets.mapPartitions{ part => {
    val kwriter = new KafkaWriter("tweets")
    val ts = part.toList
    println(s"cc -> ${ts.length}")
    ts.foreach{ s => kwriter.send(s) }
    kwriter.close
    Seq(ts.length).toIterator
  }}
  println(rs.collect())
  session.stop()
}
