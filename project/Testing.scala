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
import sbt._
import sbt.Keys._

object Testing {
  val IntegrationTest = config("it") extend (Test)
  val EndToEndTest    = config("e2e") extend (Runtime)
  val all             = Seq(IntegrationTest, EndToEndTest)

  val testAll = TaskKey[Unit]("test-all")

  val testSettings = Seq(
    fork in Test := true,
    parallelExecution in Test := true
  )

  val itSettings = inConfig(IntegrationTest)(Defaults.testSettings) ++ Seq(
    fork in IntegrationTest := true,
    parallelExecution in IntegrationTest := false,
    scalaSource in IntegrationTest := baseDirectory.value / "src/it/scala"
  )

  val e2eSettings = inConfig(EndToEndTest)(Defaults.testSettings) ++ Seq(
    fork in EndToEndTest := false,
    parallelExecution in EndToEndTest := false,
    scalaSource in EndToEndTest := baseDirectory.value / "src/e2e/scala"
  )

  val settings = testSettings ++ itSettings ++ e2eSettings ++ Seq(
    testAll := (),
    testAll := testAll.dependsOn(test in EndToEndTest).value,
    testAll := testAll.dependsOn(test in IntegrationTest).value,
    testAll := testAll.dependsOn(test in Test).value
  )
}
