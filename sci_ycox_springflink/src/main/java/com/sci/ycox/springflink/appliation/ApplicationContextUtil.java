package com.sci.ycox.springflink.appliation;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @Description  动态加载Spring容器的类
 * @Author xyb
 * @Date 2019-07-23 10:23:47
 * @Version 1.0.0
 */
@Component
public class ApplicationContextUtil implements ApplicationContextAware, Serializable {

    /** 上下文 */
    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (context == null) {
            context = applicationContext;
        }
    }

    public static ApplicationContext getContext() {
        return context;
    }

    public static <T> T getBean (Class<T> clazz){
        return  context.getBean(clazz);
    }

    public static Object getBean (String name){
        return  context.getBean(name);
    }

    public static <T> T getBean (String name, Class<T> clazz){
        return  context.getBean(name, clazz);
    }
}
