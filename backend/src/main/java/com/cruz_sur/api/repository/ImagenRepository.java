package com.cruz_sur.api.repository;

import com.cruz_sur.api.model.Imagen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImagenRepository extends JpaRepository<Imagen, Long> {
    List<Imagen> findByEstado(char c);
}
