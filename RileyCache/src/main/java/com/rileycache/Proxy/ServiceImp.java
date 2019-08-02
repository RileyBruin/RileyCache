package com.rileycache.Proxy;

import com.rileycache.CacheCore.CacheNodeLevel1;
import com.rileycache.Service.ServiceImplReal;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class ServiceImp implements Service{
    // prepare the dir for level 2 cache
    private static final String dir= System.getProperty("user.dir") + "/level2Storage/";
    // capacity of level 1 cache should be at least 1
    private int capacity;
    // a map to trace the objects in level 1 cache
    Map<String, CacheNodeLevel1> M1 = new ConcurrentHashMap<>();
    // a set to trace the files of converted objects in level 2 cache
    Set<String> S1 =new HashSet<>();
    // to count the occupation in level 1 cache
    private int count;
    // the head and tail of level 1 cache which is a linklist
    private CacheNodeLevel1 head=null;
    private CacheNodeLevel1 tail=null;


    public ServiceImp(int capacity) {
        new File(this.dir).mkdirs();
        this.capacity = capacity;
        this.count = 0;
    }

    @Override
    public Object get(String key) {
        Object valuesOut = null;
        //check if in level 1
        if(this.M1.containsKey(key)){
            CacheNodeLevel1 nd = this.M1.get(key);
            valuesOut = nd.getValue();
            if(this.capacity>1) {
                if (nd.getPre() != null) {
                    detachNd(nd);
                    addToFront(nd);
                }
            }
        }
        //check if in level 2
        else if(this.S1.contains(key)){
            // grab from level 2
            valuesOut = grabFromFile(key);
            if(this.count>this.capacity){
                removeTail();
            }
        }
        // if can not find the key in both level 1 and level 2
        // it will call default class ex ask value from data base
        else{
            System.out.println("Key: \""+ key +"\" not found in cache");
            ServiceImplReal serviceImplReal = new ServiceImplReal();

            valuesOut = serviceImplReal.get(key);
        }


        return valuesOut;
    }

    @Override
    public void put(String key, Object value) {
        if(count>=2){
            if (this.M1.containsKey(key)){
                CacheNodeLevel1 nd = this.M1.get(key);
                if(nd.getPre()!=null){
                    detachNd(nd);
                    addToFront(nd);
                }
                nd.setValue(value);
            }else if(this.S1.contains(key)){
                // grab from level 2
                grabFromFile(key);
                this.head.setValue(value);
                if(this.count>this.capacity){
                    removeTail();
                }
            }else {
                CacheNodeLevel1 newNode = new CacheNodeLevel1(key, value,null,this.head);
                this.head.setPre(newNode);
                this.head = newNode;
                this.M1.put(key,newNode);
                this.count ++;
            }
        }else if(count==0){
            this.tail =  new CacheNodeLevel1(key,value,null,null);
            this.M1.put(key,this.tail);
            this.count++;
        }else if(this.count==1){
            if(this.M1.containsKey(key)){
                this.tail.setValue(value);
            }else if(this.S1.contains(key)){
                // grab from level 2
                grabFromFile(key);
                this.head.setValue(value);
                if(this.count>this.capacity){
                    removeTail();
                }
            }else {
                this.head = new CacheNodeLevel1(key,value,null,this.tail);
                this.tail.setPre(this.head);
                this.M1.put(key,this.head);
                this.count++;
            }
        }
        if(this.count>this.capacity){
            removeTail();
        }


    }

    // when ever we get or update an object, we have to move it to the front of level 1 cache
    private void addToFront(CacheNodeLevel1 nd){
        this.head.setPre(nd);
        nd.setNext(this.head);
        nd.setPre(null);
        this.head = nd;
    }
    // detach a node from middle or last then can be put to front
    private void detachNd(CacheNodeLevel1 nd){
        nd.getPre().setNext(nd.getNext());
        if(nd.getNext()!=null){
            nd.getNext().setPre(nd.getPre());
        }else{
            this.tail = nd.getPre();
        }
    }
    //remove tail in level 1 and put to level 2
    private void removeTail(){
        //Write to file
        try {
            FileOutputStream fileOut = new FileOutputStream(dir+this.tail.getKey());
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(this.tail.getValue());
            objectOut.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        String tailKey = this.tail.getKey();
        this.tail = this.tail.getPre();
        this.tail.setNext(null);
        this.M1.remove(tailKey);
        this.S1.add(tailKey);
        this.count --;
    }
    //get file from level 2 and delete the file
    private Object grabFromFile(String key){
        Object valuesOut = null;
        try {
            FileInputStream fileIn = new FileInputStream(this.dir+key);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            Object obj = objectIn.readObject();
            objectIn.close();

            CacheNodeLevel1 nd = new CacheNodeLevel1(key,obj,null,null);
            addToFront(nd);
            valuesOut = nd.getValue();
            //then remove the file
            Files.deleteIfExists(Paths.get(this.dir+key));
            //remove from set, S1 and add to map, M1
            this.S1.remove(key);
            this.M1.put(key,nd);
            this.count++;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return valuesOut;
    }


    public Map<String, CacheNodeLevel1> getM1() {
        return M1;
    }
    public Set<String> getS1() {
        return S1;
    }
    public int getCount() {
        return count;
    }
}