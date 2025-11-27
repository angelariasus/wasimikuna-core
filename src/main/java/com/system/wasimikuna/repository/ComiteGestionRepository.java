package com.system.wasimikuna.repository;

import com.system.wasimikuna.model.ComiteGestion;
import com.system.wasimikuna.model.ComiteGestion.CargoComite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ComiteGestionRepository extends JpaRepository<ComiteGestion, Long> {
    
    List<ComiteGestion> findByInstitucionInstitucionId(Long institucionId);
    
    Optional<ComiteGestion> findByDniAndInstitucionInstitucionId(String dni, Long institucionId);
    
    List<ComiteGestion> findByCargo(CargoComite cargo);
    
    List<ComiteGestion> findByEstadoActivo(Integer estadoActivo);
    
    @Query("SELECT c FROM ComiteGestion c WHERE c.institucion.institucionId = :institucionId AND c.estadoActivo = 1")
    List<ComiteGestion> findActiveMembersByInstitucion(@Param("institucionId") Long institucionId);
    
    @Query("SELECT c FROM ComiteGestion c WHERE c.institucion.institucionId = :institucionId AND c.cargo = :cargo AND c.estadoActivo = 1")
    Optional<ComiteGestion> findActiveByInstitucionAndCargo(@Param("institucionId") Long institucionId, @Param("cargo") CargoComite cargo);
    
    @Query("SELECT c FROM ComiteGestion c WHERE c.fechaFinVigencia < :fecha AND c.estadoActivo = 1")
    List<ComiteGestion> findExpiredMembers(@Param("fecha") LocalDate fecha);
    
    @Query("SELECT c FROM ComiteGestion c WHERE c.nombreCompleto LIKE CONCAT('%', :nombre, '%')")
    List<ComiteGestion> findByNombreCompletoContaining(@Param("nombre") String nombre);
}