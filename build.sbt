ThisBuild / scalaVersion     := "2.13.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "rei"

lazy val root = (project in file("."))
  .settings(
    name := "aggregate_oom",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % "1.0.14",
      "dev.zio" %% "zio-streams" % "1.0.14"
    )
  )
