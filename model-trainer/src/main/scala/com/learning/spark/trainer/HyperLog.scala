package com.learning.spark.trainer


import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

/**
  * demoes using hyperlolog implementation in spark for counting distinct elements in a df
  */
object  HyperLog extends App{

  val session = SparkSession.builder().appName("HyperLog").master("local[2]").getOrCreate()

  val sqlContext = session.sqlContext

  val tweets = sqlContext.read.json("tweets")
  tweets.cache()

  //count using HyperLogLog
  val apps = tweets.select(approx_count_distinct("displayname", rsd=0.01).alias("Approx Count"))
  val appxCount = apps.collect()
  //exact count
  val exct =   tweets.select(countDistinct("displayname").alias("Exact Count"))
  val exactCount = exct.collect()

  println(apps.columns.head + " --> " +  appxCount.head.mkString)
  println(exct.columns.head + " --> " +  exactCount.head.mkString)
  session.stop()
}
