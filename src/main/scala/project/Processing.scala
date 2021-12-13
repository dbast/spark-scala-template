/*
 * Copyright 2017 Daniel Bast
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package project

import buildinfo.BuildInfo
import com.typesafe.scalalogging.LazyLogging
import org.apache.spark.sql.{ Dataset, Row, SparkSession }
import org.rogach.scallop._

class Processing(spark: SparkSession) extends LazyLogging {

  val steps = new Steps(spark)

  val dagSteps = Vector(
    steps.decodeData _,
    steps.selectFinalFields _
  )

  def process(
      input: String,
      output: String,
      limit: Option[Int],
      lines: Option[Int],
      debug: Boolean
  ): Unit = {
    logger.info("Starting processing")

    val rawData = steps.read(input)
    logger.debug(s"The schema is now: ${rawData.schema.treeString}")

    val fullData = combineSteps(dagSteps, rawData, stepLimit = limit, debug)
    steps.writeOrShowData(fullData, output, linesToShow = lines)
    logger.info("Finished processing")
  }

  /**
    * Combines list of steps and print
    *
    * @param dagSteps The dag steps to combine
    * @param df The input Dataframe
    * @param limit If set, Limits amount of combined steps to given number
    * @param debug If true, prints the physical and logical plan
    */
  @SuppressWarnings(Array("VariableShadowing"))
  def combineSteps(
      dagSteps: Vector[Dataset[Row] => Dataset[Row]],
      df: Dataset[Row],
      stepLimit: Option[Int],
      debug: Boolean
  ) =
    dagSteps
      .slice(0, stepLimit.getOrElse(dagSteps.size))
      .foldLeft(df) { (dataframe, step) =>
        val rf = step(dataframe)
        logger.debug(s"The schema is now: ${rf.schema.treeString}")
        if (debug) rf.explain(true)
        rf
      }
}

/**
  * Main cli parsing class
  *
  * @param arguments The unparsed command line arguments
  */
class Conf(arguments: Seq[String]) extends ScallopConf(arguments) {
  appendDefaultToDescription = true
  val nodes = opt[String](descr = "Spark nodes (local run)", default = Option("local[*]"))
  val stay = opt[Boolean](
    descr =
      "Wait for key press to exit (to keep SparkSession and webserver running while debugging)",
    default = Option(false)
  )

  val input = opt[String](
    descr = "Path to the raw data to process (local, hdfs, s3)",
    required = true
  )
  val output = opt[String](descr = "Output path (local, hdfs, s3)", required = true)
  val limit =
    opt[Int](
      descr = "Limit DAG steps to given number, the read and write/show steps are always added"
    )
  val linesToShow = opt[Int](
    descr =
      "Amount of lines to shows to the console (instead of writing snappy compressed parquet files)"
  )
  val debug = opt[Boolean](
    descr = "Explains plan during DAG construction",
    default = Option(false)
  )

  verify()
}

/**
  * Main entrypoint, parses the command line arguments, gets or creates the
  * SparkSession und starts the DAG processing.
  *
  * @param args The unparsed command line arguments
  */
object Processing extends LazyLogging {
  def main(args: Array[String]): Unit = {
    logger.info(s"Starting '${BuildInfo.name}' version '${BuildInfo.version}'")

    val conf = new Conf(args)
    logger.info(s"The command line parameters are: ${conf.summary}")

    lazy val spark = SparkSession.builder
      .master(conf.nodes())
      .appName(BuildInfo.name)
      .getOrCreate()

    val processing = new Processing(spark)
    try processing.process(
      input = conf.input(),
      output = conf.output(),
      limit = conf.limit.toOption,
      lines = conf.linesToShow.toOption,
      debug = conf.debug()
    )
    finally {
      if (conf.stay()) {
        logger.info("Waiting: press enter to exit")
        logger.info(System.in.read().toString)
      }
      spark.stop()
    }
  }
}
