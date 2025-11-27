package com.system.wasimikuna.repository;

import com.system.wasimikuna.dto.KardexDTO;
import com.system.wasimikuna.dto.StockDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReporteRepository extends JpaRepository<com.system.wasimikuna.model.AuditoriaSistema, Long> {
    
    // Consulta equivalente a la vista VW_KARDEX_ENTRADAS
    @Query("""
        SELECT new com.system.wasimikuna.dto.KardexDTO(
            ie.nombre,
            p.nombre,
            dr.loteFabricacion,
            dr.fechaVencimiento,
            CAST(SUM(dr.cantidadRecibida) AS java.math.BigDecimal),
            MAX(r.fechaRecepcion)
        )
        FROM DetalleRecepcion dr
        JOIN dr.recepcion r
        JOIN r.envio e
        JOIN e.institucion ie
        JOIN dr.producto p
        WHERE r.estadoConformidad = 'CONFORME'
        GROUP BY ie.nombre, p.nombre, dr.loteFabricacion, dr.fechaVencimiento
        """)
    List<KardexDTO> getKardexEntradas();
    
    // Consulta equivalente a la vista VW_STOCK_ACTUAL
    @Query("""
        SELECT new com.system.wasimikuna.dto.StockDTO(
            ie.nombre,
            p.nombre,
            CAST(COALESCE(SUM(dr.cantidadRecibida), 0) AS java.math.BigDecimal),
            CAST(COALESCE((
                SELECT SUM(pm.cantidadRaciones * rp.cantidadPorRacion)
                FROM ProgramacionMenu pm
                JOIN RecetaProducto rp ON pm.plato.platoId = rp.plato.platoId
                WHERE pm.institucion.institucionId = ie.institucionId
                AND rp.producto.productoId = p.productoId
                AND pm.estadoPreparacion = 'SERVIDO'
            ), 0) AS java.math.BigDecimal),
            CAST((COALESCE(SUM(dr.cantidadRecibida), 0) - COALESCE((
                SELECT SUM(pm.cantidadRaciones * rp.cantidadPorRacion)
                FROM ProgramacionMenu pm
                JOIN RecetaProducto rp ON pm.plato.platoId = rp.plato.platoId
                WHERE pm.institucion.institucionId = ie.institucionId
                AND rp.producto.productoId = p.productoId
                AND pm.estadoPreparacion = 'SERVIDO'
            ), 0)) AS java.math.BigDecimal)
        )
        FROM DetalleRecepcion dr
        JOIN dr.recepcion r
        JOIN r.envio e
        JOIN e.institucion ie
        JOIN dr.producto p
        WHERE r.estadoConformidad = 'CONFORME'
        GROUP BY ie.institucionId, ie.nombre, p.productoId, p.nombre
        """)
    List<StockDTO> getStockActual();
    
    // Stock por institución específica
    @Query("""
        SELECT new com.system.wasimikuna.dto.StockDTO(
            ie.nombre,
            p.nombre,
            CAST(COALESCE(SUM(dr.cantidadRecibida), 0) AS java.math.BigDecimal),
            CAST(0 AS java.math.BigDecimal),
            CAST(COALESCE(SUM(dr.cantidadRecibida), 0) AS java.math.BigDecimal)
        )
        FROM DetalleRecepcion dr
        JOIN dr.recepcion r
        JOIN r.envio e
        JOIN e.institucion ie
        JOIN dr.producto p
        WHERE r.estadoConformidad = 'CONFORME'
        AND ie.institucionId = :institucionId
        GROUP BY ie.nombre, p.nombre
        """)
    List<StockDTO> getStockByInstitucion(@Param("institucionId") Long institucionId);
    
    // Productos próximos a vencer
    @Query("""
        SELECT new com.system.wasimikuna.dto.KardexDTO(
            ie.nombre,
            p.nombre,
            dr.loteFabricacion,
            dr.fechaVencimiento,
            CAST(dr.cantidadRecibida AS java.math.BigDecimal),
            r.fechaRecepcion
        )
        FROM DetalleRecepcion dr
        JOIN dr.recepcion r
        JOIN r.envio e
        JOIN e.institucion ie
        JOIN dr.producto p
        WHERE r.estadoConformidad = 'CONFORME'
        AND dr.fechaVencimiento BETWEEN :fechaInicio AND :fechaFin
        ORDER BY dr.fechaVencimiento ASC
        """)
    List<KardexDTO> getProductosProximosAVencer(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);
}