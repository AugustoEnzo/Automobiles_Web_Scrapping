package models

case class OlxAdNative(adId: String, listOfImages: List[String], title: String, model: String, brand: String,
                       price: String, financialInformation: List[String], kilometers: String,
                       description: String, typeOfCar: String, typeOfShift: String,
                       typeOfFuel: String, typeOfDirection: String, yearOfFabrication: String,
                       color: String, endOfPlate: String, enginePower: String, hasGNV: String,
                       numberOfDoors: String, characteristics: Map[String, String], optionals: List[String],
                       locationInformation: Map[String, String], url: String, publishDate: String,
                       profileInformation: Map[String, String], fundingInformation: Map[String, String],
                       tagsList: List[String], verificationInformation: Map[String, String | String], averageOlxPrice: String,
                       fipePrice: String, fipePriceRef: Map[String, String], differenceToOlxAveragePrice: String,
                       differenceToFipePrice: String, vehicleSpecificData: Map[String, String])
