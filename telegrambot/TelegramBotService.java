package com.pds.telegrambot;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.meta.generics.WebhookBot;

import com.pds.telegrambot.annotation.TelegramProcessor;
import com.pds.telegrambot.command.Command;
import com.pds.telegrambot.command.CommandManager;
import com.pds.telegrambot.config.TelegramBotBuilder;
import com.pds.telegrambot.state.State;
import com.pds.telegrambot.state.StateManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TelegramBotService {

    private String username;
    private String token;
    private StateManager state_manager;
    private CommandManager command_manager;

    private TelegramBotsApi api;

    private Executor botExecutor;

    private DefaultAbsSender client;

    public TelegramBotService(TelegramBotBuilder botBuilder) throws TelegramApiException {
        this.username = botBuilder.getUsername();
        this.token = botBuilder.getToken();
        
        this.command_manager = new CommandManager();
        this.state_manager = botBuilder.getState_manager();
        
        this.api = new TelegramBotsApi();
        try {
            if (botBuilder.getType() == TelegramBotBuilder.BotType.LONG_POLLING) {
                this.botExecutor = Executors.newFixedThreadPool(100);

                this.client = new TelegramBotLongPollingImpl();
                api.registerBot((LongPollingBot) this.client);
            } else if (botBuilder.getType() == TelegramBotBuilder.BotType.WEBHOOK) {
                this.client = new TelegramBotWebHooksImpl();
                api.registerBot((WebhookBot) this.client);
            }
        } catch (TelegramApiException e) {
            log.error("Error while creating TelegramBots: {}", e.getMessage());
        }
    }

    public void updateLongPolling(Update update) {
        CompletableFuture.runAsync(() -> {
        	updateProcess(update);
        }, botExecutor);
    }

    public void updateProcess(Update update) {
        
        if (update.getMessage() != null) {
        	String message = update.getMessage().getText();
        	
        	if(CommandManager.isCommand(message)) {
        		String targetCommand = CommandManager.stractCommand(message);
        		for(Command command: this.command_manager.getCommandsActions(targetCommand))
            	{
        			Object[] arguments = makeArgumentList(command.getMethod(), update);
        			this.command_manager.runCommand(command, this.client, arguments);
    				if(command.isRun_only()) return;
            	}
        	}
        	else {
        		String user_id = update.getMessage().getFrom().getId().toString();
        		String chat_id = update.getMessage().getChat().getId().toString();
        		for(State state : this.state_manager.get_states(user_id, chat_id)) {
            		Object[] arguments = makeArgumentList(state.getMethod(), update);
    				this.state_manager.runState(state, user_id, chat_id, this.client, arguments);
    				if(state.isRun_only()) return;
            	}
        	}
        	
		}
    }

    private Object[] makeArgumentList(Method method, Update update) {
        Type[] commandArguments = method.getGenericParameterTypes();
        List<Object> arguments = new ArrayList<>(commandArguments.length);
        for (Type type : commandArguments) {
            if (type.equals(Update.class)) {
                arguments.add(update);
            } else if (type.equals(TelegramBotService.class)) {
                arguments.add(this);
            } else if (type.equals(DefaultAbsSender.class)) {
                arguments.add(this.client);
            } else if (type.equals(Message.class)) {
                arguments.add(update.getMessage());
            } else if (type.equals(User.class)) {
                arguments.add(update.getMessage().getFrom());
            } else {
            	arguments.add(update.getMessage().getText());
            }
        }

        return arguments.toArray(new Object[arguments.size()]);
    }

    public DefaultAbsSender getClient() {
        return this.client;
    }

    public void addHandler(Object bean, Method method) throws Exception {
    	TelegramProcessor processor = method.getAnnotation(TelegramProcessor.class);
    	
    	for(String state : processor.from_states())
    	{
    		this.state_manager.register_state(state, processor.success(), processor.fail(), processor.run_only(), method, bean, processor.state_type());
    	}
    	
    	for(String command : processor.commands()) {
    		this.command_manager.registerCommand(command, processor.run_only(), method, bean);
    	}
 
    }

    public class TelegramBotLongPollingImpl extends TelegramLongPollingBot {

        @Override
        public void onUpdateReceived(Update update) {
            TelegramBotService.this.updateLongPolling(update);
        }

        @Override
        public String getBotUsername() {
            return username;
        }

        @Override
        public String getBotToken() {
            return token;
        }
    }

    public class TelegramBotWebHooksImpl extends TelegramWebhookBot {

        @Override
        public BotApiMethod onWebhookUpdateReceived(Update update) {
            TelegramBotService.this.updateProcess(update);
            return null;
        }

        @Override
        public String getBotUsername() {
            return username;
        }

        @Override
        public String getBotToken() {
            return token;
        }

		@Override
		public String getBotPath() {
			return null;
		}

    }

}
