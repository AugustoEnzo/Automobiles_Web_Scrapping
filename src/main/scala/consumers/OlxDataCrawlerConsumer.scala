package consumers

import com.typesafe.scalalogging.Logger
import helpers.DataStaxCassandra
import org.apache.avro.Schema
import org.apache.avro.generic.GenericRecord
import org.apache.avro.io.{DatumReader, Decoder, DecoderFactory}
import org.apache.avro.specific.SpecificDatumReader
import schemas.OlxSchema
import org.apache.kafka.clients.consumer.{Consumer, ConsumerRecord, ConsumerRecords, OffsetAndMetadata}
import org.apache.kafka.common.TopicPartition

//import helpers.AstyanaxCassandra
import org.apache.avro.util.Utf8

import java.time.Duration
import java.{lang, util}

class OlxDataCrawlerConsumer extends GenericConsumer, OlxSchema {
  private val logger: Logger = Logger("OlxDataCrawlerConsume")
  private val avroSchema: Schema = new Schema.Parser().parse(OLX_AD_SCHEMA_STRING)
  private val consumer: Consumer[String, Array[Byte]] = createConsumer
  private val partitions: util.List[TopicPartition] = util.ArrayList[TopicPartition]()
  private var totalCount: Long = 0L
  private val partition: lang.Integer = 0
  private val topics: util.List[String] = util.ArrayList[String]()
//  private val cassandra: AstyanaxCassandra = AstyanaxCassandra()
  private val cassandra: DataStaxCassandra = DataStaxCassandra()
  topics.add("olx")
  partitions.add(TopicPartition(topics.get(0), partition))

  private def deserializeOlxAdMessage(message: Array[Byte]): GenericRecord = {
    // Deserialize to generic record
    val reader: DatumReader[GenericRecord] = new SpecificDatumReader[GenericRecord](avroSchema)
    val decoder: Decoder = DecoderFactory.get().binaryDecoder(message, null)
    val olxAdData: GenericRecord = reader.read(null, decoder)

    // Build OlxAdJava object
    olxAdData
  }

  private def printOlxAdMessage(message: Array[Byte]): Unit = {
    // Deserialize to generic record
    val reader: DatumReader[GenericRecord] = new SpecificDatumReader[GenericRecord](avroSchema)
    val decoder: Decoder = DecoderFactory.get().binaryDecoder(message, null)
    val olxAdData: GenericRecord = reader.read(null, decoder)

    // Build OlxAdJava object
    println(olxAdData)
  }

  private def getOlxDataMessagesFromBeginning(): Unit = {
    consumer.assign(partitions)
//    consumer.subscribe(topics)

    try {
      val earliestOffset: util.Map[TopicPartition, lang.Long] = consumer.beginningOffsets(partitions, Duration.ofSeconds(1))
      val lastOffset: util.Map[TopicPartition, lang.Long] = consumer.endOffsets(partitions, Duration.ofSeconds(1))
      var offset = earliestOffset.get(partitions.get(0))
      while (offset < lastOffset.get(partitions.get(0))) {
        consumer.seek(partitions.get(0), offset)
        val records: ConsumerRecords[String, Array[Byte]] = consumer.poll(Duration.ofMillis(100))
        records.forEach(record =>
          printOlxAdMessage(record.value())
        )
        offset += 1
      }
    } catch {
      case error: Error => logger.error(f"$error")
    } finally {
      consumer.close()
    }
  }

  protected def getInstantOlxAdMessages(): Unit = {
    // Subscribe to olx topic
    consumer.subscribe(topics)

    try {
      while (totalCount < 1000) {
        val records: ConsumerRecords[String, Array[Byte]] = consumer.poll(Duration.ofMillis(100))
        records.forEach(record => deserializeOlxAdMessage(record.value))
        totalCount += 1
      }
    } catch {
      case error: Error => logger.error(f"$error")
    } finally {
      consumer.close()
    }
  }
  
  cassandra.createSchema()
  getOlxDataMessagesFromBeginning()
}
