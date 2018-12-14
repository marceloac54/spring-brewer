package com.marcelo.brewer.controller;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.marcelo.brewer.controller.page.PageWrapper;
import com.marcelo.brewer.controller.validator.VendaValidator;
import com.marcelo.brewer.dto.VendaMes;
import com.marcelo.brewer.dto.VendaOrigem;
import com.marcelo.brewer.mail.Mailer;
import com.marcelo.brewer.model.Cerveja;
import com.marcelo.brewer.model.ItemVenda;
import com.marcelo.brewer.model.StatusVenda;
import com.marcelo.brewer.model.TipoPessoa;
import com.marcelo.brewer.model.Venda;
import com.marcelo.brewer.repository.Cervejas;
import com.marcelo.brewer.repository.Vendas;
import com.marcelo.brewer.repository.filter.VendaFilter;
import com.marcelo.brewer.security.UsuarioSistema;
import com.marcelo.brewer.service.CadastroVendaService;
import com.marcelo.brewer.session.TabelasItensSession;

@Controller
@RequestMapping("/vendas")
public class VendasController {
	
	@Autowired
	private Cervejas cervejas;
	
	@Autowired
	private TabelasItensSession tabelaItens;
	
	@Autowired
	private CadastroVendaService cadastroVendaService;

	@Autowired
	private VendaValidator vendaValidator;
	
	@Autowired
	private Vendas vendas;
	
	@Autowired
	private Mailer mailer;
	
	@InitBinder("venda")
	public void inicializarValidador(WebDataBinder binder) {
		binder.setValidator(vendaValidator);
	}
	
	@GetMapping("/novo")
	public ModelAndView cadastroVenda(Venda venda) {
		ModelAndView mv = new ModelAndView("venda/CadastroVenda");
		
		setUuid(venda);
		
		mv.addObject("itens", venda.getItens());
		mv.addObject("valorFrete", venda.getValorFrete());
		mv.addObject("valorDesconto", venda.getValorDesconto());
		mv.addObject("valorTotalItens", tabelaItens.getValorTotal(venda.getUuid()));
		
		return mv;
	}
	
	@PostMapping(value = "/novo", params ="salvar")
	public ModelAndView salvar(Venda venda, BindingResult result, RedirectAttributes attributes, @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		validarVenda(venda, result);
		if (result.hasErrors()) {
			return cadastroVenda(venda);
		}
		
		venda.setUsuario(usuarioSistema.getUsuario());
		
		cadastroVendaService.salvar(venda);
		attributes.addFlashAttribute("mensagem", "Venda salva com sucesso");
		return new ModelAndView("redirect:/vendas/novo");
	}
	
	@PostMapping(value = "/novo", params ="emitir")
	public ModelAndView emitir(Venda venda, BindingResult result, RedirectAttributes attributes, @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		validarVenda(venda, result);
		if (result.hasErrors()) {
			return cadastroVenda(venda);
		}
		
		venda.setUsuario(usuarioSistema.getUsuario());
		
		cadastroVendaService.emitir(venda);
		attributes.addFlashAttribute("mensagem", "Venda emitida e salva com sucesso");
		return new ModelAndView("redirect:/vendas/novo");
	}
	
	@PostMapping(value = "/novo", params ="enviarEmail")
	public ModelAndView enviarEmail(Venda venda, BindingResult result, RedirectAttributes attributes, @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		validarVenda(venda, result);
		if (result.hasErrors()) {
			return cadastroVenda(venda);
		}
		
		venda.setUsuario(usuarioSistema.getUsuario());
		mailer.enviar(venda);
		
		venda = cadastroVendaService.salvar(venda);
		attributes.addFlashAttribute("mensagem", String.format("Venda de nº %d salva e enviada com sucesso",venda.getCodigo()));
		return new ModelAndView("redirect:/vendas/novo");
	}
	
	@PostMapping("/item")
	public ModelAndView adicionarItem(Long codigoCerveja, String uuid) {
		Cerveja cerveja = cervejas.findOne(codigoCerveja);
		tabelaItens.adicionarItem(uuid, cerveja, 1);
		return mvTabelaItensVenda(uuid);
	}
	
	@PutMapping("/item/{codigoCerveja}")
	public ModelAndView alterarQuantidadeItem(@PathVariable Long codigoCerveja, Integer quantidade, String uuid) {
		Cerveja cerveja = cervejas.findOne(codigoCerveja);
		tabelaItens.alterarQuantidadeItens(uuid, cerveja, quantidade);
		return mvTabelaItensVenda(uuid);
	}
	
	@DeleteMapping("/item/{uuid}/{codigoCerveja}")
	public ModelAndView excluirItem(@PathVariable("codigoCerveja") Cerveja cerveja, @PathVariable("uuid") String uuid) {
		tabelaItens.excluirItem(uuid, cerveja);
		return mvTabelaItensVenda(uuid);
	}
	
	private ModelAndView mvTabelaItensVenda(String uuid) {
		ModelAndView mv = new ModelAndView("venda/TabelaItensVenda");
		mv.addObject("itens", tabelaItens.getItens(uuid));
		mv.addObject("valorTotal", tabelaItens.getValorTotal(uuid));
		return mv;
	}
	
	private void validarVenda(Venda venda, BindingResult result) {
		venda.adicionarItens(tabelaItens.getItens(venda.getUuid()));
		venda.calcularValorTotal();
		
		vendaValidator.validate(venda, result);
	}
	
	@GetMapping
	public ModelAndView pesquisar(VendaFilter vendaFilter,
			@PageableDefault(size = 10) Pageable pageable, HttpServletRequest httpServletRequest) {
		ModelAndView mv = new ModelAndView("/venda/PesquisaVendas");
		mv.addObject("todosStatus", StatusVenda.values());
		mv.addObject("tiposPessoa", TipoPessoa.values());
		
		PageWrapper<Venda> paginaWrapper = new PageWrapper<>(vendas.filtrar(vendaFilter, pageable)
				, httpServletRequest);
		mv.addObject("pagina", paginaWrapper);
		return mv;
	}
	
	@PostMapping(value = "/novo", params = "cancelar")
	public ModelAndView cancelar(Venda venda, BindingResult result
				, RedirectAttributes attributes, @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		try {
			cadastroVendaService.cancelar(venda);
		} catch (AccessDeniedException e) {
			return new ModelAndView("/403");
		}
		
		attributes.addFlashAttribute("mensagem", "Venda cancelada com sucesso");
		return new ModelAndView("redirect:/vendas/" + venda.getCodigo());
	}
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		Venda venda = vendas.buscarComItens(codigo);
		
		setUuid(venda);
		for (ItemVenda item : venda.getItens()) {
			tabelaItens.adicionarItem(venda.getUuid(), item.getCerveja(), item.getQuantidade());
		}
		
		ModelAndView mv = cadastroVenda(venda);
		mv.addObject(venda);
		return mv;
	}
	
	private void setUuid(Venda venda) {
		if (StringUtils.isEmpty(venda.getUuid())) {
			venda.setUuid(UUID.randomUUID().toString());
		}
	}
	
	@GetMapping("/totalPorMes")
	public @ResponseBody List<VendaMes> listarTotalVendaPorMes() {
		return vendas.totalPorMes();
	}
	
	@GetMapping("/porOrigem")
	public @ResponseBody List<VendaOrigem> vendasPorNacionalidade() {
		return this.vendas.totalPorOrigem();
	}
	
}
