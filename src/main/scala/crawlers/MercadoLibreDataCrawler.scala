//import org.jsoup.nodes.Document
//import org.jsoup.select.Elements
//import org.jsoup.{HttpStatusException, Jsoup}
//import ujson.Value.Value
//
//import scala.collection.mutable
//import scala.collection.mutable.{ArrayBuffer, ListBuffer}
//
//
//object MercadoLibreDataCrawler extends App {
//
//  private val tempAllocation: (ArrayBuffer[List[String]], ListBuffer[String]) = mongoSyncOps.readAndGetCollectionInformation()
//  private val requestArgsArray: mutable.ArrayBuffer[List[String]] = tempAllocation._1
//  private val collectionsIds: ListBuffer[String] = tempAllocation._2
//
//  for (requestArgs <- requestArgsArray) {
//    val indexOfCollection: Int = requestArgsArray.indexOf(requestArgs)
//
//    val mainPageDocument: Document = Jsoup.connect(s"https://lista.mercadolivre.com.br/acessorios-veiculos/pecas-carros-caminhonetes/${requestArgs.head.replace(" ", "-").toLowerCase()}-${requestArgs(1)}")
//      .maxBodySize(0)
//      .timeout(10000)
//      .get
//
//    var maxPage: Int = 1
//
//    if (mainPageDocument.select(".andes-pagination__page-count").text.nonEmpty) {
//       maxPage = mainPageDocument.select(".andes-pagination__page-count").text.replace("de ", "").toInt
//    }
//
//    for (page: Int <- Range(1, maxPage+1, 1)) {
//
//      for (num: Int <- Range(1, 50 + 1, 1)) {
//        val adIterator: Elements = mainPageDocument.select(s"#root-app > div > div.ui-search-main.ui-search-main--exhibitor.ui-search-main--without-header.ui-search-main--only-products.shops__search-main > section > ol > li:nth-child($num)")
//        if (adIterator.text.nonEmpty) {
//          try {
//            val innerPage: Document = Jsoup.connect(adIterator.select("div > div > div.ui-search-result__image.shops__picturesStyles > a")
//              .attr("href"))
//              .maxBodySize(0)
//              .timeout(50 * 1000)
//              .get
//
//            val masterJson: Value = ujson.read(innerPage.getElementsByAttributeValue("type", "application/ld+json").dataNodes.get(0).toString.replaceAll("(\\\\u002F)", ""))
//
//            val adId: Option[String] = masterJson("sku").strOpt
//
//            val mainCharacteristics: Map[String, Option[Any]] = Map[String, Option[Any]](
//              "name" -> masterJson("name").strOpt,
//              "image" -> masterJson("image").strOpt,
//              "price" -> masterJson("offers")("price").numOpt,
//              if (innerPage.select(".ui-pdp-buybox__quantity__available").text.nonEmpty) {
//                "availableQuantity" -> innerPage.select(".ui-pdp-buybox__quantity__available")
//                  .text.replace(" disponíveis)", "").replace("(", "").toIntOption
//              } else {
//                "availableQuantity" -> None
//              },
//              "adUrl" -> masterJson("offers")("url").strOpt,
//              "priceCurrency" -> masterJson("offers")("priceCurrency").strOpt,
//              "priceValidUntil" -> masterJson("offers")("priceValidUntil").strOpt,
//              "brand" -> masterJson("brand").strOpt,
//              if (masterJson.obj.contains("aggregateRating")) {
//                "aggregateRating" -> Some(Map[String, Option[Int]](
//                  "ratingValue" -> Some(masterJson("aggregateRating")("ratingValue").num.toInt),
//                  "reviewCount" -> Some(masterJson("aggregateRating")("reviewCount").num.toInt)))
//              } else {
//                "aggregateRating" -> Some(Map[String, Option[Int]](
//                  "ratingValue" -> None,
//                  "reviewCount" -> None
//                ))
//              }
//            )
//
//            val other: mutable.Map[String, String] = mutable.Map[String, String]()
//            if (innerPage.select(".ui-pdp-list__item:nth-child(1) .ui-pdp-list__text , .ui-pdp-list__item:nth-child(1) .ui-pdp-family--BOLD").text.nonEmpty &&
//              innerPage.select(".ui-pdp-list__item+ .ui-pdp-list__item .ui-pdp-list__text , .ui-pdp-list__item+ .ui-pdp-list__item .ui-pdp-family--BOLD").text.nonEmpty) {
//              other.addOne(
//                innerPage.select(".ui-pdp-list__item:nth-child(1) .ui-pdp-list__text , .ui-pdp-list__item:nth-child(1) .ui-pdp-family--BOLD").text.split(":")(0).trim ->
//                  innerPage.select(".ui-pdp-list__item:nth-child(1) .ui-pdp-list__text , .ui-pdp-list__item:nth-child(1) .ui-pdp-family--BOLD").text.split(":")(1)
//                    .replace(innerPage.select(".ui-pdp-list__item:nth-child(1) .ui-pdp-list__text , .ui-pdp-list__item:nth-child(1) .ui-pdp-family--BOLD").text.split(":")(0).trim, "")
//                    .trim
//              )
//              other.addOne(
//                innerPage.select(".ui-pdp-list__item+ .ui-pdp-list__item .ui-pdp-list__text , .ui-pdp-list__item+ .ui-pdp-list__item .ui-pdp-family--BOLD").text.split(":")(0) ->
//                  innerPage.select(".ui-pdp-list__item+ .ui-pdp-list__item .ui-pdp-list__text , .ui-pdp-list__item+ .ui-pdp-list__item .ui-pdp-family--BOLD").text.split(":")(1)
//                    .replace(innerPage.select(".ui-pdp-list__item+ .ui-pdp-list__item .ui-pdp-list__text , .ui-pdp-list__item+ .ui-pdp-list__item .ui-pdp-family--BOLD").text.split(":")(0), "")
//                    .trim
//              )
//            }
//
//            if (innerPage.select(".ui-pdp-list__item:nth-child(1) .ui-pdp-list__text , .ui-pdp-list__item:nth-child(1) .ui-pdp-family--BOLD").text.nonEmpty) {
//              other.addOne(
//                innerPage.select(".ui-pdp-list__item:nth-child(1) .ui-pdp-list__text , .ui-pdp-list__item:nth-child(1) .ui-pdp-family--BOLD").text.split(":")(0).trim ->
//                  innerPage.select(".ui-pdp-list__item:nth-child(1) .ui-pdp-list__text , .ui-pdp-list__item:nth-child(1) .ui-pdp-family--BOLD").text.split(":")(1)
//                    .replace(innerPage.select(".ui-pdp-list__item:nth-child(1) .ui-pdp-list__text , .ui-pdp-list__item:nth-child(1) .ui-pdp-family--BOLD").text.split(":")(0).trim, "")
//                    .trim
//              )
//            }
//
//            if (innerPage.select(".ui-pdp-list__item+ .ui-pdp-list__item .ui-pdp-list__text , .ui-pdp-list__item+ .ui-pdp-list__item .ui-pdp-family--BOLD").text.nonEmpty) {
//              other.addOne(
//                innerPage.select(".ui-pdp-list__item+ .ui-pdp-list__item .ui-pdp-list__text , .ui-pdp-list__item+ .ui-pdp-list__item .ui-pdp-family--BOLD").text.split(":")(0) ->
//                  innerPage.select(".ui-pdp-list__item+ .ui-pdp-list__item .ui-pdp-list__text , .ui-pdp-list__item+ .ui-pdp-list__item .ui-pdp-family--BOLD").text.split(":")(1)
//                    .replace(innerPage.select(".ui-pdp-list__item+ .ui-pdp-list__item .ui-pdp-list__text , .ui-pdp-list__item+ .ui-pdp-list__item .ui-pdp-family--BOLD").text.split(":")(0), "")
//                    .trim
//              )
//            }
//
//            val description: String = innerPage.select(".ui-pdp-description__content").text
//
//            val questionsAndAnswers: mutable.Map[String, List[Map[String, String]]] = mutable.Map[String, List[Map[String, String]]]()
//            if (innerPage.select(".ui-pdp-qadb__questions-list__wraper").eachText.size > 0) {
//              innerPage.select(".ui-pdp-qadb__questions-list__wraper")
//                .eachText
//                .forEach(questionAndAnswer =>
//                  questionsAndAnswers.addOne(questionAndAnswer.split("Denunciar")(0).trim
//                    -> List[Map[String, String]](Map[String, String]("Answer" -> questionAndAnswer.split("Denunciar")(1).trim
//                    .replace(questionAndAnswer.split("Denunciar")(1).trim.takeRight(10), ""),
//                    "Date of answer" -> questionAndAnswer.split("Denunciar")(1).trim.takeRight(10)))))
//            }
//
//            mongoScalaOps.insertMercadoLibreDataCrawler(olxAdId = collectionsIds(indexOfCollection),
//              mongoCast.castMercadoLibreCrawlerData(adId = adId, mainCharacteristics = mainCharacteristics,
//              other = other, description = description, questionAndAnswers = questionsAndAnswers))
//          } catch {
//            case e: HttpStatusException => println(s"Error 404 trying to fetch page: $e")
//          }
//        }
//      }
//    }
//  }
//}
