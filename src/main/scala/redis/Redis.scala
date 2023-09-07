//package redis
//
//import redis.clients.jedis.Jedis
//import redis.clients.jedis.JedisPubSub
//
//import scala.collection.immutable.HashMap
//
//class Redis {
//    private val jedisListener: JedisListener = new JedisListener()
////  new JedisPooled("srv-data", 6379)
//    private val jedis: Jedis = new Jedis("srv-data",6379)
//
//    def insertData(adId: String, listOfImages: List[String], title: Option[String], model: Option[String], brand: Option[String],
//                   price: Option[String], financialInformation: Option[List[String]], kilometers: Option[String],
//                   description: Option[String], typeOfCar: Option[String], typeOfShift: Option[String],
//                   typeOfFuel: Option[String], typeOfDirection: Option[String], yearOfFabrication: Option[String],
//                   color: Option[String], endOfPlate: Option[String], enginePower: Option[String], hasGNV: Option[String],
//                   numberOfDoors: Option[String], characteristics: Map[String, Option[String]], optionals: Option[List[String]],
//                   locationInformation: Map[String, Option[String]], url: String, publishDate: String,
//                   profileInformation: Map[String, Option[String]], fundingInformation: Map[String, Option[String]],
//                   verificationInformation: Map[String, Option[String]], averageOlxPrice: Option[String],
//                   fipePrice: Option[String], fipePriceRef: Map[String, Option[String]], differenceToOlxAveragePrice: Option[String],
//                   differenceToFipePrice: Option[String], vehicleSpecificData: Map[String, String], tagsList: List[String]): Unit = {
//
//      val tempLocationInformation: Map[String, String] = locationInformation.map(location => if location._2.isDefined then
//        location._1 -> location._2.get else location._1 -> String())
//
//      val tempProfileInformation: Map[String, String] = profileInformation.map(profile => if profile._2.isDefined then
//        profile._1 -> profile._2.get else profile._1 -> String())
//
//      val tempFundingInformation: Map[String, String] = fundingInformation.map(funding => if funding._2.isDefined then
//        funding._1 -> funding._2.get else funding._1 -> String())
//
//      val tempVerificationInformation: Map[String, String] = verificationInformation.map(verification => if verification._2.isDefined then
//        verification._1 -> verification._2.get else verification._1 -> String())
//
//      val tempFipePriceRef: Map[String, String] = fipePriceRef.map(ref => if ref._2.isDefined then
//        ref._1 -> ref._2.get else ref._1 -> String())
//
//      val tempCharacteristics: Map[String, String] = characteristics.map(characteristic => if characteristic._2.isDefined then
//        characteristic._1 -> characteristic._2.get else characteristic._1 -> String())
//
//      jedis.publish("olxAds", f"adId: $adId, listOfImages: $listOfImages," +
//        f"title: ${(x: String) => if title.isDefined then title.get else String()}," +
//        f"model: ${(x: String) => if model.isDefined then model.get else String()}," +
//        f"brand: ${(x: String) => if brand.isDefined then brand.get else String()}," +
//        f"price: ${(x: String) => if price.isDefined then price.get else String()}," +
//        f"financialInformation: ${(x: String) => if financialInformation.isDefined then financialInformation.get else List[String]()}," +
//        f"kilometers: ${(x: String) => if kilometers.isDefined then kilometers.get else String()}," +
//        f"description: ${(x: String) => if description.isDefined then description.get else String()}," +
//        f"typeOfCar: ${(x: String) => if typeOfCar.isDefined then typeOfCar.get else String()}," +
//        f"typeOfShift: ${(x: String) => if typeOfShift.isDefined then typeOfShift.get else String()}," +
//        f"typeOfFuel: ${(x: String) => if typeOfFuel.isDefined then typeOfFuel.get else String()}," +
//        f"typeOfDirection: ${(x: String) => if typeOfDirection.isDefined then typeOfDirection.get else String()}," +
//        f"yearOfFabrication: ${(x: String) => if yearOfFabrication.isDefined then typeOfDirection.get else String()}," +
//        f"color: ${(x: String) => if color.isDefined then color.get else String()}," +
//        f"endOfPlate: ${(x: String) => if endOfPlate.isDefined then endOfPlate.get else String()}," +
//        f"enginePower: ${(x: String) => if enginePower.isDefined then enginePower.get else String()}," +
//        f"hasGNV: ${(x: String) => if hasGNV.isDefined then hasGNV.get else String()}," +
//        f"numberOfDoors: ${(x: String) => if numberOfDoors.isDefined then numberOfDoors.get else String()}," +
//        f"characteristics: $tempCharacteristics, optionals: ${(x: String) => if optionals.isDefined then optionals.get else String()}," +
//        f"locationInformation: $tempLocationInformation, url: $url, publishDate: $publishDate," +
//        f"fundingInformation: $tempFundingInformation, verificationInformation: $tempVerificationInformation," +
//        f"averageOlxPrice: ${(x: String) => if averageOlxPrice.isDefined then averageOlxPrice.get else String()}," +
//        f"fipePrice: ${(x: String) => if fipePrice.isDefined then fipePrice.get else String()}," +
//        f"fipePriceRef: $tempFipePriceRef, differenceToOlxAveragePrice: ${(x: String) =>
//          if differenceToOlxAveragePrice.isDefined then differenceToOlxAveragePrice.get else String()}," +
//        f"differenceToFipePrice: ${(x: String) =>
//          if differenceToFipePrice.isDefined then differenceToFipePrice.get else String()}," +
//        f"vehicleSpecificData: $vehicleSpecificData, tagsList: $tagsList"
//      )
//    }
//}
//
//class JedisListener extends JedisPubSub {
//  override def onMessage(channel: String, message: String): Unit = {
//    println("Channel " + channel + " has sent a message : " + message)
//    if(channel == "olxAds") {
//      unsubscribe(channel)
//    }
//  }
//
//  override def onSubscribe(channel: String, subscribedChannels: Int): Unit = {
//    println("Client is Subscribed to channel : " + channel)
//    println("Client is Subscribed to " + subscribedChannels + " no. of")
//  }
//
//  override def onUnsubscribe(channel: String, subscribedChannels: Int): Unit = {
//    println("Client is Unsubscribed from channel : " + channel)
//    println("Client is Subscribed to " + subscribedChannels + " no. of")
//  }
//
//  override def onPSubscribe(pattern: String, subscribedChannels: Int): Unit = {
//  }
//
//  override def onPUnsubscribe(pattern: String, subscribedChannels: Int): Unit = {
//  }
//
//  override def onPMessage(pattern: String, channel: String, message: String): Unit = {
//  }
//}
