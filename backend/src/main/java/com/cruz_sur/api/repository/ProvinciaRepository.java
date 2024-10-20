package com.cruz_sur.api.repository;

import com.cruz_sur.api.model.Provincia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProvinciaRepository extends JpaRepository<Provincia, Long> {
    List<Provincia> findDepartamentoById(Long departamentoId );
}
