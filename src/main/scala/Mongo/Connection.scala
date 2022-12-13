package Mongo

import org.mongodb.scala.MongoClient


private class Connection {
  private val connStr = "mongodb://srv-data:27017/"

  val client: MongoClient = MongoClient(connStr)
}
