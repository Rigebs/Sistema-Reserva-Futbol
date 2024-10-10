package com.cruz_sur.api.service;

import com.cruz_sur.api.model.Rol;
import com.cruz_sur.api.model.Usuario;
import com.cruz_sur.api.model.Cliente;
import com.cruz_sur.api.model.Sede;

import java.util.Optional;

public interface IUsuarioService {
    Usuario save(Usuario usuario);
    Optional<Usuario> findById(Long userId);
    Optional<Usuario> findByEmail(String email);
    Rol getDefaultUserRole();
    Cliente getClienteById(Long clienteId);
    Sede getSedeById(Long sedeId);
}
