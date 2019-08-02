package com.rileycache.Proxy;


public interface Service {

    Object get(String key);

    void put(String key, Object value);

}