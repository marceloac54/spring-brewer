package com.marcelo.brewer.service.exception;

public class CpfCnpjClienteJaCadastrado extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CpfCnpjClienteJaCadastrado(String msg) {
		super(msg);
	}
}
