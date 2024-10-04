package com.cruz_sur.api.service;

import com.cruz_sur.api.model.Cargo;

import java.util.List;
import java.util.Optional;

public interface ICargoService {
    Cargo save(Cargo cargo);
    Cargo update(Long id, Cargo cargo);
    List<Cargo> all();
    Cargo changeStatus(Long id, Integer status);
    Optional<Cargo> byId(Long id);
}
