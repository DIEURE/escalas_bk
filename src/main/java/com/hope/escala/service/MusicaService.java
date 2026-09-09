package com.hope.escala.service;

import com.hope.escala.dto.request.MusicaRequestDTO;
import com.hope.escala.dto.response.MusicaResponseDTO;
import com.hope.escala.entity.Categoria;
import com.hope.escala.entity.Musica;
import com.hope.escala.repository.CategoriaRepository;
import com.hope.escala.repository.MusicaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MusicaService {

    private final MusicaRepository musicaRepository;
    private final CategoriaRepository categoriaRepository;

    public MusicaService(MusicaRepository musicaRepository, CategoriaRepository categoriaRepository) {
        this.musicaRepository = musicaRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional
    public MusicaResponseDTO salvar(MusicaRequestDTO dto) {
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

        if (dto.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                    .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
            musica.setCategoria(categoria);
        } else {
            musica.setCategoria(null);
        }

        Musica salva = musicaRepository.save(musica);
        return new MusicaResponseDTO(salva);
    }

    @Transactional
    public MusicaResponseDTO atualizar(Long id, MusicaRequestDTO dto) {
        Musica musica = musicaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Música não encontrada"));

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
                    .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
            musica.setCategoria(categoria);
        } else {
            musica.setCategoria(null);
        }

        Musica atualizada = musicaRepository.save(musica);
        return new MusicaResponseDTO(atualizada);
    }

    public List<MusicaResponseDTO> listarTodas() {
        return musicaRepository.findAll()
                .stream()
                .map(MusicaResponseDTO::new)
                .collect(Collectors.toList());
    }

    public MusicaResponseDTO buscarPorId(Long id) {
        Musica musica = musicaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Música não encontrada"));
        return new MusicaResponseDTO(musica);
    }

    @Transactional
    public void desativar(Long id) {
        Musica musica = musicaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Música não encontrada"));
        musica.setAtiva(false);
        musicaRepository.save(musica);
    }
}
