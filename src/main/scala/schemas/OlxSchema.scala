package schemas

import org.apache.avro.{Schema, SchemaBuilder}

import com.typesafe.scalalogging.Logger
import scala.jdk.CollectionConverters.*

trait OlxSchema {
  private val logger: Logger = Logger("AvroOlxSchema")

  private val characteristicsMapSchema: Schema = SchemaBuilder.map.values.stringType

  private val locationInformationMapSchema: Schema = SchemaBuilder.map.values.stringType

  private lazy val profileInformationMapSchema: Schema = SchemaBuilder.map.values.stringType

  private val fundingInformationMapSchema: Schema = SchemaBuilder.map.values.stringType

  private val verificationInformationMapSchema: Schema = SchemaBuilder.map.values.stringType

  private val fipePriceRefMapSchema: Schema = SchemaBuilder.map.values.stringType
  
  private val vehicleSpecificDataMapSchema: Schema = SchemaBuilder.map.values.stringType

  val OLX_AD_SCHEMA_STRING: String = SchemaBuilder.record("olxAdData")
    .namespace("kafka-avro.olxSchema")
    .fields()
    .requiredString("adId")
    .requiredString("url")
    .name("listOfImages")
      .`type`()
      .array()
      .items()
      .stringType()
      .arrayDefault(List.empty.asJava)
    .requiredString("title")
    .requiredString("model")
    .requiredString("brand")
    .requiredString("price")
    .name("financialInformation")
      .`type`()
      .array()
      .items()
      .stringType()
      .arrayDefault(List.empty.asJava)
    .requiredString("kilometers")
    .requiredString("description")
    .requiredString("typeOfCar")
    .requiredString("typeOfShift")
    .requiredString("typeOfFuel")
    .requiredString("typeOfDirection")
    .requiredString("yearOfFabrication")
    .requiredString("color")
    .requiredString("endOfPlate")
    .requiredString("enginePower")
    .requiredString("hasGNV")
    .requiredString("numberOfDoors")
    .name("characteristics")
      .`type`(characteristicsMapSchema)
      .noDefault()
    .name("optionals")
      .`type`()
      .array()
      .items()
      .stringType()
      .arrayDefault(List.empty.asJava)
    .name("locationInformation")
      .`type`(locationInformationMapSchema)
      .noDefault()
    .requiredString("publishDate")
    .name("profileInformation")
      .`type`(profileInformationMapSchema)
      .noDefault()
    .name("fundingInformation")
      .`type`(fundingInformationMapSchema)
      .noDefault()
    .name("tagsList")
      .`type`()
      .array()
      .items()
      .stringType()
      .arrayDefault(List.empty.asJava)
    .name("verificationInformation")
      .`type`(verificationInformationMapSchema)
      .noDefault()
    .optionalString("averageOlxPrice")
    .optionalString("fipePrice")
    .name("fipePriceRef")
      .`type`(fipePriceRefMapSchema)
      .noDefault()
    .optionalString("differenceToOlxAverage")
    .optionalString("differenceToFipePrice")
    .name("vehicleSpecificData")
      .`type`(vehicleSpecificDataMapSchema)
      .noDefault()
    .endRecord().toString()
}
