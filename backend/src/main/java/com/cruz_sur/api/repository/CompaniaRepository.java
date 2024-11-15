package com.cruz_sur.api.repository;

import com.cruz_sur.api.model.Compania;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompaniaRepository extends JpaRepository<Compania, Long> {
}
