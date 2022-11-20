import COS.COS
import Cloudant.CloudantCRUD
import ParseTitle.Parser
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

import java.io.FileOutputStream
import java.net.URL
import scala.collection.JavaConverters._
import scala.collection.mutable

object WebScrapping extends App{

  val cloudantClient: CloudantCRUD = new CloudantCRUD
  val parser: Parser = new Parser

  val doc: Document = Jsoup.connect(s"https://am.olx.com.br/autos-e-pecas/carros-vans-e-utilitarios")
    .data("query", "Java")
    .userAgent("Mozilla")
    .timeout(10000)
    .get()

  val docMaxPage = doc.select("#listing-main-content-slot > div.h3us20-6.ehQTxA > div > div > div.h3us20-3.csYflq > div > div.sc-EHOje.kRemsz > p").text.split(" ")(3)

  for (pg: Int <- Range(1, docMaxPage.toInt+1, 1)) {
    val page: String = pg.toString
    val doc: Document = Jsoup.connect(s"https://am.olx.com.br/autos-e-pecas/carros-vans-e-utilitarios?o=$page")
      .data("name", "jsoup", "query", "Scala", "language", "Portuguese")
      .timeout(10000)
      .get()

    val carIterator: Elements = doc.select("#ad-list > li")

    val innerPage: Document = Jsoup.connect(carIterator.select("div > a")
      .attr("href")).get

    val innerImage: Elements = innerPage.select("#content > div.ad__sc-18p038x-2.djeeke " +
      "> div > div.sc-bwzfXH.ad__h3us20-0.ikHgMx > div.ad__duvuxf-0.ad__h3us20-0.eCUDNu > div.ad__h3us20-6.bgBcvm " +
      "> div > div > div > div.ad__sc-28oze1-9.kPuCIR > ul > li")

    val carsMap: mutable.Buffer[(String, mutable.Buffer[String], String, String, String, Option[Int], Option[Int],
      String, String, String, String, String, Option[Int], String, Option[Int], Option[Double], Option[Boolean],
      String, Option[Int], String, String)] = carIterator.asScala.map(_ =>
      (
        innerPage.select("#content > div.ad__sc-18p038x-2.djeeke > div > div.sc-bwzfXH.ad__h3us20-0.ikHgMx " +
          "> div.ad__duvuxf-0.ad__h3us20-0.eCUDNu > div.ad__h3us20-6.bgBcvm > div > div > div " +
          "> div.ad__sc-28oze1-1.fUJtNV > div > div.ad__sc-28oze1-3.hlDnNa > div:nth-child(1) > img")
          .attr("src"), // Thumbnail

        innerImage.asScala.map(image => image.select("div > img").attr("src")), // Map of Images

        parser.parser(innerPage.select(".mHjSV").text), // Title

        innerPage.select(".kgyRfr:nth-child(2) .iZCAvV").text, // Model

        innerPage.select(".kgyRfr~ .kgyRfr+ .kgyRfr .iZCAvV").text, // Brand

        innerPage.select(".ghWwwU").text.replaceAll("[R$. ]", "").toIntOption, // Price

        innerPage.select(".fcMYXB:nth-child(6) .cmFKIN").text.toIntOption, // Kilometers

        innerPage.select(".fMgwdS").text, // Description

        innerPage.select(".kgyRfr+ .fcMYXB .cmFKIN").text, // Type of car

        innerPage.select(".bbEhPV").text, // Location

        innerPage.select(".fcMYXB:nth-child(10) .cmFKIN").text, // Type of shift

        innerPage.select(".fcMYXB~ .fcMYXB+ .fcMYXB .iZCAvV").text, // Type of Fuel

        innerPage.select(".fcMYXB:nth-child(5) .iZCAvV").text.trim.toIntOption, // Year of fabrication

        innerPage.select(".fcMYXB:nth-child(12) .cmFKIN").text, // Color

        innerPage.select(".fcMYXB:nth-child(14) .cmFKIN").text.toIntOption, // End of plate

        innerPage.select(".fcMYXB:nth-child(7) .cmFKIN").text.toDoubleOption, // Motor power

        if (innerPage.select(".fcMYXB:nth-child(9) .cmFKIN")
          .text == "Sim") "true".toBooleanOption else "false".toBooleanOption, // HasGNV

        innerPage.select(".fcMYXB:nth-child(11) .cmFKIN").text, // Type of Direction

        innerPage.select(".fcMYXB:nth-child(13) .cmFKIN")
          .text.replaceAll("[A-Z.a-z ]", "").toIntOption, // Number of doors

        innerPage.select(".dAHSDM").text, // Optionals

        carIterator.select("div > a").attr("href")// URL
      )
    )

    for (objectCar <- carsMap) {
      if ( objectCar._1.nonEmpty & objectCar._1 != "https://static.olx.com.br/cd/listing/notFound.png"
        & objectCar._3.nonEmpty) {

        cloudantClient.create_document("automobiles", objectCar._3, objectCar._1, objectCar._2, objectCar._3,
          objectCar._6, objectCar._4, objectCar._5, objectCar._7, objectCar._8, objectCar._9, objectCar._10,
          objectCar._11, objectCar._12, objectCar._13, objectCar._14, objectCar._15, objectCar._16, objectCar._17,
          objectCar._18, objectCar._19, objectCar._20, objectCar._21)
      }
    }

  }

}
