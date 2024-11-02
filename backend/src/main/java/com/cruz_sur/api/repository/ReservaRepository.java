package com.cruz_sur.api.repository;

import com.cruz_sur.api.model.Reserva;
import com.cruz_sur.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByUsuario(User usuario);


}
