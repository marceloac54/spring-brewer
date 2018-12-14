package com.marcelo.brewer.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.marcelo.brewer.service.CadastroCervejaService;
import com.marcelo.brewer.storage.FotoStorage;

@Configuration
@ComponentScan( basePackageClasses = { CadastroCervejaService.class, FotoStorage.class } )
public class ServiceConfig {
	
}
