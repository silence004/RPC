package com.silence004.protocol;

import com.silence004.protocol.Enum.ProtocolMessageSerializerEnum;
import com.silence004.serialize.Serializer;
import com.silence004.serialize.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

public class ProtocolMessageEncoder {


    public static Buffer encode(ProtocolMessage<?> protocolMessage) throws IOException {
        if(protocolMessage==null||protocolMessage.getHeader()==null){
            return Buffer.buffer();
        }
       ProtocolMessage.Header header=protocolMessage.getHeader();
        //向缓冲区写入字节数据
        Buffer buffer=Buffer.buffer();
        buffer.appendByte(header.getMagic());
        buffer.appendByte(header.getVersion());
        buffer.appendByte(header.getSerializer());
        buffer.appendByte(header.getType());
        buffer.appendByte(header.getStatus());
        buffer.appendLong(header.getRequestId());

        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        if(serializerEnum==null){
            throw new RuntimeException("序列化协议不存在");
        }
        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
        byte[] bodyBytes = serializer.serialize(protocolMessage.getBody());
        //写入body的长度和内容
        buffer.appendInt(bodyBytes.length);
        buffer.appendBytes(bodyBytes);
        return buffer;
    }
}
