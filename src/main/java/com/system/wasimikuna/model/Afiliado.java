package com.system.wasimikuna.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "Afiliado")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Afiliado {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "afiliado_id")
    private Long afiliadoId;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private UsuarioSistema usuario;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", length = 15)
    private TipoAfiliado tipo;
    
    @Column(name = "ruc", length = 11, unique = true, nullable = false)
    private String ruc;
    
    @Column(name = "razon_social", length = 150, nullable = false)
    private String razonSocial;
    
    @Column(name = "direccion", length = 200)
    private String direccion;
    
    @Column(name = "contacto_nombre", length = 100)
    private String contactoNombre;
    
    @Column(name = "contacto_telefono", length = 9)
    private String contactoTelefono;
    
    @Column(name = "estado")
    private Integer estado = 1;
    
    @Column(name = "calificacion_sanitaria")
    private Integer calificacionSanitaria = 100;
    
    @Column(name = "fecha_creacion")
    private LocalDate fechaCreacion;
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDate.now();
    }
    
    public enum TipoAfiliado {
        DISTRIBUIDOR, PROVEEDOR_LOCAL, AGRICULTOR
    }
}