package com.cruz_sur.api.repository;

import com.cruz_sur.api.model.TipoDeporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoDeporteRepository extends JpaRepository<TipoDeporte, Long> {

}
