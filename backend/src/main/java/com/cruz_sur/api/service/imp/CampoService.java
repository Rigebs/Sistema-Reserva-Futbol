package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.dto.CampoDTO;
import com.cruz_sur.api.model.Campo;
import com.cruz_sur.api.model.User;
import com.cruz_sur.api.repository.CampoRepository;
import com.cruz_sur.api.repository.UserRepository;
import com.cruz_sur.api.service.ICampoService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CampoService implements ICampoService {

    private final CampoRepository campoRepository;
    private final UserRepository userRepository;

    @Override
    public Campo save(Campo campo) {
        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        // Obtiene el usuario autenticado
        User usuario = userRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verifica que el usuario tenga un sedeId
        if (usuario.getSede() == null || usuario.getSede().getId() == null) {
            throw new RuntimeException("El usuario debe estar asociado a una sede para crear un campo.");
        }

        // Establece la información del campo
        campo.setUsuarioCreacion(authenticatedUsername);
        campo.setFechaCreacion(LocalDateTime.now());
        campo.setEstado('1');
        campo.setUsuario(usuario); // Asignar el usuario al campo

        return campoRepository.save(campo);
    }

    @Override
    public Campo update(Long id, Campo campo) {
        Campo campoExistente = campoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campo no encontrado"));

        campoExistente.setNombre(campo.getNombre());
        campoExistente.setPrecio(campo.getPrecio());
        campoExistente.setDescripcion(campo.getDescripcion());

        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        campoExistente.setUsuarioModificacion(authenticatedUsername);
        campoExistente.setFechaModificacion(LocalDateTime.now());

        return campoRepository.save(campoExistente);
    }

    @Override
    public List<CampoDTO> all() {
        List<Campo> campos = campoRepository.findAll();
        // Convert Campo to CampoDTO
        return campos.stream()
                .map(campo -> new CampoDTO(
                        campo.getId(),
                        campo.getNombre(),
                        campo.getPrecio(),
                        campo.getDescripcion(),
                        campo.getEstado(),
                        campo.getUsuario() != null ? campo.getUsuario().getId() : null // Get user ID if available
                ))
                .toList(); // Using Java 16+ stream method
    }


    @Override
    public Campo changeStatus(Long id, Integer status) {
        Campo campo = campoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campo no encontrado"));

        campo.setEstado(status == 1 ? '1' : '0');
        return campoRepository.save(campo);
    }

    @Override
    public Optional<CampoDTO> byId(Long id) {
        return campoRepository.findById(id)
                .map(campo -> new CampoDTO(
                        campo.getId(),
                        campo.getNombre(),
                        campo.getPrecio(),
                        campo.getDescripcion(),
                        campo.getEstado(),
                        campo.getUsuario() != null ? campo.getUsuario().getId() : null // Get user ID if available
                ));
    }

    @Override
    public List<CampoDTO> findByUsuarioIdWithSede(Long usuarioId) {
        List<Campo> campos = campoRepository.findByUsuario_IdAndUsuario_SedeIsNotNull(usuarioId);
        return campos.stream().map(this::toDTO).collect(Collectors.toList());
    }
    @Override
    public List<CampoDTO> findAllWithSede() {
        List<Campo> campos = campoRepository.findByUsuario_SedeIsNotNull();
        return campos.stream().map(this::toDTO).collect(Collectors.toList());
    }


    private CampoDTO toDTO(Campo campo) {
        return new CampoDTO(
                campo.getId(),
                campo.getNombre(),
                campo.getPrecio(),
                campo.getDescripcion(),
                campo.getEstado(),
                campo.getUsuario() != null ? campo.getUsuario().getId() : null
        );
    }

}
