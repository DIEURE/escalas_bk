package com.hope.escala.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hope.escala.dto.request.CriarAtaRequestDTO;
import com.hope.escala.dto.request.CriarPautaItemDTO;
import com.hope.escala.dto.request.RegistrarVotoRequestDTO;
import com.hope.escala.dto.response.AtaDetalheResponseDTO;
import com.hope.escala.dto.response.PautaDetalheResponseDTO;
import com.hope.escala.dto.response.PautaOpcaoResponseDTO;
import com.hope.escala.entity.AtaReuniao;
import com.hope.escala.entity.Departamento;
import com.hope.escala.entity.Empresa;
import com.hope.escala.entity.PautaOpcao;
import com.hope.escala.entity.PautaReuniao;
import com.hope.escala.entity.Usuario;
import com.hope.escala.entity.VotoPauta;
import com.hope.escala.enums.StatusAta;
import com.hope.escala.enums.StatusVotacaoPauta;
import com.hope.escala.exception.ResourceNotFoundException;
import com.hope.escala.repository.AtaReuniaoRepository;
import com.hope.escala.repository.DepartamentoRepository;
import com.hope.escala.repository.EmpresaRepository;
import com.hope.escala.repository.PautaOpcaoRepository;
import com.hope.escala.repository.PautaReuniaoRepository;
import com.hope.escala.repository.UsuarioRepository;
import com.hope.escala.repository.VotoPautaRepository;
import com.hope.escala.security.SecurityUtils;

@Service
public class AtaReuniaoService {

    private final AtaReuniaoRepository ataRepository;
    private final PautaReuniaoRepository pautaRepository;
    private final PautaOpcaoRepository pautaOpcaoRepository;
    private final VotoPautaRepository votoRepository;
    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;
    private final DepartamentoRepository departamentoRepository;
    private final SecurityUtils securityUtils;

    public AtaReuniaoService(
            AtaReuniaoRepository ataRepository,
            PautaReuniaoRepository pautaRepository,
            PautaOpcaoRepository pautaOpcaoRepository,
            VotoPautaRepository votoRepository,
            EmpresaRepository empresaRepository,
            UsuarioRepository usuarioRepository,
            DepartamentoRepository departamentoRepository,
            SecurityUtils securityUtils) {
        this.ataRepository = ataRepository;
        this.pautaRepository = pautaRepository;
        this.pautaOpcaoRepository = pautaOpcaoRepository;
        this.votoRepository = votoRepository;
        this.empresaRepository = empresaRepository;
        this.usuarioRepository = usuarioRepository;
        this.departamentoRepository = departamentoRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional
    public AtaDetalheResponseDTO criarAta(CriarAtaRequestDTO dto) {
        Long empresaId = securityUtils.empresaId();
        Long usuarioLogadoId = securityUtils.usuarioId();

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));

        Usuario criador = usuarioRepository.findById(usuarioLogadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Departamento departamento = null;
        if (dto.departamentoId() != null) {
            departamento = departamentoRepository.findById(dto.departamentoId()).orElse(null);
        }

        AtaReuniao ata = new AtaReuniao();
        ata.setEmpresa(empresa);
        ata.setCriador(criador);
        ata.setDepartamento(departamento);
        ata.setTitulo(dto.titulo());
        ata.setDataReuniao(dto.dataReuniao());
        ata.setHorarioInicio(dto.horarioInicio());
        ata.setHorarioFim(dto.horarioFim());
        ata.setLocalReuniao(dto.localReuniao());
        ata.setConteudoAta(dto.conteudoAta());
        ata.setStatus(StatusAta.RASCUNHO);

        if (dto.pautas() != null && !dto.pautas().isEmpty()) {
            for (CriarPautaItemDTO item : dto.pautas()) {
                PautaReuniao pauta = new PautaReuniao();
                pauta.setAta(ata);
                pauta.setEmpresa(empresa);
                pauta.setTitulo(item.titulo());
                pauta.setDescricao(item.descricao());
                pauta.setOrdem(item.ordem() != null ? item.ordem() : ata.getPautas().size() + 1);
                pauta.setRequerVotacao(Boolean.TRUE.equals(item.requerVotacao()));
                pauta.setStatusVotacao(StatusVotacaoPauta.NAO_INICIADA);

                if (item.opcoes() != null) {
                    int ordemOpcao = 1;
                    for (String textoOpcao : item.opcoes()) {
                        if (textoOpcao != null && !textoOpcao.trim().isEmpty()) {
                            pauta.getOpcoes().add(new PautaOpcao(pauta, textoOpcao.trim(), ordemOpcao++));
                        }
                    }
                }

                ata.getPautas().add(pauta);
            }
        }

        AtaReuniao salva = ataRepository.save(ata);
        return mapearParaDetalheResponse(salva, usuarioLogadoId);
    }

    @Transactional(readOnly = true)
    public AtaDetalheResponseDTO buscarPorIdComDetalhes(Long id) {
        Long empresaId = securityUtils.empresaId();
        Long usuarioLogadoId = securityUtils.usuarioId();

        AtaReuniao ata = ataRepository.findByIdAndEmpresaIdWithPautas(id, empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Ata não encontrada para sua congregação"));

        return mapearParaDetalheResponse(ata, usuarioLogadoId);
    }

    @Transactional(readOnly = true)
    public List<AtaDetalheResponseDTO> listarAtas(Long departamentoId) {
        Long empresaId = securityUtils.empresaId();
        Long usuarioLogadoId = securityUtils.usuarioId();

        List<AtaReuniao> atas = (departamentoId != null)
                ? ataRepository.findByEmpresaIdAndDepartamentoIdOrderByDataReuniaoDesc(empresaId, departamentoId)
                : ataRepository.findByEmpresaIdOrderByDataReuniaoDesc(empresaId);

        return atas.stream()
                .map(a -> mapearParaDetalheResponse(a, usuarioLogadoId))
                .toList();
    }

    @Transactional
    public void finalizarAta(Long ataId) {
        Long empresaId = securityUtils.empresaId();
        AtaReuniao ata = ataRepository.findByIdAndEmpresaId(ataId, empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Ata não encontrada"));

        ata.setStatus(StatusAta.CONCLUIDA);
        ataRepository.save(ata);
    }

    @Transactional
    public PautaDetalheResponseDTO adicionarPauta(Long ataId, CriarPautaItemDTO dto) {
        Long empresaId = securityUtils.empresaId();
        AtaReuniao ata = ataRepository.findByIdAndEmpresaId(ataId, empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Ata não encontrada"));

        PautaReuniao pauta = new PautaReuniao();
        pauta.setAta(ata);
        pauta.setEmpresa(ata.getEmpresa());
        pauta.setTitulo(dto.titulo());
        pauta.setDescricao(dto.descricao());
        pauta.setOrdem(dto.ordem() != null ? dto.ordem() : ata.getPautas().size() + 1);
        pauta.setRequerVotacao(Boolean.TRUE.equals(dto.requerVotacao()));
        pauta.setStatusVotacao(StatusVotacaoPauta.NAO_INICIADA);

        if (dto.opcoes() != null) {
            int ordemOpcao = 1;
            for (String textoOpcao : dto.opcoes()) {
                if (textoOpcao != null && !textoOpcao.trim().isEmpty()) {
                    pauta.getOpcoes().add(new PautaOpcao(pauta, textoOpcao.trim(), ordemOpcao++));
                }
            }
        }

        PautaReuniao salva = pautaRepository.save(pauta);

        List<PautaOpcaoResponseDTO> opcoesDto = salva.getOpcoes().stream()
                .map(o -> new PautaOpcaoResponseDTO(o.getId(), o.getTexto(), o.getOrdem(), 0, 0.0))
                .toList();

        return new PautaDetalheResponseDTO(
                salva.getId(),
                salva.getOrdem(),
                salva.getTitulo(),
                salva.getDescricao(),
                salva.getRequerVotacao(),
                salva.getStatusVotacao(),
                0,
                null,
                opcoesDto
        );
    }

    @Transactional
    public void registrarVoto(Long pautaId, RegistrarVotoRequestDTO dto) {
        Long empresaId = securityUtils.empresaId();
        Long usuarioId = securityUtils.usuarioId();

        PautaReuniao pauta = pautaRepository.findByIdAndEmpresaId(pautaId, empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Pauta não encontrada"));

        if (!pauta.getRequerVotacao() || pauta.getStatusVotacao() != StatusVotacaoPauta.EM_VOTACAO) {
            throw new IllegalStateException("Esta pauta não está aberta para votação.");
        }

        if (pauta.getAta().getStatus() == StatusAta.CONCLUIDA) {
            throw new IllegalStateException("A ata já foi concluída.");
        }

        PautaOpcao opcaoEscolhida = pautaOpcaoRepository.findByIdAndPautaId(dto.opcaoId(), pautaId)
                .orElseThrow(() -> new ResourceNotFoundException("Opção de votação não encontrada para esta pauta"));

        Optional<VotoPauta> votoExistente = votoRepository.findByPautaIdAndUsuarioId(pautaId, usuarioId);

        if (votoExistente.isPresent()) {
            VotoPauta voto = votoExistente.get();
            voto.setOpcao(opcaoEscolhida);
            voto.setJustificativa(dto.justificativa());
            votoRepository.save(voto);
        } else {
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

            VotoPauta novoVoto = new VotoPauta();
            novoVoto.setPauta(pauta);
            novoVoto.setOpcao(opcaoEscolhida);
            novoVoto.setUsuario(usuario);
            novoVoto.setEmpresa(pauta.getEmpresa());
            novoVoto.setJustificativa(dto.justificativa());
            votoRepository.save(novoVoto);
        }
    }

    @Transactional
    public void alterarStatusVotacaoPauta(Long pautaId, StatusVotacaoPauta novoStatus) {
        Long empresaId = securityUtils.empresaId();
        PautaReuniao pauta = pautaRepository.findByIdAndEmpresaId(pautaId, empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Pauta não encontrada"));

        pauta.setStatusVotacao(novoStatus);
        pautaRepository.save(pauta);
    }

    private AtaDetalheResponseDTO mapearParaDetalheResponse(AtaReuniao ata, Long usuarioLogadoId) {
        List<PautaDetalheResponseDTO> pautasDto = new ArrayList<>();

        if (ata.getPautas() != null) {
            for (PautaReuniao p : ata.getPautas()) {
                long totalGeral = votoRepository.countTotalVotosPauta(p.getId());

                Long minhaOpcaoEscolhidaId = votoRepository.findByPautaIdAndUsuarioId(p.getId(), usuarioLogadoId)
                        .map(v -> v.getOpcao().getId())
                        .orElse(null);

                List<PautaOpcaoResponseDTO> opcoesDto = new ArrayList<>();
                if (p.getOpcoes() != null) {
                    for (PautaOpcao op : p.getOpcoes()) {
                        long totalOpcao = votoRepository.countPorOpcaoId(op.getId());
                        double porcentagem = totalGeral > 0 ? ((double) totalOpcao / totalGeral) * 100.0 : 0.0;
                        opcoesDto.add(new PautaOpcaoResponseDTO(
                                op.getId(),
                                op.getTexto(),
                                op.getOrdem(),
                                totalOpcao,
                                Math.round(porcentagem * 10.0) / 10.0
                        ));
                    }
                }

                pautasDto.add(new PautaDetalheResponseDTO(
                        p.getId(),
                        p.getOrdem(),
                        p.getTitulo(),
                        p.getDescricao(),
                        p.getRequerVotacao(),
                        p.getStatusVotacao(),
                        totalGeral,
                        minhaOpcaoEscolhidaId,
                        opcoesDto
                ));
            }
        }

        return new AtaDetalheResponseDTO(
                ata.getId(),
                ata.getTitulo(),
                ata.getDataReuniao(),
                ata.getHorarioInicio(),
                ata.getHorarioFim(),
                ata.getLocalReuniao(),
                ata.getConteudoAta(),
                ata.getStatus(),
                ata.getDepartamento() != null ? ata.getDepartamento().getId() : null,
                ata.getDepartamento() != null ? ata.getDepartamento().getNome() : null,
                ata.getCriador() != null ? ata.getCriador().getNome() : "Sistema",
                pautasDto
        );
    }
}