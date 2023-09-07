/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.augenz.automobiles.models;

import java.util.List;
import java.util.Map;

/**
 *
 * @author ozne
 */
public class OlxAd {
    public String adId;
    public String url;
    public String publishDate;
    public List<String> listOfImages;
    public String title;
    public String model;
    public String brand;
    public String price;
    public String kilometers;
    public String description;
    public String typeOfCar;
    public String typeOfShift;
    public String typeOfFuel;
    public String typeOfDirection;
    public String yearOfFabrication;
    public String color;
    public String endOfPlate;
    public String enginePower;
    public String hasGNV;
    public String numberOfDoors;
    public String fipePrice;
    public String differenceToFipePrice;
    public List<String> conservationAndWarranty;
    public List<String> documentationAndRegularization;
    public String vehicleModel;
    public String financialStatus;
    public Map<String, String> characteristics;
    public List<String> features;
    public List<String> financialInfo;
    public Map<String, String> locationInfo;
    public Map<String, String> profileInfo;
    public Map<String, String> fundingInfo;
    public List<String> tags;
    public Map<String, String> verificationInfo;
    public Map<String, String> fipePriceRef;
    public Map<String, String> vehicleSpecificData;

    public OlxAd(String adId, String url, String publishDate, List<String> listOfImages, String title, String model,
                 String brand, String price, String kilometers, String description, String typeOfCar, String typeOfShift,
                 String typeOfDirection, String yearOfFabrication, String color, String endOfPlate, String enginePower,
                 String hasGNV, String numberOfDoors, String fipePrice, String differenceToFipePrice,
                 List<String> conservationAndWarranty, List<String> documentationAndRegularization,String typeOfFuel,
                 String vehicleModel, String financialStatus, Map<String, String> characteristics,
                 List<String> features, List<String> financialInfo, Map<String, String> locationInfo,
                 Map<String, String> profileInfo, Map<String, String> fundingInfo, List<String> tags,
                 Map<String, String> verificationInfo, Map<String, String> fipePriceRef,
                 Map<String, String> vehicleSpecificData) {
        this.adId = adId;
        this.url = url;
        this.publishDate = publishDate;
        this.listOfImages = listOfImages;
        this.title = title;
        this.model = model;
        this.brand = brand;
        this.price = price;
        this.kilometers = kilometers;
        this.description = description;
        this.typeOfCar = typeOfCar;
        this.typeOfShift = typeOfShift;
        this.typeOfFuel = typeOfFuel;
        this.typeOfDirection = typeOfDirection;
        this.yearOfFabrication = yearOfFabrication;
        this.color = color;
        this.endOfPlate = endOfPlate;
        this.enginePower = enginePower;
        this.hasGNV = hasGNV;
        this.numberOfDoors = numberOfDoors;
        this.fipePrice = fipePrice;
        this.differenceToFipePrice = differenceToFipePrice;
        this.conservationAndWarranty = conservationAndWarranty;
        this.documentationAndRegularization = documentationAndRegularization;
        this.vehicleModel = vehicleModel;
        this.financialStatus = financialStatus;
        this.characteristics = characteristics;
        this.features = features;
        this.financialInfo = financialInfo;
        this.locationInfo = locationInfo;
        this.profileInfo = profileInfo;
        this.fundingInfo = fundingInfo;
        this.tags = tags;
        this.verificationInfo = verificationInfo;
        this.fipePriceRef = fipePriceRef;
        this.vehicleSpecificData = vehicleSpecificData;
    }
}
