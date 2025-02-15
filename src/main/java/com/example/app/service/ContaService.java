package com.example.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.app.model.Conta;
import com.example.app.model.Usuario;
import com.example.app.repository.ContaRepository;
import com.example.app.utils.exception.DefaultErrorException;
import com.example.app.model.Conta.TipoConta;

@Service
public class ContaService {

	@Autowired
	ContaRepository contaRepository;
	
	public void criarConta(Integer numero, TipoConta tipo, Usuario usuario) {
		
		Conta conta = new Conta();
		conta.setNumero(numero); 
		conta.setTipo(tipo);
		conta.setUsuario(usuario);
		
		contaRepository.save(conta);
		
	}
	

	public void debitar(Conta conta, Double valor) {

		if (conta.getSaldo() < valor) {
			throw new DefaultErrorException("Saldo insuficiente");
		} else {
			conta.setSaldo(conta.getSaldo() - valor);
		}
	}

	public void creditar(Conta conta, Double valor) {
		conta.setSaldo(conta.getSaldo() + valor);
	}
	
	public void transferir(Conta conta, Double valor, Conta contaDestino) {
		
		if (conta.getSaldo() < valor) {
			throw new DefaultErrorException("Saldo insuficiente");
		} else {
			conta.setSaldo(conta.getSaldo() - valor);
		}
		
		Conta contaReceber = this.findById(contaDestino.getId());
		
		contaReceber.setSaldo(contaReceber.getSaldo() + valor);
	}
	
	public Conta findById(Integer id) {
	    return contaRepository.findById(id)
	    		.orElseThrow(() -> new DefaultErrorException("Conta não encontrado."))
	    		;
	}
	
	//TODO criar metodo para gerar numero de conta randomico e não repetido
}
