package com.example.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.app.model.Lancamento;
import com.example.app.model.Usuario;


@Repository
public interface LancamentoRepository extends JpaRepository<Lancamento, Integer> {
	
}
