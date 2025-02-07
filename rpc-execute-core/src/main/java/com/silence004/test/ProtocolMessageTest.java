package com.silence004.test;

import cn.hutool.core.util.IdUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.silence004.constant.RpcConstant;
import com.silence004.protocol.Enum.ProtocolMessageSerializerEnum;
import com.silence004.protocol.Enum.ProtocolMessageStatusEnum;
import com.silence004.protocol.Enum.ProtocolMessageTypeEnum;
import com.silence004.protocol.ProtocolMessage;
import com.silence004.protocol.ProtocolMessageDecoder;
import com.silence004.protocol.ProtocolMessageEncoder;
import com.silence004.protocol.constant.ProtocolConstant;
import com.silence004.request.RpcRequest;
import io.vertx.core.buffer.Buffer;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class ProtocolMessageTest {
    @Test
    public void TestEncodeAndDecode() throws IOException {
        //消息构造
        ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
        header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
        header.setSerializer((byte) ProtocolMessageSerializerEnum.JDK.getKey());
        header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
        header.setStatus((byte) ProtocolMessageStatusEnum.OK.getValue());
        header.setRequestId(IdUtil.getSnowflakeNextId());
        header.setBodyLength(0);
        RpcRequest request = new RpcRequest();
        request.setServiceName("testservice");
        request.setMethodName("testmethod");
        request.setServiceVersion(RpcConstant.DEFAULT_SERVE_VERSION);
        request.setParametersTypes(new Class[]{System.class});
        request.setArgs(new Object[]{"aaa","bbb"});

        protocolMessage.setHeader(header);
        protocolMessage.setBody(request);

        Buffer encodeBuffer = ProtocolMessageEncoder.encode(protocolMessage);
        ProtocolMessage<?> message = ProtocolMessageDecoder.Decoder(encodeBuffer);
        Assert.assertNotNull(message);
    }
}
