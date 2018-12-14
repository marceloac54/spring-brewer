package com.marcelo.brewer.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.marcelo.brewer.controller.page.PageWrapper;
import com.marcelo.brewer.model.Cidade;
import com.marcelo.brewer.repository.Cidades;
import com.marcelo.brewer.repository.Estados;
import com.marcelo.brewer.repository.filter.CidadeFilter;
import com.marcelo.brewer.service.CadastroCidadeService;
import com.marcelo.brewer.service.exception.NomeCidadeJaCadastrado;

@Controller
@RequestMapping("/cidades")
public class CidadesController {
	
	@Autowired
	private Cidades cidades;
	
	@Autowired
	private Estados estados;
	
	@Autowired
	private CadastroCidadeService cidadeService;
	
	@RequestMapping("/novo")
	public ModelAndView novo(Cidade cidade) {
		ModelAndView mv = new ModelAndView("cidade/CadastroCidade");
		mv.addObject("estados",estados.findAll());
		return mv;
	}
	
	@PostMapping("/novo")
	@CacheEvict(value = "cidades", key = "#cidade.estado.codigo", condition = "#cidade.temEstado()")
	public ModelAndView cadastrar(@Valid Cidade cidade, BindingResult result, RedirectAttributes attributes) {
		
		if (result.hasErrors()) {
			return novo(cidade);
		}
		
		try {
			cidadeService.salvar(cidade);
		} catch (NomeCidadeJaCadastrado e) {
			result.rejectValue("nome", e.getMessage(), e.getMessage());
			return novo(cidade);
		}
		
		attributes.addFlashAttribute("mensagem", "Cidade salva com sucesso");
		ModelAndView mv = new ModelAndView("redirect:/cidades/novo");
		return mv;
	}
	
	@Cacheable(value = "cidades", key = "#codigoEstado")
	@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Cidade> pesquisarPorCodigoEstado(@RequestParam(name="estado", defaultValue = "-1") Long codigoEstado) {
		
		try {
			new Thread();
			Thread.sleep(500);
		} catch (InterruptedException e) { }
		
		return cidades.findByEstadoCodigo(codigoEstado); 
	}
	
	@GetMapping
	public ModelAndView pesquisar(CidadeFilter cidadeFilter, BindingResult bindingResult, @PageableDefault(size = 10) Pageable pageable
			, HttpServletRequest httpServletRequest) {
		ModelAndView mv = new ModelAndView("cidade/PesquisaCidade");
		mv.addObject("estados", estados.findAll());
		
		PageWrapper<Cidade> pagina = new PageWrapper<>(cidades.filtrar(cidadeFilter, pageable), httpServletRequest);
		mv.addObject("pagina", pagina);
		
		return mv;
	}
	
}
