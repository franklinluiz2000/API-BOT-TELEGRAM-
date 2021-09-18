package com.pds.telegrambot.state;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.pds.telegrambot.ProcessFailureException;

public abstract class State {
	
	protected String waiting_for;
	protected String success;
	protected String fail_for;
	protected boolean run_only;
	protected Method method;
	protected Object bean;
	
	public State(String waiting_for, String success, String fail_for, boolean run_only, Method method, Object bean) {
		this.waiting_for = waiting_for;
		this.success = success;
		this.fail_for = fail_for;
		this.run_only = run_only;
		this.method = method;
		this.bean = bean;
	}
	
	public String getWaiting_for() {
		return waiting_for;
	}

	public String getSuccess() {
		return success;
	}

	public String getFail_for() {
		return fail_for;
	}
	
	public boolean isRun_only() {
		return run_only;
	}

	public Object getBean() {
		return bean;
	}

	public Method getMethod() {
		return method;
	}

	public void invokeMethod(StateManager stateManager, DefaultAbsSender client, String user_id, String chat_id, Object[] arguments) {
		try {
			SendMessage sendMessage = (SendMessage) this.getMethod().invoke(this.getBean(), arguments);
			client.execute(sendMessage);
			this.postProcessing(stateManager, user_id, chat_id);
		} catch (TelegramApiException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ProcessFailureException e) {
			//e.printStackTrace();
			this.errorProcessing(stateManager, user_id, chat_id);
		}
	}
	
	public void errorProcessing(StateManager stateManager, String user_id, String chat_id) {}

	public void postProcessing(StateManager stateManager, String user_id, String chat_id) {}

	public boolean matches(String m) {
		return this.waiting_for.equalsIgnoreCase(m);
	}
}
