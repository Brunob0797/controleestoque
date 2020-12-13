package com.example.controleestoque.Entidades;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Product {
    private String product;
    private String producer;
    private String price;
    private String key;
    private int massa;
    public Map<String, Boolean> products = new HashMap<>();

    public Product(){}

    public Product(String product, String producer, String price, String key, int massa){
        this.product = product;
        this.producer = producer;
        this.massa = massa;
        this.key = key;
        this.price = price;
    };

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getMassa() {
        return massa;
    }

    public void setMassa(int massa) {
        this.massa = massa;
    }


    public String toString(){
        return product;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("product", product);
        result.put("producer", producer);
        result.put("price", price);
        result.put("massa", massa);
        result.put("key", key);
        result.put("products", products);
        return result;
    }
}
