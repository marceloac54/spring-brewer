package com.marcelo.brewer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.marcelo.brewer.model.Venda;
import com.marcelo.brewer.repository.helper.venda.VendasQueries;

@Repository
public interface Vendas extends JpaRepository<Venda, Long>, VendasQueries{

}
