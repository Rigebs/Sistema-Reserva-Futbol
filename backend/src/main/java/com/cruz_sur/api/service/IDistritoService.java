package com.cruz_sur.api.service;

import com.cruz_sur.api.model.Distrito;

import java.util.List;
import java.util.Optional;

public interface IDistritoService {
    List<Distrito> all();
    Optional<Distrito> byId(Long id);
    List<Distrito> findProvinciaById(Long provinciaId);
}
