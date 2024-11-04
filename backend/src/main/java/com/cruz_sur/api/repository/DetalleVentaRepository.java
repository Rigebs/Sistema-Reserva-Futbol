package com.cruz_sur.api.repository;

import com.cruz_sur.api.model.DetalleVenta;
import com.cruz_sur.api.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {
    List<DetalleVenta> findByVenta(Reserva reserva);

    // Modificación del método para verificar disponibilidad por campo y fecha de reserva
    List<DetalleVenta> findByCampoIdAndVenta_Fecha(Long campoId, LocalDate fecha);
}
