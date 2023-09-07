/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.augenz.automobiles.helpers;

/**
 *
 * @author ozne
 */
public interface KafkaConstants {
    final String KAFKA_BROKERS = "srv-data:9092";
    final int MESSAGE_COUNT = 1000;
    final String OLX_PRODUCER_CLIENT_ID = "OlxDataCrawlerProducer";
    final String OLX_CONSUMER_CLIENT_ID = "OlxDataCrawlerConsumer";
    final String OLX_GROUP_ID = "OlxDataCrawlerConsumer";
    final String TOPIC_NAME = "olx";
    final String GROUP_ID_CONFIG = "crawlersProducers";
    final int MAX_NO_MESSAGE_FOUND_COUNT = 100;
    final String OFFSET_RESET_LATEST = "latest";
    final String OFFSET_RESET_EARLIER = "earliest";
    final int MAX_POLL_RECORDS = 1;
    final int AUTO_COMMIT_INTERVAL = 10000;
}
