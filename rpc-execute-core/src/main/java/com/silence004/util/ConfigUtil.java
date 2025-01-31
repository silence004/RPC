package com.silence004.util;


import cn.hutool.setting.dialect.Props;
import org.apache.commons.lang3.StringUtils;


public class ConfigUtil {
    /**
     * 加载配置对象
     * @param tClass
     * @param prefix
     * @param <T>
     * @return
     */
    public static <T> T loadConfig(Class<T> tClass,String prefix){
        return loadConfig(tClass,prefix,"");
    }

    /**
     * 加载配置对象，允许区分环境
     * @param tClass
     * @param prefix
     * @param environment
     * @param <T>
     * @return
     */
    private static <T> T loadConfig(Class<T> tClass, String prefix, String environment) {
        StringBuilder configFileBUilder=new StringBuilder("application");
        if(!StringUtils.isBlank(environment)){
            configFileBUilder.append("-").append(environment);
        }
        configFileBUilder.append(".properties");
        Props props = new Props(configFileBUilder.toString());
        return props.toBean(tClass,prefix);

    }
}
