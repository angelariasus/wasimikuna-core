package com.system.wasimikuna.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "Comite_Gestion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComiteGestion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "miembro_id")
    private Long miembroId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institucion_id", nullable = false)
    private InstitucionEducativa institucion;
    
    @Column(name = "dni", length = 8, nullable = false)
    private String dni;
    
    @Column(name = "nombre_completo", length = 150, nullable = false)
    private String nombreCompleto;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "cargo", length = 50)
    private CargoComite cargo;
    
    @Column(name = "telefono", length = 9)
    private String telefono;
    
    @Column(name = "fecha_inicio_vigencia")
    private LocalDate fechaInicioVigencia;
    
    @Column(name = "fecha_fin_vigencia")
    private LocalDate fechaFinVigencia;
    
    @Column(name = "estado_activo")
    private Integer estadoActivo = 1;
    
    @PrePersist
    protected void onCreate() {
        fechaInicioVigencia = LocalDate.now();
    }
    
    public enum CargoComite {
        PRESIDENTE, SECRETARIO, VOCAL, PADRE_VIGILANTE
    }
}