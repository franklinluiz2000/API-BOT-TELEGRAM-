package com.pds.telegrambot.state;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.telegram.telegrambots.bots.DefaultAbsSender;

import com.pds.telegrambot.StateTypes;

public class StateManager {
	
	private ArrayList<State> handlingStates;
	Map<String, Map<String, String>> memoryState;
	
	public StateManager() {
		this.handlingStates = new ArrayList<State>();
		this.memoryState = new HashMap<String, Map<String, String>>();
	}
	
	public void register_state(String waiting_for, String success, String fail_for, boolean run_only, Method method, Object bean, String state_type) throws Exception {
		State state;
		if( state_type.equalsIgnoreCase(StateTypes.Custom) ) {
			state = new CustomState(waiting_for, success, fail_for, run_only, method, bean);
		} else if(state_type.equalsIgnoreCase(StateTypes.Reset)) {
			state = new ResetState(waiting_for, success, fail_for, run_only, method, bean);
		} else if(state_type.equalsIgnoreCase(StateTypes.Keep)) {
			state = new KeepState(waiting_for, success, fail_for, run_only, method, bean);
		} else {
			throw new Exception("Inválid Telegram Type");
		}
		handlingStates.add(state);
	}
	
	public ArrayList<State> get_states(String user_id, String chat_id) {
		String current_state = this.getCurrentState(user_id, chat_id);
		ArrayList<State> states = new ArrayList<State>();
		
		for(State state : handlingStates) {
			if(state.matches(current_state)) {
				states.add(state);
			}
		}
		return states;
	}
	
	public void runState(State state, String user_id, String chat_id, DefaultAbsSender client, Object[] arguments) {
		state.invokeMethod(this, client, user_id, chat_id, arguments);
	}
	
	public String getCurrentState(String user_id, String chat_id) {
		if(this.memoryState.get(user_id)==null) {
			this.memoryState.put(user_id, new HashMap<String, String>());
			this.memoryState.get(user_id).put(user_id, "");
		}
		return this.memoryState.get(user_id).get(chat_id).toString();
	}
	
	public void setCurrentState(String user_id, String chat_id, String targetState) {
		if(this.memoryState.get(user_id)==null) {
			this.memoryState.put(user_id, new HashMap<String, String>());
		}
		Map<String, String> chat_state = this.memoryState.get(user_id);
		chat_state.put(chat_id, targetState);
		this.memoryState.put(user_id, chat_state);
	}

}
