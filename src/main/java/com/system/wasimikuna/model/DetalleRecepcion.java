package com.system.wasimikuna.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.sql.Blob;
import java.time.LocalDate;

@Entity
@Table(name = "Detalle_Recepcion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleRecepcion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "det_recepcion_id")
    private Long detRecepcionId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recepcion_id", nullable = false)
    private Recepcion recepcion;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
    
    @Column(name = "lote_fabricacion", length = 50, nullable = false)
    private String loteFabricacion;
    
    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;
    
    @Column(name = "cantidad_recibida", nullable = false)
    private Integer cantidadRecibida;
    
    @Column(name = "cantidad_rechazada")
    private Integer cantidadRechazada = 0;
    
    @Column(name = "motivo_rechazo", length = 100)
    private String motivoRechazo;
    
    @Lob
    @Column(name = "evidencia_archivo")
    private Blob evidenciaArchivo;
    
    @Column(name = "evidencia_mime_type", length = 50)
    private String evidenciaMimeType;
    
    @Column(name = "evidencia_nombre_archivo", length = 100)
    private String evidenciaNombreArchivo;
}