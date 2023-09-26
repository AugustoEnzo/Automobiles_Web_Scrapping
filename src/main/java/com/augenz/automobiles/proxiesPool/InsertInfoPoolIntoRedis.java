package com.augenz.automobiles.proxiesPool;

import com.augenz.automobiles.helpers.JedisHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.logging.Logger;

public class InsertInfoPoolIntoRedis {
    static Logger logger = Logger.getLogger(InsertInfoPoolIntoRedis.class.getName());
    static JedisHelper jedisHelper = new JedisHelper();
    public static void main(String[] args) {
        List<Map<String, String>> proxiesList = new ArrayList<>();
        List<String> listOfUserAgents = getAgents();

        jedisHelper.InsertUserAgentsToRedis(listOfUserAgents);

        try {
            File proxiesFile = new File("http_proxies.txt");
            Scanner scanner = new Scanner(proxiesFile);
            while (scanner.hasNextLine()) {
                String data = scanner.nextLine();
                Map<String, String> tempMap = new HashMap<>();
                tempMap.put(data.split(":")[0], data.split(":")[1]);
                proxiesList.add(tempMap);
            }

            jedisHelper.InsertProxiesToRedis(proxiesList);
        } catch (FileNotFoundException e) {
            logger.severe("The proxies file was not found!");
        }
    }

    private static List<String> getAgents() {
        List<String> listOfUserAgents = new ArrayList<>();
        listOfUserAgents.add("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36");
        listOfUserAgents.add("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36");
        listOfUserAgents.add("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36");
        listOfUserAgents.add("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36");
        listOfUserAgents.add("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36");
        listOfUserAgents.add("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.1 Safari/605.1.15");
        listOfUserAgents.add("Mozilla/5.0 (Macintosh; Intel Mac OS X 13_1) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.1 Safari/605.1.15");
        return listOfUserAgents;
    }
}
