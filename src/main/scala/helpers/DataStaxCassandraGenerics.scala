package helpers

import com.datastax.oss.driver.api.core.CqlSession
import com.typesafe.scalalogging.Logger

import java.net.InetSocketAddress

trait DataStaxCassandraGenerics {
  private val logger = Logger(this.getClass)
  private val host = InetSocketAddress.createUnresolved("srv-data", 9042)

  def createSession(): CqlSession = {
      val session = CqlSession.builder()
        .addContactPoint(host)
        .withLocalDatacenter("datacenter1")
        .build()

      logger.info(f"Connected session: ${session.getName}")
      session
  }
}
