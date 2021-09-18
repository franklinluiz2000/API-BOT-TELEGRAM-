package com.pds.telegrambot.state;

import java.lang.reflect.Method;

public class CustomState extends State {

	public CustomState(String waiting_for, String success, String fail_for, boolean run_only, Method method, Object bean) {
		super(waiting_for, success, fail_for, run_only, method, bean);
	}
	
	public void postProcessing(StateManager stateManager, String user_id, String chat_id) {
		stateManager.setCurrentState(user_id, chat_id, super.success);
	}
	
	public void errorProcessing(StateManager stateManager, String user_id, String chat_id) {
		stateManager.setCurrentState(user_id, chat_id, super.fail_for);
	}


}
