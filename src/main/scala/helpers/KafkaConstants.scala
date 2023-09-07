package helpers

trait KafkaConstants {
  val KAFKA_BROKERS: String = "srv-data:9092"
  val MESSAGE_COUNT: Int = 1000
  val OLX_PRODUCER_CLIENT_ID: String = "OlxDataCrawlerProducer"
  val OLX_CONSUMER_CLIENT_ID: String = "OlxDataCrawlerConsumer"
  val OLX_GROUP_ID: String = "OlxDataCrawlerConsumer"
  val TOPIC_NAME: String = "olx"
  val GROUP_ID_CONFIG: String = "crawlersProducers"
  val MAX_NO_MESSAGE_FOUND_COUNT: Int = 100
  val OFFSET_RESET_LATEST: String = "latest"
  val OFFSET_RESET_EARLIER: String = "earliest"
  val MAX_POLL_RECORDS: Int = 1
  val AUTO_COMMIT_INTERVAL: Int = 10000
}
