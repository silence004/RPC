package com.silence004.protocol.Enum;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum ProtocolMessageSerializerEnum {
    JDK(0,"jdk"),
    JSON(1,"json"),
    KRYO(2,"kryo"),
    HESSIAN(3,"hessian");

    private final int key;
    private final String value;

    ProtocolMessageSerializerEnum(int key,String value){
        this.key=key;
        this.value=value;
    }

    /**
     * 获取值列表
     * @return
     */
    public static List<String> getValues(){
        return Arrays.stream(values())
                .map(item->item.value)
                .collect(Collectors.toList());
    }

    /**
     * 根据key获取枚举
     * @param key
     * @return
     */
    public static ProtocolMessageSerializerEnum getEnumByKey(int key){
        for (ProtocolMessageSerializerEnum con : ProtocolMessageSerializerEnum.values()){
            if (con.key == key){
                return con;
            }
        }
        return null;
    }

    /**
     *根据value获取枚举
     * @param value
     * @return
     */
    public static ProtocolMessageSerializerEnum getEnumByValue(String value){
        if(ObjectUtil.isEmpty(value)){
            return null;
        }

        for (ProtocolMessageSerializerEnum con:
             ProtocolMessageSerializerEnum.values()) {
            if(con.value.equals(value)){
                return con;
            }
        }
        return null;
    }
}
