package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.model.Sucursal;
import com.cruz_sur.api.repository.SucursalRepository;
import com.cruz_sur.api.service.ISucursalService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SucursalService implements ISucursalService {
    private final SucursalRepository sucursalRepository;

    @Override
    public List<Sucursal> all() {
        return sucursalRepository.findAll();
    }

    @Override
    public Optional<Sucursal> byId(Long id) {
        return sucursalRepository.findById(id);
    }

    @Override
    public Sucursal save(Sucursal sucursal) {
        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        sucursal.setUsuarioCreacion(authenticatedUsername);
        sucursal.setFechaCreacion(LocalDateTime.now());
        sucursal.setEstado('1'); // Asumiendo que '1' significa activo

        return sucursalRepository.save(sucursal);
    }

    @Override
    public Sucursal update(Long id, Sucursal sucursal) {
        Sucursal existingSucursal = sucursalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada"));

        existingSucursal.setNombre(sucursal.getNombre());
        existingSucursal.setCompania(sucursal.getCompania());

        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        existingSucursal.setUsuarioModificacion(authenticatedUsername);
        existingSucursal.setFechaModificacion(LocalDateTime.now());

        return sucursalRepository.save(existingSucursal);
    }

    @Override
    public Sucursal changeStatus(Long id, Integer status) {
        Sucursal sucursal = sucursalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada"));

        sucursal.setEstado(status == 1 ? '1' : '0');
        return sucursalRepository.save(sucursal);
    }

    @Override
    public List<Sucursal> findCompaniaById(Long companiaId) {
        return sucursalRepository.findCompaniaById(companiaId);
    }
}
