package com.augenz.automobiles.consumers;

import com.augenz.automobiles.helpers.DataStaxCassandra;
import com.augenz.automobiles.schemas.OlxSchema;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;

public class OlxDataCrawlerCassandraConsumer extends OlxSchema implements GenericConsumer {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final Schema avroSchema = new Schema.Parser().parse(OLX_AD_SCHEMA_STRING);
    private final Consumer<String, byte[]> consumer = GenericConsumer.createConsumer();
    private final List<TopicPartition> partitions = new ArrayList<>();
    private final List<String> topics = new ArrayList<>();

    public OlxDataCrawlerCassandraConsumer() {
        topics.add("olx");
        int partition = 0;
        partitions.add(new TopicPartition(topics.get(0), partition));

        DataStaxCassandra cassandra = new DataStaxCassandra();
        cassandra.createSchema();
        getOlxAdMessagesFromBeginning();
    }

    private GenericRecord deserializeOlxAdMessage(byte[] message) throws IOException {
        DatumReader<GenericRecord> reader = new SpecificDatumReader<>(avroSchema);
        Decoder decoder = DecoderFactory.get().binaryDecoder(message, null);
        return reader.read(null, decoder);
    }

    private void outOlxAdMessage(byte[] message) throws IOException {
        DatumReader<GenericRecord> reader = new SpecificDatumReader<>(avroSchema);
        Decoder decoder = DecoderFactory.get().binaryDecoder(message, null);
        GenericRecord olxAdData = reader.read(null, decoder);

        System.out.println(olxAdData);
    }

    private void getOlxAdMessagesFromBeginning() {
        consumer.assign(partitions);

        try (consumer) {

            for (Long offset = consumer.beginningOffsets(partitions, Duration.ofSeconds(1)).get(partitions.get(0)); 
                    offset < consumer.endOffsets(partitions, Duration.ofSeconds(1)).get(partitions.get(0)); offset++) {
                consumer.seek(partitions.get(0), offset);
                for (ConsumerRecord<String, byte[]> record : consumer.poll(Duration.ofMillis(100))) {
                    outOlxAdMessage(record.value());
                }
            }
        } catch (IOException e) {
            logger.severe("We got a I/O error while trying to get all offsets!");
        }
    }

    private void getInstantOlxAdMessages() {
        consumer.subscribe(topics);

        try (consumer) {
            while (true) {
                for (ConsumerRecord<String, byte[]> record: consumer.poll(Duration.ofMillis(100))) {
                    deserializeOlxAdMessage(record.value());
                }
            }
        } catch (IOException e) {
            logger.severe("We got a I/O error while trying to get stream offsets!");
        }
    }
}
