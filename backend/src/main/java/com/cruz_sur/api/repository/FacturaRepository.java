package com.cruz_sur.api.repository;

import com.cruz_sur.api.model.Factura;
import com.cruz_sur.api.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {
    Optional<Factura> findByReserva(Reserva reserva);

}
