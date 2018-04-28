import Build._

name := "learning-spark"

enablePlugins(JavaAppPackaging, UniversalPlugin)

lazy val modelTrainer = (project in file("./model-trainer"))
  .settings(modelTrainerDependencies)
  .enablePlugins(JavaAppPackaging)

lazy val modelServer = (project in file("./model-server"))
.settings(modelServerDependencies)
.enablePlugins(JavaAppPackaging)

lazy val kafkaWriter = (project in file("./kafka-writer"))
  .settings(kafkaWriterDependencies)
  .enablePlugins(JavaAppPackaging)
