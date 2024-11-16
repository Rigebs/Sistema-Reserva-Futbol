package com.cruz_sur.api.repository;

import com.cruz_sur.api.model.Cliente;
import com.cruz_sur.api.model.Empresa;
import com.cruz_sur.api.model.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findById(Long id);
    Optional<Cliente> findByPersona(Persona existingPersona);
    Optional<Cliente> findByEmpresa(Empresa empresa);

}