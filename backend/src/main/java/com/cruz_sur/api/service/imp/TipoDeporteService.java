package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.model.TipoDeporte;
import com.cruz_sur.api.repository.TipoDeporteRepository;
import com.cruz_sur.api.service.ITipoDeporteService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TipoDeporteService implements ITipoDeporteService {
    private final TipoDeporteRepository tipoDeporteRepository;

    @Override
    public List<TipoDeporte> findAll() {
        return tipoDeporteRepository.findAll();
    }

    @Override
    public Optional<TipoDeporte> findById(Long id) {
        return tipoDeporteRepository.findById(id);
    }

    @Transactional
    @Override
    public TipoDeporte save(TipoDeporte tipoDeporte) {
        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        tipoDeporte.setUsuarioCreacion(authenticatedUsername);
        tipoDeporte.setFechaCreacion(LocalDateTime.now());
        tipoDeporte.setEstado('1');
        return tipoDeporteRepository.save(tipoDeporte);
    }

    @Override
    public TipoDeporte update(Long id, TipoDeporte tipoDeporte) {
        TipoDeporte existingTipoDeporte = tipoDeporteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TipoDeporte no encontrado"));

        existingTipoDeporte.setNombre(tipoDeporte.getNombre());

        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        existingTipoDeporte.setUsuarioModificacion(authenticatedUsername);
        existingTipoDeporte.setFechaModificacion(LocalDateTime.now());

        return tipoDeporteRepository.save(existingTipoDeporte);
    }

    @Override
    @Transactional
    public TipoDeporte changeStatus(Long id, Integer status) {
        TipoDeporte tipoDeporte = tipoDeporteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TipoDeporte no encontrado"));

        tipoDeporte.setEstado(status == 1 ? '1' : '0');

        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        tipoDeporte.setUsuarioModificacion(authenticatedUsername);
        tipoDeporte.setFechaModificacion(LocalDateTime.now());

        return tipoDeporteRepository.save(tipoDeporte);
    }
}
