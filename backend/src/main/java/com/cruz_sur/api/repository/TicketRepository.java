package com.cruz_sur.api.repository;

import com.cruz_sur.api.model.Reserva;
import com.cruz_sur.api.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByReserva(Reserva reserva);

}
