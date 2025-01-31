package com.silence004.test;

import com.silence004.serialize.Serializer;

import java.util.Iterator;
import java.util.ServiceLoader;

public class SPITest {
    public static void main(String[] args) {
        Serializer serializer=null;
        ServiceLoader<Serializer> serializerLoader= ServiceLoader.load(Serializer.class);
        Iterator<Serializer> iterator = serializerLoader.iterator();
        if(iterator.hasNext()){
            serializer=iterator.next();
        }
        System.out.println(serializer);



    }
}
