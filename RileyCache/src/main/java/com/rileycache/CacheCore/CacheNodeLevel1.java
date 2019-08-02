package com.rileycache.CacheCore;

public class CacheNodeLevel1 {
    private String key;
    private Object value;
    private CacheNodeLevel1 next;
    private CacheNodeLevel1 pre;

    public CacheNodeLevel1(String key, Object value, CacheNodeLevel1 pre, CacheNodeLevel1 next){
        this.key = key;
        this.value = value;
        this.pre = pre;
        this.next = next;
    }

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public Object getValue() {
        return value;
    }
    public void setValue(Object value) {
        this.value = value;
    }
    public CacheNodeLevel1 getNext() {
        return next;
    }
    public void setNext(CacheNodeLevel1 next) {
        this.next = next;
    }
    public CacheNodeLevel1 getPre() {
        return pre;
    }
    public void setPre(CacheNodeLevel1 pre) {
        this.pre = pre;
    }
}
