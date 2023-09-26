/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.augenz.automobiles.schemas;

import java.util.ArrayList;
import java.util.logging.Logger;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;

/**
 *
 * @author ozne
 */
public class OlxSchema {
    private static final Logger logger = Logger.getLogger(OlxSchema.class.getName());
    private final Schema characteristicsMapSchema = SchemaBuilder.map().values().stringType();
    private final Schema locationInfoMapSchema = SchemaBuilder.map().values().stringType();
    private final Schema profileInfoMapSchema = SchemaBuilder.map().values().stringType();
    private final Schema fundingInfoMapSchema = SchemaBuilder.map().values().stringType();
    private final Schema verificationInfoMapSchema = SchemaBuilder.map().values().stringType();
    private final Schema fipePriceRefMapSchema = SchemaBuilder.map().values().stringType();
    private final Schema vehicleSpecificDataMapSchema = SchemaBuilder.map().values().stringType();
    public String OLX_AD_SCHEMA_STRING = SchemaBuilder.record("olxAdData")
    .namespace("kafka-avro.olxSchema")
    .fields()
    .requiredString("adId")
    .requiredString("url")
    .name("listOfImages")
      .type()
      .array()
      .items()
      .stringType()
      .arrayDefault(new ArrayList<>())
    .optionalString("title")
    .optionalString("model")
    .optionalString("brand")
    .optionalString("price")
    .name("financialInfo")
      .type()
      .array()
      .items()
      .stringType()
      .arrayDefault(new ArrayList<>())
    .optionalString("kilometers")
    .optionalString("description")
    .optionalString("typeOfCar")
    .optionalString("typeOfShift")
    .optionalString("typeOfFuel")
    .optionalString("typeOfDirection")
    .optionalString("yearOfFabrication")
    .optionalString("color")
    .optionalString("endOfPlate")
    .optionalString("enginePower")
    .optionalString("hasGNV")
    .optionalString("numberOfDoors")
    .name("characteristics")
      .type(characteristicsMapSchema)
      .noDefault()
    .name("features")
      .type()
      .array()
      .items()
      .stringType()
      .arrayDefault(new ArrayList<>())
    .name("locationInfo")
      .type(locationInfoMapSchema)
      .noDefault()
    .optionalString("publishDate")
    .name("profileInfo")
      .type(profileInfoMapSchema)
      .noDefault()
    .name("fundingInfo")
      .type(fundingInfoMapSchema)
      .noDefault()
    .name("tags")
      .type()
      .array()
      .items()
      .stringType()
      .arrayDefault(new ArrayList<>())
    .name("verificationInfo")
      .type(verificationInfoMapSchema)
      .noDefault()
    .optionalString("fipePrice")
    .name("fipePriceRef")
      .type(fipePriceRefMapSchema)
      .noDefault()
    .optionalString("differenceToFipePrice")
    .name("vehicleSpecificData")
      .type(vehicleSpecificDataMapSchema)
      .noDefault().endRecord().toString();
}
