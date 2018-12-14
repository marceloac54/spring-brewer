package com.marcelo.brewer.repository.helper.cidade;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.marcelo.brewer.model.Cidade;
import com.marcelo.brewer.repository.filter.CidadeFilter;

public interface CidadesQueries {
	
	public Page<Cidade> filtrar(CidadeFilter filtro, Pageable pageable);
	
}
