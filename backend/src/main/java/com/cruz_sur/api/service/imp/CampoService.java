package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.dto.CampoDTO;
import com.cruz_sur.api.dto.CampoSedeDTO;
import com.cruz_sur.api.model.Campo;
import com.cruz_sur.api.model.User;
import com.cruz_sur.api.repository.CampoRepository;
import com.cruz_sur.api.repository.UserRepository;
import com.cruz_sur.api.service.ICampoService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
        User usuario = userRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getSede() == null || usuario.getSede().getId() == null) {
            throw new RuntimeException("El usuario debe estar asociado a una sede para crear un campo.");
        }

        campo.setUsuarioCreacion(authenticatedUsername);
        campo.setFechaCreacion(LocalDateTime.now());
        campo.setEstado('1');
        campo.setUsuario(usuario);

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
        return campoRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
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
                .map(this::toDTO);
    }

    @Override
    public List<CampoDTO> findByUsuarioIdWithSede(Long usuarioId) {
        return campoRepository.findByUsuario_IdAndUsuario_SedeIsNotNull(usuarioId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }


    @Override
    public List<CampoSedeDTO> findAllSedeInfo() {
        return new ArrayList<>(campoRepository.findAll().stream()
                .collect(Collectors.toMap(
                        campo -> campo.getUsuario().getId(),  // Agrupar por usuarioId
                        campo -> new CampoSedeDTO(
                                campo.getUsuario().getId(),
                                campo.getUsuario().getSede() != null ? campo.getUsuario().getSede().getNombre() : null,
                                campo.getUsuario().getSede() != null && campo.getUsuario().getSede().getSucursal() != null ? campo.getUsuario().getSede().getSucursal().getNombre() : null,
                                campo.getUsuario().getSede() != null && campo.getUsuario().getSede().getSucursal() != null && campo.getUsuario().getSede().getSucursal().getCompania() != null ? campo.getUsuario().getSede().getSucursal().getCompania().getNombre() : null,
                                campo.getUsuario().getSede() != null && campo.getUsuario().getSede().getSucursal() != null && campo.getUsuario().getSede().getSucursal().getCompania() != null ? campo.getUsuario().getSede().getSucursal().getCompania().getCorreo() : null,
                                campo.getUsuario().getSede() != null && campo.getUsuario().getSede().getSucursal() != null && campo.getUsuario().getSede().getSucursal().getCompania() != null ? campo.getUsuario().getSede().getSucursal().getCompania().getPagWeb() : null,
                                campo.getUsuario().getSede() != null && campo.getUsuario().getSede().getSucursal() != null && campo.getUsuario().getSede().getSucursal().getCompania() != null && campo.getUsuario().getSede().getSucursal().getCompania().getImagen() != null ? campo.getUsuario().getSede().getSucursal().getCompania().getImagen().getImageUrl() : null
                        ),
                        (existing, replacement) -> existing  // Mantener el primer elemento encontrado por usuario
                ))
                .values());
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
