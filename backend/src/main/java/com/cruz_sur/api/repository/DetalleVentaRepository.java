package com.cruz_sur.api.repository;

import com.cruz_sur.api.model.Campo;
import com.cruz_sur.api.model.DetalleVenta;
import com.cruz_sur.api.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {
    List<DetalleVenta> findByVenta(Reserva reserva);

}
