package com.cruz_sur.api.repository;

import com.cruz_sur.api.model.Boleta;
import com.cruz_sur.api.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoletaRepository extends JpaRepository<Boleta, Long> {


    Optional<Boleta> findByReserva(Reserva reserva);
}
