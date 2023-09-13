package com.augenz.automobiles.helpers;

import com.datastax.oss.driver.api.core.AllNodesFailedException;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.servererrors.QueryExecutionException;
import com.datastax.oss.driver.api.core.servererrors.QueryValidationException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.avro.generic.GenericRecord;

public class DataStaxCassandra implements DataStaxCassandraGenerics {
    private static final Logger logger = Logger.getLogger(DataStaxCassandra.class.getName());
    private final CqlSession session = DataStaxCassandraGenerics.createSession();

    public void createSchema() {
        try {
            session.execute("CREATE KEYSPACE IF NOT EXISTS automobiles WITH replication "
                    + "= {'class': 'SimpleStrategy', 'replication_factor': 1};");

        logger.info("The query to create keyspace have finished successfully.");
        } catch (AllNodesFailedException allNodesFailedException) {
            logger.severe("All Nodes have failed.");
        } catch (QueryExecutionException queryExecutionException) {
            logger.log(Level.SEVERE, "The query to create keyspace have returned an exception: {0}", queryExecutionException);
        } catch (QueryValidationException queryValidationException) {
            logger.log(Level.SEVERE, "The query to create key space couldn''t been validated: {0}", queryValidationException);
        }

        try {
            session.execute("CREATE TABLE IF NOT EXISTS automobiles.olxAds ("
                + "adId uuid PRIMARY KEY,"
                + "url text,"
                + "listOfImages set<text>,"
                + "title text,"
                + "model text,"
                + "brand text,"
                + "price text,"
                + "financial set<text>,"
                + "kilometers text,"
                + "description text,"
                + "type_of_car text,"
                + "type_of_shift text,"
                + "type_of_fuel text,"
                + "type_of_direction text,"
                + "year_of_fabrication text,"
                + "color text,"
                + "end_of_plate text,"
                + "engine_power text,"
                + "has_gnv text,"
                + "number_of_doors text,"
                + "characteristics map<text, text>,"
                + "optionals set<text>,"
                + "location map<text, text>,"
                + "publish_date text,"
                + "profile map<text, text>,"
                + "funding map<text, text>,"
                + "tags set<text>,"
                + "verification map<text, text>,"
                + "fipe_price text,"
                + "fipe_price_ref map<text, text>,"
                + "difference_to_fipe_price text,"
                + "vehicle_specific_data map<text, text>"
                + ");"
            );

            logger.info("The query to create automobiles.olxAds table have finished successfully.");

        } catch (AllNodesFailedException allNodesFailedException) {
            logger.severe("All Nodes have failed.");
        } catch (QueryExecutionException queryExecutionException) {
            logger.log(Level.SEVERE, "The query to create keyspace have returned an exception: {0}", queryExecutionException);
        } catch (QueryValidationException queryValidationException) {
            logger.log(Level.SEVERE, "The query to create key space couldn''t been validated: {0}", queryValidationException);
        }
    }

    void loadData(GenericRecord olxAdData) {
        session.execute("INSERT INTO automobiles.olxAds ("
          + "adId,"
          + "url,"
          + "listOfImages,"
          + "title,"
          + "model,"
          + "brand,"
          + "price,"
          + "financial,"
          + "kilometers,"
          + "description,"
          + "type_of_car,"
          + "type_of_shift,"
          + "type_of_fuel,"
          + "type_of_direction,"
          + "year_of_fabrication,"
          + "color,"
          + "end_of_plate,"
          + "engine_power,"
          + "has_gnv,"
          + "number_of_doors,"
          + "characteristics,"
          + "optionals,"
          + "location,"
          + "publish_date,"
          + "profile,"
          + "funding,"
          + "tags,"
          + "verification,"
          + "fipe_price,"
          + "fipe_price_ref,"
          + "difference_to_fipe_price,"
          + "vehicle_specific_data"
          + ") VALUES ("
          + olxAdData.get("adId") + ","
          + olxAdData.get("url") + ","
          + olxAdData.get("listOfImages").toString() + ","
          + olxAdData.get("title") + ","
          + olxAdData.get("model") + ","
          + olxAdData.get("brand") + ","
          + olxAdData.get("price") + ","
          + olxAdData.get("financialInformation").toString() + ","
          + olxAdData.get("kilometers") + ","
          + olxAdData.get("description") + ","
          + olxAdData.get("typeOfCar") + ","
          + olxAdData.get("typeOfShift") + ","
          + olxAdData.get("typeOfFuel") + ","
          + olxAdData.get("typeOfDirection") + ","
          + olxAdData.get("yearOfFabrication") + ","
          + olxAdData.get("color") + ","
          + olxAdData.get("endOfPlate") + ","
          + olxAdData.get("enginePower") + ","
          + olxAdData.get("hasGNV") + ","
          + olxAdData.get("numberOfDoors") + ","
          + olxAdData.get("characteristics") + ","
          + olxAdData.get("features") + ","
          + olxAdData.get("locationInfo") + ","
          + olxAdData.get("publishDate") + ","
          + olxAdData.get("profileInfo") + ","
          + olxAdData.get("fundingInfo") + ","
          + olxAdData.get("tags") + ","
          + olxAdData.get("verificationInfo") + ","
          + olxAdData.get("fipePrice") + ","
          + olxAdData.get("fipePriceRef") + ","
          + olxAdData.get("differenceToFipePrice") + ","
          + olxAdData.get("vehicleSpecificData") + ";"
        );
    }
}
