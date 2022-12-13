//import Cloudant.CloudantCRUD
import Mongo.CRUD
import Mongo.Cast
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.jsoup.{HttpStatusException, Jsoup}
import ujson.Value.Value

import scala.collection.immutable.HashMap
import scala.collection.{immutable, mutable}


object OlxDataCrawler extends App {

  //  val cloudantClient: CloudantCRUD = new CloudantCRUD
  val mongoOps: CRUD = new CRUD()
  val mongoCast: Cast = new Cast()

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
        try {
          val innerPage: Document = Jsoup.connect(carIterator.select("div > a")
            .attr("href"))
            .maxBodySize(0)
            .userAgent("Mozilla")
            .data("name", "jsoup")
            .timeout(50 * 1000)
            .get


          val masterJson: Value = ujson.read(innerPage.getElementsByAttributeValue("type", "text/plain").attr("data-json").replace("&quot", ""))

          val properties: mutable.HashMap[String, String] = new mutable.HashMap[String, String]()
          val propertiesList: mutable.HashMap[String, List[String]] = new mutable.HashMap[String, List[String]]()

          masterJson("ad")("properties").arr.map(item => if (item("values").isNull) {
            properties += item("name").str -> item("value").str
          }
          else {
            propertiesList += item("name").str -> item("values").arr.map(item => item("label").str).toList
          })

          val mapOfImages: List[Option[String]] = if (masterJson("ad")("images").arrOpt.isDefined) {
            masterJson("ad")("images").arr.map(image => image("original").strOpt).toList
          } else { None.toList } // Map of Images

          val title: Option[String] = masterJson("ad")("subject").strOpt // Title

          val model: Option[String] = properties.get("vehicle_model") // Model

          val brand: Option[String] = properties.get("vehicle_brand") // Brand

          val price: Option[Double] = masterJson("ad")("priceValue").strOpt.getOrElse(None).toString
            .replace("R$ ", "").replace(".", "").toDoubleOption // Price

          val financialInformation: Option[List[String]] = propertiesList.get("financial")

          val kilometers: Option[Double] = properties.get("mileage").toString.toDoubleOption // Kilometers

          val description: Option[String] = masterJson("ad")("body").strOpt // Description

          val typeOfCar: Option[String] = properties.get("cartype") // Type of car

          val typeOfShift: Option[String] = properties.get("gearbox") // Type of shift

          val typeOfFuel: Option[String] = properties.get("fuel") // Type of Fuel

          val typeOfDirection: Option[String] = properties.get("car_steering") // Type of Direction

          val yearOfFabrication: Option[String] = properties.get("regdate") // Year of fabrication

          val color: Option[String] = properties.get("carcolor") // Color

          val endOfPlate: Option[Int] = properties.get("end_tag").toString.toIntOption // End of plate

          val motorPower: Option[Double] = properties.get("motorpower").toString.toDoubleOption // Motor power

          val hasGNV: Option[Boolean] = if (properties.get("gnv_kit").toString == "Sim") {"true".toBooleanOption}
          else { "false".toBooleanOption } // HasGNV

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

          val optionals: Option[List[String]] = propertiesList.get("car_features") // Optionals

          val locationInformation: Map[String, Option[Any]] =
            Map(
              "address" -> masterJson("ad")("location")("address").strOpt,
              "neighbourhood" -> masterJson("ad")("location")("neighbourhood").strOpt,
              "neighbourhoodId" -> masterJson("ad")("location")("neighbourhoodId").numOpt,
              "municipality" -> masterJson("ad")("location")("municipality").strOpt,
              "municipalityId" -> masterJson("ad")("location")("municipalityId").numOpt,
              "zipcode" -> masterJson("ad")("location")("zipcode").strOpt,
              "mapLati" -> masterJson("ad")("location")("mapLati").numOpt,
              "mapLong" -> masterJson("ad")("location")("mapLong").numOpt,
              "uf" -> masterJson("ad")("location")("uf").strOpt,
              "ddd" -> masterJson("ad")("location")("ddd").strOpt,
              "zoneId" -> masterJson("ad")("location")("zoneId").numOpt,
              "zone" -> masterJson("ad")("location")("zone").strOpt,
              "region" -> masterJson("ad")("location")("region").strOpt
            )

          val url: String = carIterator.select("div > a").attr("href") // URL

          val adId: String = masterJson("ad")("adId").num.toString // ID

          val publishDate: String = innerPage.select(".hSZkck").text
            .replace("Publicado em ", "").replace(" às ", "T") // Publish Date

          val profileInformation: Map[String, Option[Any]] = if ( masterJson("ad")("sellerHistory").objOpt.isDefined )
          {
            Map(
              "accountId" -> masterJson("ad")("sellerHistory")("id").strOpt,
              "userId" -> masterJson("ad")("user")("userId").numOpt,
              "name" -> masterJson("ad")("user")("name").strOpt,
              "isPhoneVerified" -> masterJson("ad")("phone")("phoneVerified").boolOpt,
              "salesAmounts" -> masterJson("ad")("sellerHistory")("salesAmounts").numOpt,
              "canceledSalesAmounts" -> masterJson("ad")("sellerHistory")("canceledSalesAmounts").numOpt,
              "totalDispatchTimeInMinutes" -> masterJson("ad")("sellerHistory")("totalDispatchTimeInMinutes").numOpt,
              "averageDispatchTime" -> masterJson("ad")("sellerHistory")("averageDispatchTime").strOpt.get.replace(" minutos", "").toDoubleOption,
              if ( masterJson("ad")("user")("configs").objOpt.isDefined )
                { "proAccount" -> masterJson("ad")("user")("configs")("proAccount").boolOpt } else { "proAccount" -> None }
            )
          } else null

          val fundingInformation: Option[Map[String, Double]] =
            if ( masterJson("ad")("carSpecificData")("financing")("installment").objOpt.isDefined &&
              masterJson("ad")("carSpecificData")("financing")("upfrontPayment").objOpt.isDefined )
            {
              val tempConditions: Array[String] = masterJson("ad")("carSpecificData")("financing")("installment")("value")
                .str.split("x")
              Option(Map(
                "funding installments" -> tempConditions(0).toDouble,
                "funding installment value" -> tempConditions(1).replace("R$ ", "")
                  .replace(".", "").replace(",", ".")
                  .replace("*", "").toDouble,
                "funding entry" -> masterJson("ad")("carSpecificData")("financing")("upfrontPayment")("value")
                  .str.replace("R$ ", "").replace(".", "")
                  .replace(",", ".").toDouble
              ))
            } else None

          val verificationInformation: Map[String, Option[Any]] = if ( masterJson("ad")("vehicleReport").objOpt.isDefined )
          {
            Map(
              "isVerified" -> masterJson("ad")("vehicleReport")("enabled").boolOpt,
              if ( masterJson("ad")("vehicleReport")("description").strOpt.isDefined )
                {
                  "Query date" -> Option("[0-9]{2}/+[0-9]{2}/+[0-9]{4}".r.findFirstIn(masterJson("ad")("vehicleReport")("description").strOpt.get
                    .replace("Verifique se os dados do Histórico Veicular são os mesmos informados no anúncio. ", "")
                    .strip()).get.replace("/", "-").reverse.concat("T" + "[0-9]{2}:+[0-9]{2}:+[0-9]{2}".r.findFirstIn(masterJson("ad")("vehicleReport")("description").strOpt.get
                    .replace("Verifique se os dados do Histórico Veicular são os mesmos informados no anúncio. ", "")
                    .strip()).get))
                } else "description" -> None,
              if ( masterJson("ad")("vehicleReport")("reportLink").strOpt.isDefined )
                { "reportLink" -> masterJson("ad")("vehicleReport")("reportLink").strOpt } else { "reportLink" -> None },
              if ( masterJson("ad")("vehicleReport")("tags").arrOpt.isDefined )
                { "tags" -> Option(masterJson("ad")("vehicleReport")("tags").arr.map(tag => tag("label").str).toList) }
              else { "tags" -> None }
            )
          } else null

          val averageOlxPrice: Option[Double] = innerPage.select(".hOrZdh:nth-child(1) .iDQboK").text
            .replace("R$ ", "").replace(".", "").toDoubleOption

          val fipePrice: Option[Double] = if ( masterJson("ad")("abuyFipePrice").objOpt.isDefined )
           { masterJson("ad")("abuyFipePrice")("fipePrice").numOpt } else None

          val fipePriceRef: Map[String, Double] = if ( masterJson("ad")("abuyPriceRef").objOpt.isDefined )
           {
             Map(
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
             )
           } else null

          val differenceToOlxAveragePrice: Option[Double] = if (price.isDefined & averageOlxPrice.isDefined)
            Some(price.get - averageOlxPrice.get) else None

          val differenceToFipePrice: Option[Double] = if (price.isDefined & fipePrice.isDefined)
            Some(price.get - fipePrice.get) else None

          val vehicleSpecificData: mutable.HashMap[String, String] = new mutable.HashMap[String, String]()
          masterJson("ad")("vehicleSpecificData").arr
            .foreach(item => vehicleSpecificData.addOne(item("key").str, item("value").str))

          if (mapOfImages.nonEmpty & adId.nonEmpty) {

            mongoOps.createAndInsert(adId, mongoCast.cast(
              adId, mapOfImages, title, model, brand, price,
              financialInformation, kilometers, description, typeOfCar, typeOfShift, typeOfFuel, typeOfDirection,
              yearOfFabrication, color, endOfPlate, motorPower, hasGNV, numberOfDoors, characteristics, optionals,
              locationInformation, url, publishDate, profileInformation, fundingInformation, verificationInformation,
              averageOlxPrice, fipePrice, fipePriceRef, differenceToOlxAveragePrice, differenceToFipePrice,
              vehicleSpecificData))
          }
        } catch {
          case e: HttpStatusException => println(s"error 404 trying to fetch page: $e")
        }
      }
    }
  }
}