var Brewer = Brewer || {};

Brewer.EstiloCadastroRapido = (function() {
	
	function EstiloCadastroRapido() {
		this.modal = $('#modalCadastroRapidoEstilo');
		this.btnSalvar = this.modal.find('.js-modal-cadastro-estilo-salvar-btn')
		this.form = this.modal.find('form');
		this.url = this.form.attr('action');
		this.inputNomeEstilo = $('#nomeEstilo');
		this.containerMensagemErro = $('.js-mensagem-cadastro-rapido-estilo');
	}
	
	EstiloCadastroRapido.prototype.iniciar = function() {
		this.form.on('submit', function(event) { event.preventDefault() });
		this.modal.on('shown.bs.modal', onModalShow.bind(this));
		this.modal.on('hide.bs.modal', onModalClose.bind(this));
		this.btnSalvar.on('click', onBotaoSalvarClick.bind(this));
	}
	
	function onModalShow() {
		this.inputNomeEstilo.focus();
	}
	
	function onModalClose() {
		this.inputNomeEstilo.val('');
		this.containerMensagemErro.addClass('hidden');
		this.form.find('.form-group').removeClass('has-error');
	}
	
	function onBotaoSalvarClick() {
		var nomeEstilo = this.inputNomeEstilo.val().trim();
		$.ajax({
			url: this.url,
			method: 'POST',
			contentType: 'application/json',
			data: JSON.stringify({nome: nomeEstilo}),
			error: onErroSalvandoEstilo.bind(this),
			success: onEstiloSalvo.bind(this)
		});
	}
	
	function onErroSalvandoEstilo(obj) {
//		console.log(arguments);
		var msgErro = obj.responseText;
		this.containerMensagemErro.removeClass('hidden');
		this.containerMensagemErro.html('<span>' + msgErro + '</span>');
		this.form.find('.form-group').addClass('has-error');
	}
	
	function onEstiloSalvo(estilo) {
//		console.log(arguments);
		var comboEstilo = $('#estilo');
		comboEstilo.append('<option value=' + estilo.codigo + '>' + estilo.nome + '</option>');
		comboEstilo.val(estilo.codigo);
		this.modal.modal('hide');
	}
	
	
	return EstiloCadastroRapido;
	
}());

$(function () {
	
	var estiloCadastroRapido = new Brewer.EstiloCadastroRapido();
	estiloCadastroRapido.iniciar();
	
});