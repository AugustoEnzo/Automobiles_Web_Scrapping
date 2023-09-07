package crawlers

import com.typesafe.scalalogging.Logger
import crawlers.OlxDataCrawler.getClass
import helpers.{Simple, Transformer}
import models.{OlxAdJava, OlxAdWithOptions}
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.jsoup.{HttpStatusException, Jsoup}
import producers.OlxDataCrawlerProducer
import ujson.Value.Value

import java.io.IOException
import java.net.SocketTimeoutException
import scala.util.matching.Regex


object OlxDataCrawler extends App {
  private val logger = Logger(getClass.getName)

  private val numberPattern: Regex = "[0-9]+".r

  private val simple: Simple = new Simple
  private val kafkaProducer: OlxDataCrawlerProducer = OlxDataCrawlerProducer()
  private val transformer: Transformer = Transformer()

  try {
    logger.debug(f"Starting the crawler.")
    val homeDocument: Document = Jsoup.connect(s"https://am.olx.com.br/autos-e-pecas/carros-vans-e-utilitarios")
      .userAgent("Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1667.0 Safari/537.36")
      .header("Accept-Language", "pt-BR")
      .header("Accept-Encoding", "gzip,deflate,sdch")
      .timeout(3 * 1000)
      .get()
    logger.debug(f"Accessing the home page document.")

    val docMaxPage = homeDocument.select("#listing-main-content-slot > div.h3us20-6.eCFxPX > div > div > div.h3us20-2.bdQAUC > div > div:nth-child(2) > a").attr("href").split("o=")(1)

    for (pg: Int <- Range(1, docMaxPage.toInt + 1, 1)) {
      try {
        val page: String = pg.toString
        val pageIteratorDocument: Document = Jsoup.connect(s"https://am.olx.com.br/autos-e-pecas/carros-vans-e-utilitarios?o=$page")
          .maxBodySize(0)
          .timeout(5 * 1000)
          .ignoreContentType(true)
          .get()
        logger.debug(f"Accessing the document for page: $page")

        for (adIndex: Int <- Range(1, 53 + 1, 1)) {
          val adIteratorDocument: Elements = pageIteratorDocument.select(s"#ad-list > li:nth-child($adIndex)")
          logger.debug(f"Accessing data for ad number: $adIndex, page: $page")

          if (adIteratorDocument.text != "") {
            try {
              val adDocument: Document = Jsoup.connect(adIteratorDocument.select("div > a")
                .attr("href"))
                .maxBodySize(0)
                .timeout(5 * 1000)
                .get()

              val masterJson: Value = ujson.read(adDocument.getElementsByAttributeValue("type", "text/plain").attr("data-json").replace("&quot", ""))

              if (masterJson("ad")("images").arrOpt.isDefined && masterJson("ad")("adId").numOpt.isDefined) {

                val adId: String = masterJson("ad")("adId").num.toString // ID

                logger.debug(f"Collecting data for ad: $adId")

                val url: String = adIteratorDocument.select("div > a").attr("href") // URL

                // Utilized for later calculations
                val properties: Map[String, String] = masterJson("ad")("properties").arr
                  .map(item => item("name").str -> item("value").str).toMap[String, String]

                // Utilized for later calculations
                val propertiesMap: Map[String, List[String]] =
                  masterJson("ad")("properties").arr.map(item => if (!item("values").isNull)
                    item("name").str -> item("values").arr.map(inner_item => inner_item("label").str).toList
                  else item("name").str -> List[String]()).toMap[String, List[String]]

                val listOfImages: List[String] =
                  if (masterJson("ad")("images").arrOpt.isDefined) {
                    masterJson("ad")("images").arr.map(image =>
                      if simple.verifyIfImageLinkExists(image) then image("original").strOpt.get
                      else String()
                    ).toList
                  } else List[String]() // Map of Images

                val title: Option[String] = masterJson("ad")("subject").strOpt // Title

                val model: Option[String] = properties.get("vehicle_model") // Model

                val brand: Option[String] = properties.get("vehicle_brand") // Brand

                val price: Option[String] = if (masterJson("ad")("priceValue").strOpt.isDefined) {
                  Some(masterJson("ad")("priceValue").strOpt.get
                    .replace("R$ ", "").replace(".", ""))
                } else None // Price

                val financialInformation: Option[List[String]] = propertiesMap.get("financial")

                val kilometers: Option[String] = properties.get("mileage") // Kilometers

                val description: Option[String] = masterJson("ad")("body").strOpt // Description

                val typeOfCar: Option[String] = properties.get("cartype") // Type of car

                val typeOfShift: Option[String] = properties.get("gearbox") // Type of shift

                val typeOfFuel: Option[String] = properties.get("fuel") // Type of Fuel

                val typeOfDirection: Option[String] = properties.get("car_steering") // Type of Direction

                val yearOfFabrication: Option[String] = properties.get("regdate") // Year of fabrication

                val color: Option[String] = properties.get("carcolor") // Color

                val endOfPlate: Option[String] = properties.get("end_tag")// End of plate

                val enginePower: Option[String] = properties.get("motorpower") // Motor power

                val hasGNV: Option[String] = properties.get("has_gnv") // HasGNV

                val numberOfDoors: Option[String] = properties.get("doors") // Number of doors

                val characteristics: Map[String, Option[String]] = Map[String, Option[String]](
                  properties.get("owner") match {
                    case Some("Sim") => "haveUniqueOwner" -> Some("true")
                    case Some("Não") => "haveUniqueOwner" -> Some("false")
                    case None => "haveUniqueOwner" -> None
                    case Some(_) => "haveUniqueOwner" -> None
                  },
                  properties.get("exchange") match {
                    case Some("Sim") => "acceptsExchanges" -> Some("true")
                    case Some("Não") => "acceptsExchanges" -> Some("false")
                    case None => "acceptsExchanges" -> None
                    case Some(_) => "acceptsExchanges" -> None
                  },
                  properties.get("owner_manual") match {
                    case Some("Sim") => "haveOwnerManual" -> Some("true")
                    case Some("Não") => "haveOwnerManual" -> Some("false")
                    case None => "haveOwnerManual" -> None
                    case Some(_) => "haveOwnerManual" -> None
                  },
                  properties.get("dealership_review") match {
                    case Some("Sim") => "haveDealershipReview" -> Some("true")
                    case Some("Não") => "haveDealershipReview" -> Some("false")
                    case None => "haveDealershipReview" -> None
                    case Some(_) => "haveDealershipReview" -> None
                  },
                  properties.get("haveWarranty") match {
                    case Some("Sim") => "haveWarranty" -> Some("true")
                    case Some("Não") => "haveWarranty" -> Some("false")
                    case None => "haveWarranty" -> None
                    case Some(_) => "haveWarranty" -> None
                  },
                  if (masterJson("ad")("carSpecificData")("isFinanceable").boolOpt.isDefined) {
                    "isFinanceable" -> Some(masterJson("ad")("carSpecificData")("isFinanceable").boolOpt.get.toString)
                  } else {
                    "isFinanceable" -> None
                  },
                  if (masterJson("ad")("isFeatured").boolOpt.isDefined) {
                    "isFeatured" -> Some(masterJson("ad")("isFeatured").boolOpt.get.toString)
                  } else {
                    "isFeatured" -> None
                  }
                )

                val optionals: Option[List[String]] = propertiesMap.get("car_features") // Optionals

                val locationInformation: Map[String, Option[String]] =
                  Map(
                    "address" -> masterJson("ad")("location")("address").strOpt,
                    "neighbourhood" -> masterJson("ad")("location")("neighbourhood").strOpt,
                    "neighbourhoodId" -> Some(masterJson("ad")("location")("neighbourhoodId").numOpt.get.toString),
                    "municipality" -> masterJson("ad")("location")("municipality").strOpt,
                    "municipalityId" -> Some(masterJson("ad")("location")("municipalityId").numOpt.get.toString),
                    "zipcode" -> masterJson("ad")("location")("zipcode").strOpt,
                    "mapLati" -> Some(masterJson("ad")("location")("mapLati").numOpt.get.toString),
                    "mapLong" -> Some(masterJson("ad")("location")("mapLong").numOpt.get.toString),
                    "uf" -> masterJson("ad")("location")("uf").strOpt,
                    "ddd" -> masterJson("ad")("location")("ddd").strOpt,
                    "zoneId" -> Some(masterJson("ad")("location")("zoneId").numOpt.get.toString),
                    "zone" -> masterJson("ad")("location")("zone").strOpt,
                    "region" -> masterJson("ad")("location")("region").strOpt
                  )

                val publishDate: String = adDocument.select(".hSZkck").text
                  .replace("Publicado em ", "").replace(" às ", "T") // Publish Date

                val profileInformation: Map[String, Option[String]] =
                  if (masterJson("ad")("sellerHistory").objOpt.isDefined) {
                    val tempUserInfo: Value = masterJson("ad")("user")
                    // Mount the final map with the temporary data
                    masterJson("ad")("sellerHistory") match
                      case sellerHistory: Value =>
                        val averageDispatchTime: String = masterJson("ad")("sellerHistory")("averageDispatchTime").strOpt.get
                        Map[String, Option[String]](
                          "accountId" -> tempUserInfo("accountId").strOpt,
                          "userId" -> Some(tempUserInfo("userId").numOpt.get.toString),
                          "name" -> tempUserInfo("name").strOpt,
                          "isPhoneVerified" -> Some(masterJson("ad")("phone")("phoneVerified").boolOpt.get.toString),
                          "salesAmounts" -> Some(sellerHistory("salesAmounts").numOpt.get.toString),
                          "canceledSalesAmounts" -> Some(sellerHistory("canceledSalesAmounts").numOpt.get.toString),
                          "totalDispatchTimeInMinutes" -> Some(sellerHistory("totalDispatchTimeInMinutes").numOpt.get.toString),
                          if (averageDispatchTime.contains("dia") || averageDispatchTime.contains("dias")) {
                            "averageDispatchTime" -> Some(
                              (numberPattern.findFirstMatchIn(averageDispatchTime
                                .replace(" ", "")
                                .replace("minutos", "")
                                .replace("minuto", "")
                                .replace("dia", "")
                                .replace("dias", "")).get.start * 3600).toString
                            )
                          } else if (averageDispatchTime.contains("mês") || averageDispatchTime.contains("meses")) {
                            "averageDispatchTime" -> Some(
                              (numberPattern.findFirstMatchIn(averageDispatchTime
                                .replace(" ", "")
                                .replace("minutos", "")
                                .replace("minuto", "")
                                .replace("dia", "")
                                .replace("dias", "")
                                .replace("mês", "")
                                .replace("meses", "")).get.start * 43800.048).toString
                            )
                          } else {
                            "averageDispatchTime" -> Some(
                              numberPattern.findFirstMatchIn(averageDispatchTime
                                .replace(" ", "")
                                .replace("minutes", "")
                                .replace("minuto", "")
                                .replace("dia", "")
                                .replace("dias", "")).get.start.toString
                            )
                          },
                          if (tempUserInfo("configs").objOpt.isDefined) {
                            "proAccount" -> Some(tempUserInfo("configs")("proAccount").boolOpt.get.toString)
                          } else {
                            "proAccount" -> None
                          }
                        )
                  } else {
                    Map(
                      "accountId" -> None,
                      "userId" -> None,
                      "name" -> None,
                      "isPhoneVerified" -> None,
                      "salesAmounts" -> None,
                      "canceledSalesAmounts" -> None,
                      "totalDispatchTimeInMinutes" -> None,
                      "averageDispatchTime" -> None,
                      "proAccount" -> None
                    )
                  }

                val fundingInformation: Map[String, Option[String]] =
                  if (masterJson("ad")("carSpecificData")("financing")("installment").objOpt.isDefined &&
                    masterJson("ad")("carSpecificData")("financing")("upfrontPayment").objOpt.isDefined) {
                    val tempConditions: Array[String] = masterJson("ad")("carSpecificData")("financing")("installment")("value")
                      .str.split("x") // temp variables are used to get data

                    Map[String, Option[String]](
                      "fundingInstallments" -> Some(tempConditions(0)),
                      "fundingInstallmentValue" -> Some(
                        tempConditions(1).replace("R$ ", "")
                        .replace(".", "").replace(",", ".")
                        .replace("*", "")
                      ),
                      "fundingEntry" -> Some(
                        masterJson("ad")("carSpecificData")("financing")("upfrontPayment")("value")
                        .str.replace("R$ ", "").replace(".", "")
                        .replace(",", ".")
                      )
                    )
                  } else {
                    Map[String, Option[String]](
                    "fundingInstallments" -> None,
                    "fundingInstallmentValue" -> None,
                    "fundingEntry" -> None
                    )
                  }

                val tagsList: List[String] =
                  if (masterJson("ad")("vehicleReport")("tags").arrOpt.isDefined) {
                    masterJson("ad")("vehicleReport")("tags").arr.map(tag => tag("label").str).toList
                  } else List[String]()

                val verificationInformation: Map[String, Option[String]] =
                  if (masterJson("ad")("vehicleReport")("enabled").boolOpt.get) {
                    Map(
                      "isVerified" -> Some(masterJson("ad")("vehicleReport")("enabled").boolOpt.get.toString),
                      "QueryDate" -> Some("[0-9]{2}/+[0-9]{2}/+[0-9]{4}".r.findFirstIn(masterJson("ad")("vehicleReport")("description").strOpt.get
                        .replace("Verifique se os dados do Histórico Veicular são os mesmos informados no anúncio. ", "")
                        .strip()).get.replace("/", "-").reverse.concat("T" + "[0-9]{2}:+[0-9]{2}:+[0-9]{2}".r.findFirstIn(masterJson("ad")("vehicleReport")("description").strOpt.get
                        .replace("Verifique se os dados do Histórico Veicular são os mesmos informados no anúncio. ", "")
                        .strip()).get)),
                      "reportLink" -> masterJson("ad")("vehicleReport")("reportLink").strOpt
                    )
                  } else {
                    Map(
                      "isVerified" -> Some("false"),
                      "QueryDate" -> None,
                      "reportLink" -> None,
                      "tags" -> None
                    )
                  }

                val averageOlxPrice: Option[String] = if (adIteratorDocument.select(".jJwVUw:nth-child(1) .cUyonN").text.nonEmpty) {
                  Some(adIteratorDocument.select(".jJwVUw:nth-child(1) .cUyonN").text
                  .replace("R$ ", String()).replace(".", String()))} else None

                val fipePrice: Option[String] = if (masterJson("ad")("abuyFipePrice").objOpt.isDefined && !masterJson("ad")("abuyFipePrice").isNull) {
                  Some(masterJson("ad")("abuyFipePrice")("fipePrice").numOpt.get.toString)
                } else None

                val fipePriceRef: Map[String, Option[String]] = if (masterJson("ad")("abuyPriceRef").objOpt.isDefined) {
                  Map(
                    "yearMonthReference" -> Some(masterJson("ad")("abuyPriceRef")("year_month_ref").num.toString),
                    "priceMin" -> Some(masterJson("ad")("abuyPriceRef")("price_min").num.toString),
                    "priceP25" -> Some(masterJson("ad")("abuyPriceRef")("price_p25").num.toString),
                    "priceP33" -> Some(masterJson("ad")("abuyPriceRef")("price_p33").num.toString),
                    "priceP50" -> Some(masterJson("ad")("abuyPriceRef")("price_p50").num.toString),
                    "priceP66" -> Some(masterJson("ad")("abuyPriceRef")("price_p66").num.toString),
                    "priceP75" -> Some(masterJson("ad")("abuyPriceRef")("price_p75").num.toString),
                    "priceMax" -> Some(masterJson("ad")("abuyPriceRef")("price_max").num.toString),
                    "priceStdDev" -> Some(masterJson("ad")("abuyPriceRef")("price_stddev").num.toString),
                    "vehicleCount" -> Some(masterJson("ad")("abuyPriceRef")("vehicle_count").num.toString)
                  )
                } else
                  Map(
                    "yearMonthReference" -> None,
                    "priceMin" -> None,
                    "priceP25" -> None,
                    "priceP33" -> None,
                    "priceP50" -> None,
                    "priceP66" -> None,
                    "priceP75" -> None,
                    "priceMax" -> None,
                    "priceStdDev" -> None,
                    "vehicleCount" -> None
                  )

                val differenceToOlxAveragePrice: Option[String] = if (price.isDefined & averageOlxPrice.isDefined)
                  Some((price.get.toDouble - averageOlxPrice.get.toDouble).toString) else None

                val differenceToFipePrice: Option[String] = if (price.isDefined & fipePrice.isDefined)
                  Some((price.get.toDouble - fipePrice.get.toDouble).toString) else None

                val vehicleSpecificData: Map[String, String] =
                  masterJson("ad")("vehicleSpecificData").arr
                    .map(item => if item("value").strOpt.isDefined then
                      item("key").str -> item("value").str else item("key").str -> String()
                    ).toMap[String, String]

                val olxAd: OlxAdJava = transformer.mapToJava(OlxAdWithOptions(
                  adId = adId, listOfImages = listOfImages, title = title, model = model, brand = brand, price = price,
                  financialInformation = financialInformation, kilometers = kilometers, description = description,
                  typeOfCar = typeOfCar, typeOfShift = typeOfShift, typeOfFuel = typeOfFuel,
                  typeOfDirection = typeOfDirection, yearOfFabrication = yearOfFabrication, color = color,
                  endOfPlate = endOfPlate, enginePower = enginePower, hasGNV = hasGNV, numberOfDoors = numberOfDoors,
                  characteristics = characteristics, optionals = optionals, locationInformation = locationInformation,
                  url = url, publishDate = publishDate, profileInformation = profileInformation,
                  fundingInformation = fundingInformation, tagsList = tagsList,
                  verificationInformation = verificationInformation, averageOlxPrice = averageOlxPrice,
                  fipePrice = fipePrice, fipePriceRef = fipePriceRef,
                  differenceToOlxAveragePrice = differenceToOlxAveragePrice,
                  differenceToFipePrice = differenceToFipePrice, vehicleSpecificData = vehicleSpecificData))

                kafkaProducer.send(kafkaProducer.TOPIC_NAME, olxAd)
              }
            } catch {
              case httpStatusException: HttpStatusException => logger.error(f"Error trying to fetch page ad page: $httpStatusException")
              case socketTimeoutException: SocketTimeoutException => logger.error(f"Error trying to fetch ad page: $socketTimeoutException")
              case inputOutputException: IOException => logger.error(f"Jsoup could not read the date from this document. $inputOutputException")
            }
          }
        }
      } catch {
        case httpStatusException: HttpStatusException => logger.error(f"Error trying to fetch list page: $httpStatusException")
        case socketTimeoutException: SocketTimeoutException => logger.error(f"Error trying to fetch list page: $socketTimeoutException")
        case inputOutputException: IOException => logger.error(f"Jsoup could not read the date from this document. $inputOutputException")
      }
    }
  } catch {
    case httpStatusException: HttpStatusException =>
      logger.error(f"Error trying fetch the page list in the first document iteration, recommend to restart the app.\n $httpStatusException")
    case socketTimeoutException: SocketTimeoutException =>
      logger.error(f"Error trying fetch the page list in the first document iteration, recommend to restart the app.\n $socketTimeoutException")
    case inputOutputException: IOException => logger.error(f"Jsoup could not read the date from this document. $inputOutputException")
  }
}