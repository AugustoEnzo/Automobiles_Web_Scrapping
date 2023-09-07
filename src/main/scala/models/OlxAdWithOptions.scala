package models

case class OlxAdWithOptions(adId: String, listOfImages: List[String], title: Option[String], model: Option[String], brand: Option[String],
                            price: Option[String], financialInformation: Option[List[String]], kilometers: Option[String],
                            description: Option[String], typeOfCar: Option[String], typeOfShift: Option[String],
                            typeOfFuel: Option[String], typeOfDirection: Option[String], yearOfFabrication: Option[String],
                            color: Option[String], endOfPlate: Option[String], enginePower: Option[String], hasGNV: Option[String],
                            numberOfDoors: Option[String], characteristics: Map[String, Option[String]], optionals: Option[List[String]],
                            locationInformation: Map[String, Option[String]], url: String, publishDate: String,
                            profileInformation: Map[String, Option[String]], fundingInformation: Map[String, Option[String]],
                            verificationInformation: Map[String, Option[String]], averageOlxPrice: Option[String],
                            fipePrice: Option[String], fipePriceRef: Map[String, Option[String]], differenceToOlxAveragePrice: Option[String],
                            differenceToFipePrice: Option[String], vehicleSpecificData: Map[String, String], tagsList: List[String])
