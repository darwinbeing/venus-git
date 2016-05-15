package com.venus.service.market;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by erix-mac on 15/8/5.
 */
public class BeanContext {

    private final static String SPRING_APP_XML = "/spring-app.xml";

    public static <T> T getBean(Class<T> clazz){
        ApplicationContext appContext = new ClassPathXmlApplicationContext(SPRING_APP_XML);

        return appContext.getBean(clazz);
    }
}
