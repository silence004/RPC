package com.silence004.service.tcp;

import com.silence004.protocol.constant.ProtocolConstant;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;


public class TcpBufferHandlerWrapper implements Handler<Buffer> {
    private final RecordParser recordParser;


    public TcpBufferHandlerWrapper(Handler<Buffer> bufferHandler){
        recordParser=initRecordParser(bufferHandler);
    }

    @Override
    public void handle(Buffer buffer) {
        recordParser.handle(buffer);
    }

    private RecordParser initRecordParser(Handler<Buffer> bufferHandler) {
        //构造parser
        RecordParser parser=RecordParser.newFixed(ProtocolConstant.MESSAGE_HEADER_LENGTH);

        parser.setOutput(new Handler<Buffer>() {
            int size=-1;
            Buffer resultBuffer=Buffer.buffer();

            @Override
            public void handle(Buffer buffer) {
                if(-1 == size){
                    //读取消息体
                    size  = buffer.getInt(13);
                    parser.fixedSizeMode(size);
                    //写入消息头到结果缓冲区
                    resultBuffer.appendBuffer(buffer);
                }else{
                    //写入消息体
                    resultBuffer.appendBuffer(buffer);
                    //buffer已经完整，处理buffer
                    bufferHandler.handle(resultBuffer);
                    parser.fixedSizeMode(ProtocolConstant.MESSAGE_HEADER_LENGTH);
                    size=-1;
                    resultBuffer=Buffer.buffer();

                }
            }
        });
        return parser;
    }


}
