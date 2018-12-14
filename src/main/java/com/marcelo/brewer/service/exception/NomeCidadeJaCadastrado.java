package com.marcelo.brewer.service.exception;

public class NomeCidadeJaCadastrado extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	
	public NomeCidadeJaCadastrado(String message) {
		super(message);
	}
	
}
