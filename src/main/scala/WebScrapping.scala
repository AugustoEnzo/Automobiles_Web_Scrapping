import COS.COS
import Cloudant.CloudantCRUD
import ParseTitle.Parser
import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}
import org.jsoup.select.Elements

import scala.collection.JavaConverters._
import scala.collection.mutable

object WebScrapping extends App {

  val cloudantClient: CloudantCRUD = new CloudantCRUD
  val parser: Parser = new Parser

  val doc: Document = Jsoup.connect(s"https://am.olx.com.br/autos-e-pecas/carros-vans-e-utilitarios")
    .data("query", "Java")
    .userAgent("Mozilla")
    .timeout(10000)
    .get()

  val docMaxPage = doc.select("#listing-main-content-slot > div.h3us20-6.ehQTxA > div > div > div.h3us20-3.csYflq > div > div.sc-EHOje.kRemsz > p").text.split(" ")(3)

  for (pg: Int <- Range(1, docMaxPage.toInt + 1, 1)) {
    val page: String = pg.toString
    val doc: Document = Jsoup.connect(s"https://am.olx.com.br/autos-e-pecas/carros-vans-e-utilitarios?o=$page")
      .timeout(10000)
      .get()

    for (num: Int <- Range(1, 55+1, 1)) {
      val carIterator: Elements = doc.select(s"#ad-list > li:nth-child($num)")

      if (carIterator.text != "") {
        val innerPage: Document = Jsoup.connect(carIterator.select("div > a")
          .attr("href")).get

        val innerImage: Elements = innerPage.select("#content > div.ad__sc-18p038x-2.djeeke " +
          "> div > div.sc-bwzfXH.ad__h3us20-0.ikHgMx > div.ad__duvuxf-0.ad__h3us20-0.eCUDNu > div.ad__h3us20-6.bgBcvm " +
          "> div > div > div > div.ad__sc-28oze1-9.kPuCIR > ul > li")

        val thumbnail: String = innerPage.select("#content > div.ad__sc-18p038x-2.djeeke > div > div.sc-bwzfXH.ad__h3us20-0.ikHgMx " +
          "> div.ad__duvuxf-0.ad__h3us20-0.eCUDNu > div.ad__h3us20-6.bgBcvm > div > div > div " +
          "> div.ad__sc-28oze1-1.fUJtNV > div > div.ad__sc-28oze1-3.hlDnNa > div:nth-child(1) > img")
          .attr("src") // Thumbnail

        val mapOfImages: mutable.Buffer[String] = innerImage.asScala.map(image => image.select("div > img").attr("src")) // Map of Images

        val title: String = parser.parser(innerPage.select(".mHjSV").text) // Title

        val model: String = innerPage.select(".kgyRfr:nth-child(2) .iZCAvV").text // Model

        val brand: String = innerPage.select(".kgyRfr~ .kgyRfr+ .kgyRfr .iZCAvV").text // Brand

        val price: Option[Int] = innerPage.select(".ghWwwU").text.replaceAll("[R$. ]", "").toIntOption // Price

        val kilometers: Option[Int] = innerPage.select(".fcMYXB:nth-child(6) .cmFKIN").text.toIntOption // Kilometers

        val description: String = innerPage.select(".fMgwdS").text // Description

        val typeOfCar: String = innerPage.select(".kgyRfr+ .fcMYXB .cmFKIN").text // Type of car

        val location: String = innerPage.select(".bbEhPV").text // Location

        val typeOfShift: String = innerPage.select(".fcMYXB:nth-child(10) .cmFKIN").text // Type of shift

        val typeOfFuel: String = innerPage.select(".fcMYXB~ .fcMYXB+ .fcMYXB .iZCAvV").text // Type of Fuel

        val yearOfFabrication: Option[Int] = innerPage.select(".fcMYXB:nth-child(5) .iZCAvV").text.trim.toIntOption // Year of fabrication

        val color: String = innerPage.select(".fcMYXB:nth-child(12) .cmFKIN").text // Color

        val endOfPlate: Option[Int] = innerPage.select(".fcMYXB:nth-child(14) .cmFKIN").text.toIntOption // End of plate

        val motorPower: Option[Double] = innerPage.select(".fcMYXB:nth-child(7) .cmFKIN").text.toDoubleOption // Motor power

        val hasGNV: Option[Boolean] = if (innerPage.select(".fcMYXB:nth-child(9) .cmFKIN")
          .text == "Sim") "true".toBooleanOption else "false".toBooleanOption // HasGNV

        val typeOfDirection: String = innerPage.select(".fcMYXB:nth-child(11) .cmFKIN").text // Type of Direction

        val numberOfDoors: Option[Int] = innerPage.select(".fcMYXB:nth-child(13) .cmFKIN")
          .text.replaceAll("[A-Z.a-z ]", "").toIntOption // Number of doors

        val optionals: String = innerPage.select(".dAHSDM").text // Optionals

        val url: String = carIterator.select("div > a").attr("href") // URL

        val id: String = innerPage.select(".bTSFxO").text.replace("cód. ", "") // ID

        val publishData: String = innerPage.select(".hSZkck").text
          .replace("Publicado em ", "").replace(" às ", ":") // Publish Date

        if (thumbnail.nonEmpty & thumbnail != "https://static.olx.com.br/cd/listing/notFound.png"
          & id.nonEmpty) {


        if (!cloudantClient.documentOnDatabase("automobiles", id)) {
          cloudantClient.create_document("automobiles", id, thumbnail, mapOfImages, title,
            price, model, brand, kilometers, description, typeOfCar, location,
            typeOfShift, typeOfFuel, yearOfFabrication, color, endOfPlate, motorPower, hasGNV,
            typeOfDirection, numberOfDoors, optionals, url)
          }
        }
      }
    }
  }