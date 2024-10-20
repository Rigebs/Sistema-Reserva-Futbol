package com.cruz_sur.api.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "departamento")
public class Departamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nombre;

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
}
