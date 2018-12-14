package com.marcelo.brewer.session;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.marcelo.brewer.model.Cerveja;
import com.marcelo.brewer.model.ItemVenda;

@SessionScope
@Component
public class TabelasItensSession {
	
	Set<TabelaItensVenda> tabelas = new HashSet<TabelaItensVenda>();

	public void adicionarItem(String uuid, Cerveja cerveja, int quantidade) {
		TabelaItensVenda tb = buscarTabelaPorUuid(uuid);
		
		tb.adicionarItem(cerveja, quantidade);
		tabelas.add(tb);
	}

	private TabelaItensVenda buscarTabelaPorUuid(String uuid) {
		TabelaItensVenda tb = tabelas.stream()
				.filter(t -> t.getUuid().equals(uuid))
				.findAny().orElse(new TabelaItensVenda(uuid));
		return tb;
	}

	public void alterarQuantidadeItens(String uuid, Cerveja cerveja, Integer quantidade) {
		TabelaItensVenda tb = buscarTabelaPorUuid(uuid);
		tb.alterarQuantidadeItens(cerveja, quantidade);		
	}

	public void excluirItem(String uuid, Cerveja cerveja) {
		TabelaItensVenda tb = buscarTabelaPorUuid(uuid);
		tb.excluirItem(cerveja);
	}
	public Object getValorTotal(String uuid) {
		
		return buscarTabelaPorUuid(uuid).getValorTotal(); 
	}

	public List<ItemVenda> getItens(String uuid) {
		TabelaItensVenda tb = buscarTabelaPorUuid(uuid);
		return tb.getItens();
	}

	
}
