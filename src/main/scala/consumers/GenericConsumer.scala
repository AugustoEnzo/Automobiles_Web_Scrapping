package consumers
import java.util.Properties
import org.apache.avro.Schema
import org.apache.avro.io.DatumReader
import org.apache.avro.io.Decoder
import org.apache.avro.specific.SpecificDatumReader
import org.apache.avro.generic.GenericRecord
import org.apache.avro.io.DecoderFactory
import helpers.KafkaConstants
import org.apache.kafka.clients.consumer
import org.apache.kafka.clients.consumer.{Consumer, ConsumerConfig, ConsumerRecord, ConsumerRecords, KafkaConsumer}
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.errors.TimeoutException
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}

trait GenericConsumer extends KafkaConstants {
  def createConsumer: Consumer[String, Array[Byte]] = {
    val props = new Properties()
    props.put(ConsumerConfig.GROUP_ID_CONFIG, OLX_GROUP_ID)
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKERS)
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer().getClass.getName)
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer().getClass.getName)
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, OFFSET_RESET_EARLIER)
    props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, AUTO_COMMIT_INTERVAL)

    new KafkaConsumer[String, Array[Byte]](props)
  }
}
