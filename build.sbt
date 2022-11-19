ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "Automobiles Web Scrapping"
  )

libraryDependencies += "org.jsoup" % "jsoup" % "1.15.3"
libraryDependencies += "com.ibm.cloud" % "cloudant" % "0.4.0"
libraryDependencies += "com.ibm.cos" % "ibm-cos-java-sdk" % "2.12.1"