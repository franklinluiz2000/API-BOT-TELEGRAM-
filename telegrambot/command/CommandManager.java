package com.pds.telegrambot.command;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.telegram.telegrambots.bots.DefaultAbsSender;

public class CommandManager {

	private ArrayList<Command> handling_commands;
	
	public CommandManager() {
		this.handling_commands = new ArrayList<Command>();
	}
	
	public void registerCommand(String command_name, boolean run_only, Method method, Object bean) {
		Command command = new Command(command_name, run_only, method, bean);
		handling_commands.add(command);
	}
	
	public void runCommand(Command command, DefaultAbsSender client, Object[] arguments) {
		command.invokeMethod(client, arguments);
	}
	
	public static boolean isCommand(String string_command) {
		return string_command.startsWith("/");
	}
	
	public static String stractCommand(String string_command) {
		return string_command.split(" ")[0].split("@")[0].substring(1);
	}

	public ArrayList<Command> getCommandsActions(String targetCommand) {
		ArrayList<Command> commands = new ArrayList<Command>();
		for(Command command : this.handling_commands)
		{
			if(command.matches(targetCommand)) {
				commands.add(command);
			}
		}
		return commands;
	}
	
}
