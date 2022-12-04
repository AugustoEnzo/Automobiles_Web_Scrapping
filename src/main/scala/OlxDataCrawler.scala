import Cloudant.CloudantCRUD
import ParseTitle.Parser
import com.google.gson.{JsonObject, JsonParser}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import ujson.Value.Value

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import java.util
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer


object OlxDataCrawler extends App {

  val cloudantClient: CloudantCRUD = new CloudantCRUD
  val parser: Parser = new Parser

  private val doc: Document = Jsoup.connect(s"https://am.olx.com.br/autos-e-pecas/carros-vans-e-utilitarios")
    .data("query", "Java")
    .userAgent("Mozilla")
    .timeout(10000)
    .get()

  private val docMaxPage = doc.select("#listing-main-content-slot > div.h3us20-6.ehQTxA > div > div > div.h3us20-3.csYflq > div > div.sc-EHOje.kRemsz > p").text.split(" ")(3)

  for (pg: Int <- Range(1, docMaxPage.toInt + 1, 1)) {
    val page: String = pg.toString
    val doc: Document = Jsoup.connect(s"https://am.olx.com.br/autos-e-pecas/carros-vans-e-utilitarios?o=$page")
      .maxBodySize(1024 * 1024 * 100)
      .userAgent("Mozilla")
      .data("name", "jsoup")
      .timeout(10 * 1000)
      .ignoreContentType(true)
      .get

    for (num: Int <- Range(1, 55 + 1, 1)) {
      val carIterator: Elements = doc.select(s"#ad-list > li:nth-child($num)")

      if (carIterator.text != "") {
        val innerPage: Document = Jsoup.connect(carIterator.select("div > a")
          .attr("href"))
          .maxBodySize(0)
          .userAgent("Mozilla")
          .data("name", "jsoup")
          .timeout(50 * 1000)
          .ignoreContentType(true)
          .get

//        Files.write(Paths.get("pageHTML.html"), util.Arrays.asList(innerPage.html), StandardCharsets.UTF_8)

        val masterJson: Value = ujson.read(innerPage.getElementsByAttributeValue("type", "text/plain").attr("data-json").replace("&quot", ""))

        val properties: mutable.HashMap[String, String] = new mutable.HashMap[String, String]()
        val propertiesArray: mutable.HashMap[String, ArrayBuffer[String]] = new mutable.HashMap[String, ArrayBuffer[String]]()

        masterJson("ad")("properties").arr.map(item => if item("values").isNull
          then { properties += item("name").str -> item("value").str }
          else { propertiesArray += item("name").str -> item("values").arr.map(item => item("label").str) } )

        val mapOfImages: mutable.ArrayBuffer[Option[String]] = if masterJson("ad")("images").objOpt.getOrElse(None) != None
          then masterJson("ad")("images").arr.map(image => image("thumbnail").strOpt) else ArrayBuffer(None) // Map of Images

        val title: Option[String] = masterJson("ad")("subject").strOpt // Title

        val model: Option[String] = properties.get("vehicle_model") // Model

        val brand: Option[String] = properties.get("vehicle_brand") // Brand

        val price: Option[Double] = masterJson("ad")("priceValue").strOpt.getOrElse(None).toString
          .replace("R$ ","").replace(".", "").toDoubleOption // Price

        val financialInformation: Option[ArrayBuffer[String]] = propertiesArray.get("financial")

        val kilometers: Option[Double] = properties.get("mileage").toString.toDoubleOption // Kilometers

        val description: Option[String] = masterJson("ad")("body").strOpt // Description

        val typeOfCar: Option[String] = properties.get("cartype") // Type of car

        val typeOfShift: Option[String] = properties.get("gearbox") // Type of shift

        val typeOfFuel: Option[String] = properties.get("fuel") // Type of Fuel

        val yearOfFabrication: Option[Int] = properties.get("regdate").toString.toIntOption // Year of fabrication

        val color: Option[String] = properties.get("carcolor") // Color

        val endOfPlate: Option[Int] = properties.get("end_tag").toString.toIntOption // End of plate

        val motorPower: Option[Double] = properties.get("motorpower").toString.toDoubleOption // Motor power

        val hasGNV: Option[Boolean] = if properties.get("gnv_kit").toString == "Sim" then "true".toBooleanOption
        else "false".toBooleanOption // HasGNV

        val typeOfDirection: Option[String] = properties.get("car_steering") // Type of Direction

        val numberOfDoors: Option[Int] = properties.get("doors").toString.toIntOption // Number of doors

        val characteristics: Map[String, Option[Boolean]] = Map[String, Option[Boolean]](
          properties.get("owner") match {
            case Some("Sim") => "haveUniqueOwner" -> "true".toBooleanOption
            case Some("Não") => "haveUniqueOwner" -> "false".toBooleanOption
            case None => "haveUniqueOwner" -> "None".toBooleanOption
            case Some(_) => "haveUniqueOwner" -> "None".toBooleanOption
          },
          properties.get("exchange") match {
            case Some("Sim") => "acceptsExchanges" -> "true".toBooleanOption
            case Some("Não") => "acceptsExchanges" -> "false".toBooleanOption
            case None => "acceptsExchanges" -> "None".toBooleanOption
            case Some(_) => "acceptsExchanges" -> "None".toBooleanOption
          },
          properties.get("owner_manual") match {
            case Some("Sim") => "haveOwnerManual" -> "true".toBooleanOption
            case Some("Não") => "haveOwnerManual" -> "false".toBooleanOption
            case None => "haveOwnerManual" -> "None".toBooleanOption
            case Some(_) => "haveOwnerManual" -> "None".toBooleanOption
          },
          properties.get("dealership_review") match {
            case Some("Sim") => "haveDealershipReview" -> "true".toBooleanOption
            case Some("Não") => "haveDealershipReview" -> "false".toBooleanOption
            case None => "haveDealershipReview" -> "None".toBooleanOption
            case Some(_) => "haveDealershipReview" -> "None".toBooleanOption
          },
          properties.get("haveWarranty") match {
            case Some("Sim") => "haveWarranty" -> "true".toBooleanOption
            case Some("Não") => "haveWarranty" -> "false".toBooleanOption
            case None => "haveWarranty" -> "None".toBooleanOption
            case Some(_) => "haveWarranty" -> "None".toBooleanOption
          },
          "isFinanceable" -> masterJson("ad")("carSpecificData")("isFinanceable").boolOpt,
          "isFeatured" -> masterJson("ad")("isFeatured").boolOpt
        )

        val optionals: Option[ArrayBuffer[String]] = propertiesArray.get("car_features") // Optionals

        val locationInformation: ArrayBuffer[Option[String | Double ]] =
          mutable.ArrayBuffer(
            masterJson("ad")("location")("address").strOpt,
            masterJson("ad")("location")("neighbourhood").strOpt,
            masterJson("ad")("location")("neighbourhoodId").numOpt,
            masterJson("ad")("location")("municipality").strOpt,
            masterJson("ad")("location")("municipalityId").numOpt,
            masterJson("ad")("location")("zipcode").strOpt,
            masterJson("ad")("location")("mapLati").numOpt,
            masterJson("ad")("location")("mapLong").numOpt,
            masterJson("ad")("location")("uf").strOpt,
            masterJson("ad")("location")("ddd").strOpt,
            masterJson("ad")("location")("zoneId").numOpt,
            masterJson("ad")("location")("zone").strOpt,
            masterJson("ad")("location")("region").strOpt
          )

        val url: String = carIterator.select("div > a").attr("href") // URL

        val adId: String = masterJson("ad")("adId").num.toString // ID

        val publishDate: String = innerPage.select(".hSZkck").text
          .replace("Publicado em ", "").replace(" às ", "T") // Publish Date

        val profileInformation: Option[Map[String, Option[String | Boolean | Double]]] = if masterJson("ad")("sellerHistory").objOpt.isDefined
          then Option(Map(
              "accountId" -> masterJson("ad")("sellerHistory")("id").strOpt,
              "userId" -> masterJson("ad")("user")("userId").numOpt,
              "name" -> masterJson("ad")("user")("name").strOpt,
              "isPhoneVerified" -> masterJson("ad")("phone")("phoneVerified").boolOpt,
              "salesAmounts" -> masterJson("ad")("sellerHistory")("salesAmounts").numOpt,
              "canceledSalesAmounts" -> masterJson("ad")("sellerHistory")("canceledSalesAmounts").numOpt,
              "totalDispatchTimeInMinutes" -> masterJson("ad")("sellerHistory")("totalDispatchTimeInMinutes").numOpt,
              "averageDispatchTime" -> masterJson("ad")("sellerHistory")("averageDispatchTime").strOpt.get.replace(" minutos", "").toDoubleOption,
              if masterJson("ad")("user")("configs").objOpt.isDefined
                then "proAccount" -> masterJson("ad")("user")("configs")("proAccount").boolOpt else "proAccount" -> None
            )) else None

        val fundingInformation: Option[Map[String, Int | Double]] =
          if masterJson("ad")("carSpecificData")("financing")("installment").objOpt.isDefined ||
            masterJson("ad")("carSpecificData")("financing")("upfrontPayment").objOpt.isDefined
          then {
            val tempConditions: Array[String] = masterJson("ad")("carSpecificData")("financing")("installment")("value")
              .str.split("x")
            Option(Map(
              "funding installments" -> tempConditions(0).toInt,
              "funding installment value" -> tempConditions(1).replace("R$ ", "")
                .replace(".", "").replace(",", ".")
                .replace("*", "").toDouble,
              "funding entry" -> masterJson("ad")("carSpecificData")("financing")("upfrontPayment")("value")
                .str.replace("R$ ", "").replace(".", "")
                .replace(",", ".").toDouble
            ))
          } else None

        val verificationInformation: Map[String, Option[Boolean | String | ArrayBuffer[String]]] = if masterJson("ad")("vehicleReport").objOpt.isDefined
          then Map(
            "isVerified" -> masterJson("ad")("vehicleReport")("enabled").boolOpt,
            if masterJson("ad")("vehicleReport")("description").strOpt.isDefined then
              "Query date" -> Option("[0-9]{2}/+[0-9]{2}/+[0-9]{4}".r.findFirstIn(masterJson("ad")("vehicleReport")("description").strOpt.get
                .replace("Verifique se os dados do Histórico Veicular são os mesmos informados no anúncio. ", "")
                .strip()).get.replace("/", "-").reverse.concat("T" + "[0-9]{2}:+[0-9]{2}:+[0-9]{2}".r.findFirstIn(masterJson("ad")("vehicleReport")("description").strOpt.get
                .replace("Verifique se os dados do Histórico Veicular são os mesmos informados no anúncio. ", "")
                .strip()).get))
            else "description" -> None,
            if masterJson("ad")("vehicleReport")("reportLink").strOpt.isDefined then
              "reportLink" -> masterJson("ad")("vehicleReport")("reportLink").strOpt
            else "reportLink" -> None,
            if masterJson("ad")("vehicleReport")("tags").arrOpt.isDefined then
              "tags" -> Option(masterJson("ad")("vehicleReport")("tags").arr.map(tag => tag("label").str))
            else "tags" -> None
          ) else null

        val tags: mutable.ArrayBuffer[String] = masterJson("ad")("tags").arr.map(tag => tag("label").str)

        val averageOlxPrice: Option[Int] = innerPage.select(".hOrZdh:nth-child(1) .iDQboK").text
          .replace("R$ ", "").replace(".", "").toIntOption

        val fipePrice: Option[Double] = if masterJson("ad")("abuyFipePrice").objOpt.getOrElse(None) != None
        then masterJson("ad")("abuyFipePrice")("fipePrice").numOpt else None

        val fipePriceRef: Option[Map[String, Double]] = if masterJson("ad")("abuyPriceRef").objOpt.getOrElse(None) != None
          then Option(Map(
              "year_month_ref" -> masterJson("ad")("abuyPriceRef")("year_month_ref").num,
              "price_min" -> masterJson("ad")("abuyPriceRef")("price_min").num,
              "price_p25" -> masterJson("ad")("abuyPriceRef")("price_p25").num,
              "price_p33" -> masterJson("ad")("abuyPriceRef")("price_p33").num,
              "price_p50" -> masterJson("ad")("abuyPriceRef")("price_p50").num,
              "price_p66" -> masterJson("ad")("abuyPriceRef")("price_p66").num,
              "price_p75" -> masterJson("ad")("abuyPriceRef")("price_p75").num,
              "price_max" -> masterJson("ad")("abuyPriceRef")("price_max").num,
              "price_stddev" -> masterJson("ad")("abuyPriceRef")("price_stddev").num,
              "vehicle_count" -> masterJson("ad")("abuyPriceRef")("vehicle_count").num,
            )) else None

        val differenceToOlxAveragePrice: Option[Double] = if (price.isDefined & averageOlxPrice.isDefined)
          Some(price.get - averageOlxPrice.get) else None

        val differenceToFipePrice: Option[Double] = if (price.isDefined & fipePrice.isDefined)
          Some(price.get - fipePrice.get) else None

        val vehicleSpecificData: mutable.HashMap[String, String] = new mutable.HashMap[String, String]()
        masterJson("ad")("vehicleSpecificData").arr
          .map(item => vehicleSpecificData += item("key").str -> item("value").str)

        if (mapOfImages.nonEmpty & adId.nonEmpty) {

//          if (!cloudantClient.documentOnDatabase("automobiles", id)) {
//            cloudantClient.create_document("automobiles", id, thumbnail, mapOfImages, title,
//              price, model, brand, kilometers, description, typeOfCar, location,
//              typeOfShift, typeOfFuel, yearOfFabrication, color, endOfPlate, motorPower, hasGNV,
//              typeOfDirection, numberOfDoors, optionals, url, publishData, publisher, isVerified, profile, cep,
//              characteristics, isHighlighted, averageOlxPrice, fipePrice, differenceToOlxAveragePrice, differenceToFipePrice)
//          }
        }
      }
    }
  }
}