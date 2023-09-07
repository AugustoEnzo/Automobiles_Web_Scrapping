package producers

import com.typesafe.scalalogging.Logger
import models.OlxAdJava
import org.apache.avro.Schema
import org.apache.avro.generic.GenericRecord
import org.apache.avro.io.BinaryEncoder
import org.apache.avro.specific.SpecificDatumWriter
import org.apache.kafka.clients.producer.{Producer, ProducerRecord}
import schemas.OlxSchema

import java.io.ByteArrayOutputStream
import scala.jdk.CollectionConverters._


class OlxDataCrawlerProducer extends OlxSchema, GenericProducer {
  private val logger: Logger = Logger(getClass.getName)
  private val producer: Producer[String, Array[Byte]] = createProducer
  private val olxAdSchema: Schema = createSchema(OLX_AD_SCHEMA_STRING)
  private val writer: SpecificDatumWriter[GenericRecord] = createWriter(olxAdSchema)
  private val output: ByteArrayOutputStream = new ByteArrayOutputStream()
  private val encoder: BinaryEncoder = createEncoder(output)


  def send(topic: String, olxAd: OlxAdJava): Unit = {
    try {
      output.reset()

      val genericOlxDataCrawlerRecord: GenericRecord = createRecord(olxAdSchema)
      genericOlxDataCrawlerRecord.put("adId", olxAd.adId)
      genericOlxDataCrawlerRecord.put("url", olxAd.url)
      genericOlxDataCrawlerRecord.put("listOfImages", olxAd.listOfImages)
      genericOlxDataCrawlerRecord.put("title", olxAd.title)
      genericOlxDataCrawlerRecord.put("model", olxAd.model)
      genericOlxDataCrawlerRecord.put("brand", olxAd.brand)
      genericOlxDataCrawlerRecord.put("price", olxAd.price)
      genericOlxDataCrawlerRecord.put("financialInformation", olxAd.financialInformation)
      genericOlxDataCrawlerRecord.put("kilometers", olxAd.kilometers)
      genericOlxDataCrawlerRecord.put("description", olxAd.description)
      genericOlxDataCrawlerRecord.put("typeOfCar", olxAd.typeOfCar)
      genericOlxDataCrawlerRecord.put("typeOfShift", olxAd.typeOfShift)
      genericOlxDataCrawlerRecord.put("typeOfFuel", olxAd.typeOfFuel)
      genericOlxDataCrawlerRecord.put("typeOfDirection", olxAd.typeOfDirection)
      genericOlxDataCrawlerRecord.put("yearOfFabrication", olxAd.yearOfFabrication)
      genericOlxDataCrawlerRecord.put("color", olxAd.color)
      genericOlxDataCrawlerRecord.put("endOfPlate", olxAd.endOfPlate)
      genericOlxDataCrawlerRecord.put("enginePower", olxAd.enginePower)
      genericOlxDataCrawlerRecord.put("hasGNV", olxAd.hasGNV)
      genericOlxDataCrawlerRecord.put("numberOfDoors", olxAd.numberOfDoors)
      genericOlxDataCrawlerRecord.put("characteristics", olxAd.characteristics)
      genericOlxDataCrawlerRecord.put("optionals", olxAd.optionals)
      genericOlxDataCrawlerRecord.put("locationInformation", olxAd.locationInformation)
      genericOlxDataCrawlerRecord.put("publishDate", olxAd.publishDate)
      genericOlxDataCrawlerRecord.put("profileInformation", olxAd.profileInformation)
      genericOlxDataCrawlerRecord.put("fundingInformation", olxAd.fundingInformation)
      genericOlxDataCrawlerRecord.put("tagsList", olxAd.tagsList)
      genericOlxDataCrawlerRecord.put("verificationInformation", olxAd.verificationInformation)
      genericOlxDataCrawlerRecord.put("averageOlxPrice", olxAd.averageOlxPrice)
      genericOlxDataCrawlerRecord.put("fipePrice", olxAd.fipePrice)
      genericOlxDataCrawlerRecord.put("fipePriceRef", olxAd.fipePriceRef)
      genericOlxDataCrawlerRecord.put("differenceToOlxAverage", olxAd.differenceToOlxAveragePrice)
      genericOlxDataCrawlerRecord.put("differenceToFipePrice", olxAd.differenceToFipePrice)
      genericOlxDataCrawlerRecord.put("vehicleSpecificData", olxAd.vehicleSpecificData)

      writer.write(genericOlxDataCrawlerRecord, encoder)
      encoder.flush()

      val serializedBytes: Array[Byte] = output.toByteArray
      val message: ProducerRecord[String, Array[Byte]] = ProducerRecord[String, Array[Byte]](topic, olxAd.adId, serializedBytes)
      producer.send(message)
    } catch {
      case error: Exception => logger.error(error.printStackTrace().toString)
        error.printStackTrace()
    }
  }

  def close(): Unit = {
    output.close()
  }
}
