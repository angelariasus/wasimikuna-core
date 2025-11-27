package com.system.wasimikuna.repository;

import com.system.wasimikuna.model.Envio;
import com.system.wasimikuna.model.Envio.EstadoEnvio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.sql.Timestamp;
import java.util.List;

@Repository
public interface EnvioRepository extends JpaRepository<Envio, Long> {
    
    List<Envio> findByOrdenCompraOrdenCompraId(Long ordenCompraId);
    
    List<Envio> findByInstitucionInstitucionId(Long institucionId);
    
    List<Envio> findByEstadoEnvio(EstadoEnvio estadoEnvio);
    
    List<Envio> findByUsuarioDespachoUsuarioId(Long usuarioId);
    
    @Query("SELECT e FROM Envio e WHERE e.fechaSalida BETWEEN :fechaInicio AND :fechaFin")
    List<Envio> findByFechaSalidaBetween(@Param("fechaInicio") Timestamp fechaInicio, @Param("fechaFin") Timestamp fechaFin);
    
    @Query("SELECT e FROM Envio e WHERE e.placaVehiculo = :placa")
    List<Envio> findByPlacaVehiculo(@Param("placa") String placa);
    
    @Query("SELECT e FROM Envio e WHERE e.conductorNombre LIKE %:nombre%")
    List<Envio> findByConductorNombreContaining(@Param("nombre") String nombre);
    
    @Query("SELECT e FROM Envio e WHERE e.institucion.institucionId = :institucionId AND e.estadoEnvio = :estado")
    List<Envio> findByInstitucionAndEstado(@Param("institucionId") Long institucionId, @Param("estado") EstadoEnvio estado);
    
    @Query("SELECT e FROM Envio e WHERE e.estadoEnvio IN ('PENDIENTE', 'EN_RUTA')")
    List<Envio> findActiveShipments();
}