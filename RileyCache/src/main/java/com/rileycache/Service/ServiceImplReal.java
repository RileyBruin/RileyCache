package com.rileycache.Service;

import com.rileycache.Proxy.Service;

public class ServiceImplReal implements Service {
    @Override
    public Object get(String key) {
        System.out.println("In real target");
        return "value of " + key;
    }

    @Override
    public void put(String key, Object value) {
        System.out.println("Default put method");
    }
}
