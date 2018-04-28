package com.learning.spark.trainer

import org.apache.spark.mllib.feature.HashingTF
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.GradientBoostedTrees
import org.apache.spark.mllib.tree.configuration.{Algo, BoostingStrategy}
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.sql.SparkSession


object TweetTrainer extends App {

  //{"tweet_id":841809347768377344,"created_unixtime":1489537963226,"created_time":"Wed Mar 15 00:32:43 +0000 2017",
  // "lang":"en","displayname":"meantforpeace","time_zone":"Eastern Time (US & Canada)","msg":"ogmorgaan xo_rarebreed Happy Birthday boo"}
  case class Tweet(msg: String)

  val session = SparkSession.builder().appName("TweetTrainer").master("local[2]").getOrCreate()

  import session.implicits._

  val sqlContext = session.sqlContext

  val tweets = sqlContext.read.json("tweets").select("msg").filter(!_.anyNull).as[Tweet]
  tweets.cache()
  val labelledTweets = tweets.map(t =>
    if (t.msg.contains("happy"))
      (t.msg.replaceAll("happy", "").split(" ").map(_.trim), 1)
    else if (t.msg.contains("sad"))
      (t.msg.replaceAll("sad", "").split(" ").map(_.trim), 0)
    else (Array.empty[String], 1)
  ).filter(_._1.nonEmpty)

  val hashTf = new HashingTF(2000)

  val labeledPoints = labelledTweets.map { case (words, lab) =>
    new LabeledPoint(lab, hashTf.transform(words))
  }

  val split = labeledPoints.randomSplit(Array(0.7, 0.3))
  val (trainingData, validationData) = (split(0), split(1))
  trainingData.cache()

  val boostingStrategy = BoostingStrategy.defaultParams(Algo.Classification)
  boostingStrategy.setNumIterations(20)
  boostingStrategy.treeStrategy.setNumClasses(2)
  boostingStrategy.treeStrategy.setMaxDepth(5)

  val model = GradientBoostedTrees.train(trainingData.rdd, boostingStrategy)

  val testingPreds = validationData.map { t => (model.predict(t.features), t.label) }

  val metrics = new BinaryClassificationMetrics(testingPreds.rdd)

  metrics.precisionByThreshold().foreach { p =>
    println(s"Precision . Threshold =${p._1} -> ${p._2}")
  }

  metrics.recallByThreshold().foreach { p =>
    println(s"Recall . Threshold =${p._1} -> ${p._2}")
  }
  model.save(session.sparkContext, "tweet_model")
  session.stop()
}














