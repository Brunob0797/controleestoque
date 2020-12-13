package com.example.controleestoque.Entidades;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Inventory {
    private String productinv;
    private String key;
    private int quantidade;
    public Map<String, Boolean> inventory = new HashMap<>();

    public Inventory(){}

    public Inventory(String productinv, String key, int quantidade){
        this.productinv = productinv;
        this.key = key;
        this.quantidade = quantidade;

    };

    public String getProductinv() {
        return productinv;
    }

    public void setProductinv(String productinv) {
        this.productinv = productinv;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public String toString(){
        return productinv + ": " + quantidade;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("productinv", productinv);
        result.put("key", key);
        result.put("quantidade", quantidade);
        return result;
    }
}
