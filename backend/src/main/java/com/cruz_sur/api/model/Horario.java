package com.cruz_sur.api.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Time;
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

    private Time horaInicio;

    private Time horaFinal;

    private LocalDateTime fechaCreacion;

    private String usuarioModificacion;

    private LocalDateTime fechaModificacion;

    private Character estado;
}
