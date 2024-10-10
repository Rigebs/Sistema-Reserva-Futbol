package com.cruz_sur.api.service;

import com.cruz_sur.api.model.Campo;

import java.util.List;
import java.util.Optional;

public interface ICampoService {
    Campo save(Campo campo);
    Campo update(Long id, Campo campo);
    List<Campo> all();
    Campo changeStatus(Long id, Integer status);
    Optional<Campo> byId(Long id);
}
