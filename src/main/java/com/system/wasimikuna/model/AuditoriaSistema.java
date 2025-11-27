package com.system.wasimikuna.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.sql.Timestamp;

@Entity
@Table(name = "Auditoria_Sistema")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriaSistema {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auditoria_id")
    private Long auditoriaId;
    
    @Column(name = "tabla_afectada", length = 50)
    private String tablaAfectada;
    
    @Column(name = "accion", length = 10)
    private String accion;
    
    @Column(name = "id_registro")
    private Long idRegistro;
    
    @Column(name = "usuario_db", length = 50)
    private String usuarioDb;
    
    @Column(name = "fecha")
    private Timestamp fecha;
    
    @Column(name = "datos_anteriores", length = 4000)
    private String datosAnteriores;
    
    @PrePersist
    protected void onCreate() {
        fecha = new Timestamp(System.currentTimeMillis());
    }
}