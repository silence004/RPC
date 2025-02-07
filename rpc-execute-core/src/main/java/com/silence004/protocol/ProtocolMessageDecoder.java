package com.silence004.protocol;

import cn.hutool.socket.protocol.Protocol;
import com.silence004.protocol.Enum.ProtocolMessageSerializerEnum;
import com.silence004.protocol.Enum.ProtocolMessageTypeEnum;
import com.silence004.protocol.constant.ProtocolConstant;
import com.silence004.response.RpcResponse;
import com.silence004.serialize.Serializer;
import com.silence004.serialize.SerializerFactory;
import com.silence004.request.RpcRequest;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

public class ProtocolMessageDecoder {

    public static ProtocolMessage<?> Decoder(Buffer buffer) throws IOException {
        //读取指定位置的数据
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        byte magic = buffer.getByte(0);
        if(magic!= ProtocolConstant.PROTOCOL_MAGIC){
            throw new RuntimeException("消息 magic 非法");
        }

        header.setMagic(magic);
        header.setVersion(buffer.getByte(1));
        header.setSerializer(buffer.getByte(2));
        header.setType(buffer.getByte(3));
        header.setStatus(buffer.getByte(4));
        header.setRequestId(buffer.getLong(5));
        header.setBodyLength(buffer.getInt(13));

        //解决沾包问题，只读指定长度的数据
        byte[] bodyBytes= buffer.getBytes(17,17 + header.getBodyLength());

        //解析消息体
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        if(serializerEnum==null) {
            throw new RuntimeException("序列化协议不存在");
        }

        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
        ProtocolMessageTypeEnum messageTypeEnum = ProtocolMessageTypeEnum.getEnumByType(header.getType());
        if(messageTypeEnum==null){
            throw new RuntimeException("序列化消息的类型不存在");
        }
        switch (messageTypeEnum){
            case REQUEST:
                RpcRequest request = serializer.deSerialize(bodyBytes,RpcRequest.class);
                return new ProtocolMessage(header,request);
            case RESPONSE:
                RpcResponse response=serializer.deSerialize(bodyBytes, RpcResponse.class);
                return new ProtocolMessage(header,response);
            case HEART_BEAT:
            case OTHER:
            default:
                throw new RuntimeException("暂不支持该消息类型");

        }

    }
}
