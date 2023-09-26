package com.augenz.automobiles.sinks;

import com.augenz.automobiles.consumers.OlxDataCrawlerCassandraConsumer;

public class CassandraToElasticSink {

    public static void main (final String[] args) {
        OlxDataCrawlerCassandraConsumer cassandraConsumer = new OlxDataCrawlerCassandraConsumer();
    }
}
