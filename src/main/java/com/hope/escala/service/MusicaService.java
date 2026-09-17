package com.hope.escala.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.hope.escala.dto.request.MusicaRequestDTO;
import com.hope.escala.dto.response.MusicaResponseDTO;
import com.hope.escala.entity.Categoria;
import com.hope.escala.entity.Empresa;
import com.hope.escala.entity.Musica;
import com.hope.escala.exception.ResourceNotFoundException;
import com.hope.escala.repository.CategoriaRepository;
import com.hope.escala.repository.EmpresaRepository;
import com.hope.escala.repository.MusicaRepository;
import com.hope.escala.security.SecurityUtils;

import jakarta.transaction.Transactional;

@Service
public class MusicaService {

    private final MusicaRepository musicaRepository;
    private final CategoriaRepository categoriaRepository;
    private final EmpresaRepository empresaRepository;
    private final SecurityUtils securityUtils;

    public MusicaService(
    		MusicaRepository musicaRepository,
    		CategoriaRepository categoriaRepository,
    		EmpresaRepository empresaRepository,
    		SecurityUtils securityUtils
    ) {
        this.musicaRepository = musicaRepository;
        this.categoriaRepository = categoriaRepository;
        this.empresaRepository = empresaRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional
    public MusicaResponseDTO salvar(MusicaRequestDTO dto) {
        Long empresaId = securityUtils.empresaId();
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));
        
        Musica musica = new Musica();

        musica.setNome(dto.getNome());
        musica.setCantor(dto.getCantor());
        musica.setTom(dto.getTom());
        musica.setBpm(dto.getBpm());
        musica.setCifra(dto.getCifra());
        musica.setCifraUrl(dto.getCifraUrl());
        musica.setLinkPlaylistManual(dto.getLinkPlaylistManual());
        musica.setYoutubeVideoId(dto.getYoutubeVideoId());
        musica.setAtiva(dto.getAtiva() != null ? dto.getAtiva() : true);
        musica.setEmpresa(empresa);

        if (dto.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
            
            // Opcional: Validar se a categoria pertence à mesma empresa
            if (categoria.getEmpresa() != null && !categoria.getEmpresa().getId().equals(empresaId)) {
                throw new ResourceNotFoundException("Categoria não pertence à sua instituição");
            }

            musica.setCategoria(categoria);
        } else {
            musica.setCategoria(null);
        }

        Musica salva = musicaRepository.save(musica);
        return new MusicaResponseDTO(salva);
    }

    @Transactional
    public MusicaResponseDTO atualizar(Long id, MusicaRequestDTO dto) {
        Long empresaId = securityUtils.empresaId();

        Musica musica = musicaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Música não encontrada"));

        // 🟢 Validação de segurança Multi-Tenant
        if (musica.getEmpresa() == null || !musica.getEmpresa().getId().equals(empresaId)) {
            throw new ResourceNotFoundException("Música não pertence à sua instituição");
        }

        musica.setNome(dto.getNome());
        musica.setCantor(dto.getCantor());
        musica.setTom(dto.getTom());
        musica.setBpm(dto.getBpm());
        musica.setCifra(dto.getCifra());
        musica.setCifraUrl(dto.getCifraUrl());
        musica.setLinkPlaylistManual(dto.getLinkPlaylistManual());
        musica.setYoutubeVideoId(dto.getYoutubeVideoId());
        musica.setAtiva(dto.getAtiva() != null ? dto.getAtiva() : musica.getAtiva());

        if (dto.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
            musica.setCategoria(categoria);
        } else {
            musica.setCategoria(null);
        }

        Musica atualizada = musicaRepository.save(musica);
        return new MusicaResponseDTO(atualizada);
    }

    public List<MusicaResponseDTO> listarTodas() {
        Long empresaId = securityUtils.empresaId();
        
        // 🟢 Certifique-se de ter o método findByEmpresaId no MusicaRepository
        return musicaRepository.findByEmpresaId(empresaId)
                .stream()
                .map(MusicaResponseDTO::new)
                .collect(Collectors.toList());
    }

    public MusicaResponseDTO buscarPorId(Long id) {
        Long empresaId = securityUtils.empresaId();

        Musica musica = musicaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Música não encontrada"));

        // 🟢 Validação de segurança Multi-Tenant
        if (musica.getEmpresa() == null || !musica.getEmpresa().getId().equals(empresaId)) {
            throw new ResourceNotFoundException("Música não pertence à sua instituição");
        }

        return new MusicaResponseDTO(musica);
    }

    @Transactional
    public void desativar(Long id) {
        Long empresaId = securityUtils.empresaId();

        Musica musica = musicaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Música não encontrada"));

        // 🟢 Validação de segurança Multi-Tenant
        if (musica.getEmpresa() == null || !musica.getEmpresa().getId().equals(empresaId)) {
            throw new ResourceNotFoundException("Música não pertence à sua instituição");
        }

        musica.setAtiva(false);
        musicaRepository.save(musica);
    }
}
