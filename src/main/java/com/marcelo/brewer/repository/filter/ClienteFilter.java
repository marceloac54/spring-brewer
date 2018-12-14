package com.marcelo.brewer.repository.filter;

import com.marcelo.brewer.model.TipoPessoa;

public class ClienteFilter {
	
	private String nome;
	
	private String cpfOuCnpj;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCpfOuCnpj() {
		return cpfOuCnpj;
	}

	public void setCpfOuCnpj(String cpfOuCnpj) {
		this.cpfOuCnpj = cpfOuCnpj;
	}

	public Object getCpfOuCnpjSemFormatacao() {
		return TipoPessoa.removerFormatacaoCpfCnpj(this.cpfOuCnpj);
	}
	
	

}
