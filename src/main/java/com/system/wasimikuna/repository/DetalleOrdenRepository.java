package com.system.wasimikuna.repository;

import com.system.wasimikuna.model.DetalleOrden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface DetalleOrdenRepository extends JpaRepository<DetalleOrden, Long> {
    
    List<DetalleOrden> findByOrdenCompraOrdenCompraId(Long ordenCompraId);
    
    List<DetalleOrden> findByProductoProductoId(Long productoId);
    
    @Query("SELECT d FROM DetalleOrden d WHERE d.cantidad >= :cantidadMinima")
    List<DetalleOrden> findByCantidadGreaterThanEqual(@Param("cantidadMinima") Integer cantidadMinima);
    
    @Query("SELECT d FROM DetalleOrden d WHERE d.subtotal >= :montoMinimo")
    List<DetalleOrden> findBySubtotalGreaterThanEqual(@Param("montoMinimo") BigDecimal montoMinimo);
    
    @Query("SELECT SUM(d.subtotal) FROM DetalleOrden d WHERE d.ordenCompra.ordenCompraId = :ordenCompraId")
    BigDecimal calculateTotalByOrdenCompra(@Param("ordenCompraId") Long ordenCompraId);
    
    @Query("SELECT d FROM DetalleOrden d WHERE d.producto.productoId = :productoId AND d.ordenCompra.fechaEmision BETWEEN :fechaInicio AND :fechaFin")
    List<DetalleOrden> findByProductoAndFechaRange(@Param("productoId") Long productoId, @Param("fechaInicio") java.time.LocalDate fechaInicio, @Param("fechaFin") java.time.LocalDate fechaFin);
}