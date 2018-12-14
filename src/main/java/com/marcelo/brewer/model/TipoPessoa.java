package com.marcelo.brewer.model;

import com.marcelo.brewer.model.validation.group.CnpjGroups;
import com.marcelo.brewer.model.validation.group.CpfGroups;

public enum TipoPessoa {
	FISICA("Física","CPF","000.000.000-00", CpfGroups.class) {
		@Override
		public String formatar(String cpfOuCnpj) {
			return cpfOuCnpj.replaceAll("(\\d{3})(\\d{3})(\\d{3})", "$1.$2.$3-");
		}
	},
	JURIDICA("Jurídica", "CNPJ", "00.000.000/0000-00", CnpjGroups.class) {
		@Override
		public String formatar(String cpfOuCnpj) {
			return cpfOuCnpj.replaceAll("(\\d{2})(\\d{3})(\\d{3})(\\d{4})", "$1.$2.$3/$4-");
		}
	};
	
	private String descricao;
	private String documento;
	private String mascara;
	private Class<?> group;
	
	TipoPessoa(String descicao, String documento, String mascara, Class<?> group) {
		this.descricao = descicao;
		this.documento = documento;
		this.mascara = mascara;
		this.group = group;
	}
	
    public abstract String formatar(String cpfOuCnpj);
	
	public String getDescricao() {
		return descricao;
	}

	public String getDocumento() {
		return documento;
	}

	public String getMascara() {
		return mascara;
	}

	public Class<?> getGroup() {
		return group;
	}
	
	public static String removerFormatacaoCpfCnpj(String cpfCnpj) {
		return cpfCnpj.replaceAll("\\.|-|/", "");
	}
}
