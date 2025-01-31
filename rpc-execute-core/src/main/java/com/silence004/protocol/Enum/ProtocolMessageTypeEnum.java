package com.silence004.protocol.Enum;

import lombok.Getter;

/**
 * 协议消息的类型枚举
 */
@Getter
public enum ProtocolMessageTypeEnum {
    REQUEST(0),
    RESPONSE(1),
    HEART_BEAT(2),
    OTHER(3);

    private final int key;

    ProtocolMessageTypeEnum(int key){
        this.key=key;
    }

    public static ProtocolMessageTypeEnum getEnumByType(int key){
        for (ProtocolMessageTypeEnum con: ProtocolMessageTypeEnum.values()) {
            if(con.key==key){
                return con;
            }
        }
        return null;
    }
}
