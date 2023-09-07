package models

import java.util

case class OlxAdJava(adId: String, listOfImages: util.List[String], title: String, model: String, brand: String,
                     price: String, financialInformation: util.List[String], kilometers: String,
                     description: String, typeOfCar: String, typeOfShift: String,
                     typeOfFuel: String, typeOfDirection: String, yearOfFabrication: String,
                     color: String, endOfPlate: String, enginePower: String, hasGNV: String,
                     numberOfDoors: String, characteristics: util.Map[String, String], optionals: util.List[String],
                     locationInformation: util.Map[String, String], url: String, publishDate: String,
                     profileInformation: util.Map[String, String], fundingInformation: util.Map[String, String],
                     tagsList: util.List[String], verificationInformation: util.Map[String, String | String], averageOlxPrice: String,
                     fipePrice: String, fipePriceRef: util.Map[String, String], differenceToOlxAveragePrice: String,
                     differenceToFipePrice: String, vehicleSpecificData: util.Map[String, String])
