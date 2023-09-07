//package helpers
//
//import com.netflix.astyanax.connectionpool.OperationResult
//import com.netflix.astyanax.connectionpool.exceptions.ConnectionException
//import com.netflix.astyanax.{AstyanaxContext, Keyspace}
//import com.typesafe.scalalogging.Logger
//import models.OlxAdJava
//import org.apache.avro.generic.GenericRecord
//
//import java.util
//
//class AstyanaxCassandra extends AstyanaxCassandraGenerics {
//  private val logger = Logger(this.getClass)
//  val context: AstyanaxContext[Keyspace] = createAutomobilesContext()
//
//  private val olxAdColumnFamily = createOlxAdStringColumnFamily()
//
//  context.start()
//
//  private val keyspace: Keyspace = context.getClient
//
//  def insertOlxDataToCassandra(olxAdMessage: GenericRecord): Unit = {
//    val listOfImages: util.List[String] = olxAdMessage.get("listOfImages").asInstanceOf[util.List[String]]
//    val mutationBatch = keyspace.prepareMutationBatch()
//
//    mutationBatch.withRow(olxAdColumnFamily, olxAdMessage.get("adId").toString)
//      .putColumn("adId", olxAdMessage.get("adId").toString, null)
//      .putColumn("url", olxAdMessage.get("url").toString, null)
//
//    try {
//      val result: OperationResult[Void] = mutationBatch.execute()
//    } catch {
//      case error: ConnectionException => logger.error("Astyanax couldn't connect to cassandra cluster!\n" +
//      f"$error")
//    }
////    listOfImages.forEach(imageLink =>
////      mutationBatch.withRow(olxAdColumnFamily, olxAdMessage.get("adId").toString)
////        .putColumn(f"imageURL$imageLink.index")
////    )
//  }
//}
