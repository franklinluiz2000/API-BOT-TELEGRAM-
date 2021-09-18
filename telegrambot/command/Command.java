package com.pds.telegrambot.command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Command {
	
	private String command_name;
	private boolean run_only;
	private Method method;
	private Object bean;
	
	public Command(String command_name, boolean run_only, Method method, Object bean) {
		this.command_name = command_name;
		this.run_only = run_only;
		this.method = method;
		this.bean = bean;
	}
	
	public Method getMethod() {
		return method;
	}

	public Object getBean() {
		return bean;
	}

	public boolean isRun_only() {
		return run_only;
	}

	public void invokeMethod(DefaultAbsSender client, Object[] arguments) {
		try {
			SendMessage sendMessage = (SendMessage) this.method.invoke(this.bean, arguments);
			client.execute(sendMessage);
		} catch (TelegramApiException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public boolean matches(String targetCommand) {
		return this.command_name.equalsIgnoreCase(targetCommand);
	}

}
