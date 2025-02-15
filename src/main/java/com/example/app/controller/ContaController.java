package com.example.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.model.Conta;
import com.example.app.repository.ContaRepository;
import com.example.app.service.ContaService;

@RestController
@RequestMapping("/contas")
public class ContaController {

	@Autowired
	ContaService Contaservice;
	
	@Autowired
	private ContaRepository contaRepository;
	
	@GetMapping
	public List<Conta> listar(){
		return contaRepository.findAll();
	}
	
	@GetMapping(value = "/{id}")
	public ResponseEntity<Conta> findById(@PathVariable Integer id){
		Conta obj = Contaservice.findById(id);
		return ResponseEntity.ok().body(obj);
	}
	
	
}
