package com.myself.app.kafka.spark

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.types.StringType

object KafkaStructured extends App {

  Logger.getLogger("org").setLevel(Level.OFF)

  implicit val spark: SparkSession = SparkSessionWrapper(
    Some(
      Map(
        "spark.master" -> "local[2]",
        "spark.sql.streaming.schemaInference" -> "true"
      ))
  )
  spark.sparkContext.setLogLevel("OFF")

  import spark.implicits._

  // https://spark.apache.org/docs/latest/structured-streaming-kafka-integration.html
  //
  val df = spark
    .read
    .format("kafka")
    .option("kafka.bootstrap.servers", "localhost:9092")
    .option("subscribe", "messages")
    .option("startingOffsets", "earliest") // "latest" is not possible with batch processing from Kafka
    .load()

  val value_ds = df.select(col("value").cast(StringType)).as[String]
  val from_json_df = spark.read.json(value_ds)

  println(from_json_df.schema.prettyJson)

}
