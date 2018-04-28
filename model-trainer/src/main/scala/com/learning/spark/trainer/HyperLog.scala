package com.learning.spark.trainer


import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

/**
  * demoes using hyperlolog implementation in spark for counting distinct elements in a df
  */
object  HyperLog extends App{

  val session = SparkSession.builder().appName("HyperLog").master("local[2]").getOrCreate()

  import session.implicits._

  val sqlContext = session.sqlContext

  val tweets = sqlContext.read.json("tweets")

  val apps = tweets.select(approx_count_distinct("displayname", rsd=0.01))
  val exct =   tweets.select(countDistinct("displayname"))
  apps.show(5)
  exct.show(2)

  session.stop()
}
