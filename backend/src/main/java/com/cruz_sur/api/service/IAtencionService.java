package com.cruz_sur.api.service;

import com.cruz_sur.api.model.Atencion;

import java.util.List;
import java.util.Optional;

public interface IAtencionService {
    Atencion save(Atencion atencion);
    Atencion update(Long id, Atencion atencion);
    List<Atencion> all();
    Atencion changeStatus(Long id, Integer status);
    Optional<Atencion> findById(Long id);

}
