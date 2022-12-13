package Mongo

import org.mongodb.scala._
import org.mongodb.scala.model.Aggregates._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Projections._
import org.mongodb.scala.model.Sorts._
import org.mongodb.scala.model.Updates._
import org.mongodb.scala.model._

import org.mongodb.scala.result.InsertOneResult

import java.util
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY

class CRUD {
  private val mongoClient: MongoClient = new Connection().client

  private val database: MongoDatabase = mongoClient.getDatabase("automobiles")

  def createAndInsert(adId: String, document: Document): Unit = {
    database.createCollection(adId)

    val collection: MongoCollection[Document] = database.getCollection(adId)

    val observable: Observable[InsertOneResult] = collection.insertOne(document)

    observable.subscribe(new Observer[InsertOneResult] {
      override def onSubscribe(subscription: Subscription): Unit = subscription.request(1)

      override def onNext(result: InsertOneResult): Unit = println(s"onNext $result")

      override def onError(e: Throwable): Unit = println(e)

      override def onComplete(): Unit = println("Completed")
    })
  }
}
