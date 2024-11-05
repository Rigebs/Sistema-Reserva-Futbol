package com.cruz_sur.api.service;

import com.cruz_sur.api.dto.CampoDTO;
import com.cruz_sur.api.model.Campo;

import java.util.List;
import java.util.Optional;

public interface ICampoService {
    Campo save(Campo campo);
    Campo update(Long id, Campo campo);
    List<CampoDTO> all();
    Campo changeStatus(Long id, Integer status);
    Optional<CampoDTO> byId(Long id);

    List<CampoDTO> findByUsuarioIdWithSede(Long usuarioId);

    List<CampoDTO> findAllWithSede();
}
