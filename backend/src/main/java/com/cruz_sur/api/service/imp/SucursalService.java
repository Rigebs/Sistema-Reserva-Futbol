package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.model.Sucursal;
import com.cruz_sur.api.repository.SucursalRepository;
import com.cruz_sur.api.service.ISucursalService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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
        return sucursalRepository.save(sucursal);
    }

    @Override
    public Sucursal update(Long id, Sucursal sucursal) {
        Sucursal sucursal1 = sucursalRepository.findById(id).orElseThrow(
                ()->new RuntimeException("sucursal no encontrado")
        );
        sucursal1.setNombre(sucursal.getNombre());
        sucursal1.setUsuarioCreacion(sucursal.getUsuarioCreacion());
        sucursal1.setUsuarioModificacion(sucursal.getUsuarioModificacion());
        sucursal1.setFechaModificacion(sucursal.getFechaModificacion());
        sucursal1.setCompania(sucursal.getCompania());
        return sucursalRepository.save(sucursal1);
    }

    @Override
    public Sucursal changeStatus(Long id, Integer status) {
        Sucursal sucursal = sucursalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sucursal  no encontrada"));

        sucursal.setEstado(status == 1 ? '1' : '0');
        return sucursalRepository.save(sucursal);
    }

    @Override
    public List<Sucursal> findCompaniaById(Long companiaId) {
        return sucursalRepository.findCompaniaById(companiaId);
    }
}
