package com.system.wasimikuna.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.sql.Timestamp;

@Entity
@Table(name = "Envio")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Envio {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "envio_id")
    private Long envioId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_compra_id", nullable = false)
    private OrdenCompra ordenCompra;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institucion_id", nullable = false)
    private InstitucionEducativa institucion;
    
    @Column(name = "conductor_nombre", length = 100)
    private String conductorNombre;
    
    @Column(name = "placa_vehiculo", length = 10)
    private String placaVehiculo;
    
    @Column(name = "fecha_salida")
    private Timestamp fechaSalida;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_envio", length = 20)
    private EstadoEnvio estadoEnvio = EstadoEnvio.PENDIENTE;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_despacho_id")
    private UsuarioSistema usuarioDespacho;
    
    @PrePersist
    protected void onCreate() {
        fechaSalida = new Timestamp(System.currentTimeMillis());
    }
    
    public enum EstadoEnvio {
        PENDIENTE, EN_RUTA, ENTREGADO, DEVUELTO
    }
}