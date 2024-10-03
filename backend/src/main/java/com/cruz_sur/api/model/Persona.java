package com.cruz_sur.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "persona")
public class Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dni", nullable = false, length = 8)
    private String dni;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(name = "ape_paterno", nullable = false, length = 50)
    private String apePaterno;

    @Column(name = "ape_materno", nullable = false, length = 50)
    private String apeMaterno;

    @Column(length = 9)
    private String celular;

    @Column(length = 250)
    private String correo;

    @Column(name = "fecha_nac")
    private LocalDate fechaNac;

    @Column(length = 2)
    private String genero;

    @Column(length = 150)
    private String direccion;

    @Column(name = "usuario_creacion", length = 20)
    private String usuarioCreacion;

    @Column(name = "fecha_creacion", columnDefinition = "DATETIME DEFAULT GETDATE()")
    private LocalDateTime fechaCreacion;


    @Column(name = "usuario_modificacion", length = 20)
    private String usuarioModificacion;

    @Column(name = "fecha_modificacion", columnDefinition = "DATETIME DEFAULT GETDATE()")
    private LocalDateTime fechaModificacion;


    @Column(length = 1)
    private Character estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "distrito_id")
    @JsonIgnore
    private Distrito distrito;

}
