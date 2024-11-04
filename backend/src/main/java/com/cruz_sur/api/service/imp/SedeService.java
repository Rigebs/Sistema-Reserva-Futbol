package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.model.Sede;
import com.cruz_sur.api.repository.SedeRepository;
import com.cruz_sur.api.service.ISedeService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SedeService implements ISedeService {
    private final SedeRepository sedeRepository;

    @Override
    public List<Sede> all() {
        return sedeRepository.findAll();
    }

    @Override
    public Optional<Sede> byId(Long id) {
        return sedeRepository.findById(id);
    }

    @Override
    public Sede save(Sede sede) {
        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        sede.setUsuarioCreacion(authenticatedUsername);
        sede.setFechaCreacion(LocalDateTime.now());
        sede.setEstado('1'); // Asumiendo que '1' significa activo

        return sedeRepository.save(sede);
    }

    @Override
    public Sede update(Long id, Sede sede) {
        Sede existingSede = sedeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sede no encontrada"));

        existingSede.setNombre(sede.getNombre());
        existingSede.setHoraInicio(sede.getHoraInicio());
        existingSede.setHoraFin(sede.getHoraFin());
        existingSede.setSucursal(sede.getSucursal());

        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        existingSede.setUsuarioModificacion(authenticatedUsername);
        existingSede.setFechaModificacion(LocalDateTime.now());

        return sedeRepository.save(existingSede);
    }

    @Override
    public Sede changeStatus(Long id, Integer status) {
        Sede sede = sedeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sede no encontrada"));

        sede.setEstado(status == 1 ? '1' : '0');
        return sedeRepository.save(sede);
    }

    @Override
    public List<Sede> findSucursalById(Long sucursalId) {
        return sedeRepository.findBySucursalId(sucursalId);
    }
}
