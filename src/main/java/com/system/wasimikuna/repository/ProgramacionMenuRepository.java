package com.system.wasimikuna.repository;

import com.system.wasimikuna.model.ProgramacionMenu;
import com.system.wasimikuna.model.ProgramacionMenu.EstadoPreparacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProgramacionMenuRepository extends JpaRepository<ProgramacionMenu, Long> {
    
    List<ProgramacionMenu> findByInstitucionInstitucionId(Long institucionId);
    
    List<ProgramacionMenu> findByPlatoPlatoId(Long platoId);
    
    List<ProgramacionMenu> findByEstadoPreparacion(EstadoPreparacion estadoPreparacion);
    
    List<ProgramacionMenu> findByFechaConsumo(LocalDate fechaConsumo);
    
    @Query("SELECT p FROM ProgramacionMenu p WHERE p.fechaConsumo BETWEEN :fechaInicio AND :fechaFin")
    List<ProgramacionMenu> findByFechaConsumoBetween(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);
    
    List<ProgramacionMenu> findByInstitucionInstitucionIdAndFechaConsumo(Long institucionId, LocalDate fechaConsumo);
    
    List<ProgramacionMenu> findByInstitucionInstitucionIdAndFechaConsumoBetween(Long institucionId, LocalDate fechaInicio, LocalDate fechaFin);
    
    boolean existsByInstitucionInstitucionIdAndFechaConsumoAndPlatoPlatoId(Long institucionId, LocalDate fechaConsumo, Long platoId);
    
    @Query("SELECT p FROM ProgramacionMenu p WHERE p.institucion.institucionId = :institucionId AND p.fechaConsumo = :fecha")
    List<ProgramacionMenu> findByInstitucionAndFecha(@Param("institucionId") Long institucionId, @Param("fecha") LocalDate fecha);
    
    @Query("SELECT p FROM ProgramacionMenu p WHERE p.institucion.institucionId = :institucionId AND p.estadoPreparacion = :estado")
    List<ProgramacionMenu> findByInstitucionAndEstado(@Param("institucionId") Long institucionId, @Param("estado") EstadoPreparacion estado);
    
    @Query("SELECT p FROM ProgramacionMenu p WHERE p.fechaConsumo = :fecha AND p.estadoPreparacion = 'PLANIFICADO'")
    List<ProgramacionMenu> findTodayPlannedMeals(@Param("fecha") LocalDate fecha);
    
    @Query("SELECT SUM(p.cantidadRaciones) FROM ProgramacionMenu p WHERE p.plato.platoId = :platoId AND p.estadoPreparacion = 'SERVIDO'")
    Integer getTotalServedPortionsByPlato(@Param("platoId") Long platoId);
    
    @Query("SELECT p FROM ProgramacionMenu p WHERE p.cantidadRaciones >= :cantidadMinima")
    List<ProgramacionMenu> findByCantidadRacionesGreaterThanEqual(@Param("cantidadMinima") Integer cantidadMinima);
}