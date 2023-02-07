package Mongo

import org.mongodb.scala._
import org.mongodb.scala.result.InsertOneResult

import scala.collection.mutable


class Ops {
  private val mongoClient: MongoClient = new Connection().client

  private val database: MongoDatabase = mongoClient.getDatabase("automobiles")

  def createAndInsertOlxDataCrawler(adId: String, document: Document): Unit = {
    val collection: MongoCollection[Document] = database.getCollection(adId)

    val observable: Observable[InsertOneResult] = collection.insertOne(document)

    observable.subscribe(new Observer[InsertOneResult] {
      override def onSubscribe(subscription: Subscription): Unit = subscription.request(1)

      override def onNext(result: InsertOneResult): Unit = println(s"onNext $result")

      override def onError(e: Throwable): Unit = println(e)

      override def onComplete(): Unit = println("Completed")
    })
  }

  def insertMercadoLibreDataCrawler(olxAdId: String, document: Document): Unit = {
    val collection: MongoCollection[Document] = database.getCollection(olxAdId)

    val observable: Observable[InsertOneResult] = collection.insertOne(document)

    observable.subscribe(new Observer[InsertOneResult] {
      override def onSubscribe(subscription: Subscription): Unit = subscription.request(1)

      override def onNext(result: InsertOneResult): Unit = println(s"onNext $result")

      override def onError(e: Throwable): Unit = println(e)

      override def onComplete(): Unit = println("Completed")
    })
  }
}
