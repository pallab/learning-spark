A repo for learning about:
- Spark in general
- Spark MLLIB
- Spark structured streaming
- Kafka
- Spark GraphX


Has the following componenets:
- Model trainer : Batch mode trainer for ML. Saves the trained model to disk
- Model Server : Loads saved model from disk, performs prediction on streaming data from Kafka
- Kafka Writer : Writes data to Kafka for later consumption as stream

