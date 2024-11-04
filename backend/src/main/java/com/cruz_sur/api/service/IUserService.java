package com.cruz_sur.api.service;

import com.cruz_sur.api.model.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    List<User> all();
    Optional<User> byId(Long id);
    User update(Long id, User user);
    User save(User user);
    User changeStatus(Long id, Integer status);
}
