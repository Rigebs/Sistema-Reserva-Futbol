package com.cruz_sur.api.repository;

import com.cruz_sur.api.model.Opinion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OpinionRepository extends JpaRepository<Opinion, Long> {
    List<Opinion> findByCompaniaId(Long companiaId);
    List<Opinion> findByUserId(Long userId);

}