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
@Table(name = "atencion")
public class Atencion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "horaInicio", nullable = false)
    private LocalDateTime horaInicio;

    @Column(name = "horaFin", nullable = false)
    private LocalDateTime horaFin;

    private LocalDateTime fechaCreacion = LocalDateTime.now();

    private LocalDateTime fechaModificacion = LocalDateTime.now();

    private String usuarioCreacion;

    private String usuarioModificacion;

    @Column(name = "estado", nullable = false)
    private String estado;
}
