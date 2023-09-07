package helpers

import models.{OlxAdNative, OlxAdJava, OlxAdWithOptions}
import java.util

class Transformer {
  def mapToNative(olxAdWithOptions: OlxAdWithOptions): OlxAdNative = {

    val tempLocationInformation: Map[String, String] = olxAdWithOptions.locationInformation.map(location => if location._2.isDefined then
      location._1 -> location._2.get else location._1 -> String())

    val tempProfileInformation: Map[String, String] = olxAdWithOptions.profileInformation.map(profile => if profile._2.isDefined then
      profile._1 -> profile._2.get else profile._1 -> String())

    val tempFundingInformation: Map[String, String] = olxAdWithOptions.fundingInformation.map(funding => if funding._2.isDefined then
      funding._1 -> funding._2.get else funding._1 -> String())

    val tempVerificationInformation: Map[String, String] = olxAdWithOptions.verificationInformation.map(verification => if verification._2.isDefined then
      verification._1 -> verification._2.get else verification._1 -> String())

    val tempFipePriceRef: Map[String, String] = olxAdWithOptions.fipePriceRef.map(ref => if ref._2.isDefined then
      ref._1 -> ref._2.get else ref._1 -> String())

    val tempCharacteristics: Map[String, String] = olxAdWithOptions.characteristics.map(characteristic => if characteristic._2.isDefined then
      characteristic._1 -> characteristic._2.get else characteristic._1 -> String())

    val olxAd: OlxAdNative = OlxAdNative(
      adId = olxAdWithOptions.adId,
      listOfImages = olxAdWithOptions.listOfImages,
      title = if olxAdWithOptions.title.isDefined then olxAdWithOptions.title.get else String(),
      model = if olxAdWithOptions.model.isDefined then olxAdWithOptions.model.get else String(),
      brand = if olxAdWithOptions.brand.isDefined then olxAdWithOptions.brand.get else String(),
      price = if olxAdWithOptions.price.isDefined then olxAdWithOptions.price.get else String(),
      financialInformation = if olxAdWithOptions.financialInformation.isDefined then olxAdWithOptions.financialInformation.get else List[String](),
      kilometers = if olxAdWithOptions.kilometers.isDefined then olxAdWithOptions.kilometers.get else String(),
      description = if olxAdWithOptions.description.isDefined then olxAdWithOptions.description.get else String(),
      typeOfCar =  if olxAdWithOptions.typeOfCar.isDefined then olxAdWithOptions.typeOfCar.get else String(),
      typeOfShift = if olxAdWithOptions.typeOfShift.isDefined then olxAdWithOptions.typeOfShift.get else String(),
      typeOfFuel = if olxAdWithOptions.typeOfFuel.isDefined then olxAdWithOptions.typeOfFuel.get else String(),
      typeOfDirection = if olxAdWithOptions.typeOfDirection.isDefined then olxAdWithOptions.typeOfDirection.get else String(),
      yearOfFabrication = if olxAdWithOptions.yearOfFabrication.isDefined then olxAdWithOptions.typeOfDirection.get else String(),
      color = if olxAdWithOptions.color.isDefined then olxAdWithOptions.color.get else String(),
      endOfPlate = if olxAdWithOptions.endOfPlate.isDefined then olxAdWithOptions.endOfPlate.get else String(),
      enginePower = if olxAdWithOptions.enginePower.isDefined then olxAdWithOptions.enginePower.get else String(),
      hasGNV = if olxAdWithOptions.hasGNV.isDefined then olxAdWithOptions.hasGNV.get else String(),
      numberOfDoors = if olxAdWithOptions.numberOfDoors.isDefined then olxAdWithOptions.numberOfDoors.get else String(),
      characteristics = tempCharacteristics,
      optionals = if olxAdWithOptions.optionals.isDefined then olxAdWithOptions.optionals.get else List[String](),
      locationInformation = tempLocationInformation,
      url = olxAdWithOptions.url,
      publishDate = olxAdWithOptions.publishDate,
      fundingInformation = tempFundingInformation,
      verificationInformation = tempVerificationInformation,
      averageOlxPrice = if olxAdWithOptions.averageOlxPrice.isDefined then olxAdWithOptions.averageOlxPrice.get else String(),
      fipePrice = if olxAdWithOptions.fipePrice.isDefined then olxAdWithOptions.fipePrice.get else String(),
      fipePriceRef = tempFipePriceRef,
      differenceToOlxAveragePrice = if olxAdWithOptions.differenceToOlxAveragePrice.isDefined then olxAdWithOptions.differenceToOlxAveragePrice.get else String(),
      differenceToFipePrice = if olxAdWithOptions.differenceToFipePrice.isDefined then olxAdWithOptions.differenceToFipePrice.get else String(),
      profileInformation = tempProfileInformation,
      vehicleSpecificData = olxAdWithOptions.vehicleSpecificData,
      tagsList = olxAdWithOptions.tagsList
    )
    olxAd
  }

  def mapToJava(olxAdWithOptions: OlxAdWithOptions): OlxAdJava = {
    val tempListOfImages: util.List[String] = util.ArrayList[String]()
    olxAdWithOptions.listOfImages.foreach(value => tempListOfImages.add(value))

    val tempFinancialInformation: util.List[String] = util.ArrayList[String]()
    if (olxAdWithOptions.financialInformation.isDefined) {
      olxAdWithOptions.financialInformation.get.foreach(value => tempFinancialInformation.add(value))
    }

    val tempOptionals: util.List[String] = util.ArrayList[String]()
    if (olxAdWithOptions.optionals.isDefined) {
      olxAdWithOptions.optionals.get.foreach(value => tempOptionals.add(value))
    }

    val tempTagsList: util.List[String] = util.ArrayList[String]()
    olxAdWithOptions.tagsList.foreach(value => tempTagsList.add(value))

    val tempLocationInformation: util.Map[String, String] = util.HashMap[String, String]()
    olxAdWithOptions.locationInformation.map(location => if location._2.isDefined then
      tempLocationInformation.put(location._1, location._2.get) else tempLocationInformation.put(location._1, String()))

    val tempProfileInformation: util.Map[String, String] = util.HashMap[String, String]()
    olxAdWithOptions.profileInformation.map(profile => if profile._2.isDefined then
      tempProfileInformation.put(profile._1, profile._2.get) else tempProfileInformation.put(profile._1, String()))

    val tempFundingInformation: util.Map[String, String] = util.HashMap[String, String]()
    olxAdWithOptions.fundingInformation.map(funding => if funding._2.isDefined then
      tempFundingInformation.put(funding._1, funding._2.get) else tempFundingInformation.put(funding._1, String()))

    val tempVerificationInformation: util.Map[String, String] = util.HashMap[String, String]()
    olxAdWithOptions.verificationInformation.map(verification => if verification._2.isDefined then
      tempVerificationInformation.put(verification._1, verification._2.get) else tempVerificationInformation.put(verification._1, String()))

    val tempFipePriceRef: util.Map[String, String] = util.HashMap[String, String]()
    olxAdWithOptions.fipePriceRef.map(ref => if ref._2.isDefined then
      tempFipePriceRef.put(ref._1, ref._2.get) else tempFipePriceRef.put(ref._1, String()))

    val tempCharacteristics: util.Map[String, String] = util.HashMap[String, String]()
    olxAdWithOptions.characteristics.map(characteristic => if characteristic._2.isDefined then
      tempCharacteristics.put(characteristic._1, characteristic._2.get) else tempCharacteristics.put(characteristic._1, String()))

    val tempVehicleSpecificData: util.Map[String, String] = util.HashMap[String, String]()
    olxAdWithOptions.vehicleSpecificData.map(vehicle => tempVehicleSpecificData.put(vehicle._1, vehicle._2))

    val olxAdJava: OlxAdJava = OlxAdJava(
      adId = olxAdWithOptions.adId,
      listOfImages = tempListOfImages,
      title = if olxAdWithOptions.title.isDefined then olxAdWithOptions.title.get else String(),
      model = if olxAdWithOptions.model.isDefined then olxAdWithOptions.model.get else String(),
      brand = if olxAdWithOptions.brand.isDefined then olxAdWithOptions.brand.get else String(),
      price = if olxAdWithOptions.price.isDefined then olxAdWithOptions.price.get else String(),
      financialInformation = tempFinancialInformation,
      kilometers = if olxAdWithOptions.kilometers.isDefined then olxAdWithOptions.kilometers.get else String(),
      description = if olxAdWithOptions.description.isDefined then olxAdWithOptions.description.get else String(),
      typeOfCar = if olxAdWithOptions.typeOfCar.isDefined then olxAdWithOptions.typeOfCar.get else String(),
      typeOfShift = if olxAdWithOptions.typeOfShift.isDefined then olxAdWithOptions.typeOfShift.get else String(),
      typeOfFuel = if olxAdWithOptions.typeOfFuel.isDefined then olxAdWithOptions.typeOfFuel.get else String(),
      typeOfDirection = if olxAdWithOptions.typeOfDirection.isDefined then olxAdWithOptions.typeOfDirection.get else String(),
      yearOfFabrication = if olxAdWithOptions.title.isDefined then olxAdWithOptions.title.get else String(),
      color = if olxAdWithOptions.color.isDefined then olxAdWithOptions.color.get else String(),
      endOfPlate = if olxAdWithOptions.endOfPlate.isDefined then olxAdWithOptions.endOfPlate.get else String(),
      enginePower = if olxAdWithOptions.enginePower.isDefined then olxAdWithOptions.enginePower.get else String(),
      hasGNV = if olxAdWithOptions.hasGNV.isDefined then olxAdWithOptions.hasGNV.get else String(),
      numberOfDoors = if olxAdWithOptions.numberOfDoors.isDefined then olxAdWithOptions.numberOfDoors.get else String(),
      characteristics = tempCharacteristics,
      optionals = tempOptionals,
      locationInformation = tempLocationInformation,
      url = olxAdWithOptions.url,
      publishDate = olxAdWithOptions.publishDate,
      profileInformation = tempProfileInformation,
      fundingInformation = tempFundingInformation,
      tagsList = tempTagsList,
      verificationInformation = tempVerificationInformation,
      averageOlxPrice = if olxAdWithOptions.averageOlxPrice.isDefined then olxAdWithOptions.averageOlxPrice.get else String(),
      fipePrice = if olxAdWithOptions.fipePrice.isDefined then olxAdWithOptions.fipePrice.get else String(),
      fipePriceRef = tempFipePriceRef,
      differenceToOlxAveragePrice = if olxAdWithOptions.differenceToOlxAveragePrice.isDefined then olxAdWithOptions.differenceToOlxAveragePrice.get else String(),
      differenceToFipePrice = if olxAdWithOptions.title.isDefined then olxAdWithOptions.title.get else String(),
      vehicleSpecificData = tempVehicleSpecificData
    )
    olxAdJava
  }
}
