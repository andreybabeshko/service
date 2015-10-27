import sbt.Keys._

name := "services"

version := "1.0"

scalaVersion := "2.11.1"

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
    "junit" % "junit" % "4.12" % "test"
  )
}