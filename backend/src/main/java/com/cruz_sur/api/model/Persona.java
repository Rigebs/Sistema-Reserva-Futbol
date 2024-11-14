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

    private String dni;

    @Column(unique = true, length = 50)
    private String nombre;

    private String apePaterno;

    private String apeMaterno;

    @Column(length = 9)
    private String celular;

    @Column(length = 250)
    private String correo;

    private LocalDate fechaNac;

    @Column(length = 2)
    private String genero;

    @Column(length = 150)
    private String direccion;

    private String usuarioCreacion;

    private LocalDateTime fechaCreacion;


    private String usuarioModificacion;

    private LocalDateTime fechaModificacion;


    @Column(length = 1)
    private Character estado;

    @ManyToOne
    @JoinColumn(name = "distrito_id")
    private Distrito distrito;
    // En la clase Persona
    public String getNombreCompleto() {
        return nombre + " " + apePaterno + " " + apeMaterno;
    }

}
