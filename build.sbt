import sbt.Keys._

name := "services"

version := "1.0"

scalaVersion := "2.11.4"

seq(webSettings :_*)

libraryDependencies ++= {
  val liftVersion = "2.6"
  Seq(
    "net.liftweb"       %% "lift-webkit"        % liftVersion        % "compile" withSources(),
    "net.liftweb" %% "lift-json-ext" % liftVersion % "compile" withSources(),
    "commons-codec" % "commons-codec" % "1.6",
    "org.eclipse.jetty" % "jetty-webapp" % "9.1.5.v20140505" % "container",
    "org.eclipse.jetty" % "jetty-plus" % "9.1.5.v20140505" % "container",
    "javax.servlet" % "javax.servlet-api" % "3.1.0",
    "org.mongodb" % "casbah_2.11" % "2.7.4",
    "com.novus" % "salat_2.11" % "1.9.9",
    "junit" % "junit" % "4.12" % "test",
    "com.fasterxml.jackson.module" % "jackson-module-scala_2.10" % "2.4.3" withSources(),
    "com.fasterxml.jackson.datatype" % "jackson-datatype-joda" % "2.4.3" withSources(),
    "com.typesafe" % "scalalogging-slf4j_2.10" % "1.1.0"
  )
}