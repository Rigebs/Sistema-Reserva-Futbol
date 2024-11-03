package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.model.Atencion;
import com.cruz_sur.api.model.User;
import com.cruz_sur.api.repository.AtencionRepository;
import com.cruz_sur.api.repository.UserRepository;
import com.cruz_sur.api.service.IAtencionService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AtencionService implements IAtencionService {

    private final AtencionRepository atencionRepository;
    private final UserRepository userRepository;

    @Override
    public Atencion save(Atencion atencion) {
        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        atencion.setUsuarioCreacion(authenticatedUsername);
        atencion.setFechaCreacion(LocalDateTime.now());

        User usuario = userRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        atencion.setUsuarioCreacion(usuario.getUsername());

        return atencionRepository.save(atencion);
    }

    @Override
    public Atencion update(Long id, Atencion atencion) {
        Atencion atencionExistente = atencionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Atencion no encontrada"));

        atencionExistente.setHoraInicio(atencion.getHoraInicio());
        atencionExistente.setHoraFin(atencion.getHoraFin());

        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        atencionExistente.setUsuarioModificacion(authenticatedUsername);
        atencionExistente.setFechaModificacion(LocalDateTime.now());

        return atencionRepository.save(atencionExistente);
    }

    @Override
    public List<Atencion> all() {
        return atencionRepository.findAll();
    }

    @Override
    public Atencion changeStatus(Long id, Integer status) {
        Atencion atencion = atencionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Atencion no encontrada"));

        atencion.setEstado(status == 1 ? "Activo" : "Inactivo");
        return atencionRepository.save(atencion);
    }

    @Override
    public Optional<Atencion> findById(Long id) {
        return atencionRepository.findById(id);
    }
}
