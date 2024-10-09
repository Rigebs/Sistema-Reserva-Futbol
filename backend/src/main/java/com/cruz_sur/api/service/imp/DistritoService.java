package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.model.Distrito;
import com.cruz_sur.api.repository.DistritoRepository;
import com.cruz_sur.api.service.IDistritoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DistritoService implements IDistritoService {

    private final DistritoRepository distritoRepository;

    @Override
    public List<Distrito> all() {
        return distritoRepository.findAll();
    }

    @Override
    public Optional<Distrito> byId(Long id) {
        return distritoRepository.findById(id);
    }

    @Override
    public List<Distrito> findProvinciaById(Long provinciaId) {
        return distritoRepository.findProvinciaById(provinciaId);
    }
}
