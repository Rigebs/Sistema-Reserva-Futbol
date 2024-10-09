package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.model.Usuario;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

import java.util.stream.Collectors;

@AllArgsConstructor
public class UserDetailsimp implements UserDetails {
    @Autowired
    private Usuario usuario;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return usuario.getRol().stream()
                .map(role -> new SimpleGrantedAuthority(role.getNomtipo()))
                .collect(Collectors.toList());
    }


    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return "";
    }
}
