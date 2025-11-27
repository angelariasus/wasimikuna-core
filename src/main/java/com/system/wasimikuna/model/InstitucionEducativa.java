package com.system.wasimikuna.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "Institucion_Educativa")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstitucionEducativa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "institucion_id")
    private Long institucionId;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private UsuarioSistema usuario;
    
    @Column(name = "codigo_modular", length = 7, nullable = false)
    private String codigoModular;
    
    @Column(name = "anexo", length = 1)
    private String anexo = "0";
    
    @Column(name = "nombre", length = 150, nullable = false)
    private String nombre;
    
    @Column(name = "direccion", length = 200)
    private String direccion;
    
    @Column(name = "departamento", length = 50)
    private String departamento;
    
    @Column(name = "provincia", length = 50)
    private String provincia;
    
    @Column(name = "distrito", length = 50)
    private String distrito;
    
    @Column(name = "ubigeo", length = 6)
    private String ubigeo;
    
    @Column(name = "fecha_registro")
    private LocalDate fechaRegistro;
    
    @Column(name = "estado_activo")
    private Integer estadoActivo = 1;
    
    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDate.now();
    }
}