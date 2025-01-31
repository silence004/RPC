package com.silence004.utils.serialize;

import java.io.IOException;

public interface Serializer {
    /**
     *
     * @param object
     * @param <T>
     * @return
     * @throws IOException
     */
    <T> byte[] serialize(T object) throws IOException;


    <T> T deSerialize(byte[] bytes,Class<T> type) throws IOException;

}
