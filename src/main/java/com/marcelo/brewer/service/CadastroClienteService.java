package com.marcelo.brewer.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.marcelo.brewer.model.Cliente;
import com.marcelo.brewer.repository.Clientes;
import com.marcelo.brewer.service.exception.CpfCnpjClienteJaCadastrado;

@Service
public class CadastroClienteService {
	
	@Autowired
	private Clientes clientes;
	
	@Transactional
	public void salvar(Cliente cliente) {
		Optional<Cliente> clienteExistente = clientes.findByCpfOuCnpj(cliente.getCpfCnpjSemFormatacao());
		
		if (clienteExistente.isPresent()) {
			throw new CpfCnpjClienteJaCadastrado("CPF/CNPJ já cadastrado");
		}
		
		clientes.save(cliente);
	}

}
