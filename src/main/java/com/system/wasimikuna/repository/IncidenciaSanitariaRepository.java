package com.system.wasimikuna.repository;

import com.system.wasimikuna.model.IncidenciaSanitaria;
import com.system.wasimikuna.model.IncidenciaSanitaria.TipoRiesgo;
import com.system.wasimikuna.model.IncidenciaSanitaria.EstadoAtencion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.sql.Timestamp;
import java.util.List;

@Repository
public interface IncidenciaSanitariaRepository extends JpaRepository<IncidenciaSanitaria, Long> {
    
    List<IncidenciaSanitaria> findByInstitucionInstitucionId(Long institucionId);
    
    List<IncidenciaSanitaria> findByProductoProductoId(Long productoId);
    
    List<IncidenciaSanitaria> findByTipoRiesgo(TipoRiesgo tipoRiesgo);
    
    List<IncidenciaSanitaria> findByEstadoAtencion(EstadoAtencion estadoAtencion);
    
    List<IncidenciaSanitaria> findByLoteAfectado(String loteAfectado);
    
    List<IncidenciaSanitaria> findByUsuarioReportanteMiembroId(Long miembroId);
    
    List<IncidenciaSanitaria> findByMonitorAsignadoUsuarioId(Long usuarioId);
    
    @Query("SELECT i FROM IncidenciaSanitaria i WHERE i.fechaReporte BETWEEN :fechaInicio AND :fechaFin")
    List<IncidenciaSanitaria> findByFechaReporteBetween(@Param("fechaInicio") Timestamp fechaInicio, @Param("fechaFin") Timestamp fechaFin);
    
    @Query("SELECT i FROM IncidenciaSanitaria i WHERE i.estadoAtencion IN ('REPORTADO', 'EN_EVALUACION')")
    List<IncidenciaSanitaria> findPendingIncidents();
    
    @Query("SELECT i FROM IncidenciaSanitaria i WHERE i.tipoRiesgo IN ('INTOXICACION', 'CUERPO_EXTRAÑO') AND i.estadoAtencion != 'DESCARTADO'")
    List<IncidenciaSanitaria> findCriticalIncidents();
    
    @Query("SELECT i FROM IncidenciaSanitaria i WHERE i.institucion.institucionId = :institucionId AND i.estadoAtencion = :estado")
    List<IncidenciaSanitaria> findByInstitucionAndEstado(@Param("institucionId") Long institucionId, @Param("estado") EstadoAtencion estado);
}