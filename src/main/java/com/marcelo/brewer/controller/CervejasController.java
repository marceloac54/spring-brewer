package com.marcelo.brewer.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.marcelo.brewer.controller.page.PageWrapper;
import com.marcelo.brewer.dto.CervejaDTO;
import com.marcelo.brewer.model.Cerveja;
import com.marcelo.brewer.model.Origem;
import com.marcelo.brewer.model.Sabor;
import com.marcelo.brewer.repository.Cervejas;
import com.marcelo.brewer.repository.Estilos;
import com.marcelo.brewer.repository.filter.CervejaFilter;
import com.marcelo.brewer.service.CadastroCervejaService;
import com.marcelo.brewer.service.exception.ImpossivelExcluirEntidadeException;

@Controller
@RequestMapping("/cervejas")
public class CervejasController {
	
	@Autowired
	private Estilos estilos;
	
	@Autowired
	private CadastroCervejaService cadastroCervejaService;
	
	@Autowired
	private Cervejas cervejas;
	
	@RequestMapping("/novo")
	public ModelAndView novo(Cerveja cerveja) {
		ModelAndView mv = new ModelAndView("cerveja/CadastroCervejas");
		mv.addObject("estilos", estilos.findAll());
		mv.addObject("sabores", Sabor.values());
		mv.addObject("origens", Origem.values());
		
		return mv;
	}

	@RequestMapping(value = { "/novo", "{\\d+}" }, method = RequestMethod.POST)
	public ModelAndView cadastrar(@Valid Cerveja cerveja, BindingResult result, Model model, RedirectAttributes attr) {
		
		if (result.hasErrors()) {
			throw new RuntimeException();
//			return novo(cerveja);
		}
		
		attr.addFlashAttribute("mensagem", "Cerveja salva com sucesso!");
		cadastroCervejaService.salvar(cerveja);
		return new ModelAndView("redirect:/cervejas/novo");
	}
 
	@GetMapping
	public ModelAndView pesquisar(CervejaFilter cervejaFilter, BindingResult bindingResult, @PageableDefault(size = 2) Pageable pageable
			, HttpServletRequest httpServletRequest) {
		ModelAndView mv = new ModelAndView("cerveja/PesquisaCervejas");
		mv.addObject("estilos", estilos.findAll());
		mv.addObject("sabores", Sabor.values());
		mv.addObject("origens", Origem.values());
		
		System.out.println(">>>>>>>>>>>>> Numero da pagina :" + pageable.getPageNumber());
		System.out.println(">>>>>>>>>>>>> Tamanho da pagina :" + pageable.getPageSize());
		
//		mv.addObject("cervejas", cervejas.findAll(pageable));
		PageWrapper<Cerveja> pagina = new PageWrapper<>(cervejas.filtrar(cervejaFilter, pageable), httpServletRequest);
		
		mv.addObject("pagina", pagina);
		return mv;
	}
	
	@RequestMapping( consumes = MediaType.APPLICATION_JSON_VALUE )
	public @ResponseBody List<CervejaDTO> pesquisar(String skuOuNome) {
		return cervejas.porSkuOuNome(skuOuNome);
	}
	
	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable("codigo") Cerveja cerveja) {
		try {
			cadastroCervejaService.excluir(cerveja);
		} catch (ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable("codigo") Cerveja cerveja) {
		ModelAndView mv = novo(cerveja);
		mv.addObject(cerveja);
		return mv;
	}
	
}
