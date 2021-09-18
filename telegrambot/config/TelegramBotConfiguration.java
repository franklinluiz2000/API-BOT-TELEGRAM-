package com.pds.telegrambot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.type.AnnotationMetadata;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.pds.telegrambot.TelegramBeanProcessor;
import com.pds.telegrambot.TelegramBotService;
import com.pds.telegrambot.state.StateManager;

@Slf4j
public class TelegramBotConfiguration implements ImportAware {

    @Bean
    public TelegramBeanProcessor telegramBeanProcessor(TelegramBotService telegramBotService) {
        return new TelegramBeanProcessor(telegramBotService);
    }

    @Bean
    public TelegramBotService telegramBotService(TelegramBotBuilder telegramBotBuilder) throws TelegramApiException {
        ApiContextInitializer.init();
        return new TelegramBotService(telegramBotBuilder);
    }

    public void setImportMetadata(AnnotationMetadata importMetadata) {
    }
}
