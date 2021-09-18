package com.pds.telegrambot;

public class ProcessFailureException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public ProcessFailureException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
