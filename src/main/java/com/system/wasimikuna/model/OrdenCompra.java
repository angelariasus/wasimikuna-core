package com.system.wasimikuna.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Orden_Compra")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdenCompra {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orden_compra_id")
    private Long ordenCompraId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "afiliado_id", nullable = false)
    private Afiliado afiliado;
    
    @Column(name = "fecha_emision")
    private LocalDate fechaEmision;
    
    @Column(name = "estado")
    private Integer estado = 0; // 0=PENDIENTE, 1=APROBADA, 2=EN_PROCESO, 3=COMPLETADA
    
    @Column(name = "total", precision = 12, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;
    
    @Column(name = "fecha_entrega_prevista")
    private LocalDate fechaEntregaPrevista;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_creacion_id")
    private UsuarioSistema usuarioCreacion;
    
    @Column(name = "fecha_modificacion")
    private LocalDate fechaModificacion;
    
    @PrePersist
    protected void onCreate() {
        fechaEmision = LocalDate.now();
    }
}