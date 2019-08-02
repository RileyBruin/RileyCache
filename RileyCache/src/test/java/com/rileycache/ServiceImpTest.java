package com.rileycache;

import com.rileycache.Proxy.Service;
import com.rileycache.Proxy.ServiceImp;
import org.junit.Test;

import java.io.File;
import static org.junit.Assert.*;

public class ServiceImpTest {
    final static String dir= System.getProperty("user.dir") + "/level2Storage/";

    /*
     make sure the object in level 1 cache will be update if use "put method" with existed key
      make sure nothing flow into level 2 before level 1 is full
     */
    @Test
    public void testcase1() {
        System.out.println("==testcase1==");
        cleanfolder(dir);
        File file = new File(dir);
        Service service = new ServiceImp(3);
        service.put("test01",new String("111"));
        assertEquals(0,file.list().length);
        service.put("test02",new String("222"));
        assertEquals(0,file.list().length);
        service.put("test03",new Integer(333));
        assertEquals(0,file.list().length);
        service.put("test01",new String("changed!"));
        assertEquals("changed!",service.get("test01"));
        assertEquals(0,file.list().length);
    }

    /*
     make sure the object put to level 2 cache will be update if use "put method" with existed key
     make sure cache level 1 work well with capacity=1
     make sure test01 will be push to level 2 when level 1 is full
     make sure test02 will be push to level 2 when test01 being taken back to level 1
     make sure test01 will be removed from level 2
    */
    @Test
    public void testcase2() {
        System.out.println("==testcase2==");
        cleanfolder(dir);
        Service service = new ServiceImp(1);
        service.put("test01",new String("111"));
        service.put("test02",new String("222"));
        File checkFile = new File(dir+"test01");
        assertTrue(checkFile.exists());
        service.put("test01",new String("changed!"));
        assertEquals("changed!",service.get("test01"));
        assertFalse(checkFile.exists());
        checkFile = new File(dir+"test02");
        assertTrue(checkFile.exists());
    }

    /*
    make sure get method will use the default implementation
    if the key is not found in cache
    */
    @Test
    public void testcase3() {
        System.out.println("==testcase3==");
        cleanfolder(dir);
        Service service = new ServiceImp(2);
        assertEquals("value of 01",service.get("01"));
        service.put("01",new Integer(111));
        assertEquals("value of 02",service.get("02"));
        service.put("02",new Integer(222));
        assertEquals("value of 03",service.get("03"));
    }

    public void cleanfolder(String directory){
        File index = new File(directory);
        String[]entries = index.list();
        for(String s: entries){
            File currentFile = new File(index.getPath(),s);
            currentFile.delete();
        }
    }

}