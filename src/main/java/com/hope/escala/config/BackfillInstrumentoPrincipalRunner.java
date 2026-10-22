package com.hope.escala.config;

import com.hope.escala.entity.Usuario;
import com.hope.escala.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class BackfillInstrumentoPrincipalRunner implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;

    public BackfillInstrumentoPrincipalRunner(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void run(String... args) {
        var usuarios = usuarioRepository.findAll();
        for (Usuario u : usuarios) {
            if (u.getInstrumentoPrincipal() == null
                    && u.getInstrumentos() != null
                    && !u.getInstrumentos().isEmpty()) {
                u.setInstrumentoPrincipal(u.getInstrumentos().iterator().next());
                usuarioRepository.save(u);
            }
        }
    }
}
