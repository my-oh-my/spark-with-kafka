package com.myself.app.kafka.spark

import org.apache.spark.sql.{Dataset, SparkSession}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.functions.{col, lit}

object KafkaStructuredStreaming extends App {

  Logger.getLogger("org").setLevel(Level.OFF)

  implicit val spark: SparkSession = SparkSessionWrapper(
    Some(
      Map(
        "spark.master" -> "local[2]",
        "spark.sql.streaming.schemaInference" -> "true"
      ))
  )
  import spark.implicits._

  // https://spark.apache.org/docs/latest/structured-streaming-programming-guide.html#schema-inference-and-partition-of-streaming-dataframesdatasets
  //
  val ds = spark
    .readStream
    .format("kafka")
    .option("kafka.bootstrap.servers", "localhost:29092")
    .option("subscribe", "messages")
    .option("startingOffsets", "earliest")
//    .option("includeHeaders", "true")
    .load()
    .selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)") //, "headers")
    .select(col("value")).as[String]

  val t_1 = ds
    .writeStream
    .format("console")
    .start()

  spark.read.json(ds)
//  println(ds.schema.prettyJson)

//  val test = spark.read.json(df.as[String])
//  println(test.schema.prettyJson)

//  spark.streams.awaitAnyTermination()
  t_1.awaitTermination()
}
