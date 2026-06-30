package com.higienizaplus.backend.repository;

import com.higienizaplus.backend.model.ServicoPreco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicoPrecoRepository extends JpaRepository<ServicoPreco, Long> {

    List<ServicoPreco> findAllByOrderByOrdemAsc();
}
