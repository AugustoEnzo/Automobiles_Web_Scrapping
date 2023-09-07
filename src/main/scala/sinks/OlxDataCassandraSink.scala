package sinks

import consumers.OlxDataCrawlerConsumer

object OlxDataCassandraSink {
  def main(args: Array[String]): Unit = {
    val consumer: OlxDataCrawlerConsumer = OlxDataCrawlerConsumer()
  }
}
