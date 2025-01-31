package com.silence004.serialize.impl;

import com.silence004.serialize.Serializer;

import java.io.*;

public class JdkSerializer implements Serializer {
    @Override
    public <T> byte[] serialize(T object) throws IOException {
        System.out.println("jdk serializer is used");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(object);
        objectOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public <T> T deSerialize(byte[] bytes, Class<T> type) throws IOException {
        System.out.println("jdk serializer is used");
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        try(ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)){
            return (T)objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            throw  new RuntimeException();
        }
    }
}
