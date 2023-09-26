package com.augenz.automobiles.helpers;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.session.Session;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.logging.Logger;

public interface DataStaxCassandraGenerics {
     Logger logger = Logger.getLogger(DataStaxCassandraGenerics.class.getName());
     InetSocketAddress host = InetSocketAddress.createUnresolved("srv-data", 9042);

     static CqlSession createSession() {
         CqlSession session = CqlSession.builder().addContactPoint(host)
                 .withLocalDatacenter("datacenter1").build();

         logger.info("Connected session: " + session.getName());
         return session;
     }
}
