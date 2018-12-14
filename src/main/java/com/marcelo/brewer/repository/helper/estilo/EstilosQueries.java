package com.marcelo.brewer.repository.helper.estilo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.marcelo.brewer.model.Estilo;
import com.marcelo.brewer.repository.filter.EstiloFilter;

public interface EstilosQueries {
	public Page<Estilo> filtrar(EstiloFilter filtro, Pageable pageable);
}
