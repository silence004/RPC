package com.silence004.serialize.impl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.ByteBufferInput;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.silence004.serialize.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class KryoSerializer implements Serializer {

    /**
     * kryo是线程不安全的，使用本地线程保证每个线程的kryo独立
     */
    private static final ThreadLocal<Kryo> LOCAL_KRYO_Thread=ThreadLocal.withInitial(()->{
        Kryo kryo=new Kryo();
        kryo.setRegistrationRequired(false);
        return kryo;
    });
    @Override
    public <T> byte[] serialize(T object) throws IOException {
        System.out.println("kryo serializer is used");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);
        LOCAL_KRYO_Thread.get().writeObject(output,object);
        output.close();
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public <T> T deSerialize(byte[] bytes, Class<T> type) throws IOException {
        System.out.println("kryo serializer is used");
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Input input = new Input(byteArrayInputStream);
        T result = LOCAL_KRYO_Thread.get().readObject(input, type);
        input.close();
        return result;
    }
}
