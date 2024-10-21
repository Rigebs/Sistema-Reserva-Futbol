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
@Table(name = "metodo_pago")
public class MetodoPago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String usuarioCreacion;

    private LocalDateTime fechaCreacion;

    private String usuarioModificacion;

    private LocalDateTime fechaModificacion;

    @Column(name = "estado", nullable = false)
    private Character estado;
}
