package com.example.app.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.app.model.Conta;
import com.example.app.model.Lancamento;
import com.example.app.model.PlanoConta;
import com.example.app.model.PlanoConta.TipoMovimento;
import com.example.app.model.Usuario;
import com.example.app.repository.ContaRepository;
import com.example.app.repository.LancamentoRepository;
import com.example.app.repository.UsuarioRepository;
import com.example.app.utils.Formatador;

@Service
public class LancamentoService {

	@Autowired
	private LancamentoRepository lancamentoRepository;

	@Autowired
	private ContaRepository contaRepository;

	@Autowired
	private ContaService contaService;

	@Autowired
	private PlanoContaService planoContaService;

	@Autowired
	UsuarioService usuarioService;

	@Autowired
	UsuarioRepository usuarioRepository;

	@Transactional

	public void cadastrarLancamento(Lancamento lancamento) throws Exception {
			//coletando dados e instanciando o Lancamento
			Conta conta = contaService.findById(lancamento.getConta().getId());
			Usuario usuario = usuarioService.findById(conta.getUsuario().getId());
			PlanoConta planoConta = planoContaService.findById(lancamento.getPlano().getId());
			Date data = Calendar.getInstance().getTime();			
			Lancamento l = new Lancamento();
			l.setDate(data);
			l.setPlano(planoConta);
			l.setConta(conta);
			l.setValor(lancamento.getValor());
			l.setDescricao(lancamento.getDescricao());
			Double valor = lancamento.getValor();

			if (planoConta.getTipo().equals(TipoMovimento.R) && planoConta.getNome().isEmpty()) {
				planoConta.setNome("RECEITA");
				contaService.creditar(conta, valor);
			} else if (planoConta.getTipo().equals(TipoMovimento.R)) {
				contaService.creditar(conta, valor);
			} else if (planoConta.getTipo().equals(TipoMovimento.D) && planoConta.getNome().isEmpty()) {
				planoConta.setNome("DESPESA");
				contaService.debitar(conta, valor);
			} else if (planoConta.getTipo().equals(TipoMovimento.D)) {
				contaService.debitar(conta, valor);
			} else if (planoConta.getTipo().equals(TipoMovimento.TC)) {
				//buscando a conta crédito do usuário para ser utilizada como conta destino 
				List<Conta> contas = contaRepository.findByUsuarioId(usuario.getId());
				l.setContaDestino(contas.get(1));
				Conta contaDestino = contas.get(1);				
				//clonando o lançamento para constar também na lista de lancamentos da outra conta
				Lancamento lancContaDestino = (Lancamento) l.clone();			
				lancContaDestino.setConta(contaDestino);
				lancContaDestino.setDescricao("Recebido da conta corrente");
				
				lancamentoRepository.save(lancContaDestino);
				
				contaService.transferir(conta, valor, contaDestino);

			} else if (planoConta.getTipo().equals(TipoMovimento.TU)) {
				Conta contaDest = lancamento.getContaDestino();
				//clonando o lançamento para constar também na lista de lancamentos da outra conta
				Lancamento lancContaDestino = (Lancamento) l.clone();
				lancContaDestino.setConta(contaDest);
				lancContaDestino.setDescricao("Recebido de: " + l.getConta().getUsuario().getNome());
				
				lancamentoRepository.save(lancContaDestino);
				
				contaService.transferir(conta, valor, contaDest);
			}
			
			lancamentoRepository.save(l);
	}

	public Map<String, Object> listarLancamentosPorData(String dataI, String dataF, String login) throws ParseException {

		Optional<Usuario> opp = usuarioRepository.findByLogin(login);
		Usuario usuario = opp.get();
		List<Conta> contas = contaRepository.findByUsuarioId(usuario.getId());
		Conta contaBanco = contas.get(0);
		Conta contaCredito = contas.get(1);
		
		Date dataInicial = Formatador.stringParaDate(dataI);
		Date dataFinal = Formatador.stringParaDate(dataF);

		List<Lancamento> lancamentos = lancamentoRepository.findAll();

		List<Object> lancamentosContaBanco = listarLancamentos(lancamentos, contaBanco, dataInicial, dataFinal);
		List<Object> lancamentosContaCredito = listarLancamentos(lancamentos, contaCredito, dataInicial, dataFinal);

		Map<String, Object> listafinal = new LinkedHashMap<>();
		listafinal.put("contaDebito", contaBanco);
		listafinal.put("contaCredito", contaCredito);
		listafinal.put("lancamentosContaBanco", lancamentosContaBanco);
		listafinal.put("lancamentosContaCredito", lancamentosContaCredito);

		return listafinal;

	}

	public List<Object> listarLancamentos(List<Lancamento> lancamentos, Conta conta, Date dataInicial,
			Date dataFinal) {

		List<Object> listaTeste= new ArrayList<Object>();
		
		for (Lancamento lancamento : lancamentos) {
			Date dataLancamento = lancamento.getDate();
			if (lancamento.getConta().getId() == conta.getId()
					&& (dataLancamento.after(dataInicial) && dataLancamento.before(dataFinal))) {
				
				Map<String, Object> lancamentosConsolidados = new LinkedHashMap<>();		
				
				lancamentosConsolidados.put("id", lancamento.getId());
				lancamentosConsolidados.put("data", Formatador.formatarData(lancamento.getDate()));
				lancamentosConsolidados.put("valor", lancamento.getValor());
				lancamentosConsolidados.put("conta", lancamento.getConta().getId());
				lancamentosConsolidados.put("descrição", lancamento.getDescricao());
				lancamentosConsolidados.put("plano", lancamento.getPlano().getId());
				lancamentosConsolidados.put("tipo", lancamento.getPlano().getTipo());
				
				listaTeste.add(lancamentosConsolidados);

			}

		}
		return listaTeste;

	}

}
