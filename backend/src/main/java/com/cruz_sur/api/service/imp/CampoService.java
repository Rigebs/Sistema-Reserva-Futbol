package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.dto.CampoDTO;
import com.cruz_sur.api.dto.CamposHomeDTO;
import com.cruz_sur.api.model.Campo;
import com.cruz_sur.api.model.TipoDeporte;
import com.cruz_sur.api.model.User;
import com.cruz_sur.api.repository.CampoRepository;
import com.cruz_sur.api.repository.TipoDeporteRepository;
import com.cruz_sur.api.repository.UserRepository;
import com.cruz_sur.api.service.ICampoService;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CampoService implements ICampoService {

    private final CampoRepository campoRepository;
    private final UserRepository userRepository;
    private final TipoDeporteRepository tipoDeporteRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Campo save(Campo campo) {
        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User usuario = userRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getSede() == null || usuario.getSede().getId() == null) {
            throw new RuntimeException("El usuario debe estar asociado a una sede para crear un campo.");
        }

        TipoDeporte tipoDeporte = tipoDeporteRepository.findById(campo.getTipoDeporte().getId())
                .orElseThrow(() -> new RuntimeException("Tipo de Deporte no encontrado"));

        campo.setUsuarioCreacion(authenticatedUsername);
        campo.setFechaCreacion(LocalDateTime.now());
        campo.setEstado('1');
        campo.setUsuario(usuario);
        campo.setTipoDeporte(tipoDeporte);

        return campoRepository.save(campo);
    }

    @Override
    public Campo update(Long id, Campo campo) {
        Campo campoExistente = campoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campo no encontrado"));

        TipoDeporte tipoDeporte = tipoDeporteRepository.findById(campo.getTipoDeporte().getId())
                .orElseThrow(() -> new RuntimeException("Tipo de Deporte no encontrado"));

        campoExistente.setNombre(campo.getNombre());
        campoExistente.setPrecio(campo.getPrecio());
        campoExistente.setDescripcion(campo.getDescripcion());
        campoExistente.setTipoDeporte(tipoDeporte);

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
        return campoRepository.findByUsuario_IdAndUsuario_SedeIsNotNullAndEstado(usuarioId, '1')
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private CampoDTO toDTO(Campo campo) {
        return new CampoDTO(
                campo.getId(),
                campo.getNombre(),
                campo.getPrecio(),
                campo.getDescripcion(),
                campo.getEstado(),
                campo.getUsuario() != null ? campo.getUsuario().getId() : null,
                campo.getTipoDeporte() != null ? campo.getTipoDeporte().getId() : null
        );
    }

    public List<CamposHomeDTO> getAvailableSedesAndCamposWithSede(Long usuarioId, String distritoNombre, String provinciaNombre, String departamentoNombre, String fechaReserva, String tipoDeporteNombre) {
        String sql = "{CALL GetAvailableSedes(?, ?, ?, ?, ?)}";

        List<CamposHomeDTO> availableSedes = jdbcTemplate.query(
                sql,
                new Object[]{distritoNombre, provinciaNombre, departamentoNombre, fechaReserva, tipoDeporteNombre},
                (rs, rowNum) -> {
                    String tipoDeporteStr = rs.getString("tipo_deporte_nombre");
                    List<String> tipoDeporteList = tipoDeporteStr != null ? Arrays.asList(tipoDeporteStr.split(",")) : new ArrayList<>();
                    return new CamposHomeDTO(
                            rs.getLong("user_id"),
                            rs.getLong("compania_id"),
                            rs.getString("compania_nombre"),
                            rs.getString("compania_imagen_url"),
                            rs.getString("direccion"),
                            tipoDeporteList,
                            new ArrayList<>()
                    );
                }
        );

        availableSedes.forEach(sede -> {
            List<CampoDTO> camposWithSede = findByUsuarioIdWithSede(sede.getUserId());
            sede.setCamposWithSede(camposWithSede);
        });

        return availableSedes;
    }


}
