package com.cruz_sur.api.repository;

import com.cruz_sur.api.model.Campo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampoRepository extends JpaRepository<Campo, Long> {
}
