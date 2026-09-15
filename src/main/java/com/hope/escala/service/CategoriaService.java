package com.hope.escala.service;

import com.hope.escala.entity.Categoria;
import com.hope.escala.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll();
    }

    public List<Categoria> listarAtivas() {
        return categoriaRepository.findByAtivoTrue();
    }

    public Optional<Categoria> buscarPorId(Long id) {
        return categoriaRepository.findById(id);
    }

    public Categoria salvar(Categoria categoria) {
        if (categoria.getAtivo() == null) {
            categoria.setAtivo(true);
        }
        return categoriaRepository.save(categoria);
    }

    public Categoria atualizar(Long id, Categoria categoriaAtualizada) {
        return categoriaRepository.findById(id).map(categoria -> {
            categoria.setNome(categoriaAtualizada.getNome());
            if (categoriaAtualizada.getAtivo() != null) {
                categoria.setAtivo(categoriaAtualizada.getAtivo());
            }
            return categoriaRepository.save(categoria);
        }).orElseThrow(() -> new RuntimeException("Categoria não encontrada com o ID: " + id));
    }

    public void deletar(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada com o ID: " + id));
        
        // Exclusão lógica (soft delete) recomendada para manter integridade com músicas
        categoria.setAtivo(false);
        categoriaRepository.save(categoria);
        
        // Ou se preferir exclusão física:
        // categoriaRepository.deleteById(id);
    }
}
