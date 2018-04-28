package com.leaning.spark.server

import org.apache.spark.mllib.feature.HashingTF
import org.apache.spark.mllib.tree.GradientBoostedTrees
import org.apache.spark.mllib.tree.model.GradientBoostedTreesModel
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{DataType, StructType}

object StreamConsumer extends App {

  val session = SparkSession.builder().appName("TweetConsumer").master("local[2]").getOrCreate()

  import session.implicits._

  case class Tweet(msg : String){ override def toString = s"Tweet is - $msg"}
  val tweetStruck = new StructType().add("msg", "string")

  val model = GradientBoostedTreesModel.load(session.sparkContext, "tweet_model")

  val df = session.readStream.format("kafka")
    .option("kafka.bootstrap.servers", "localhost:9092")
    .option("subscribe", "tweets")
    .option("startingOffsets", "earliest")
    .load()

  val tweets = df.selectExpr("CAST(value AS STRING)")
      .select(from_json($"value", tweetStruck) as "json")
      .select("json.*") // flatten the struct field
     .as[Tweet]

  val hashTf = new HashingTF(2000)
  def getFeatureVector(msg : String) = {
    val words = if (msg.contains("happy")) msg.replaceAll("happy", "").split(" ").map(_.trim)
    else if (msg.contains("sad")) msg.replaceAll("sad", "").split(" ").map(_.trim)
    else msg.split(" ").map(_.trim)
    hashTf.transform(words)
  }

  val preds = tweets.map(t => ( model.predict(getFeatureVector(t.msg)), t.msg))

  val rs = preds.writeStream.format("console").outputMode("append").start()

  rs.processAllAvailable()
  session.stop()
}
