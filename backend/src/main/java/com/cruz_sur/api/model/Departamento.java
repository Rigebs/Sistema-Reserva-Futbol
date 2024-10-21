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

    private String usuarioCreacion;

    private LocalDateTime fechaCreacion;

    private String usuarioModificacion;

    private LocalDateTime fechaModificacion;

    @Column(length = 1)
    private Character estado;
}
