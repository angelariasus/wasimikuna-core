package com.system.wasimikuna.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.sql.Timestamp;

@Entity
@Table(name = "Incidencia_Sanitaria")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncidenciaSanitaria {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "incidencia_id")
    private Long incidenciaId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institucion_id", nullable = false)
    private InstitucionEducativa institucion;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Producto producto;
    
    @Column(name = "lote_afectado", length = 50)
    private String loteAfectado;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_riesgo", length = 30)
    private TipoRiesgo tipoRiesgo;
    
    @Column(name = "descripcion_detallada", length = 400)
    private String descripcionDetallada;
    
    @Column(name = "fecha_reporte")
    private Timestamp fechaReporte;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_atencion", length = 20)
    private EstadoAtencion estadoAtencion = EstadoAtencion.REPORTADO;
    
    @Column(name = "accion_tomada", length = 200)
    private String accionTomada;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_reportante_id")
    private ComiteGestion usuarioReportante;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monitor_asignado_id")
    private UsuarioSistema monitorAsignado;
    
    @PrePersist
    protected void onCreate() {
        fechaReporte = new Timestamp(System.currentTimeMillis());
    }
    
    public enum TipoRiesgo {
        OLOR_EXTRAÑO, ENVASE_DAÑADO, FECHA_VENCIDA, CUERPO_EXTRAÑO, INTOXICACION
    }
    
    public enum EstadoAtencion {
        REPORTADO, EN_EVALUACION, CONFIRMADO, DESCARTADO
    }
}