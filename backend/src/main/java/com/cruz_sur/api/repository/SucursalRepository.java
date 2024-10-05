package com.cruz_sur.api.repository;

import com.cruz_sur.api.model.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SucursalRepository extends JpaRepository<Sucursal, Long> {
    List<Sucursal> findCompaniaById(Long companiaId );

}
