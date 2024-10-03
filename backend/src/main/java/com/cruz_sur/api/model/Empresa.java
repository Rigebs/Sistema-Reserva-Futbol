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

    @Column(name = "razon_social", nullable = false, length = 100)
    private String razonSocial;

    @Column(nullable = false, length = 11)
    private String telefono;

    @Column(length = 150)
    private String direccion;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "distrito_id")
    @JsonIgnore
    private Distrito  distrito;
}
