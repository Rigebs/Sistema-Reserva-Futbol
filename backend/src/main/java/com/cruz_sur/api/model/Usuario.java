package com.cruz_sur.api.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String logeo;
    private String clave;
    private String email;
    private String googleId;
    @Column(name = "imagen_url", columnDefinition = "nvarchar(max)")
    private String imagenUrl;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST) // O CascadeType.ALL si deseas más flexibilidad
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private Set<Rol> rol = new HashSet<>();


    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "sede_id")
    private Sede sede;

    @Column(name = "estado", nullable = false)
    private char estado = '1';

    private String usuarioCreacion;
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    private String usuarioModificacion;
    private LocalDateTime fechaModificacion;

    @ManyToOne
    @JoinColumn(name = "empleado_id")
    private Empleado empleado;
}
