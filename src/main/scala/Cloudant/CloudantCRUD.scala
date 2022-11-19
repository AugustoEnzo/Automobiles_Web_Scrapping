package Cloudant

import com.ibm.cloud.cloudant.v1.Cloudant
import com.ibm.cloud.cloudant.v1.model.{Document, DocumentResult, Ok, PutDatabaseOptions, PutDocumentOptions}
import com.ibm.cloud.sdk.core.service.exception.{NotFoundException, ServiceResponseException}
import com.ibm.cloud.sdk.core.security.IamAuthenticator
import JSIMPLE.parse

import scala.collection.mutable

class CloudantCRUD {
  // 1. Create a client with `CLOUDANT` default service name ============
  val credentials: Map[String, String] =
    parse("/home/ozne-otsugua/IdeaProjects/Automobiles Web Scrapping/cloudant_iam_key.json")

  val authenticator: IamAuthenticator = new IamAuthenticator.Builder()
    .apikey(credentials("apikey"))
    .build()

  val client: Cloudant = new  Cloudant(Cloudant.DEFAULT_SERVICE_NAME, authenticator)

  client.setServiceUrl(credentials("url"))

  // 2. Create a database ===============================================
  // Create a database object with "automobiles" id
  def create_database(name: String): Unit = {
    val dbName: String = name
    val putDBOptions: PutDatabaseOptions = new PutDatabaseOptions.Builder().db(dbName).build()

    try {
      val putDBResult: Ok = client
        .putDatabase(putDBOptions)
        .execute
        .getResult


      if (putDBResult.isOk) {
        println("\"" + dbName + "\" database created. ")
      }
    } catch {
      case sre: ServiceResponseException => if (sre.getStatusCode == 412) {
        println("Cannot create \"" + dbName + "\" database, it already exists.")
      }
      case e: Throwable => println(s"Error: $e")
    }
  }

  // 3. Create a document ===============================================
  // Create the car document id
  def create_document(database: String, id: String, Image: String, MapOfImages: mutable.Buffer[String],Title: String,
                      Price: Option[Int], Model: String, Brand: String, Kilometers: Option[Int], Description: String,
                      TypeOfCar: String, Location: String, TypeOfShift: String, TypeOfFuel: String,
                      YearOfFabrication: Option[Int], Color: String, Plate: Option[Int], MotorPower: Option[Double],
                      HasGNV: Option[Boolean], TypeOfDirection: String, NumberOfDoors: Option[Int], Optional: String) : Unit = {

    val documentIBM: Document = new Document
    // Setting id for the document is optional when "postDocument" method is used for CREATE.
    // When id is not provided the server will generate one for your document.

    documentIBM.setId(id)
    documentIBM.put("Thumbnail", Image)
    documentIBM.put("Other images", MapOfImages)
    documentIBM.put("Title", Title)
    documentIBM.put("Model", Model)
    documentIBM.put("Brand", Brand)
    documentIBM.put("Price", Price)
    documentIBM.put("Kilometers", Kilometers)
    documentIBM.put("Description", Description)
    documentIBM.put("Location", Location)
    documentIBM.put("Type of Car", TypeOfCar)
    documentIBM.put("Type of Shift", TypeOfShift)
    documentIBM.put("Type of Fuel", TypeOfFuel)
    documentIBM.put("Year of Fabrication", YearOfFabrication)
    documentIBM.put("Color", Color)
    documentIBM.put("End of Plate", Plate)
    documentIBM.put("Motor Power", MotorPower)
    documentIBM.put("Has VNG Kit", HasGNV)
    documentIBM.put("Type of Direction", TypeOfDirection)
    documentIBM.put("Number of doors", NumberOfDoors)
    documentIBM.put("Optional", Optional)

    val documentOptions: PutDocumentOptions = new PutDocumentOptions.Builder()
      .db(database)
      .docId(id)
      .document(documentIBM)
      .build

    try {
      val documentResponse: DocumentResult = client
        .putDocument(documentOptions)
        .execute
        .getResult

      // ====================================================================
      // Note: saving the document can also be done with the "putDocument"
      // method. In this case `docId` is required for a CREATE operation:

      // ====================================================================
      // Keeping track of the revision number of the document object
      // is necessary for further UPDATE/DELETE operations:

      documentIBM.setRev(documentResponse.getRev)

      if (documentResponse.isOk) {
        println(s"Document $id created.")

      }
    } catch {
        case sre: ServiceResponseException => if (sre.getStatusCode == 412) {
          println("Cannot create \"" + id + "\" Collection already exists.")
        }
      }


    // Save the document in the database with "postDocument" method
    /*
    val documentOptions: PostDocumentOptions = new PostDocumentOptions.Builder()
      .db(dbName)
      .document(example_doc)
      .build()

    val documentResponse: DocumentResult = client
      .postDocument(documentOptions)
      .execute()
      .getResult
    */
  }
  def update_document(database: String, id: String, document: Document): Unit = {
    try {
      val updateDocumentOptions: PutDocumentOptions =
        new PutDocumentOptions.Builder()
          .db(database)
          .docId(id)
          .rev(document.getRev)
          .document(document)
          .build

      val updateDocumentResult: DocumentResult = client
        .putDocument(updateDocumentOptions)
        .execute
        .getResult

      document.setRev(updateDocumentResult.getRev)
      println(s"You've updated the document: \n $document")
    } catch {
      case nfe: NotFoundException => println("Cannot update document because " +
      s"either \"$database\" database or the \"$id\" " +
      "document was not found. \n" + s"$nfe")
    }
  }


}
