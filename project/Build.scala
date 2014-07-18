import sbt._
import Keys._
import sbtassembly.Plugin._
import AssemblyKeys._

object Build extends Build {

  val commonSettings = Defaults.defaultSettings ++ Seq(
      crossScalaVersions := Seq("2.10.0", "2.11.0"),
      scalacOptions := Seq(
        "-encoding",
        "utf8",
        "-feature",
        "-unchecked",
        "-deprecation",
        "-target:jvm-1.7"
      ),
      javacOptions := Seq(
        "-source:1.7",
        "-target:1.7"
      ),
      resolvers ++= Seq(
        "Sonatype OSS Releases"  at "http://oss.sonatype.org/content/repositories/releases/"
      )
    )

  lazy val main = Project("main", file("."))
    .settings(commonSettings: _*)
    .settings(assemblySettings: _*)
    .settings(
      libraryDependencies <+= scalaVersion("org.scala-lang" % "scala-compiler" % _),
      jarName in assembly := "scala-ast.jar"
    )

}
