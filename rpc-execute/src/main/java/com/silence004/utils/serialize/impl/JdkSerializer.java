package com.silence004.utils.serialize.impl;

import com.silence004.utils.serialize.Serializer;
import java.io.*;

public class JdkSerializer implements Serializer {
    @Override
    public <T> byte[] serialize(T object) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(object);
        objectOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public <T> T deSerialize(byte[] bytes, Class<T> type) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        try(ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)){
            return (T)objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            throw  new RuntimeException();
        }
    }
}
