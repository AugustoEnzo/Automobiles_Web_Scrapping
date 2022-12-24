import Mongo.{CRUD, Cast}
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.jsoup.{HttpStatusException, Jsoup}
import ujson.Value.Value

import scala.collection.mutable


object MercadoLibreDataCrawler extends App{
  private val mongoOps: CRUD = new CRUD()

  private val doc: Document = Jsoup.connect(s"https://lista.mercadolivre.com.br/${}").get
}
