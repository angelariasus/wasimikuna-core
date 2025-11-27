package com.system.wasimikuna.repository;

import com.system.wasimikuna.model.DetalleRecepcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface DetalleRecepcionRepository extends JpaRepository<DetalleRecepcion, Long> {
    
    List<DetalleRecepcion> findByRecepcionRecepcionId(Long recepcionId);
    
    List<DetalleRecepcion> findByProductoProductoId(Long productoId);
    
    List<DetalleRecepcion> findByLoteFabricacion(String loteFabricacion);
    
    @Query("SELECT d FROM DetalleRecepcion d WHERE d.fechaVencimiento BETWEEN :fechaInicio AND :fechaFin")
    List<DetalleRecepcion> findByFechaVencimientoBetween(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);
    
    @Query("SELECT d FROM DetalleRecepcion d WHERE d.fechaVencimiento < :fecha")
    List<DetalleRecepcion> findExpiredProducts(@Param("fecha") LocalDate fecha);
    
    @Query("SELECT d FROM DetalleRecepcion d WHERE d.cantidadRechazada > 0")
    List<DetalleRecepcion> findWithRejections();
    
    @Query("SELECT d FROM DetalleRecepcion d WHERE d.motivoRechazo IS NOT NULL AND d.motivoRechazo != ''")
    List<DetalleRecepcion> findWithRejectionReasons();
    
    @Query("SELECT d FROM DetalleRecepcion d WHERE d.cantidadRechazada > 0")
    List<DetalleRecepcion> findRejectedItems();
    
    @Query("SELECT d FROM DetalleRecepcion d WHERE d.fechaVencimiento <= :fechaLimite")
    List<DetalleRecepcion> findExpiringSoon(@Param("fechaLimite") LocalDate fechaLimite);
    
    @Query("SELECT d FROM DetalleRecepcion d WHERE d.producto.productoId = :productoId AND d.loteFabricacion = :lote")
    List<DetalleRecepcion> findByProductoAndLote(@Param("productoId") Long productoId, @Param("lote") String lote);
    
    @Query("SELECT SUM(d.cantidadRecibida) FROM DetalleRecepcion d WHERE d.producto.productoId = :productoId AND d.recepcion.estadoConformidad = 'CONFORME'")
    Integer getTotalReceivedByProduct(@Param("productoId") Long productoId);
}