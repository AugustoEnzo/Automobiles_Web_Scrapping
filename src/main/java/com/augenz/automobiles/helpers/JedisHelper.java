/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.augenz.automobiles.helpers;

import redis.clients.jedis.JedisPooled;

import java.util.Map;
import java.util.List;


/**
 *
 * @author augustoenzo
 */
public class JedisHelper {
    JedisPooled jedis = new JedisPooled("srv-data", 6379);

    public void InsertProxiesToRedis(List<Map<String, String>> proxiesList) {
        for (int proxyIndex = 0; proxyIndex < proxiesList.size()-1; proxyIndex++) {
            jedis.hset(String.format("proxy-%d", proxyIndex), proxiesList.get(proxyIndex));
        }
    }

    public void InsertUserAgentsToRedis(List<String> listOfUserAgents) {
        for (int userAgentsIndex = 0; userAgentsIndex < listOfUserAgents.size()-1; userAgentsIndex++) {
            jedis.set(String.format("user-agent-%d", userAgentsIndex), listOfUserAgents.get(userAgentsIndex));
        }
    }

    public Map<String, String> getProxy(String id) {
        return jedis.hgetAll(id);
    }

    public String getUserAgent(String id) {
        return jedis.get(id);
    }
}
