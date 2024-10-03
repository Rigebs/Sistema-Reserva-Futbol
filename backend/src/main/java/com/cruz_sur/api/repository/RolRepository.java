package com.cruz_sur.api.repository;

import com.cruz_sur.api.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNomtipo(String nomtipo);
}
