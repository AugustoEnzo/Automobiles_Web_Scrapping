package producers

import org.apache.avro.Schema
import org.apache.avro.generic.{GenericData, GenericRecord}
import org.apache.avro.io.{BinaryEncoder, EncoderFactory}
import org.apache.avro.specific.SpecificDatumWriter
import org.apache.kafka.clients.producer.{KafkaProducer, Producer, ProducerConfig}
import helpers.KafkaConstants
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}
import java.io.ByteArrayOutputStream
import java.util.{Properties, UUID}

trait GenericProducer extends KafkaConstants {
  def createProducer: Producer[String, Array[Byte]] = {
    val props: Properties = new Properties()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKERS)
    props.put(ProducerConfig.CLIENT_ID_CONFIG, OLX_PRODUCER_CLIENT_ID)
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer().getClass.getName)
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer().getClass.getName)
    new KafkaProducer(props)
  }

  def createSchema(schemaString: String): Schema = {
    new Schema.Parser().parse(schemaString)
  }

  def createRecord(schema: Schema): GenericRecord = {
    new GenericData.Record(schema)
  }

  def createWriter(schema: Schema): SpecificDatumWriter[GenericRecord] = {
    new SpecificDatumWriter[GenericRecord](schema)
  }

  def createEncoder(output: ByteArrayOutputStream): BinaryEncoder = {
    EncoderFactory.get().binaryEncoder(output, null)
  }
}
