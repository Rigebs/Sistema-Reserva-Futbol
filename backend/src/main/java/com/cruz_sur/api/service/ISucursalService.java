package com.cruz_sur.api.service;

import com.cruz_sur.api.model.Sucursal;

import java.util.List;
import java.util.Optional;

public interface ISucursalService {
    List<Sucursal> all();
    Optional<Sucursal> byId(Long id);
    Sucursal save(Sucursal sucursal);
    Sucursal update(Long id, Sucursal sucursal);
    Sucursal changeStatus(Long id, Integer status);
    List<Sucursal> findCompaniaById(Long companiaId);

}
