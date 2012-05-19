import sbt._
import sbt.Keys._

object ZoidbergBuild extends Build {
  lazy val androidHome = SettingKey[File]("android-home", "root dir of android")
  lazy val Zoidberg = Project(
    id = "Zoidberg",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "Zoidberg",
      organization := "com.novoda",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.9.2",
      androidHome := file(System.getenv("ANDROID_HOME")),
      resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases",
      libraryDependencies += "com.typesafe.akka" % "akka-actor" % "2.0.1",
      unmanagedJars in Compile <<= androidHome map {
        androidHome: File => (androidHome / "tools/lib/" ** (
          "monkeyrunner.jar" || "chimpchat.jar" || "hierarchyviewer*" || "guava*" || "ddm*" || "swt*")).classpath
      }
    )
  )
}
