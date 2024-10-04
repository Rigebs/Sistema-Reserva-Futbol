package com.cruz_sur.api.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "compania")
public class Compania {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String concepto;
    private String correo;
    private String pagWeb;

    private String usuarioCreacion;
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    private String pcCreacion;
    private String usuarioModificacion;
    private LocalDateTime fechaModificacion;

    @Column(name = "estado", nullable = false)
    private char estado;

    @ManyToOne
    @JoinColumn(name = "imagen_id", nullable = false)
    private Imagen imagen;
}
