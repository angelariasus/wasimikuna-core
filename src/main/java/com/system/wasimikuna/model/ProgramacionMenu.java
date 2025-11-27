package com.system.wasimikuna.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "Programacion_Menu")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramacionMenu {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "programacion_id")
    private Long programacionId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institucion_id", nullable = false)
    private InstitucionEducativa institucion;
    
    @Column(name = "fecha_consumo", nullable = false)
    private LocalDate fechaConsumo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plato_id", nullable = false)
    private Plato plato;
    
    @Column(name = "cantidad_raciones", nullable = false)
    private Integer cantidadRaciones;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_preparacion", length = 20)
    private EstadoPreparacion estadoPreparacion = EstadoPreparacion.PLANIFICADO;
    
    @Column(name = "fecha_registro")
    private LocalDate fechaRegistro;
    
    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDate.now();
    }
    
    public enum EstadoPreparacion {
        PLANIFICADO, COCINADO, SERVIDO
    }
}