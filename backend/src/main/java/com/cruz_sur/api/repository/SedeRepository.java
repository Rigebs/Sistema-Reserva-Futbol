package com.cruz_sur.api.repository;

import com.cruz_sur.api.model.Sede;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SedeRepository extends JpaRepository<Sede, Long> {
    List<Sede> findBySucursalId(Long sucursalId );
}
