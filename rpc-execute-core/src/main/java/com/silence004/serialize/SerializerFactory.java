package com.silence004.serialize;

import com.silence004.serialize.constant.SerislizerKeys;
import com.silence004.serialize.impl.HessianSerializer;
import com.silence004.serialize.impl.JdkSerializer;
import com.silence004.serialize.impl.JsonSerializer;
import com.silence004.serialize.impl.KryoSerializer;
import com.silence004.spi.SpiLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * 工厂模式获取序列化器对象
 */
public class SerializerFactory {
//    /**
//     * 单例模式，序列化器映射
//     */
//    private static final Map<String,Serializer> KEY_SERIALIZER_MAP=new HashMap<String,Serializer>(){{
//                put(SerislizerKeys.JDK,new JdkSerializer());
//                put(SerislizerKeys.JSON,new JsonSerializer());
//                put(SerislizerKeys.KRYO,new KryoSerializer());
//                put(SerislizerKeys.HESSIAN,new HessianSerializer());
//     }
//    };
    static {
        SpiLoader.load(Serializer.class);
    }


    /**
     * 默认序列化器
     */
    private static final Serializer DEFAULT_SERIALIZER=new JdkSerializer();

    /**
     * 获取实例
     */
    public static Serializer getInstance(String key){
        return SpiLoader.getInstance(Serializer.class,key);
    }
}
