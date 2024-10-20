package com.cruz_sur.api.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "horario")
public class Horario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_final", nullable = false)
    private LocalTime horaFinal;

    @Column(name = "fecha_creacion", columnDefinition = "DATETIME DEFAULT GETDATE()")
    private LocalDateTime fechaCreacion;

    private String usuarioModificacion;

    private LocalDateTime fechaModificacion;

    @Column(name = "estado", nullable = false)
    private Character estado;
}
