package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.model.Provincia;
import com.cruz_sur.api.repository.ProvinciaRepository;
import com.cruz_sur.api.service.IProvinciaService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProvinciaService implements IProvinciaService {
    private final ProvinciaRepository provinciaRepository;

    @Override
    public List<Provincia> all() {
        return provinciaRepository.findAll();
    }

    @Override
    public Optional<Provincia> byId(Long id) {
        return provinciaRepository.findById(id);
    }

    @Override
    public List<Provincia> findDepartamentoById(Long departamentoId) {
        return provinciaRepository.findDepartamentoById(departamentoId);
    }
}
