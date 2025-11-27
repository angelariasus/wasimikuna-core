package com.system.wasimikuna.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.sql.Blob;
import java.sql.Timestamp;

@Entity
@Table(name = "Recepcion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recepcion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recepcion_id")
    private Long recepcionId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "envio_id", nullable = false)
    private Envio envio;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comite_miembro_id")
    private ComiteGestion comiteMiembro;
    
    @Column(name = "fecha_recepcion")
    private Timestamp fechaRecepcion;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_conformidad", length = 20)
    private EstadoConformidad estadoConformidad;
    
    @Column(name = "observaciones_generales", length = 400)
    private String observacionesGenerales;
    
    @Lob
    @Column(name = "acta_archivo")
    private Blob actaArchivo;
    
    @Column(name = "acta_mime_type", length = 50)
    private String actaMimeType;
    
    @Column(name = "acta_nombre_archivo", length = 100)
    private String actaNombreArchivo;
    
    @PrePersist
    protected void onCreate() {
        fechaRecepcion = new Timestamp(System.currentTimeMillis());
    }
    
    public enum EstadoConformidad {
        CONFORME, OBSERVADO, RECHAZADO
    }
}