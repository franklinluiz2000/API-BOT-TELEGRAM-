package com.pds.telegrambot;

import java.lang.reflect.Method;

import com.pds.telegrambot.annotation.TelegramProcessor;

import lombok.Getter;

@Getter
public class TelegramHandler {
    private Object bean;
    private Method method;
    private TelegramProcessor telegramProcessor;

    public TelegramHandler(Object bean, Method method, TelegramProcessor telegramProcessor) {
        this.bean = bean;
        this.method = method;
        this.telegramProcessor = telegramProcessor;
    }
}