package com.higienizaplus.backend.repository;

import com.higienizaplus.backend.model.Orcamento;
import com.higienizaplus.backend.model.OrcamentoStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrcamentoRepository extends JpaRepository<Orcamento, Long> {

    Page<Orcamento> findAllByOrderByCriadoEmDesc(Pageable pageable);

    Page<Orcamento> findByStatusOrderByCriadoEmDesc(OrcamentoStatus status, Pageable pageable);
}
