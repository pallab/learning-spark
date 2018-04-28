import sbt._
import Keys._
import com.typesafe.sbt.packager.Keys._

object Build {

  val release = "0.1"

  val scala = "2.11.11"

  lazy val commonSettings =  Defaults.defaultConfigs ++ Seq(
    organization := "com.learn-spark",
    version := release,
    scalaVersion := scala,
    publishMavenStyle := true,
    mappings in(Compile, packageDoc) := Seq(),
    publishArtifact in Test := false,
    publishArtifact in(Compile, packageDoc) := false,
    publishArtifact in(Compile, packageSrc) := false,
    fork in test := false,
    scalacOptions ++= Seq(
      "-feature",
      "-unchecked",
      "-deprecation",
      "-language:_",
      //"-Xlint:_",
      "-encoding", "UTF-8")
  )
  lazy val sparkVersion = "2.3.0"
  lazy val jacksonVersion = "2.9.5"

  lazy val sparkCore = "org.apache.spark" % "spark-core_2.11" % sparkVersion exclude ("net.jpountz.lz4", "lz4")
  lazy val sparkSql = "org.apache.spark" % "spark-sql_2.11" % sparkVersion exclude ("net.jpountz.lz4", "lz4")
  lazy val sparkMlib = "org.apache.spark" % "spark-mllib_2.11" % sparkVersion exclude ("net.jpountz.lz4", "lz4")
  lazy val sparkStream = "org.apache.spark" % "spark-streaming_2.11" % sparkVersion exclude ("net.jpountz.lz4", "lz4")
  lazy val sparkLaunch = "org.apache.spark" % "spark-launcher_2.11" % sparkVersion exclude ("net.jpountz.lz4", "lz4")
  lazy val sparkKafkaStrm = "org.apache.spark" % "spark-streaming-kafka-0-10_2.11" % sparkVersion exclude ("net.jpountz.lz4", "lz4")
  lazy val sparkKafkaStrmASql = "org.apache.spark" % "spark-sql-kafka-0-10_2.11" % sparkVersion exclude ("net.jpountz.lz4", "lz4")
  lazy val lz4 = "net.jpountz.lz4" % "lz4" % "1.3.0"

  lazy val kafka =  "org.apache.kafka" %% "kafka" % "1.1.0" //exclude("com.fasterxml.jackson", "databind")
  lazy val kafkaClient = "org.apache.kafka" % "kafka-clients" % "1.1.0" //exclude("com.fasterxml.jackson", "databind")

  lazy val jacksonCore = "com.fasterxml.jackson.core" % "jackson-core" % jacksonVersion
  lazy val jacksonBind = "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion
  lazy val jacksonMod = "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % jacksonVersion

  lazy val spark = Seq(sparkCore,sparkSql,sparkMlib,sparkStream,sparkLaunch,sparkKafkaStrm,sparkKafkaStrmASql,lz4)
  lazy val kafkaz = Seq(kafka, kafkaClient)
  lazy val jackson = Seq(jacksonCore, jacksonBind, jacksonMod)


  lazy val modelTrainerDependencies = Seq(name:="modelTrainer") ++ commonSettings ++ (libraryDependencies ++=spark)

  lazy val modelServerDependencies = Seq(name:="modelServer") ++ commonSettings ++ (libraryDependencies ++=spark)

  lazy val kafkaWriterDependencies = Seq(name:="kafkaWriter") ++ commonSettings ++ (libraryDependencies ++=kafkaz++spark++jackson)
}
