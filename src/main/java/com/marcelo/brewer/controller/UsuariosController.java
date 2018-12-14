package com.marcelo.brewer.controller;


import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.marcelo.brewer.controller.page.PageWrapper;
import com.marcelo.brewer.model.Usuario;
import com.marcelo.brewer.repository.Grupos;
import com.marcelo.brewer.repository.Usuarios;
import com.marcelo.brewer.repository.filter.UsuarioFilter;
import com.marcelo.brewer.service.CadastroUsuarioService;
import com.marcelo.brewer.service.StatusUsuario;
import com.marcelo.brewer.service.exception.EmailUsuarioJaCadastrado;
import com.marcelo.brewer.service.exception.SenhaObrigatorioUsuarioException;

@Controller
@RequestMapping("/usuarios")
public class UsuariosController {
	
	@Autowired
	private CadastroUsuarioService usuarioService;
	
	@Autowired
	private Grupos grupos;
	
	@Autowired
	private Usuarios usuarios;
	
	@RequestMapping("/novo")
	public ModelAndView novo(Usuario usuario) {
		ModelAndView mv = new ModelAndView("usuario/CadastroUsuario");
		mv.addObject("grupos", grupos.findAll());
		return mv;
	}
	
	@PostMapping({ "/novo", "{\\+d}" })
	public ModelAndView cadastrar(@Valid Usuario usuario, BindingResult result, RedirectAttributes attr) {
		
		if (result.hasErrors()) {
			return novo(usuario);
		}
		
		
		try {
			usuarioService.salvar(usuario);
		} catch (EmailUsuarioJaCadastrado e) {
			result.rejectValue("email", e.getMessage(), e.getMessage());
			return novo(usuario);
		} catch (SenhaObrigatorioUsuarioException e) {
			result.rejectValue("senha", e.getMessage(), e.getMessage());
			return novo(usuario);
		}
		
		attr.addFlashAttribute("mensagem", "Usuário salvo com sucesso!");
		return new ModelAndView("redirect:/usuarios/novo");
	}
	
	@GetMapping
	public ModelAndView pesquisar(UsuarioFilter usuarioFilter
			, @PageableDefault(size = 3) Pageable pageable, HttpServletRequest httpServletRequest) {
		ModelAndView mv = new ModelAndView("/usuario/PesquisaUsuario");
		mv.addObject("grupos", grupos.findAll());
		
		PageWrapper<Usuario> paginaWrapper = new PageWrapper<>(usuarios.filtrar(usuarioFilter, pageable)
				, httpServletRequest);
		mv.addObject("pagina", paginaWrapper);
		return mv;
	}
	
	@PutMapping("/status")
	@ResponseStatus(HttpStatus.OK)
	public void atualizarStatus(@RequestParam("codigos[]") Long[] codigos, @RequestParam("status") StatusUsuario statusUsuario) {
		usuarioService.alterarStatus(codigos, statusUsuario);
	}
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		Usuario usuario = usuarios.buscarComGrupos(codigo);
		ModelAndView mv = novo(usuario);
		mv.addObject(usuario);
		return mv;
	}
	
}
