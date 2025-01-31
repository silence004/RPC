package com.silence004.protocol.Enum;


import lombok.Getter;

@Getter
public enum ProtocolMessageStatusEnum {

    OK("ok",20),
    BAD_REQUEST("badRequest",40),
    BAD_RESPONSE("badResponse",60);

    private final String text;
    private int value;

    ProtocolMessageStatusEnum(String text,int value){
        this.text=text;
        this.value=value;
    }

    /**
     * 根据value获取枚举
     * @param value
     * @return
     */
    public static ProtocolMessageStatusEnum getEnumByValue(int value){
        for (ProtocolMessageStatusEnum con:
             ProtocolMessageStatusEnum.values()) {
            if(con.value==value){
                return con;
            }
        }
        return null;

    }



}
