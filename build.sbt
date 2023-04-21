name := "spark-with-kafka"

version := "0.1"

scalaVersion := "2.13.10"

val sparkVersion = "3.3.2"
val hadoopVersion = "3.2.2"

val kafkaVersion = "3.4.0"

val log4jVersion = "1.2.17"
val sl4jVersion = "1.7.32"
val jodaTimeVersion = "2.10.13"
val jacksonVersion = "2.13.1"
val jsonAssertVersion = "1.5.0"
val commonsLangVersion = "3.12.0"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-catalyst" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-sql" % sparkVersion % "provided")
  .map(_.excludeAll(
    ExclusionRule(organization = "org.scalacheck"),
    ExclusionRule(organization = "org.scalactic"),
    ExclusionRule(organization = "org.scalatest")
  ))

libraryDependencies ++= Seq(
  "org.apache.kafka" %% "kafka" % kafkaVersion % "provided",
  "org.apache.kafka" % "kafka-clients" % kafkaVersion % "provided",
//  "org.apache.spark" %% "spark-streaming" % "3.3.2" % "provided",
  "org.apache.spark" %% "spark-sql-kafka-0-10" % "3.3.2" % "provided"
)

libraryDependencies ++= Seq(
  "log4j" % "log4j" % log4jVersion % "provided",
  "org.slf4j" % "slf4j-api" % sl4jVersion % "provided",
  "joda-time" % "joda-time" % jodaTimeVersion,
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion,
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonVersion,
  "org.skyscreamer" % "jsonassert" % jsonAssertVersion % Test,
  "org.apache.commons" % "commons-lang3" % commonsLangVersion % Test
)

assembly / assemblyJarName := "spark-for-kafka.jar"
assembly / assemblyMergeStrategy := {
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

Compile / publishArtifact := false
Compile / run  := Defaults.runTask(Compile / fullClasspath , Compile / run / mainClass, Compile / run / runner)
Compile / runMain := Defaults.runMainTask(Compile / fullClasspath , Compile / run / runner)
scalacOptions ++= Seq("-feature", "-deprecation")

Test / fork := false
Test / javaOptions ++= Seq("-Xms1G", "-Xmx4096M", "-XX:MaxPermSize=2048M", "-XX:+CMSClassUnloadingEnabled")
Test / parallelExecution := false

publish / packagedArtifacts := {
  val artifacts: Map[sbt.Artifact, java.io.File] = (publish / packagedArtifacts).value
  artifacts + (Artifact("spark", "jar", "jar") -> baseDirectory.value / "/target" / "scala-2.13" / "spark-for-kafka.jar")
}