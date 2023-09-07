/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.augenz.automobiles.producers;

import com.augenz.automobiles.helpers.KafkaConstants;
import java.io.ByteArrayOutputStream;
import java.util.Properties;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;


/**
 *
 * @author ozne
 */
public interface GenericProducer extends KafkaConstants {

    /**
     *
     * @return 
     */
    static Producer<String, byte[]> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKERS);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, OLX_PRODUCER_CLIENT_ID);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
        return new KafkaProducer(props);
    }
    
    static Schema createSchema(String schemaString) {
        return new Schema.Parser().parse(schemaString);
    }
    
    static GenericRecord createRecord(Schema schema) {
        return new GenericData.Record(schema);
    }
    
    static SpecificDatumWriter<GenericRecord> createWriter(Schema schema) {
        return new SpecificDatumWriter<>(schema);
    }
    
    static BinaryEncoder createEncoder(ByteArrayOutputStream output) {
        return EncoderFactory.get().binaryEncoder(output, null);
    }
}
