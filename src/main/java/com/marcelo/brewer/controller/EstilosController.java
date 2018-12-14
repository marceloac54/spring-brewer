package com.marcelo.brewer.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.marcelo.brewer.controller.page.PageWrapper;
import com.marcelo.brewer.model.Estilo;
import com.marcelo.brewer.repository.Estilos;
import com.marcelo.brewer.repository.filter.EstiloFilter;
import com.marcelo.brewer.service.CadastroEstiloService;
import com.marcelo.brewer.service.exception.NomeEstiloJaCadastradoException;

@Controller
@RequestMapping("/estilos")
public class EstilosController {
	
	@Autowired
	private CadastroEstiloService cadastroEstiloService;
	
	@Autowired
	private Estilos estilos;
	
	@RequestMapping("/novo")
	public ModelAndView novo(Estilo estilo) {
		ModelAndView mv = new ModelAndView("estilo/CadastroEstilo");
		return mv;
	}
	
	@RequestMapping(value = "/novo", method = RequestMethod.POST)
	public ModelAndView cadastrar(@Valid Estilo estilo, BindingResult resultado, RedirectAttributes redirectAttr) {
		
		if (resultado.hasErrors()) {
			return novo(estilo);
		}
		
		try {
			cadastroEstiloService.salvar(estilo);
		} catch (NomeEstiloJaCadastradoException e) {
			resultado.rejectValue("nome", e.getMessage(), e.getMessage());
			return novo(estilo);
		}
		redirectAttr.addFlashAttribute("mensagem", "Estilo cadastrado com sucesso!");
		
		return new ModelAndView("redirect:/estilos/novo");	
	}
	
	@RequestMapping(method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE})
	public @ResponseBody ResponseEntity<?> salvar(@RequestBody @Valid Estilo estilo, BindingResult result) {
		if (result.hasErrors()) {
			return ResponseEntity.badRequest().body(result.getFieldError("nome").getDefaultMessage());
		}
		
		estilo = cadastroEstiloService.salvar(estilo);
		
		return ResponseEntity.ok(estilo);
		
	}
	
	@GetMapping
	public ModelAndView pesquisar(EstiloFilter estiloFilter, BindingResult result, 
			@PageableDefault(size = 2) Pageable pageable, HttpServletRequest httpServletRequest) {
		ModelAndView mv = new ModelAndView("estilo/PesquisaEstilos");
		
		PageWrapper<Estilo> pagina = new PageWrapper<>(estilos.filtrar(estiloFilter, pageable), httpServletRequest);
		mv.addObject("pagina",pagina);
		
		return mv;
	}
	
}
