package com.example.app.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.app.model.Conta.TipoConta;
import com.example.app.model.PlanoConta.TipoMovimento;
import com.example.app.model.Usuario;
import com.example.app.repository.UsuarioRepository;
import com.example.app.utils.exception.DefaultErrorException;


@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	PlanoContaService planoContaService;

	@Autowired
	ContaService contaService;

	@Transactional
	public void cadastrarUsuario(Usuario usuario) throws DefaultErrorException{

		if(usuarioRepository.existsByLogin(usuario.getLogin()))
			throw new DefaultErrorException("Login já existente!");
		
		String senhaCriptografada = encoder.encode(usuario.getSenha());

		usuario.setSenha(senhaCriptografada);

		usuarioRepository.save(usuario);

		contaService.criarConta(123456, TipoConta.BANCO, usuario);
		contaService.criarConta(123456, TipoConta.CREDITO, usuario);

		planoContaService.criarPlanoContaPadrao(usuario, "RECEITAS", TipoMovimento.R, true);
		planoContaService.criarPlanoContaPadrao(usuario, "DESPESAS", TipoMovimento.D, true);
		planoContaService.criarPlanoContaPadrao(usuario, "TRANSFERÊNCIA ENTRE USUÁRIOS", TipoMovimento.TU, true);
		planoContaService.criarPlanoContaPadrao(usuario, "TRANSFERÊNCIA ENTRE CONTAS", TipoMovimento.TC, true);
	
		
	}

	public Usuario findById(Integer id) {
		return usuarioRepository.findById(id).orElseThrow(() -> new DefaultErrorException("Id não encontrado."));
	}

}
