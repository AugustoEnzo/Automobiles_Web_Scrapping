package helpers

import com.datastax.oss.driver.api.core.{AllNodesFailedException, CqlSession}
import com.datastax.oss.driver.api.core.servererrors.{QueryExecutionException, QueryValidationException}
import com.typesafe.scalalogging.Logger
import org.apache.avro.generic.GenericRecord
import org.apache.avro.util.Utf8

import java.util

class DataStaxCassandra extends DataStaxCassandraGenerics {
  private val logger: Logger = Logger(this.getClass)
  private val session: CqlSession = createSession()

  def createSchema(): Unit = {
    try {
      session.execute("CREATE KEYSPACE IF NOT EXISTS automobiles WITH replication "
        + "= {'class': 'SimpleStrategy', 'replication_factor': 1};")

      logger.info("The query to create keyspace have finished successfully.")
    } catch {
      case allNodesFailed: AllNodesFailedException => logger.error(f"All Nodes have failed: \n$allNodesFailed")
      case queryExecutionException: QueryExecutionException => logger.error(f"The query to create keyspace have returned an exception: \n$queryExecutionException")
      case queryValidationException: QueryValidationException => logger.error(f"The query to create key space couldn't been validated: \n$queryValidationException")
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
        + "average_olx_price text,"
        + "fipe_price text,"
        + "fipe_price_ref map<text, text>,"
        + "difference_to_olx_average text,"
        + "difference_to_fipe_price text,"
        + "vehicle_specific_data map<text, text>"
        + ");"
      )

      logger.info("The query to create automobiles.olxAds table have finished successfully.")
    } catch {
      case allNodesFailed: AllNodesFailedException => logger.error(f"All Nodes have failed: \n$allNodesFailed")
      case queryExecutionException: QueryExecutionException => logger.error(f"The query to create automobiles.olxAds have returned an exception: \n$queryExecutionException")
      case queryValidationException: QueryValidationException => logger.error(f"The query to create automobiles.olxAds couldn't been validated: \n$queryValidationException")
    }
  }

  def loadData(olxAdData: GenericRecord): Unit = {
//    val tempCharacteristics: ListBuffer[String] = ListBuffer[String]()
//    olxAdData.get("characteristics").asInstanceOf[util.HashMap[Utf8, Utf8]].forEach((k, v) => tempCharacteristics.+() ))


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
      + "average_olx_price,"
      + "fipe_price,"
      + "fipe_price_ref,"
      + "difference_to_olx_average,"
      + "difference_to_fipe_price,"
      + "vehicle_specific_data"
      + ") VALUES ("
      + f"${olxAdData.get("adId")},"
      + f"${olxAdData.get("url")},"
      + f"${olxAdData.get("listOfImages").toString},"
      + f"${olxAdData.get("title")},"
      + f"${olxAdData.get("model")},"
      + f"${olxAdData.get("brand")},"
      + f"${olxAdData.get("price")},"
      + f"${olxAdData.get("financialInformation").toString},"
      + f"${olxAdData.get("kilometers")},"
      + f"${olxAdData.get("description")},"
      + f"${olxAdData.get("typeOfCar")},"
      + f"${olxAdData.get("typeOfShift")},"
      + f"${olxAdData.get("typeOfFuel")},"
      + f"${olxAdData.get("typeOfDirection")},"
      + f"${olxAdData.get("yearOfFabrication")},"
      + f"${olxAdData.get("color")},"
      + f"${olxAdData.get("endOfPlate")},"
      + f"${olxAdData.get("enginePower")},"
      + f"${olxAdData.get("hasGNV")},"
      + f"${olxAdData.get("numberOfDoors")},"
      + f"${olxAdData.get("characteristics")},"
    )
  }
}
