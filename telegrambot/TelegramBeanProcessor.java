package com.pds.telegrambot;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;

import com.pds.telegrambot.annotation.TelegramBot;
import com.pds.telegrambot.annotation.TelegramProcessor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TelegramBeanProcessor implements BeanPostProcessor, Ordered {

    private TelegramBotService telegramBotService;

    private Map<String, Class> botControllerMap = new HashMap<>();

    public TelegramBeanProcessor(TelegramBotService telegramBotService) {
        this.telegramBotService = telegramBotService;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(TelegramBot.class)) {
            botControllerMap.put(beanName, bean.getClass());
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(!botControllerMap.containsKey(beanName)) return bean;

        Class original = botControllerMap.get(beanName);

        Arrays.stream(original.getDeclaredMethods())
            .filter(method -> method.isAnnotationPresent(TelegramProcessor.class))
            .forEach((Method method) -> {
				try {
					bindController(bean, method);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});

        return bean;
    }

    private void bindController(Object bean, Method method) throws Exception {
        if (method.getAnnotation(TelegramProcessor.class) != null) {
            this.telegramBotService.addHandler(bean, method);
        }
    }

    @Override
    public int getOrder() {
        return 10;
    }
}
