package com.cruz_sur.api.service;

import com.cruz_sur.api.model.Sede;

import java.util.List;
import java.util.Optional;

public interface ISedeService {
    List<Sede> all();
    Optional<Sede> byId(Long id);
    Sede save(Sede sede);
    Sede update(Long id, Sede sede);
    Sede changeStatus(Long id, Integer status);
    List<Sede> findSucursalById(Long sucursalId);

}
