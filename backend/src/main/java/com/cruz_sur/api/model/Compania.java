package com.cruz_sur.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private String celular;

    @Column(name = "horaInicio", nullable = false)
    private Time horaInicio;

    @Column(name = "horaFin", nullable = false)
    private Time horaFin;

    @OneToMany(mappedBy = "compania", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Opinion> opiniones = new ArrayList<>();

    private String usuarioCreacion;
    private LocalDateTime fechaCreacion;
    private String usuarioModificacion;
    private LocalDateTime fechaModificacion;

    @Column(name = "estado", nullable = false)
    private Character estado ;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @ManyToOne
    @JoinColumn(name = "imagen_id", nullable = false)
    private Imagen imagen; // Imagen principal

    @ManyToOne
    @JoinColumn(name = "qr_imagen_id", nullable = true)
    private Imagen qrImagen; // Imagen para el código QR
}
