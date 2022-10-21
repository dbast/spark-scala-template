/*
 * Copyright 2011-2016 Marconi Lanna
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

/*
 * Project metadata
 */

name := "PROJECT"

// enable versioning based on tags, see https://git-scm.com/docs/git-describe
// requires a full repo clone on the continuous integration machine (not a shallow clone)
enablePlugins(GitVersioning)
git.useGitDescribe := true

description := "PROJECT DESCRIPTION"
// organization := "org.example"
// organizationName := "Example, Inc."
// organizationHomepage := Some(url("http://example.org"))
// homepage := Some(url("http://project.org"))
startYear := Some(2011)

licenses += "Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.html")
// "GPLv2" -> url("http://www.gnu.org/licenses/gpl-2.0.html")

/*
 * scalac configuration
 */
// Use the same scala version Spark is build with, see scala.version in
// https://github.com/apache/spark/blob/master/pom.xml
ThisBuild / scalaVersion := "2.12.16"

compileOrder := CompileOrder.JavaThenScala

// Load test configuration and enable BuildInfo
lazy val root = Project("root", file("."))
  .configs(Testing.all: _*)
  .settings(Testing.settings)
  .enablePlugins(BuildInfoPlugin)
  .settings(
    buildInfoKeys := BuildInfoKey.ofN(name, version, scalaVersion, sbtVersion)
  )

// more memory Spark in local mode, see https://github.com/holdenk/spark-testing-base
javaOptions ++= Seq("-Xms512M", "-Xmx2048M", "-XX:+CMSClassUnloadingEnabled")

val commonScalacOptions = Seq(
  "-encoding",
  "UTF-8", // Specify character encoding used by source files
  "-target:jvm-1.8", // Target platform for object files
  "-Xexperimental", // Enable experimental extensions
  "-Xfuture" // Turn on future language features
//"-Ybackend:GenBCode" // Choice of bytecode emitter
)

val compileScalacOptions = Seq(
  "-deprecation", // Emit warning and location for usages of deprecated APIs
  "-feature", // Emit warning and location for usages of features that should be imported explicitly
  "-g:vars",  // Set level of generated debugging info: none, source, line, vars, notailcalls
//"-language:_" // Enable or disable language features (see list below)
//"-optimise",  // Generates faster bytecode by applying optimisations to the program
  "-unchecked", // Enable additional warnings where generated code depends on assumptions
//"-Xdev" // Indicates user is a developer - issue warnings about anything which seems amiss
  "-Xfatal-warnings", // Fail the compilation if there are any warnings
  "-Xlint:_", // Enable or disable specific warnings (see list below)
  "-Xstrict-inference", // Don't infer known-unsound types
//"-Yclosure-elim", // Perform closure elimination
//"-Yconst-opt", // Perform optimization with constant values
//"-Ydead-code", // Perform dead code elimination
//"-Yinline", // Perform inlining when possible
// "-Yinline-handlers", // Perform exception handler inlining when possible
//"-Yinline-warnings", // Emit inlining warnings
  "-Yno-adapted-args", // Do not adapt an argument list to match the receiver
//"-Yno-imports" // Compile without importing scala.*, java.lang.*, or Predef
//"-Yno-predef" // Compile without importing Predef
//"-Yopt:_", // Enable optimizations (see list below)
  "-Ywarn-dead-code", // Warn when dead code is identified
  "-Ywarn-numeric-widen", // Warn when numerics are widened
  "-Ywarn-unused", // Warn when local and private vals, vars, defs, and types are unused
  "-Ywarn-unused-import", // Warn when imports are unused
  "-Ywarn-value-discard" // Warn when non-Unit expression results are unused
)

scalacOptions ++= commonScalacOptions ++ compileScalacOptions ++ Seq(
  "-Ywarn-value-discard" // Warn when non-Unit expression results are unused
)

Test / compile / scalacOptions := commonScalacOptions ++ compileScalacOptions

Compile / console / scalacOptions := commonScalacOptions ++ Seq(
  "-language:_", // Enable or disable language features (see list below)
  "-nowarn" // Generate no warnings
)

Test / console / scalacOptions  := (Compile / console / scalacOptions).value

// Have fullClasspath during compile, test and run, but don't assemble what is marked provided
// https://github.com/sbt/sbt-assembly#-provided-configuration
Compile /run := Defaults
  .runTask(Compile / fullClasspath, Compile / run / mainClass,  Compile / run / runner)
  .evaluated

/*
scalac -language:help

dynamics             Allow direct or indirect subclasses of scala.Dynamic
existentials         Existential types (besides wildcard types) can be written and inferred
experimental.macros  Allow macro definition (besides implementation and application)
higherKinds          Allow higher-kinded types
implicitConversions  Allow definition of implicit functions called views
postfixOps           Allow postfix operator notation, such as `1 to 10 toList'
reflectiveCalls      Allow reflective access to members of structural types

 */ /*

scalac -Xlint:help

adapted-args               Warn if an argument list is modified to match the receiver
by-name-right-associative  By-name parameter of right associative operator
delayedinit-select         Selecting member of DelayedInit
doc-detached               A Scaladoc comment appears to be detached from its element
inaccessible               Warn about inaccessible types in method signatures
infer-any                  Warn when a type argument is inferred to be `Any`
missing-interpolator       A string literal appears to be missing an interpolator id
nullary-override           Warn when non-nullary `def f()' overrides nullary `def f'
nullary-unit               Warn when nullary methods return Unit
option-implicit            Option.apply used implicit view
package-object-classes     Class or object defined in package object
poly-implicit-overload     Parameterized overloaded implicit methods are not visible as view bounds
private-shadow             A private field (or class parameter) shadows a superclass field
stars-align                Pattern sequence wildcard must align with sequence component
type-parameter-shadow      A local type parameter shadows a type already in scope
unsound-match              Pattern match may not be typesafe

 */ /*

scalac -Yopt:help

compact-locals      Eliminate empty slots in the sequence of local variables
empty-labels        Eliminate and collapse redundant labels in the bytecode
empty-line-numbers  Eliminate unnecessary line number information
inline-global       Inline methods from any source, including classfiles on the compile classpath
inline-project      Inline only methods defined in the files being compiled
nullness-tracking   Track nullness / non-nullness of local variables and apply optimizations
simplify-jumps      Simplify branching instructions, eliminate unnecessary ones
unreachable-code    Eliminate unreachable code, exception handlers protecting no instructions, debug information of eliminated variables
l:none              Don't enable any optimizations
l:default           Enable default optimizations: unreachable-code
l:method            Enable intra-method optimizations: unreachable-code,simplify-jumps,empty-line-numbers,empty-labels,compact-locals,nullness-tracking
l:project           Enable cross-method optimizations within the current project: l:method,inline-project
l:classpath         Enable cross-method optimizations across the entire classpath: l:project,inline-global
 */

/*
 * Managed dependencies
 */
val sparkVersion           = "3.3.0"
val clusterDependencyScope = "provided"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion % clusterDependencyScope,
  "org.apache.spark" %% "spark-sql"  % sparkVersion % clusterDependencyScope,
  // "org.typelevel"             %% "frameless-dataset" % "0.4.0",
  // "org.apache.hadoop"          % "hadoop-aws"     % "2.7.3" % clusterDependencyScope,
  // "org.apache.hadoop"          % "hadoop-client"  % "2.7.3" % clusterDependencyScope,
  //"org.vegas-viz"              %% "vegas-spark"   % "0.3.11",
  "org.slf4j"                  % "slf4j-log4j12"  % "2.0.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
  "org.rogach"                 %% "scallop"       % "4.1.0"
).map(_.exclude("ch.qos.logback", "logback-classic"))

libraryDependencies ++= Seq(
  "org.scalatest"    %% "scalatest"          % "3.2.14",
  "com.holdenkarau"  %% "spark-testing-base" % "3.3.0_1.2.0",
  "org.apache.spark" %% "spark-hive"         % sparkVersion // required by spark-testing-base
  // "org.scalacheck"    %% "scalacheck"                  % "1.13.5",
  // "org.scalamock"     %% "scalamock-scalatest-support" % "3.6.0",
  // "com.storm-enroute" %% "scalameter"                  % "0.8.2",
  // "es.ucm.fdi"        %% "sscheck"                     % "0.3.2",
) map (_ % Test)

/*
 * sbt options
 */

// Add task to check for sbt plugin updates
addCommandAlias("pluginUpdates", "; reload plugins; dependencyUpdates; reload return")

// Statements executed when starting the Scala REPL (sbt's `console` task)
initialCommands := """
import
  project.Functions._,
  project.Processing,
  project.Steps,
  org.apache.spark.sql.SparkSession,
  scala.annotation.{switch, tailrec},
  scala.beans.{BeanProperty, BooleanBeanProperty},
  scala.collection.JavaConverters._,
  scala.collection.{breakOut, mutable},
  scala.concurrent.{Await, ExecutionContext, Future},
  scala.concurrent.ExecutionContext.Implicits.global,
  scala.concurrent.duration._,
  scala.language.experimental.macros,
  scala.math._,
  scala.reflect.macros.blackbox,
  scala.util.{Failure, Random, Success, Try},
  scala.util.control.NonFatal,
  java.io._,
  java.net._,
  java.nio.file._,
  java.time.{Duration => jDuration, _},
  java.lang.System.{currentTimeMillis => now},
  java.lang.System.nanoTime

val sparkNodes = sys.env.getOrElse("SPARK_NODES", "local[*]")

def desugarImpl[T](c: blackbox.Context)(expr: c.Expr[T]): c.Expr[Unit] = {
  import c.universe._, scala.io.AnsiColor.{BOLD, GREEN, RESET}

  val exp = show(expr.tree)
  val typ = expr.actualType.toString takeWhile '('.!=

  println(s"$exp: $BOLD$GREEN$typ$RESET")
  reify { (): Unit }
}

def desugar[T](expr: T): Unit = macro desugarImpl[T]

var _sparkInitialized = false
@transient lazy val spark = {
  _sparkInitialized = true
  SparkSession.builder
    .master(sparkNodes)
    .appName("Console test")
    .getOrCreate()
}
@transient lazy val sc = spark.sparkContext
"""

console / cleanupCommands := """
if (_sparkInitialized) {spark.stop()}
"""

// Do not exit sbt when Ctrl-C is used to stop a running app
Global / cancelable := true

// Improved dependency management
updateOptions := updateOptions.value.withCachedResolution(true)

// Uncomment to enable offline mode
// offline := true

// Download and create Eclipse source attachments for library dependencies
// EclipseKeys.withSource := true

// Enable colors in Scala console (2.11.4+)
initialize ~= { _ =>
  val ansi = System.getProperty("sbt.log.noformat", "false") != "true"
  if (ansi) System.setProperty("scala.color", "true")
}

// Draw a separator between triggered runs (e.g, ~test)
triggeredMessage := { ws =>
  if (ws.count > 1) {
    val ls = System.lineSeparator * 2
    ls + "#" * 100 + ls
  } else { "" }
}

/*
 * Scalastyle: http://www.scalastyle.org/
 */
scalastyleConfig := baseDirectory.value / "project" / "scalastyle-config.xml"
scalastyleFailOnError := true

// Create a default Scalastyle task to run with tests
lazy val mainScalastyle = taskKey[Unit]("mainScalastyle")
lazy val testScalastyle = taskKey[Unit]("testScalastyle")

mainScalastyle := (Compile / scalastyle).toTask("").value
testScalastyle := (Test / scalastyle).toTask("").value

(Test / test) := ((Test / test) dependsOn testScalastyle).value
(Test / test) := ((Test / test) dependsOn mainScalastyle).value

/*
 * sbt-assembly https://github.com/sbt/sbt-assembly
 */
assembly / test := {}
// scala-library is provided by spark cluster execution environment
assembly / assemblyOption := (assembly / assemblyOption).value.withIncludeScala(false)

/*
 * WartRemover: http://github.com/wartremover/wartremover
 */
wartremoverErrors ++= Seq(
  Wart.Any,
  Wart.ArrayEquals,
  Wart.AsInstanceOf,
  Wart.DefaultArguments,
  Wart.EitherProjectionPartial,
  Wart.Enumeration,
//Wart.Equals,
  Wart.ExplicitImplicitTypes,
//Wart.FinalCaseClass,
  Wart.FinalVal,
  Wart.ImplicitConversion,
//Wart.ImplicitParameter,
  Wart.IsInstanceOf,
  Wart.JavaConversions,
  Wart.LeakingSealed,
  Wart.MutableDataStructures,
//Wart.NonUnitStatements,
  Wart.Nothing,
  Wart.Null,
  Wart.Option2Iterable,
  Wart.OptionPartial,
  Wart.Overloading,
  Wart.Product,
//Wart.PublicInference,
//Wart.Recursion,
  Wart.Return,
  Wart.Serializable,
  Wart.StringPlusAny,
  Wart.Throw,
  Wart.ToString,
  Wart.TryPartial,
  Wart.Var,
  Wart.While,
  ContribWart.OldTime,
  ContribWart.SealedCaseClass,
  ContribWart.SomeApply,
  ExtraWart.EnumerationPartial,
  ExtraWart.FutureObject,
  ExtraWart.GenMapLikePartial,
  ExtraWart.GenTraversableLikeOps,
  ExtraWart.GenTraversableOnceOps,
  ExtraWart.ScalaGlobalExecutionContext,
  ExtraWart.StringOpsPartial,
  ExtraWart.TraversableOnceOps
)

/*
 * Scapegoat: http://github.com/sksamuel/scapegoat
 */
ThisBuild / scapegoatVersion := "1.4.15"
scapegoatDisabledInspections := Seq.empty
scapegoatIgnoredFiles := Seq.empty

// Create a default Scapegoat task to run with tests
lazy val mainScapegoat = taskKey[Unit]("mainScapegoat")
lazy val testScapegoat = taskKey[Unit]("testScapegoat")

mainScapegoat := (Compile / scapegoat).value
testScapegoat := (Test / scapegoat).value

(Test / test) := ((Test / test) dependsOn testScapegoat).value
(Test / test) := ((Test / test) dependsOn mainScapegoat).value

/*
 * Linter: http://github.com/HairyFotr/linter
 */

addCompilerPlugin("org.psywerx.hairyfotr" %% "linter" % "0.1.17")

scalacOptions += "-P:linter:enable-only:" +
"AssigningOptionToNull+" +
"AvoidOptionCollectionSize+" +
"AvoidOptionMethod+" +
"AvoidOptionStringSize+" +
"BigDecimalNumberFormat+" +
"BigDecimalPrecisionLoss+" +
"CloseSourceFile+" +
"ContainsTypeMismatch+" +
"DecomposingEmptyCollection+" +
"DivideByOne+" +
"DivideByZero+" +
"DuplicateIfBranches+" +
"DuplicateKeyInMap+" +
"EmptyStringInterpolator+" +
"FilterFirstThenSort+" +
"FloatingPointNumericRange+" +
"FuncFirstThenMap+" +
"IdenticalCaseBodies+" +
"IdenticalCaseConditions+" +
"IdenticalIfCondition+" +
"IdenticalIfElseCondition+" +
"IdenticalStatements+" +
"IfDoWhile+" +
"IndexingWithNegativeNumber+" +
"InefficientUseOfListSize+" +
"IntDivisionAssignedToFloat+" +
"InvalidParamToRandomNextInt+" +
"InvalidStringConversion+" +
"InvalidStringFormat+" +
"InvariantCondition+" +
"InvariantExtrema+" +
"InvariantReturn+" +
"JavaConverters+" +
"LikelyIndexOutOfBounds+" +
"MalformedSwap+" +
"MergeMaps+" +
"MergeNestedIfs+" +
"ModuloByOne+" +
"NumberInstanceOf+" +
"OnceEvaluatedStatementsInBlockReturningFunction+" +
"OperationAlwaysProducesZero+" +
"OptionOfOption+" +
"PassPartialFunctionDirectly+" +
"PatternMatchConstant+" +
"PossibleLossOfPrecision+" +
"PreferIfToBooleanMatch+" +
"ProducesEmptyCollection+" +
"ReflexiveAssignment+" +
"ReflexiveComparison+" +
"RegexWarning+" +
"StringMultiplicationByNonPositive+" +
"SuspiciousMatches+" +
"SuspiciousPow+" +
"TransformNotMap+" +
"TypeToType+" +
"UndesirableTypeInference+" +
"UnextendedSealedTrait+" +
"UnitImplicitOrdering+" +
"UnlikelyEquality+" +
"UnlikelyToString+" +
"UnnecessaryMethodCall+" +
"UnnecessaryReturn+" +
"UnnecessaryStringIsEmpty+" +
"UnnecessaryStringNonEmpty+" +
"UnsafeAbs+" +
"UnthrownException+" +
"UnusedForLoopIteratorValue+" +
"UnusedParameter+" +
"UseAbsNotSqrtSquare+" +
"UseCbrt+" +
"UseConditionDirectly+" +
"UseContainsNotExistsEquals+" +
"UseCountNotFilterLength+" +
"UseExistsNotCountCompare+" +
"UseExistsNotFilterIsEmpty+" +
"UseExistsNotFindIsDefined+" +
"UseExp+" +
"UseExpm1+" +
"UseFilterNotFlatMap+" +
"UseFindNotFilterHead+" +
"UseFlattenNotFilterOption+" +
"UseFuncNotFold+" +
"UseFuncNotReduce+" +
"UseFuncNotReverse+" +
"UseGetOrElseNotPatMatch+" +
"UseGetOrElseOnOption+" +
"UseHeadNotApply+" +
"UseHeadOptionNotIf+" +
"UseHypot+" +
"UseIfExpression+" +
"UseInitNotReverseTailReverse+" +
"UseIsNanNotNanComparison+" +
"UseIsNanNotSelfComparison+" +
"UseLastNotApply+" +
"UseLastNotReverseHead+" +
"UseLastOptionNotIf+" +
"UseLog10+" +
"UseLog1p+" +
"UseMapNotFlatMap+" +
"UseMinOrMaxNotSort+" +
"UseOptionExistsNotPatMatch+" +
"UseOptionFlatMapNotPatMatch+" +
"UseOptionFlattenNotPatMatch+" +
"UseOptionForallNotPatMatch+" +
"UseOptionForeachNotPatMatch+" +
"UseOptionGetOrElse+" +
"UseOptionIsDefinedNotPatMatch+" +
"UseOptionIsEmptyNotPatMatch+" +
"UseOptionMapNotPatMatch+" +
"UseOptionOrNull+" +
"UseOrElseNotPatMatch+" +
"UseQuantifierFuncNotFold+" +
"UseSignum+" +
"UseSqrt+" +
"UseTakeRightNotReverseTakeReverse+" +
"UseUntilNotToMinusOne+" +
"UseZipWithIndexNotZipIndices+" +
"VariableAssignedUnusedValue+" +
"WrapNullWithOption+" +
"YodaConditions+" +
"ZeroDivideBy"

/*
 * scoverage: http://github.com/scoverage/sbt-scoverage
 */
coverageMinimumStmtTotal := 90
coverageFailOnMinimum := false
coverageOutputCobertura := true
coverageOutputHTML := true
coverageOutputXML := true

/*
 * Scalafmt: http://github.com/lucidsoftware/neo-sbt-scalafmt
 */
ThisBuild / scalafmtConfig := baseDirectory.value / "project" / "scalafmt.conf"
ThisBuild / scalafmtOnCompile := true

/*
 * Scaladoc options
 */
autoAPIMappings := true
Compile / doc / scalacOptions ++= Seq("-groups", "-implicits") // "-diagrams")
