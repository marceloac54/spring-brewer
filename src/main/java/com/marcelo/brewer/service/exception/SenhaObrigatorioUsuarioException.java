package com.marcelo.brewer.service.exception;

public class SenhaObrigatorioUsuarioException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public SenhaObrigatorioUsuarioException(String message) {
		super(message);
	}

}
