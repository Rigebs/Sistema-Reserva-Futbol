package com.cruz_sur.api.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "empresa")
public class Empresa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 11)
    private String ruc;

    private String razonSocial;

    @Column(nullable = false, length = 11)
    private String telefono;

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
    private Distrito  distrito;
}
