package com.cruz_sur.api.service;

import com.cruz_sur.api.model.MetodoPago;

import java.util.List;
import java.util.Optional;

public interface IMetodoPagoService {
    MetodoPago save(MetodoPago metodoPago);
    MetodoPago update(Long id, MetodoPago metodoPago);
    List<MetodoPago> all();
    MetodoPago changeStatus(Long id, Integer status);
    Optional<MetodoPago> byId(Long id);
}
