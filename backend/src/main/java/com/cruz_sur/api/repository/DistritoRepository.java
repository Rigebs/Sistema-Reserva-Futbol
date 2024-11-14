package com.cruz_sur.api.repository;

import com.cruz_sur.api.model.Distrito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DistritoRepository extends JpaRepository<Distrito,Long> {
    List<Distrito> findByProvinciaId(Long provinciaId );
}
