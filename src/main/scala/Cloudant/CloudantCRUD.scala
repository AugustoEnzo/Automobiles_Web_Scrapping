//package Cloudant
//
//import com.ibm.cloud.cloudant.v1.Cloudant
//import com.ibm.cloud.cloudant.v1.model.{AllDocsResult, Document, DocumentResult, Ok, PostAllDocsOptions, PutDatabaseOptions, PutDocumentOptions}
//import com.ibm.cloud.sdk.core.service.exception.{NotFoundException, ServiceResponseException}
//import com.ibm.cloud.sdk.core.security.IamAuthenticator
//import JSIMPLE.{parse, parseDocResult}
//
//import scala.collection.mutable.ArrayBuffer
//import scala.collection.{immutable, mutable}
//
//class CloudantCRUD {
//  // 1. Create a client with `CLOUDANT` default service name ============
//  val credentials: Map[String, String] =
//    parse("cloudant_iam_key.json")
//
//  private val authenticator: IamAuthenticator = new IamAuthenticator.Builder()
//    .apikey(credentials("apikey"))
//    .build()
//
//  val client: Cloudant = new  Cloudant(Cloudant.DEFAULT_SERVICE_NAME, authenticator)
//
//  client.setServiceUrl(credentials("url"))
//
//  // 2. Create a database ===============================================
//  // Create a database object with "automobiles" id
//  def create_database(name: String): Unit = {
//    val dbName: String = name
//    val putDBOptions: PutDatabaseOptions = new PutDatabaseOptions.Builder().db(dbName).build()
//
//    try {
//      val putDBResult: Ok = client
//        .putDatabase(putDBOptions)
//        .execute
//        .getResult
//
//
//      if (putDBResult.isOk) {
//        println("\"" + dbName + "\" database created. ")
//      }
//    } catch {
//      case sre: ServiceResponseException => if (sre.getStatusCode == 412) {
//        println("Cannot create \"" + dbName + "\" database, it already exists.")
//      }
//      case e: Throwable => println(s"Error: $e")
//    }
//  }
//
//  // 3. Create a document ===============================================
//  // Create the car document id
//  def create_document(database: String, adId: String, mapOfImages: mutable.ArrayBuffer[Option[String]],
//                      title: Option[String], model: Option[String], brand: Option[String], price: Option[Double],
//                      financialInformation: Option[ArrayBuffer[String]], kilometers: Option[Double],
//                      description: Option[String], typeOfCar: Option[String], typeOfShift: Option[String],
//                      typeOfFuel: Option[String], typeOfDirection: Option[String], yearOfFabrication: Option[String],
//                      color: Option[String], endOfPlate: Option[Int], motorPower: Option[Double],
//                      hasGNV: Option[Boolean], numberOfDoors: Option[Int], characteristics: Map[String, Option[Boolean]],
//                      optionals: Option[ArrayBuffer[String]], locationInformation: ArrayBuffer[Option[String Double]],
//                      url: String, publishDate: String,
//                      profileInformation: Option[Map[String, Option[String | Boolean | Double]]],
//                      fundingInformation: Option[Map[String, Int | Double]],
//                      verificationInformation: Map[String, Option[Boolean | String | ArrayBuffer[String]]],
//                      tags: ArrayBuffer[String], averageOlxPrice: Option[Double], fipePrice: Option[Double],
//                      fipePriceRef: Option[Map[String, Double]],
//                      differenceToOlxAveragePrice: Option[Double], differenceToFipePrice: Option[Double],
//                      vehicleSpecificData: mutable.HashMap[String, String]
//                     ) : Unit = {
//
//    val documentIBM: Document = new Document
//    // Setting id for the document is optional when "postDocument" method is used for CREATE.
//    // When id is not provided the server will generate one for your document.
//
//    documentIBM.setId(adId)
//    documentIBM.put("Images", mapOfImages)
//    documentIBM.put("Title", title.getOrElse(None))
//    documentIBM.put("Model", model.getOrElse(None))
//    documentIBM.put("Brand", brand.getOrElse(None))
//    documentIBM.put("Price", price.getOrElse(None))
//    documentIBM.put("Financial Information", financialInformation.getOrElse(None))
//    documentIBM.put("Kilometers", kilometers.getOrElse(None))
//    documentIBM.put("Description", description.getOrElse(None))
//    documentIBM.put("Location", locationInformation)
//    documentIBM.put("Type of Car", typeOfCar.getOrElse(None))
//    documentIBM.put("Type of Shift", typeOfShift.getOrElse(None))
//    documentIBM.put("Type of Fuel", typeOfFuel.getOrElse(None))
//    documentIBM.put("Type of Direction", typeOfDirection.getOrElse(None))
//    documentIBM.put("Year of Fabrication", yearOfFabrication.getOrElse(None))
//    documentIBM.put("Color", color.getOrElse(None))
//    documentIBM.put("End of Plate", endOfPlate.getOrElse(None))
//    documentIBM.put("Motor Power", motorPower.getOrElse(None))
//    documentIBM.put("Has VNG Kit", hasGNV.getOrElse(None))
//    documentIBM.put("Number of doors", numberOfDoors.getOrElse(None))
//    documentIBM.put("Characteristics", characteristics)
//    documentIBM.put("Optionals", optionals.getOrElse(None))
//    documentIBM.put("Location Information", locationInformation)
//    documentIBM.put("URL", url)
//    documentIBM.put("Publish Date", publishDate)
//    documentIBM.put("Profile Information", profileInformation.getOrElse(None))
//    documentIBM.put("Funding Information", fundingInformation.getOrElse(None))
//    documentIBM.put("Verification Information", verificationInformation)
//    documentIBM.put("Tags", tags)
//    documentIBM.put("Average Olx Price", averageOlxPrice.getOrElse(None))
//    documentIBM.put("FIPE Price", fipePrice.getOrElse(None))
//    documentIBM.put("FIPE Price Reference", fipePriceRef.getOrElse(None))
//    documentIBM.put("Difference to Olx Average Price", differenceToOlxAveragePrice.getOrElse(None))
//    documentIBM.put("Difference to FIPE Price", differenceToFipePrice.getOrElse(None))
//    documentIBM.put("Vehicle Specif Data", vehicleSpecificData)
//
//    val documentOptions: PutDocumentOptions = new PutDocumentOptions.Builder()
//      .db(database)
//      .docId(adId)
//      .document(documentIBM)
//      .build
//
//    try {
//      val documentResponse: DocumentResult = client
//        .putDocument(documentOptions)
//        .execute
//        .getResult
//
//      // ====================================================================
//      // Note: saving the document can also be done with the "putDocument"
//      // method. In this case `docId` is required for a CREATE operation:
//
//      // ====================================================================
//      // Keeping track of the revision number of the document object
//      // is necessary for further UPDATE/DELETE operations:
//
//      documentIBM.setRev(documentResponse.getRev)
//
//      if (documentResponse.isOk) {
//        println(s"Document $adId created.")
//
//      }
//    } catch {
//        case sre: ServiceResponseException => if (sre.getStatusCode == 412) {
//          println("Cannot create \"" + adId + "\" Collection already exists.")
//        }
//      }
//
//    // Save the document in the database with "postDocument" method
//    /*
//    val documentOptions: PostDocumentOptions = new PostDocumentOptions.Builder()
//      .db(dbName)
//      .document(example_doc)
//      .build()
//
//    val documentResponse: DocumentResult = client
//      .postDocument(documentOptions)
//      .execute()
//      .getResult
//    */
//  }
//  def update_document(database: String, adId: String, document: Document): Unit = {
//    try {
//      val updateDocumentOptions: PutDocumentOptions =
//        new PutDocumentOptions.Builder()
//          .db(database)
//          .docId(adId)
//          .rev(document.getRev)
//          .document(document)
//          .build
//
//      val updateDocumentResult: DocumentResult = client
//        .putDocument(updateDocumentOptions)
//        .execute
//        .getResult
//
//      document.setRev(updateDocumentResult.getRev)
//      println(s"You've updated the document: \n $document")
//    } catch {
//      case nfe: NotFoundException => println(s"""Cannot update document because either \"$database\" database or the \"$adId\" document was not found. \n $nfe""")
//    }
//  }
//
//  def documentOnDatabase(database: String, documentID: String): Boolean = {
//    val docsOptions: PostAllDocsOptions =
//      new PostAllDocsOptions.Builder()
//        .db(database)
//        .key(documentID)
//        .build()
//
//    val response: AllDocsResult = client.postAllDocs(docsOptions).execute.getResult
//
//    val parsedResponse: String = parseDocResult(response)
//
//    if (parsedResponse == documentID) {
//      true
//    } else false
//  }
//
//}
