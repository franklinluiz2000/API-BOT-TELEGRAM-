package com.pds.telegrambot.config;

import com.pds.telegrambot.state.StateManager;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TelegramBotBuilder {

    public enum BotType {
        LONG_POLLING, WEBHOOK
    }

    private BotType type = BotType.LONG_POLLING;

    private String username;
    private String token;
    private StateManager state_manager;

    public TelegramBotBuilder(String username, String token, StateManager state_manager) {
        this.username = username;
        this.token = token;
        this.state_manager = state_manager;
    }

    @Override
    public String toString() {
        return "{" +
            "type=" + type +
            ", username='" + username + '\'' +
            ", token='" + token + '\'' +
            '}';
    }
}
