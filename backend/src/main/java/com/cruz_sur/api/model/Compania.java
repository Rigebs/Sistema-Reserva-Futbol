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
    @Column(name = "fecha_creacion", columnDefinition = "DATETIME DEFAULT GETDATE()")
    private LocalDateTime fechaCreacion;
    private String usuarioModificacion;
    private LocalDateTime fechaModificacion;

    @Column(name = "estado", nullable = false)
    private Character estado = '1';

    @ManyToOne
    @JoinColumn(name = "imagen_id", nullable = false)
    private Imagen imagen;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;
}
