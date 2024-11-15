package com.cruz_sur.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
@Table(name = "opiniones")
public class Opinion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String contenido; // Texto de la opinión del cliente

    @Column(nullable = false)
    private Integer calificacion; // Calificación de 1 a 5, por ejemplo

    private String usuarioCreacion;

    private LocalDateTime fechaCreacion;

    private String usuarioModificacion;

    private LocalDateTime fechaModificacion;
    private Character estado ;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Usuario que es cliente y deja la opinión

    @ManyToOne
    @JoinColumn(name = "compania_id", nullable = false)
    @JsonProperty("compania")
    private Compania compania;


    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }
}
