package com.example.controleestoque.Entidades;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String name;
    private String email;
    private String usertype;
    private String key;
    private String password;
    public Map<String, Boolean> user = new HashMap<>();

    public User(){}

    public User(String name, String email, String usertype, String key){
        this.name = name;
        this.email = email;
        this.usertype = usertype;
        this.key = key;
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Exclude
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String toString(){
        return name;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("email", email);
        result.put("usertype", usertype);
        result.put("key", key);
        result.put("user", user);
        return result;
    }
}
