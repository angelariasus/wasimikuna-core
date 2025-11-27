package com.system.wasimikuna.repository;

import com.system.wasimikuna.model.Recepcion;
import com.system.wasimikuna.model.Recepcion.EstadoConformidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.sql.Timestamp;
import java.util.List;

@Repository
public interface RecepcionRepository extends JpaRepository<Recepcion, Long> {
    
    List<Recepcion> findByEnvioEnvioId(Long envioId);
    
    List<Recepcion> findByComiteMiembroMiembroId(Long miembroId);
    
    List<Recepcion> findByEstadoConformidad(EstadoConformidad estadoConformidad);
    
    @Query("SELECT r FROM Recepcion r WHERE r.fechaRecepcion BETWEEN :fechaInicio AND :fechaFin")
    List<Recepcion> findByFechaRecepcionBetween(@Param("fechaInicio") Timestamp fechaInicio, @Param("fechaFin") Timestamp fechaFin);
    
    @Query("SELECT r FROM Recepcion r WHERE r.envio.institucion.institucionId = :institucionId")
    List<Recepcion> findByInstitucionId(@Param("institucionId") Long institucionId);
    
    @Query("SELECT r FROM Recepcion r WHERE r.envio.institucion.institucionId = :institucionId AND r.estadoConformidad = :estado")
    List<Recepcion> findByInstitucionAndEstado(@Param("institucionId") Long institucionId, @Param("estado") EstadoConformidad estado);
    
    @Query("SELECT r FROM Recepcion r WHERE r.estadoConformidad = 'OBSERVADO' OR r.estadoConformidad = 'RECHAZADO'")
    List<Recepcion> findProblematicReceptions();
    
    @Query("SELECT r FROM Recepcion r WHERE r.observacionesGenerales IS NOT NULL AND r.observacionesGenerales != ''")
    List<Recepcion> findWithObservations();
}