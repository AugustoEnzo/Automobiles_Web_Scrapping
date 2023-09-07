//package helpers
//
//import com.netflix.astyanax.{AstyanaxContext, Keyspace}
//import com.netflix.astyanax.connectionpool.NodeDiscoveryType
//import com.netflix.astyanax.connectionpool.impl.{ConnectionPoolConfigurationImpl, CountingConnectionPoolMonitor}
//import com.netflix.astyanax.impl.AstyanaxConfigurationImpl
//import com.netflix.astyanax.model.ColumnFamily
//import com.netflix.astyanax.serializers.StringSerializer
//import com.netflix.astyanax.thrift.ThriftFamilyFactory
//
//trait AstyanaxCassandraGenerics {
//  protected def createAutomobilesContext(): AstyanaxContext[Keyspace] = {
//    val context: AstyanaxContext[Keyspace] = new AstyanaxContext.Builder()
//      .forCluster("AutomobilesCluster")
//      .forKeyspace("automobiles")
//      .withAstyanaxConfiguration(new AstyanaxConfigurationImpl()
//        .setDiscoveryType(NodeDiscoveryType.NONE)
//      )
//      .withConnectionPoolConfiguration(new ConnectionPoolConfigurationImpl("automobilesConnectionPool")
//        .setPort(9160)
//        .setMaxConnsPerHost(1)
//        .setSeeds("192.168.3.20:9160")
//      )
//      .withConnectionPoolMonitor(new CountingConnectionPoolMonitor())
//      .buildKeyspace(ThriftFamilyFactory.getInstance())
//
//    context
//  }
//
//  protected def createOlxAdStringColumnFamily(): ColumnFamily[String, String] = {
//    val OLX_AD_STRING_COLUMNS_FAMILY = new ColumnFamily[String, String](
//      "OlxAdStringsColumnFamily",
//      StringSerializer.get, // Key Serializer
//      StringSerializer.get // Column Serializer
//    )
//    
//    OLX_AD_STRING_COLUMNS_FAMILY
//  }
//
////  protected def createOlxAdCollectionColumnFamily(): ColumnFamily[util.Collection[String], String] = {
////    val OLX_AD_LIST_COLUMNS_FAMILY = new ColumnFamily[util.Collection[String], String](
////      "OlxAdCollectionColumnFamily",
////      ListSerializer[String](),
////      StringSerializer.get
////    )
////
////    OLX_AD_LIST_COLUMNS_FAMILY
////  }
//}
