package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.model.Empresa;
import com.cruz_sur.api.repository.EmpresaRepository;
import com.cruz_sur.api.service.IEmpresaService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import com.cruz_sur.api.model.Cliente;
import com.cruz_sur.api.repository.ClienteRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class EmpresaService implements IEmpresaService {
    private final EmpresaRepository empresaRepository;
    private final ClienteRepository clienteRepository;

    @Override
    public List<Empresa> all() {
        return empresaRepository.findAll();
    }

    @Override
    public Optional<Empresa> byId(Long id) {
        return empresaRepository.findById(id);
    }

    @Transactional
    @Override
    public Empresa save(Empresa empresa) {
        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        empresa.setUsuarioCreacion(authenticatedUsername);
        empresa.setFechaCreacion(LocalDateTime.now());
        empresa.setEstado('1');
        Empresa newEmpresa = empresaRepository.save(empresa);

        Cliente cliente = new Cliente();
        cliente.setEmpresa(newEmpresa);
        cliente.setEstado('1');
        cliente.setUsuarioCreacion(authenticatedUsername);
        cliente.setFechaCreacion(LocalDateTime.now());

        clienteRepository.save(cliente);
        return newEmpresa;
    }

    @Override
    public Empresa update(Long id, Empresa empresa) {
        Empresa existingEmpresa = empresaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));
        existingEmpresa.setRuc(empresa.getRuc());
        existingEmpresa.setRazonSocial(empresa.getRazonSocial());
        existingEmpresa.setTelefono(empresa.getTelefono());
        existingEmpresa.setDireccion(empresa.getDireccion());

        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        existingEmpresa.setUsuarioModificacion(authenticatedUsername);
        existingEmpresa.setFechaModificacion(LocalDateTime.now());
        existingEmpresa.setDistrito(empresa.getDistrito());

        return empresaRepository.save(existingEmpresa);
    }

    @Override
    @Transactional
    public Empresa changeStatus(Long id, Integer status) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

        empresa.setEstado(status == 1 ? '1' : '0');

        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        empresa.setUsuarioModificacion(authenticatedUsername);
        empresa.setFechaModificacion(LocalDateTime.now());

        return empresaRepository.save(empresa);
    }
}
