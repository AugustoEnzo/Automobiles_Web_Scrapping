ThisBuild / version := "2.0.5.2-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.0"

lazy val root = (project in file("."))
  .settings(
    name := "Automobiles Web Scrapping"
  )

libraryDependencies += "org.jsoup" % "jsoup" % "1.15.3"
libraryDependencies += "com.ibm.cloud" % "cloudant" % "0.4.0"
libraryDependencies += "com.ibm.cos" % "ibm-cos-java-sdk" % "2.12.1"
libraryDependencies += "com.lihaoyi" %% "upickle" % "2.0.0"
libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "4.8.0"