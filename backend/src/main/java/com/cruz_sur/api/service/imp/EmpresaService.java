package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.model.Empresa;
import com.cruz_sur.api.repository.EmpresaRepository;
import com.cruz_sur.api.service.IEmpresaService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EmpresaService implements IEmpresaService {
    private final EmpresaRepository empresaRepository;

    @Override
    public List<Empresa> all() {
        return empresaRepository.findAll();
    }

    @Override
    public Optional<Empresa> byId(Long id) {
        return empresaRepository.findById(id);
    }

    @Override
    public Empresa update(Long id, Empresa empresa) {
        Empresa existingEmpresa = empresaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

        existingEmpresa.setRuc(empresa.getRuc());
        existingEmpresa.setRazonSocial(empresa.getRazonSocial());
        existingEmpresa.setTelefono(empresa.getTelefono());
        existingEmpresa.setDireccion(empresa.getDireccion());
        existingEmpresa.setUsuarioModificacion(empresa.getUsuarioModificacion());
        existingEmpresa.setFechaModificacion(empresa.getFechaModificacion());
        existingEmpresa.setDistrito(empresa.getDistrito());

        return empresaRepository.save(existingEmpresa);
    }

    @Override
    public Empresa save(Empresa empresa) {
        return empresaRepository.save(empresa);
    }

    @Override
    public Empresa changeStatus(Long id, Integer status) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

        empresa.setEstado(status == 1 ? '1' : '0');
        return empresaRepository.save(empresa);
    }
}
