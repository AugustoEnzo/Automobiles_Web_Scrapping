/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.augenz.automobiles.producers;

import com.augenz.automobiles.models.OlxAd;
import com.augenz.automobiles.schemas.OlxSchema;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

/**
 *
 * @author ozne
 */
public class OlxDataCrawlerProducer extends OlxSchema implements GenericProducer {
    private static final Logger logger = Logger.getLogger(OlxDataCrawlerProducer.class.getName());
    Producer<String, byte[]> producer = GenericProducer.createProducer();
    Schema olxAdSchema = GenericProducer.createSchema(OLX_AD_SCHEMA_STRING);
    SpecificDatumWriter<GenericRecord> writer = GenericProducer.createWriter(olxAdSchema);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    BinaryEncoder encoder = GenericProducer.createEncoder(output);


    public void send(String topic, OlxAd olxAd) {
        try {
            output.reset();

            GenericRecord genericOlxDataCrawlerRecord = GenericProducer.createRecord(olxAdSchema);
            genericOlxDataCrawlerRecord.put("adId", olxAd.adId);
            genericOlxDataCrawlerRecord.put("url", olxAd.url);
            genericOlxDataCrawlerRecord.put("listOfImages", olxAd.listOfImages);
            genericOlxDataCrawlerRecord.put("title", olxAd.title);
            genericOlxDataCrawlerRecord.put("model", olxAd.model);
            genericOlxDataCrawlerRecord.put("brand", olxAd.brand);
            genericOlxDataCrawlerRecord.put("price", olxAd.price);
            genericOlxDataCrawlerRecord.put("financialInfo", olxAd.financialInfo);
            genericOlxDataCrawlerRecord.put("kilometers", olxAd.kilometers);
            genericOlxDataCrawlerRecord.put("description", olxAd.description);
            genericOlxDataCrawlerRecord.put("typeOfCar", olxAd.typeOfCar);
            genericOlxDataCrawlerRecord.put("typeOfShift", olxAd.typeOfShift);
            genericOlxDataCrawlerRecord.put("typeOfFuel", olxAd.typeOfFuel);
            genericOlxDataCrawlerRecord.put("typeOfDirection", olxAd.typeOfDirection);
            genericOlxDataCrawlerRecord.put("yearOfFabrication", olxAd.yearOfFabrication);
            genericOlxDataCrawlerRecord.put("color", olxAd.color);
            genericOlxDataCrawlerRecord.put("endOfPlate", olxAd.endOfPlate);
            genericOlxDataCrawlerRecord.put("enginePower", olxAd.enginePower);
            genericOlxDataCrawlerRecord.put("hasGNV", olxAd.hasGNV);
            genericOlxDataCrawlerRecord.put("numberOfDoors", olxAd.numberOfDoors);
            genericOlxDataCrawlerRecord.put("characteristics", olxAd.characteristics);
            genericOlxDataCrawlerRecord.put("features", olxAd.features);
            genericOlxDataCrawlerRecord.put("locationInfo", olxAd.locationInfo);
            genericOlxDataCrawlerRecord.put("publishDate", olxAd.publishDate);
            genericOlxDataCrawlerRecord.put("profileInfo", olxAd.profileInfo);
            genericOlxDataCrawlerRecord.put("fundingInfo", olxAd.fundingInfo);
            genericOlxDataCrawlerRecord.put("tags", olxAd.tags);
            genericOlxDataCrawlerRecord.put("verificationInfo", olxAd.verificationInfo);
            genericOlxDataCrawlerRecord.put("fipePrice", olxAd.fipePrice);
            genericOlxDataCrawlerRecord.put("fipePriceRef", olxAd.fipePriceRef);
            genericOlxDataCrawlerRecord.put("differenceToFipePrice", olxAd.differenceToFipePrice);
            genericOlxDataCrawlerRecord.put("vehicleSpecificData", olxAd.vehicleSpecificData);

            writer.write(genericOlxDataCrawlerRecord, encoder);
            encoder.flush();

            byte[] serializeBytes = output.toByteArray();
            ProducerRecord<String, byte[]> message = new ProducerRecord<>(topic, olxAd.adId, serializeBytes);
            producer.send(message);

        } catch (SecurityException | IllegalArgumentException | IOException e) {
            logger.severe(e.toString());
        }
    }

    public void close() throws IOException {
        output.close();
    }
}
