package com.pds.telegrambot.annotation;

import org.springframework.context.annotation.Import;

import com.pds.telegrambot.config.TelegramBotConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(TelegramBotConfiguration.class)
public @interface EnableTelegramBot {
}
