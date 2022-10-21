addSbtPlugin("io.get-coursier" % "sbt-coursier" % "2.0.11-1")

// This looks redundant, but is required for the `pluginUpdates` task defined
// in build.sbt, see https://github.com/rtimush/sbt-updates/issues/10
// http://github.com/rtimush/sbt-updates
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.3.4")
