package Mongo

import org.mongodb.scala.Document
import org.mongodb.scala.bson._

import scala.collection.mutable

class Cast {
  def cast(adId: String, mapOfImages: List[Option[String]],
           title: Option[String], model: Option[String], brand: Option[String], price: Option[Double],
           financialInformation: Option[List[String]], kilometers: Option[Double],
           description: Option[String], typeOfCar: Option[String], typeOfShift: Option[String],
           typeOfFuel: Option[String], typeOfDirection: Option[String], yearOfFabrication: Option[String],
           color: Option[String], endOfPlate: Option[Int], motorPower: Option[Double],
           hasGNV: Option[Boolean], numberOfDoors: Option[Int], characteristics: Map[String, Option[Boolean]],
           optionals: Option[List[String]], locationInformation: Map[String, Option[Any]],
           url: String, publishDate: String, profileInformation: Option[Map[String, Option[Any]]],
           fundingInformation: Option[Map[String, Double]],
           verificationInformation: Option[Map[String, Option[Any]]],
           averageOlxPrice: Option[Double], fipePrice: Option[Double],
           fipePriceRef: Option[Map[String, Double]], differenceToOlxAveragePrice: Option[Double],
           differenceToFipePrice: Option[Double], vehicleSpecificData: mutable.HashMap[String, String]): Document = {

    val tempMapOfImages: BsonArray = new BsonArray
    mapOfImages.foreach(image => if (image.isDefined) tempMapOfImages.add(BsonString(image.get)))

    val tempModel: BsonString = if (model.isDefined)  BsonString(model.get) else BsonString("")
    val tempBrand: BsonString = if (brand.isDefined)  BsonString(brand.get) else BsonString("")
    val tempPrice: BsonDouble = if (price.isDefined)  BsonDouble(price.get) else BsonDouble(-1)

    val tempFinancialInformation: BsonArray = new BsonArray
    if (financialInformation.isDefined) {
      financialInformation.get.foreach(financial => tempFinancialInformation.add(BsonString(financial)))
    }

    val tempKilometers: BsonDouble = if (kilometers.isDefined) BsonDouble(kilometers.get) else BsonDouble(-1)
    val tempDescription: BsonString = if (description.isDefined) BsonString(description.get) else BsonString("")
    val tempTypeOfCar: BsonString = if (typeOfCar.isDefined) BsonString(typeOfCar.get) else BsonString("")
    val tempTypeOfShift: BsonString = if (typeOfShift.isDefined) BsonString(typeOfShift.get) else BsonString("")
    val tempTypeOfFuel: BsonString = if (typeOfFuel.isDefined) BsonString(typeOfFuel.get) else BsonString("")
    val tempTypeOfDirection: BsonString = if (typeOfDirection.isDefined) BsonString(typeOfDirection.get) else BsonString("")
    val tempYearOfFabrication: BsonString = if (yearOfFabrication.isDefined) BsonString(yearOfFabrication.get) else BsonString("")
    val tempColor: BsonString = if (color.isDefined) BsonString(color.get) else BsonString("")
    val tempEndOfPlate: BsonInt32 = if (endOfPlate.isDefined) BsonInt32(endOfPlate.get) else BsonInt32(-1)
    val tempMotorPower: BsonDouble = if (motorPower.isDefined) BsonDouble(motorPower.get) else BsonDouble(-1)
    val tempHasGNV: BsonBoolean = if (hasGNV.isDefined) BsonBoolean(hasGNV.get) else BsonBoolean(false)
    val tempNumberOfDoors: BsonInt32 = if (numberOfDoors.isDefined) BsonInt32(numberOfDoors.get) else BsonInt32(-1)

    val tempCharacteristics: BsonDocument = new BsonDocument
    characteristics.foreach(characteristic => if (characteristic._2.isDefined)
      tempCharacteristics.append(characteristic._1, BsonBoolean(characteristic._2.get)))

    val tempOptionals: BsonArray = new BsonArray
    if (optionals.isDefined) {
      optionals.get.foreach(optional => tempOptionals.add(BsonString(optional)))
    }

    val tempLocationInformation: BsonDocument = new BsonDocument
      locationInformation.foreach(location => if (location._2.isDefined) {
        location._2.get match {
          case d: Double => tempLocationInformation.append(location._1, BsonDouble(d))
          case s: String => tempLocationInformation.append(location._1, BsonString(s))
          case _ => null
        }
      })

    val tempProfileInformation: BsonDocument = new BsonDocument
    if (profileInformation.isDefined) {
      profileInformation.get.foreach(profile => if (profile._2.isDefined) {
        profile._2.get match {
          case d: Double => tempProfileInformation.append(profile._1, BsonDouble(d))
          case s: String => tempProfileInformation.append(profile._1, BsonString(s))
          case _ => null
        }
      })
    }

    val tempFundingInformation: BsonDocument = new BsonDocument
      if (fundingInformation.isDefined) {
        fundingInformation.get.foreach(funding => tempFundingInformation.append(funding._1, BsonDouble(funding._2)))
      }

    val tempVerificationInformation: BsonDocument = new BsonDocument
    if (profileInformation.isDefined) {
      verificationInformation.get.foreach(verification => if (verification._2.isDefined) {
        verification._2.get match {
          case b: Boolean => tempVerificationInformation.append(verification._1, BsonBoolean(b))
          case s: String => tempVerificationInformation.append(verification._1, BsonString(s))
          case list: List[String] => tempVerificationInformation.append(verification._1, BsonArray(list))
        }
      })
    }

    val tempAverageOlxPrice: BsonDouble = if (averageOlxPrice.isDefined) BsonDouble(averageOlxPrice.get) else BsonDouble(-1)

    val tempFipePrice: BsonDouble = if (fipePrice.isDefined) BsonDouble(fipePrice.get) else BsonDouble(-1)

    val tempFipePriceRef: BsonDocument = new BsonDocument
    if (fipePriceRef.isDefined) {
      fipePriceRef.get.foreach(reference => tempFipePriceRef.append(reference._1, BsonDouble(reference._2)))
    }

    val tempDifferenceToOlxAveragePrice: BsonDouble = if (differenceToOlxAveragePrice.isDefined)
      BsonDouble(differenceToOlxAveragePrice.get) else BsonDouble(-1)

    val tempDifferenceToFipePrice: BsonDouble = if (differenceToFipePrice.isDefined)
      BsonDouble(differenceToFipePrice.get) else BsonDouble(-1)

    val tempVehicleSpecificData: BsonDocument = new BsonDocument
    vehicleSpecificData.foreach(vehicle => tempVehicleSpecificData.append(vehicle._1, BsonString(vehicle._2)))

    val document: Document = Document(
      "_id" -> adId,
      "images" -> tempMapOfImages,
      "title" -> title.get,
      "model" -> tempModel,
      "brand" -> tempBrand,
      "price" -> tempPrice,
      "financialInformation" -> tempFinancialInformation,
      "kilometers" -> tempKilometers,
      "description" -> tempDescription,
      "typeOfCar" -> tempTypeOfCar,
      "typeOfShift" -> tempTypeOfShift,
      "typeOfFuel" -> tempTypeOfFuel,
      "typeOfDirection" -> tempTypeOfDirection,
      "yearOfFabrication" -> tempYearOfFabrication,
      "color" -> tempColor,
      "endOfPlate" -> tempEndOfPlate,
      "motorPower" -> tempMotorPower,
      "hasGNV" -> tempHasGNV,
      "numberOfDoors" -> tempNumberOfDoors,
      "characteristics" -> tempCharacteristics,
      "optionals" -> tempOptionals,
      "locationInformation" -> tempLocationInformation,
      "url" -> url,
      "publishDate" -> publishDate,
      "profileInformation" -> tempProfileInformation,
      "fundingInformation" -> tempFundingInformation,
      "verificationInformation" -> tempVerificationInformation,
      "averageOlxPrice" -> tempAverageOlxPrice,
      "fipePrice" -> tempFipePrice,
      "fipePriceRef" -> tempFipePriceRef,
      "differenceToOlxAveragePrice" -> tempDifferenceToOlxAveragePrice,
      "differenceToFipePrice" -> tempDifferenceToFipePrice,
      "vehicleSpecificData" -> tempVehicleSpecificData
    )

    document
  }
}
