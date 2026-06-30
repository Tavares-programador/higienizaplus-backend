package com.higienizaplus.backend.service;

import com.higienizaplus.backend.dto.OrcamentoRequest;
import com.higienizaplus.backend.dto.OrcamentoResponse;
import com.higienizaplus.backend.exception.ResourceNotFoundException;
import com.higienizaplus.backend.model.Orcamento;
import com.higienizaplus.backend.model.OrcamentoStatus;
import com.higienizaplus.backend.repository.OrcamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrcamentoService {

    private final OrcamentoRepository orcamentoRepository;

    @Value("${app.whatsapp.numero}")
    private String numeroWhatsappEmpresa;

    @Transactional
    public OrcamentoResponse criar(OrcamentoRequest request) {
        Orcamento orcamento = Orcamento.builder()
                .nome(request.nome())
                .email(request.email())
                .whatsapp(request.whatsapp())
                .servico(request.servico())
                .mensagem(request.mensagem())
                .status(OrcamentoStatus.NOVO)
                .build();

        Orcamento salvo = orcamentoRepository.save(orcamento);
        return OrcamentoResponse.from(salvo, numeroWhatsappEmpresa);
    }

    @Transactional(readOnly = true)
    public Page<OrcamentoResponse> listar(Pageable pageable, OrcamentoStatus status) {
        Page<Orcamento> page = (status == null)
                ? orcamentoRepository.findAllByOrderByCriadoEmDesc(pageable)
                : orcamentoRepository.findByStatusOrderByCriadoEmDesc(status, pageable);

        return page.map(o -> OrcamentoResponse.from(o, numeroWhatsappEmpresa));
    }

    @Transactional(readOnly = true)
    public OrcamentoResponse buscarPorId(Long id) {
        Orcamento orcamento = orcamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orçamento não encontrado: id=" + id));
        return OrcamentoResponse.from(orcamento, numeroWhatsappEmpresa);
    }

    @Transactional
    public OrcamentoResponse atualizarStatus(Long id, OrcamentoStatus novoStatus) {
        Orcamento orcamento = orcamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orçamento não encontrado: id=" + id));
        orcamento.setStatus(novoStatus);
        Orcamento salvo = orcamentoRepository.save(orcamento);
        return OrcamentoResponse.from(salvo, numeroWhatsappEmpresa);
    }

    @Transactional
    public void deletar(Long id) {
        if (!orcamentoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Orçamento não encontrado: id=" + id);
        }
        orcamentoRepository.deleteById(id);
    }
}
