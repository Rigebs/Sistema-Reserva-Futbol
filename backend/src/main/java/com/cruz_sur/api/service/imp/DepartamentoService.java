package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.model.Departamento;
import com.cruz_sur.api.repository.DepartamentoRepository;
import com.cruz_sur.api.service.IDepartamentoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DepartamentoService implements IDepartamentoService {

    private final DepartamentoRepository departamentoRepository;

    @Override
    public List<Departamento> all() {
        return departamentoRepository.findAll();
    }

    @Override
    public Optional<Departamento> byId(Long id) {
        return departamentoRepository.findById(id);
    }
}
