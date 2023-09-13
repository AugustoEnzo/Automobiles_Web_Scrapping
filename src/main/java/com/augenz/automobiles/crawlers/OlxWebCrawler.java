package com.augenz.automobiles.crawlers;

import com.augenz.automobiles.helpers.JedisHelper;
import com.augenz.automobiles.models.OlxAd;
import com.augenz.automobiles.producers.OlxDataCrawlerProducer;
import java.io.IOException;
import static java.lang.String.format;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.logging.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Gets data from Olx
 */
public class OlxWebCrawler {
    private static final int LAST_PAGE = 101;
    private static final int ADS_TO_PROCESS = 54 * 100;
    private static final int TIMEOUT_FOR_WEB_CRAWILNG_IN_MILISECONDS = 50000;
    private static final String CLASS_NAME = OlxWebCrawler.class.getName();
    private static final Logger logger = Logger.getLogger(OlxWebCrawler.CLASS_NAME);
    private static final OlxDataCrawlerProducer producer = new OlxDataCrawlerProducer();
    private static final Random rand = new Random();
    private static final JedisHelper jedisHelper = new JedisHelper();

    private static boolean verifyIfImageLinkExists(final JSONObject image) {
        try {
            image.getString("original");

           return true;
        } catch (final JSONException e) {
            return false;
        }
    }
    
    private static String removeValuesFromString(String stringToCleanValues, final String[] valuesToRemove) {
        for (String valueToRemove : valuesToRemove) {
            stringToCleanValues = stringToCleanValues.replace(valueToRemove, "");
        } return stringToCleanValues;
    }

    public static void main(final String[] args) {
        int processedAds = 0;
        int notValidAds = 0;

        logger.setLevel(Level.SEVERE);

        for (int pg = 1; pg < LAST_PAGE; pg++) {
            try {
                String page = String.valueOf(pg);
                List<Proxy> listOfProxies = new ArrayList<>();
                for (Map.Entry<String, String> entry: jedisHelper.getProxy(String.format("proxy-%d", rand.nextInt(385))).entrySet()) {
                    listOfProxies.add(
                            new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(entry.getKey(), Integer.parseInt(entry.getValue())))
                    );
                }
                final Document pageOfAds = Jsoup.connect("https://am.olx.com.br/autos-e-pecas/carros-vans-e-utilitarios?o=" + page)
                        .maxBodySize(0)
                        .timeout(TIMEOUT_FOR_WEB_CRAWILNG_IN_MILISECONDS)
                        .ignoreContentType(true)
                        .userAgent(jedisHelper.getUserAgent(String.format("user-agent-%d", rand.nextInt(7))))
                        .proxy(listOfProxies.get(0))
                        .header("Content-Language", "pt-BR")
                        .get();
                
                logger.log(Level.INFO, "Looking the document for page: {0}", page);
                
                
                Elements divsWithTheClass = pageOfAds.getElementsByClass("sc-fdaac595-0");
                int NUMBER_OF_ADS_PER_PAGE = 0;
                for (Element div: divsWithTheClass) {
                    if (div.hasClass("sc-fdaac595-0"))
                        NUMBER_OF_ADS_PER_PAGE ++;
                }
                
                System.out.println(NUMBER_OF_ADS_PER_PAGE); 
               

                for (int adIndex = 1; adIndex < NUMBER_OF_ADS_PER_PAGE; adIndex++) {
//                      legacy: #main-content > div:nth-child(%d)
//                    div.sc-fdaac595-0:nth-child(1)
                    Elements adPageElements = pageOfAds.select(String.format("div.sc-fdaac595-0:nth-child(%d)", adIndex));

                    if (adPageElements.text().isEmpty()) {
                        notValidAds += 1;
                        double percentageOfNotValidAds = BigDecimal.valueOf(((float) notValidAds / ADS_TO_PROCESS) * 100)
                                .setScale(2, RoundingMode.HALF_UP).doubleValue();
                        
                        logger.log(Level.INFO, "Percentage of not valid Ads: {0} %", percentageOfNotValidAds);
                    } else {
                        try {
                            // div.sc-fdaac595-0:nth-child(53) > section:nth-child(1) > a:nth-child(1)
                            // legacy: !adPageElements.select("div > a").attr("href").isEmpty()

                            for (Map.Entry<String, String> entry: jedisHelper.getProxy(String.format("proxy-%d", rand.nextInt(385))).entrySet()) {
                                listOfProxies.add(
                                        new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(entry.getKey(), Integer.parseInt(entry.getValue())))
                                );
                            }

                            if (!adPageElements.select("section:nth-child(1) > a:nth-child(1)").attr("href").isEmpty()) {
                                Document adDocument = Jsoup.connect(adPageElements.select("section:nth-child(1) > a:nth-child(1)")
                                        .attr("href"))
                                        .maxBodySize(0)
                                        .timeout(TIMEOUT_FOR_WEB_CRAWILNG_IN_MILISECONDS)
                                        .userAgent(jedisHelper.getUserAgent(String.format("user-agent-%d", rand.nextInt(7))))
                                        .proxy(listOfProxies.get(1))
                                        .header("Content-Language", "pt-BR")
                                        .get();

                                JSONObject adJson = new JSONObject(adDocument
                                        .getElementsByAttributeValue("type", "text/plain").attr("data-json")
                                        .replace("&quot", ""));

                                if (!adJson.getJSONObject("ad").getJSONArray("images").isEmpty() &&
                                        !String.valueOf(adJson.getJSONObject("ad").getInt("adId")).isEmpty()) {

                                    String adId = String.valueOf(adJson.getJSONObject("ad").getInt("adId"));

                                    logger.log(Level.INFO, "Collection data for ad: {0}", adId);

                                    String url = adPageElements.select("div > a").attr("href");

                                    Map<String, String> properties = new HashMap<>();

                                    Map<String, List<String>> propertiesMap = new HashMap<>();
                                    JSONArray propertiesArray = adJson.getJSONObject("ad").getJSONArray("properties");

                                    for (int index = 0; index < propertiesArray.length(); index++) {

                                        JSONObject tempProperty = new JSONObject(propertiesArray.get(index).toString());
                                        properties.put(tempProperty.get("name").toString(), tempProperty.get("value").toString().trim());

                                        if (!tempProperty.isNull("values") && !tempProperty.getJSONArray("values").isEmpty()) {

                                            List<String> listOfValues = new ArrayList<>();
                                            final JSONArray tempInnerProperties = tempProperty.getJSONArray("values");

                                            for (int innerIndex = 0; innerIndex < tempInnerProperties.length(); innerIndex++) {

                                                JSONObject value = new JSONObject(tempInnerProperties.get(innerIndex).toString());

                                                if (!"0".equals(value.getString("label"))) {
                                                    listOfValues.add(value.getString("label").trim());
                                                }
                                            }
                                            
                                            propertiesMap.put(tempProperty.get("name").toString(), listOfValues);
                                        }
                                    }

                                    List<String> listOfImages = new ArrayList<>();
                                    if (!adJson.getJSONObject("ad").isNull("images")) {
                                        JSONArray tempImages = adJson.getJSONObject("ad").getJSONArray("images");

                                        for (int imageIndex=0; imageIndex < tempImages.length(); imageIndex++) {
                                            JSONObject tempImage = new JSONObject(tempImages.get(imageIndex).toString());
                                            if (verifyIfImageLinkExists(tempImage)) {
                                                listOfImages.add(tempImage.getString("original").trim());

                                            }
                                        }
                                    }

                                    String title = adJson.getJSONObject("ad").getString("subject");

                                    String model = properties.get("vehicle_model");

                                    String brand = properties.get("vehicle_brand");

                                    String price = "";
                                    if (!adJson.getJSONObject("ad").isNull("priceValue")) {
                                        price = adJson.getJSONObject("ad").getString("priceValue")
                                                .replace("R$", "").replace(".", "");
                                    }

                                    List<String> financialInfo = new ArrayList<>();
                                    if (propertiesMap.get("financial") != null) {
                                        financialInfo = propertiesMap.get("financial");
                                    }

                                    List<String> conservationAndWarranty = propertiesMap.get("conservation_and_warranty");

                                    List<String> documentationAndRegularization = propertiesMap.get("documentation_and_regularization");

                                    List<String> features = new ArrayList<>();
                                    if (propertiesMap.get("car_features") != null) {
                                        features = propertiesMap.get("car_features");
                                    }

                                    String kilometers = properties.get("mileage");

                                    String description = adJson.getJSONObject("ad").getString("body");

                                    String typeOfCar = properties.get("cartype");

                                    String typeOfShift = properties.get("gearbox");

                                    String typeOfFuel = properties.get("fuel");

                                    String typeOfDirection = properties.get("car_steering");

                                    String yearOfFabrication = properties.get("regdate");

                                    String color = properties.get("carcolor");

                                    String endOfPlate = properties.get("end_tag");

                                    String enginePower = properties.get("motorpower");

                                    String hasGNV = properties.get("has_gnv");

                                    String numberOfDoors = properties.get("doors");

                                    String vehicleModel = properties.get("vehicle_model");

                                    String financialStatus = properties.get("indexed_financial_status");

                                    Map<String, String> characteristics = new HashMap<>();

                                    if (properties.get("owner") != null) {
                                        switch (properties.get("owner")) {
                                            case "Sim" -> characteristics.putIfAbsent("haveUniqueOwnwer", "true");
                                            case "Não" -> characteristics.putIfAbsent("haveUniqueOwnwer", "false");
                                            default -> logger.warning("Non binary for ownwer");
                                        }
                                    }
                                    if (properties.get("exchange") != null) {
                                        switch (properties.get("exchange")) {
                                            case "Sim" -> characteristics.putIfAbsent("acceptsExchanges", "true");
                                            case "Não" -> characteristics.putIfAbsent("acceptsExchanges", "false");
                                            default -> logger.warning("Non binary for exchange");
                                        }
                                    }
                                    if (properties.get("owner_manual") != null) {
                                        switch (properties.get("owner_manual")) {
                                            case "Sim" -> characteristics.putIfAbsent("haveOwnerManual", "true");
                                            case "Não" -> characteristics.putIfAbsent("haveOwnerManual", "false");
                                            default -> logger.warning("Non binary for owner_manual");
                                        }
                                    }
                                    if (properties.get("dealership_review") != null) {
                                        switch (properties.get("dealership_review")) {
                                            case "Sim" -> characteristics.putIfAbsent("haveDealershipReview", "true");
                                            case "Não" -> characteristics.putIfAbsent("haveDealershipReview", "false");
                                            default -> logger.warning("Non binary for dealership_review");
                                        }
                                    }
                                    if (properties.get("warranty") != null) {
                                        switch (properties.get("warranty")) {
                                            case "Sim" -> characteristics.putIfAbsent("haveWarranty", "true");
                                            case "Não" -> characteristics.putIfAbsent("haveWarranty", "false");
                                            default -> logger.warning("Non binary for warranty");
                                        }
                                    }
                                    if (properties.get("has_paid_ipva") != null) {
                                        switch (properties.get("has_paid_ipva")) {
                                            case "Sim" -> characteristics.putIfAbsent("haveIPVAPaid", "true");
                                            case "Não" -> characteristics.putIfAbsent("haveIPVAPaid", "false");
                                            default -> logger.warning("Non binary for has_paid_ipva");
                                        }
                                    }
                                    if (properties.get("extra_key") != null) {
                                        switch (properties.get("extra_key")) {
                                            case "Sim" -> characteristics.putIfAbsent("haveExtraKey", "true");
                                            case "Não" -> characteristics.putIfAbsent("haveExtraKey", "false");
                                            default -> logger.warning("Non binary for extra key");
                                        }
                                    }

                                    if (!adJson.getJSONObject("ad").getJSONObject("carSpecificData").isNull("isFinanceable")) {
                                        characteristics.putIfAbsent("isFinanceable", String.valueOf(adJson.getJSONObject("ad").getJSONObject("carSpecificData").getBoolean("isFinanceable")));
                                    } else {
                                        characteristics.putIfAbsent("isFinanceable", null);
                                    }

                                    Map<String, String> locationInfo = new HashMap<>();
                                    locationInfo.putIfAbsent("address", String.valueOf(adJson.getJSONObject("ad").getJSONObject("location").get("address")));
                                    locationInfo.putIfAbsent("neighbourhood", String.valueOf(adJson.getJSONObject("ad").getJSONObject("location").get("neighbourhood")));
                                    locationInfo.putIfAbsent("neighbourhoodId", String.valueOf(adJson.getJSONObject("ad").getJSONObject("location").get("neighbourhoodId")));
                                    locationInfo.putIfAbsent("municipality", String.valueOf(adJson.getJSONObject("ad").getJSONObject("location").get("municipality")));
                                    locationInfo.putIfAbsent("municipalityId", String.valueOf(adJson.getJSONObject("ad").getJSONObject("location").get("municipalityId")));
                                    locationInfo.putIfAbsent("zipcode", String.valueOf(adJson.getJSONObject("ad").getJSONObject("location").get("zipcode")));
                                    locationInfo.putIfAbsent("mapLati", String.valueOf(adJson.getJSONObject("ad").getJSONObject("location").get("mapLati")));
                                    locationInfo.putIfAbsent("mapLong", String.valueOf(adJson.getJSONObject("ad").getJSONObject("location").get("mapLong")));
                                    locationInfo.putIfAbsent("uf", String.valueOf(adJson.getJSONObject("ad").getJSONObject("location").get("uf")));
                                    locationInfo.putIfAbsent("ddd", String.valueOf(adJson.getJSONObject("ad").getJSONObject("location").get("ddd")));
                                    locationInfo.putIfAbsent("zoneId", String.valueOf(adJson.getJSONObject("ad").getJSONObject("location").get("zoneId")));
                                    locationInfo.putIfAbsent("zone", String.valueOf(adJson.getJSONObject("ad").getJSONObject("location").get("zone")));
                                    locationInfo.putIfAbsent("region", String.valueOf(adJson.getJSONObject("ad").getJSONObject("location").get("region")));

                                    String publishDate = adDocument.select(".hSZkck").text()
                                            .replace("Publicado em ", "")
                                            .replace(" às ", "T");
                                    
                                    Map<String, String> profileInfo = new HashMap<>();
                                    if (!adJson.getJSONObject("ad").isNull("sellerHistory")) {
                                        Pattern numberPattern = Pattern.compile("[0-9]+", Pattern.CASE_INSENSITIVE);
                                        
                                        JSONObject tempUserInfo = adJson.getJSONObject("ad").getJSONObject("user");
                                        
                                        JSONObject sellerHistory = adJson.getJSONObject("ad").getJSONObject("sellerHistory");
                                        
                                        String averageDispatchTime = adJson.getJSONObject("ad").getJSONObject("sellerHistory").getString("averageDispatchTime");
                                        profileInfo.putIfAbsent("accountId", String.valueOf(tempUserInfo.get("accountId")));
                                        profileInfo.putIfAbsent("userId", String.valueOf(tempUserInfo.get("userId")));
                                        profileInfo.putIfAbsent("name", String.valueOf(tempUserInfo.get("name")));
                                        profileInfo.putIfAbsent("isPhoneVerified", String.valueOf(adJson.getJSONObject("ad").getJSONObject("phone").get("phoneVerified")));
                                        profileInfo.putIfAbsent("salesAmounts", String.valueOf(sellerHistory.getInt("salesAmounts")));
                                        profileInfo.putIfAbsent("canceledSalesAmounts", String.valueOf(sellerHistory.getInt("canceledSalesAmounts")));
                                        profileInfo.putIfAbsent("totalDispatchTimeInMinutes", String.valueOf(sellerHistory.getDouble("totalDispatchTimeInMinutes")));
                                        Matcher matcher = numberPattern.matcher(averageDispatchTime);
                                        if (matcher.find()) {
                                            if (averageDispatchTime.contains("dia") || averageDispatchTime.contains("dias")) {
                                                profileInfo.putIfAbsent("averageDispatchTime", String.valueOf(Double.parseDouble(matcher.group()) * 3600));
                                            } else if (averageDispatchTime.contains("mês") || averageDispatchTime.contains("meses")) {
                                                profileInfo.putIfAbsent("averageDispatchTime", String.valueOf(Double.parseDouble(matcher.group()) * 43800.048));
                                            } else {
                                                profileInfo.putIfAbsent("averageDispatchTime", String.valueOf(Double.parseDouble(matcher.group())));
                                            }
                                        }
                                        if (!tempUserInfo.isNull("configs")) {
                                            profileInfo.putIfAbsent("proAccount", String.valueOf(tempUserInfo.getJSONObject("configs").get("proAccount")));
                                        }
                                    }

                                    final Map<String, String> fundingInfo = new HashMap<>();
                                    if (!adJson.getJSONObject("ad").getJSONObject("carSpecificData").getJSONObject("financing").isNull("installment") &&
                                            !adJson.getJSONObject("ad").getJSONObject("carSpecificData").getJSONObject("financing").isNull("upfrontPayment")) {
                                        String[] tempConditions = adJson.getJSONObject("ad").getJSONObject("carSpecificData").getJSONObject("financing").getJSONObject("installment").get("value").toString().split("x");
                                        String[] valuesToRemove = {"R$", ",", ".", "*"};
                                        fundingInfo.putIfAbsent("fundingInstallments", tempConditions[0]);
                                        fundingInfo.putIfAbsent("fundingInstallmentValue", removeValuesFromString(tempConditions[1], valuesToRemove));
                                        fundingInfo.putIfAbsent("fundingEntry", removeValuesFromString(
                                                adJson.getJSONObject("ad")
                                                        .getJSONObject("carSpecificData"
                                                        ).getJSONObject("financing")
                                                        .getJSONObject("upfrontPayment")
                                                        .get("value").toString(), valuesToRemove));
                                    }
                                
                                    final List<String> tags = new ArrayList<>();
                                    if (!adJson.getJSONObject("ad").getJSONObject("vehicleReport").isNull("tags")) {
                                        for(Object tag: adJson.getJSONObject("ad").getJSONObject("vehicleReport").getJSONArray("tags")) {
                                            tags.add(new JSONObject(tag.toString()).getString("label"));
                                        }
                                    }
                                    
                                    final Map<String, String> verificationInfo = new HashMap<>();
                                    if (!adJson.getJSONObject("ad").getJSONObject("vehicleReport").isNull("enabled")) {
                                        JSONObject vehicleReport = adJson.getJSONObject("ad").getJSONObject("vehicleReport");
                                        verificationInfo.putIfAbsent("isVerified", vehicleReport.get("enabled").toString());
                                        
                                        String[] valuesToRemove = {"Verifique se os dados do Histórico Veicular são os mesmos informados no anúncio. "};
                                        
                                        Pattern datePattern = Pattern.compile("[0-9]{2}/+[0-9]{2}/+[0-9]{4}", Pattern.CASE_INSENSITIVE);
                                        Pattern timePattern = Pattern.compile("[0-9]{2}:+[0-9]{2}:+[0-9]{2}", Pattern.CASE_INSENSITIVE);
                                        Matcher dateMatcher = datePattern.matcher(vehicleReport.get("description").toString());
                                        Matcher timeMatcher = timePattern.matcher(vehicleReport.get("description").toString());
                                        if (dateMatcher.find() && timeMatcher.find()) {
                                            String[] dateStringArray = removeValuesFromString(dateMatcher.group(), valuesToRemove)
                                                    .strip().split("/");
                                            
                                            verificationInfo.putIfAbsent("queryDate", dateStringArray[2]+"-"+dateStringArray[1]+"-"+dateStringArray[0]+"T"
                                                    +removeValuesFromString(timeMatcher.group(), valuesToRemove).strip());
                                            
                                            verificationInfo.putIfAbsent("reportLink", (String) vehicleReport.get("reportLink"));
                                        }
                                        
                                        String fipePrice = "";
                                        if (!adJson.getJSONObject("ad").isNull("abuyFipePrice")) {
                                            fipePrice = adJson.getJSONObject("ad").getJSONObject("abuyFipePrice").get("fipePrice").toString();
                                        }
                                        
                                        Map<String, String> fipePriceRef = new HashMap<>();
                                        if (!adJson.getJSONObject("ad").isNull("abuyPriceRef")) {
                                            JSONObject abuyPriceRef = adJson.getJSONObject("ad").getJSONObject("abuyPriceRef");
                                            fipePriceRef.putIfAbsent("yearMonthReference", abuyPriceRef.get("year_month_ref").toString());
                                            fipePriceRef.putIfAbsent("priceMin", abuyPriceRef.get("price_min").toString());
                                            fipePriceRef.putIfAbsent("price25", abuyPriceRef.get("price_p25").toString());
                                            fipePriceRef.putIfAbsent("price33", abuyPriceRef.get("price_p33").toString());
                                            fipePriceRef.putIfAbsent("price50", abuyPriceRef.get("price_p50").toString());
                                            fipePriceRef.putIfAbsent("price66", abuyPriceRef.get("price_p66").toString());
                                            fipePriceRef.putIfAbsent("price75", abuyPriceRef.get("price_p75").toString());
                                            fipePriceRef.putIfAbsent("priceMax", abuyPriceRef.get("price_max").toString());
                                            fipePriceRef.putIfAbsent("priceStdDev", abuyPriceRef.get("price_stddev").toString());
                                            fipePriceRef.putIfAbsent("vehicleCount", abuyPriceRef.get("vehicle_count").toString());
                                        }
                                        
                                        String differenceToFipePrice = "";
                                        if (!price.isBlank() && !fipePrice.isBlank()) {
                                            differenceToFipePrice = String.valueOf(Double.parseDouble(price) - Double.parseDouble(fipePrice));
                                        }
                                        
                                        Map<String, String> vehicleSpecificData = new HashMap<>();
                                        for(Object item : adJson.getJSONObject("ad").getJSONArray("vehicleSpecificData")) {
                                            JSONObject itemJSON = new JSONObject(item.toString());
                                            if (!itemJSON.isNull("value")) {
                                                vehicleSpecificData.putIfAbsent(itemJSON.get("value").toString(), itemJSON.get("key").toString());
                                            }
                                        }
                                        
                                        OlxAd olxAd = new OlxAd(adId, url, publishDate, listOfImages, title, model, brand, price, kilometers, description, typeOfCar, typeOfShift,
                                                typeOfDirection, yearOfFabrication, color, endOfPlate, enginePower, hasGNV, numberOfDoors, fipePrice, differenceToFipePrice,
                                                conservationAndWarranty, documentationAndRegularization, typeOfFuel, vehicleModel, financialStatus,
                                                characteristics, features, financialInfo, locationInfo, profileInfo, fundingInfo, tags, verificationInfo,
                                                fipePriceRef, vehicleSpecificData);

//                                        producer.send("olx", olxAd);

                                        processedAds ++;
                                        double percentageOfProcessedAds = BigDecimal.valueOf(((float) processedAds / ADS_TO_PROCESS) * 100)
                                                .setScale(2, RoundingMode.HALF_UP).doubleValue();

                                        logger.log(Level.INFO, "Number of already processed ads {0}", processedAds);
                                        logger.log(Level.INFO, "Percentage of processed ads: {0} %", percentageOfProcessedAds);
                                    }
                                    
                                }
                            }
                        } catch (HttpStatusException | SocketTimeoutException httpStatusException) {
                            logger.severe(format("Error trying fetch ads page %d", pg));
                        } catch (IOException ioException) {
                            logger.severe(format("Jsoup could not read the data from this document.%s", ioException));
                        }
                    }
                }

            } catch (HttpStatusException | SocketTimeoutException httpStatusException) {
                logger.severe(format("Error trying fetch ads page %d", pg));
            } catch (IOException ioException) {
                logger.severe(format("Jsoup could not read the data from this document.%s", ioException));
            }
        }
    }
}