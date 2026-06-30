package com.higienizaplus.backend.service;

import com.higienizaplus.backend.model.ServicoPreco;
import com.higienizaplus.backend.repository.ServicoPrecoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicoPrecoService {

    private final ServicoPrecoRepository servicoPrecoRepository;

    @Transactional(readOnly = true)
    public List<ServicoPreco> listarTodos() {
        return servicoPrecoRepository.findAllByOrderByOrdemAsc();
    }
}
