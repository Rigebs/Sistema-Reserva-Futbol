package com.cruz_sur.api.service;

import com.cruz_sur.api.model.TipoDeporte;

import java.util.List;
import java.util.Optional;

public interface ITipoDeporteService {
    TipoDeporte save(TipoDeporte tipoDeporte);
    Optional<TipoDeporte> findById(Long id);
    List<TipoDeporte> findAll();
    TipoDeporte update(Long id, TipoDeporte tipoDeporte);
    TipoDeporte changeStatus(Long id, Integer status);

}
