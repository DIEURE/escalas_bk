package com.hope.escala.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hope.escala.dto.ConfirmacaoEscalaMusicoDTO;
import com.hope.escala.dto.SubstituicaoEscalaMusicoDTO;
import com.hope.escala.dto.request.EscalaMusicoRequestDTO;
import com.hope.escala.dto.response.EscalaMusicoResponseDTO;
import com.hope.escala.entity.Escala;
import com.hope.escala.entity.EscalaMusico;
import com.hope.escala.entity.Usuario;
import com.hope.escala.exception.ResourceNotFoundException;
import com.hope.escala.repository.EscalaMusicoRepository;
import com.hope.escala.repository.EscalaRepository;
import com.hope.escala.repository.UsuarioRepository;
import com.hope.escala.security.SecurityUtils;

@Service
public class EscalaMusicoService {

	private final EscalaMusicoRepository escalaMusicoRepository;
	private final EscalaRepository escalaRepository;
	private final UsuarioRepository usuarioRepository;
	private final EscalaService escalaService;
	// Importe SecurityUtils no topo
	 private final com.hope.escala.security.SecurityUtils securityUtils;

	public EscalaMusicoService(EscalaMusicoRepository escalaMusicoRepository, EscalaRepository escalaRepository,
			UsuarioRepository usuarioRepository, EscalaService escalaService, SecurityUtils securityUtils) {
		this.escalaMusicoRepository = escalaMusicoRepository;
		this.escalaRepository = escalaRepository;
		this.usuarioRepository = usuarioRepository;
		this.escalaService = escalaService;
		this.securityUtils = securityUtils;
	}

	public EscalaMusicoResponseDTO salvar(EscalaMusicoRequestDTO dto) {

		Escala escala = escalaRepository.findById(dto.getEscalaId())
				.orElseThrow(() -> new ResourceNotFoundException("Escala não encontrada"));

		Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
				.orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

		EscalaMusico escalaMusico = new EscalaMusico();

		escalaMusico.setEscala(escala);
		escalaMusico.setUsuario(usuario);
		escalaMusico.setObservacao(dto.getObservacao());

		escalaMusico.setConfirmado(false);

		EscalaMusico escalaMusicoSalvo = escalaMusicoRepository.save(escalaMusico);

		return converterParaDTO(escalaMusicoSalvo);
	}

	public List<EscalaMusicoResponseDTO> listarPorEscala(Long escalaId) {

		List<EscalaMusico> escalaMusicos = escalaMusicoRepository.findByEscalaId(escalaId);

		return escalaMusicos.stream().map(this::converterParaDTO).toList();
	}

	public EscalaMusicoResponseDTO atualizarConfirmacao(Long id, ConfirmacaoEscalaMusicoDTO dto) {

		EscalaMusico escalaMusico = escalaMusicoRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Escala músico não encontrada"));

		escalaMusico.setConfirmado(dto.getConfirmado());

		EscalaMusico escalaMusicoAtualizado = escalaMusicoRepository.save(escalaMusico);

		verificarFechamentoEscala(escalaMusico.getEscala().getId());

		return converterParaDTO(escalaMusicoAtualizado);
	}

	public EscalaMusicoResponseDTO substituirMusico(Long id, SubstituicaoEscalaMusicoDTO dto) {

		EscalaMusico escalaMusico = escalaMusicoRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Escala músico não encontrada"));

		Usuario substituto = usuarioRepository.findById(dto.getUsuarioSubstitutoId())
				.orElseThrow(() -> new ResourceNotFoundException("Usuário substituto não encontrado"));

		escalaMusico.setSubstituido(true);
		escalaMusico.setUsuarioSubstituto(substituto);
		escalaMusico.setMotivoSubstituicao(dto.getMotivoSubstituicao());

		EscalaMusico escalaMusicoAtualizado = escalaMusicoRepository.save(escalaMusico);

		verificarFechamentoEscala(escalaMusico.getEscala().getId());

		return converterParaDTO(escalaMusicoAtualizado);
	}
  
	  
	public List<EscalaMusicoResponseDTO> listarMinhasEscalas() {
	    Long usuarioId = securityUtils.usuarioId();
	    return escalaMusicoRepository.findByUsuarioId(usuarioId)
	            .stream()
	            .map(this::converterParaDTO)
	            .toList();
	}

	public EscalaMusicoResponseDTO confirmarMinhaEscala(Long escalaId, Boolean confirmado) {
	    Long usuarioId = securityUtils.usuarioId();
	    // Busca a relação entre esse músico e essa escala
	    EscalaMusico em = escalaMusicoRepository.findByEscalaId(escalaId).stream()
	            .filter(m -> m.getUsuario().getId().equals(usuarioId))
	            .findFirst()
	            .orElseThrow(() -> new ResourceNotFoundException("Você não está escalado nesta escala"));
	            
	    em.setConfirmado(confirmado);
	    EscalaMusico salvo = escalaMusicoRepository.save(em);
	    verificarFechamentoEscala(escalaId);
	    return converterParaDTO(salvo);
	}
	
	private EscalaMusicoResponseDTO converterParaDTO(EscalaMusico escalaMusico) {

		EscalaMusicoResponseDTO dto = new EscalaMusicoResponseDTO();

		dto.setId(escalaMusico.getId());

		dto.setEscalaId(escalaMusico.getEscala().getId());

		dto.setCulto(escalaMusico.getEscala().getCulto());
		
		 
	    dto.setDataEscala(escalaMusico.getEscala().getDataEscala()); 
	    
	    dto.setHorario(escalaMusico.getEscala().getHorario());

		dto.setUsuarioId(escalaMusico.getUsuario().getId());

		dto.setNomeUsuario(escalaMusico.getUsuario().getNome());

		if (escalaMusico.getUsuario().getInstrumento() != null) {

			dto.setInstrumento(escalaMusico.getUsuario().getInstrumento().getNome());
		}

		dto.setConfirmado(escalaMusico.getConfirmado());

		dto.setObservacao(escalaMusico.getObservacao());

		dto.setSubstituido(escalaMusico.getSubstituido());

		if (escalaMusico.getUsuarioSubstituto() != null) {

			dto.setUsuarioSubstitutoId(escalaMusico.getUsuarioSubstituto().getId());

			dto.setNomeSubstituto(escalaMusico.getUsuarioSubstituto().getNome());
		}

		dto.setMotivoSubstituicao(escalaMusico.getMotivoSubstituicao());

		return dto;
	}

	private void verificarFechamentoEscala(Long escalaId) {

	    List<EscalaMusico> musicos =
	            escalaMusicoRepository.findByEscalaId(escalaId);

	    boolean todosConfirmados = musicos.stream()
	            .allMatch(m ->
	                    Boolean.TRUE.equals(m.getConfirmado())
	                    || Boolean.TRUE.equals(m.getSubstituido()));

	    if (todosConfirmados) {

	    		
	        escalaService.fecharEscala(escalaId);
	    }
	}

}