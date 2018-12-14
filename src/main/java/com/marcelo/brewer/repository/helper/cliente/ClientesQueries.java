package com.marcelo.brewer.repository.helper.cliente;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.marcelo.brewer.model.Cliente;
import com.marcelo.brewer.repository.filter.ClienteFilter;

public interface ClientesQueries {
	
	public Page<Cliente> filtrar(ClienteFilter filtro, Pageable pageable);
	
}
