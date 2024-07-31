Spark Scala Template
====================

Assortment of default settings, best practices, and general goodies for Scala projects.

Layers
------

There are 3 layers:
* The reusable shared DAG steps, implemented via (SQL like) Dataset API
* The user defined functions used by the steps
* A command with cli, that builds a whole DAG by combining steps

The cli
-------

```
-d, --debug                  Explains plan during DAG construction
-i, --input  <arg>           Path to the raw data to process (local, hdfs, s3)
-l, --limit  <arg>           Limit DAG steps to given number, the read and
                             write/show steps are always added
    --lines-to-show  <arg>   Amount of lines to shows to the console (instead
                             of writing snappy compressed parquet files)
-n, --nodes  <arg>           Spark nodes (local run) (default = local[*])
-o, --output  <arg>          Output path (local, hdfs, s3)
-s, --stay                   Wait for key press to exit (to keep SparkSession
                             and webserver running while debugging)
    --help                   Show help message
```

Testing
-------

There are several ways to test the code:
* With local in memory cluster and included data, e.g.:
  * A whole DAG `./sbt "run -i data/*.gz -o test"`
  * A reduced DAG `./sbt "run -i data/*.gz -o test --limit 1 --lines-to-show 2"`
  * Sometimes setting SPARK_LOCAL_IP="127.0.0.1" is required (depending on the network settings), e.g.
    `SPARK_LOCAL_IP="127.0.0.1" ./sbt ...`
* Sherlock Holmes mode: Start the scala console via `./sbt console`, there is a
  preconfigured SparkSession named `spark` and all the Steps are imported.
* Execute the automated tests via: `./sbt test` and `./sbt it:test`, or with
  coverage report: `./sbt coverage test coverageReport`. The report can be found
  in `target/scala-2.11/scoverage-report`.
* Submitting a Spark job with any desired cli argument on a running cluster or
  import the code into Apache Zeppelin and use it there.

Releasing
---------

The versioning is done via Annotated Git Tags, see [git-describe](https://git-scm.com/docs/git-describe) and [Git-Basics-Tagging](https://git-scm.com/book/en/v2/Git-Basics-Tagging) for annotated tags.

Tags have to be pushed via `git push --tags`.

This requires a full repo clone on the continuous integration machine (no shallow clone).

The benefit of git describe based versioning is, that every commit has an distinct
automatic version. This facilitates also continuous integration and delivery.


sbt bash wrapper
----------------

The sbt bash wrapper from https://github.com/paulp/sbt-extras is included as `./sbt` to reduce the build dependencies to OpenJDK 8 and bash.

Scala REPL
----------

The `console` and `consoleQuick` sbt tasks start the Scala REPL with a few commonly used imports such as `JavaConverters`, `collection.mutable`, `Future`, `Random`, `Try` and `NonFatal` already in scope.

The REPL also defines a `desugar` macro, that expands an expression to its desugared form and inferred type. Try, for instance `desugar(1 to 3)`.

Scalastyle
----------

The `scalastyle` and `test:scalastyle` sbt tasks are used to check source and test code with Scalastyle.
The error list is saved to Checkstyle-compatible files `target/scalastyle-result.xml` and `target/scalastyle-test-result.xml` respectively.

Scalastyle runs automatically against source and test code with the sbt `test` task.

It is not recommended to make the `compile` task dependent on Scalastyle.
Since Scalastyle runs first and fails if the code does not compile, one would not get the Scala compiler error messages.

scoverage
---------

To execute tests with code coverage enabled run the following sbt tasks: `clean` `coverage` `test` `coverageReport`.
Coverage reports are saved to `target/scala-2.11/scoverage-report/`.

The `coverage` command is sticky. To turn it off, use `coverageOff`.

Scalariform
-----------

To format source and test code run the `scalariformFormat` and `test:scalariformFormat` sbt tasks.

Scalariform is provided as a convenience and starting point; it is not sufficient to be fully compliant with the Scala Style Guide.

sbt-updates
-----------

There is a `dependencyUpdates` sbt task to check Maven repositories for dependency updates and a `pluginUpdates` task to check for sbt plugin updates.

sbt-license-report
------------------

To generate a report with the dependency licenses used by the project, use the `dumpLicenseReport` sbt task.
The report is saved to the `target/license-reports` directory.

dependency tree
---------------

The task `coursierDependencyTree` displays the project dependencies as a tree and `coursierDependencyInverseTree` shows everything that depends on an artifact.

sbt-stats
---------

The `stats` sbt task shows basic line counting statistics.

sbteclipse
----------

The `eclipse` sbt task to create Eclipse project definitions.

sbt-api-mappings
----------------

Fills apiMappings for common Scala libraries during `doc` task.

License
-------

Copyright 2011-2017 Marconi Lanna
Copyright 2017-2018 Daniel Bast

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
