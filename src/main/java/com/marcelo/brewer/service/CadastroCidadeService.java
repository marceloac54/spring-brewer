package com.marcelo.brewer.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.marcelo.brewer.model.Cidade;
import com.marcelo.brewer.repository.Cidades;
import com.marcelo.brewer.service.exception.NomeCidadeJaCadastrado;

@Service
public class CadastroCidadeService {
	
	@Autowired
	private Cidades cidades;
	
	@Transactional
	public Cidade salvar(Cidade cidade) {
		
		Optional<Cidade> optional = cidades.findByNomeAndEstado(cidade.getNome(), cidade.getEstado());
		
		if(optional.isPresent()) {
			throw new NomeCidadeJaCadastrado("Nome da cidade já cadastrada");
		}
		
		return cidades.save(cidade);
	}
	
}
