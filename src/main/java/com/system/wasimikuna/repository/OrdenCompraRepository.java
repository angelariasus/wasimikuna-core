package com.system.wasimikuna.repository;

import com.system.wasimikuna.model.OrdenCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Long> {
    
    List<OrdenCompra> findByAfiliadoAfiliadoId(Long afiliadoId);
    
    List<OrdenCompra> findByEstado(Integer estado);
    
    List<OrdenCompra> findByUsuarioCreacionUsuarioId(Long usuarioId);
    
    @Query("SELECT o FROM OrdenCompra o WHERE o.fechaEmision BETWEEN :fechaInicio AND :fechaFin")
    List<OrdenCompra> findByFechaEmisionBetween(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);
    
    @Query("SELECT o FROM OrdenCompra o WHERE o.fechaEntregaPrevista BETWEEN :fechaInicio AND :fechaFin")
    List<OrdenCompra> findByFechaEntregaPrevistaBetween(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);
    
    @Query("SELECT o FROM OrdenCompra o WHERE o.total >= :montoMinimo")
    List<OrdenCompra> findByTotalGreaterThanEqual(@Param("montoMinimo") BigDecimal montoMinimo);
    
    @Query("SELECT o FROM OrdenCompra o WHERE o.afiliado.afiliadoId = :afiliadoId AND o.estado = :estado")
    List<OrdenCompra> findByAfiliadoAndEstado(@Param("afiliadoId") Long afiliadoId, @Param("estado") Integer estado);
    
    @Query("SELECT o FROM OrdenCompra o WHERE o.fechaEntregaPrevista < :fecha AND o.estado IN (0, 1)")
    List<OrdenCompra> findOverdueOrders(@Param("fecha") LocalDate fecha);
}