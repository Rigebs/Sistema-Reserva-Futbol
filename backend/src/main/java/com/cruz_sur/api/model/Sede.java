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
@Table(name = "sede")
public class Sede {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String usuarioCreacion;
    @Column(name = "fecha_creacion", columnDefinition = "DATETIME DEFAULT GETDATE()")
    private LocalDateTime fechaCreacion;
    private String usuarioModificacion;
    private LocalDateTime fechaModificacion;

    @Column(name = "estado", nullable = false)
    private Character estado;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sucursal_id", nullable = false)
    private Sucursal sucursal;
}
