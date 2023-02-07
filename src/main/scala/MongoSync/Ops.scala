package MongoSync

import com.mongodb.client.model.{Filters, Projections}
import com.mongodb.client.{MongoClient, MongoClients, MongoCollection, MongoDatabase}
import org.bson.Document
import org.bson.conversions.Bson

import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, ListBuffer}

class Ops {
  private val client: MongoClient = MongoClients.create("mongodb://srv-data:27017")

  private val database: MongoDatabase = client.getDatabase("automobiles")

  def readAndGetCollectionInformation(): (ArrayBuffer[List[String]], ListBuffer[String]) = {

    val listOfCollections: mutable.ListBuffer[String] = mutable.ListBuffer[String]()

    database.listCollectionNames().forEach(collection => listOfCollections.append(collection))

    val resultString: mutable.ArrayBuffer[List[String]] = mutable.ArrayBuffer[List[String]]()

    for ( collection <- listOfCollections) {
      val coll: MongoCollection[Document] = database.getCollection(collection)

      val modelProjectionsFields: Bson = Projections.fields(
        Projections.include("model", "yearOfFabrication"),
        Projections.excludeId()
      )

      val modelDocument: Document = coll.find(Filters.eq("_id", collection))
        .projection(modelProjectionsFields)
        .first()

      resultString.append(List(modelDocument.get("model").toString, modelDocument.get("yearOfFabrication").toString))
    }

    (resultString, listOfCollections)
  }
}
