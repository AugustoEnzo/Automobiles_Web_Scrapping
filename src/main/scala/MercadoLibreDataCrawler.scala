import MongoSync.Ops
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.jsoup.{HttpStatusException, Jsoup}
import ujson.Value.Value

import scala.collection.mutable


object MercadoLibreDataCrawler extends App {
  private val mongoOps: Ops = new Ops()

  private val requestArgsArray: mutable.ArrayBuffer[List[String]] = mongoOps.readAndGetCollectionInformation()

  for (requestArgs <- requestArgsArray) {
    val mainPageDocument: Document = Jsoup.connect(s"https://lista.mercadolivre.com.br/acessorios-veiculos/pecas-carros-caminhonetes/${requestArgs.head.replace(" ", "-").toLowerCase()}-${requestArgs(1)}")
      .maxBodySize(0)
      .timeout(20000)
      .get

//    val maxPage: Elements = mainPageDocument.select("#root-app > div > div.ui-search-main.ui-search-main--exhibitor.ui-search-main--without-header.ui-search-main--only-products.shops__search-main > section > div.ui-search-pagination.shops__pagination-content > ul > li.andes-pagination__page-count")

    for (num: Int <- Range(1, 50+1, 1) ) {
      val adIterator: Elements = mainPageDocument.select(s"#root-app > div > div.ui-search-main.ui-search-main--exhibitor.ui-search-main--without-header.ui-search-main--only-products.shops__search-main > section > ol > li:nth-child($num)")

      println(adIterator)

      if (adIterator.text.nonEmpty) {
        try {
          val innerPage: Document = Jsoup.connect(adIterator.select("div > div > div.ui-search-result__image.shops__picturesStyles > a")
            .attr("href"))
            .maxBodySize(0)
            .timeout(50 * 1000)
            .get()

          println(innerPage)

          val masterJson: Value = ujson.read(innerPage.select("head > script:nth-child(41)").attr("data-json").replace("\u002F", ""))

          println(masterJson)

          val mainCharacteristicsMap: mutable.Map[String, String] = mutable.Map[String, String]()

          for (item <- Range(1,3+1, 1)) {

            if (item == 1) {
               mainCharacteristicsMap.addOne("Brand" -> innerPage.select(s"#technical_specifications > div > div.ui-pdp-specs__table > table > tbody > tr:nth-child($item)").text)
            }

            if (item == 2) {
              mainCharacteristicsMap.addOne("Piece ID" -> innerPage.select(s"#technical_specifications > div > div.ui-pdp-specs__table > table > tbody > tr:nth-child($item)").text)
            }

            if (item == 3) {
              mainCharacteristicsMap.addOne("Model" -> innerPage.select(s"#technical_specifications > div > div.ui-pdp-specs__table > table > tbody > tr:nth-child($item)").text)
            }
            println(mainCharacteristicsMap)


          }
        }
      }
    }
  }
}
