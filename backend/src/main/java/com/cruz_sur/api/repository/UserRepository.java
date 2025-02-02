package com.cruz_sur.api.repository;


import com.cruz_sur.api.model.Compania;
import com.cruz_sur.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    Optional<Object> findFirstBySede(Compania compania);

    List<User> findByClienteIsNotNull();

    List<User> findBySedeIsNotNull();
}