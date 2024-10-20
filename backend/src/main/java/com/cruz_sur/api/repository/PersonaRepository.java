package com.cruz_sur.api.repository;

import com.cruz_sur.api.model.Persona;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonaRepository extends JpaRepository<Persona, Long> {
}
