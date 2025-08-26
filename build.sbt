import org.typelevel.scalacoptions.ScalacOptions

import scala.sys.process.Process

val Http4sVersion = "0.23.30"
val CirceVersion = "0.14.10"
val LogbackVersion = "1.5.6"
val TapirVersion = "1.11.40"

inThisBuild(
  List(
    scalaVersion := "3.6.1",
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    scalafixDependencies += "org.typelevel" %% "typelevel-scalafix" % "0.4.0",
    resolvers ++= Resolver.sonatypeOssRepos("snapshots"),
    resolvers ++= Resolver.sonatypeOssRepos("releases"),
    organization := "com.github.njustus",
    version := "0.0.1-SNAPSHOT",
    scalacOptions ++= Seq(
      "-no-indent", "-rewrite"
    ),
    tpolecatExcludeOptions ++= Set(
      ScalacOptions.fatalWarnings
    ),
  )
)

lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("shared"))
  .settings(
    name := "shared",
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.tapir" %%% "tapir-core" % TapirVersion,
      "com.softwaremill.sttp.tapir" %%% "tapir-json-circe" % TapirVersion,
      "io.circe" %%% "circe-generic" % CirceVersion,
      "io.circe" %%% "circe-parser" % CirceVersion,
      "io.scalaland" %%% "chimney" % "1.6.0",
      "com.softwaremill.macwire" %%% "macros" % "2.6.6",
    )
  )

lazy val client =
  project
    .in(file("client"))
    .dependsOn(shared.js)
    .enablePlugins(ScalaJSPlugin)
    .settings(
      name := "locale-share-client",
      // This is an application with a main method
      scalaJSUseMainModuleInitializer := true,
      scalaJSLinkerConfig ~= {
        _.withModuleKind(ModuleKind.ESModule)
      },
      libraryDependencies ++= Seq(
        "org.scala-js" %%% "scalajs-dom" % "2.8.0",
        "com.raquo" %%% "laminar" % "17.2.1",
        "dev.frontroute" %%% "frontroute" % "0.19.1",
        "io.github.cquiroz" %%% "scala-java-time" % "2.6.0",
              "com.softwaremill.sttp.tapir" %%% "tapir-sttp-client4" % TapirVersion,
      ),
      // excludeDependencies ++= Seq(
      //   "org.scala-lang.modules" %% "scala-collection-compat_sjs1",
      // ),
      externalNpm := {
        Process("npm", baseDirectory.value).!
        baseDirectory.value
      },
      // stIgnore ++= List(
      //   "@tailwindcss/vite"
      // ),
      stShortModuleNames := true,
      stEnableScalaJsDefined := Selection.All
)


lazy val backend = (project in file("backend"))
  .dependsOn(shared.jvm)
  .enablePlugins(JavaAppPackaging)
  .settings(
    fork := true,
    connectInput := true,
    name := "locale-share-backend",
    (Compile/unmanagedResourceDirectories) += (client.base.getAbsoluteFile / "dist"),
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % TapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % TapirVersion,
      "org.http4s" %% "http4s-ember-server" % Http4sVersion,
      "org.http4s" %% "http4s-ember-client" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,

      "com.github.pureconfig" %% "pureconfig-core" % "0.17.8",
      "com.github.pureconfig" %% "pureconfig-cats-effect" % "0.17.8",

      "ch.qos.logback" % "logback-classic" % LogbackVersion,
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4",
    ),
    addCommandAlias("cleanRun", ";clean;run"),
    addCommandAlias("cleanup", ";clean;scalafix;scalafmt"),
    addCommandAlias("fix", "cleanup")
  )
