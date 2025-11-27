package com.system.wasimikuna.repository;

import com.system.wasimikuna.model.AuditoriaSistema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.sql.Timestamp;
import java.util.List;

@Repository
public interface AuditoriaSistemaRepository extends JpaRepository<AuditoriaSistema, Long> {
    
    List<AuditoriaSistema> findByTablaAfectada(String tablaAfectada);
    
    List<AuditoriaSistema> findByAccion(String accion);
    
    List<AuditoriaSistema> findByUsuarioDb(String usuarioDb);
    
    List<AuditoriaSistema> findByIdRegistro(Long idRegistro);
    
    @Query("SELECT a FROM AuditoriaSistema a WHERE a.fecha BETWEEN :fechaInicio AND :fechaFin")
    List<AuditoriaSistema> findByFechaBetween(@Param("fechaInicio") Timestamp fechaInicio, @Param("fechaFin") Timestamp fechaFin);
    
    @Query("SELECT a FROM AuditoriaSistema a WHERE a.tablaAfectada = :tabla AND a.idRegistro = :idRegistro")
    List<AuditoriaSistema> findByTablaAndIdRegistro(@Param("tabla") String tabla, @Param("idRegistro") Long idRegistro);
    
    @Query("SELECT a FROM AuditoriaSistema a WHERE a.tablaAfectada = :tabla AND a.accion = :accion")
    List<AuditoriaSistema> findByTablaAndAccion(@Param("tabla") String tabla, @Param("accion") String accion);
    
    @Query("SELECT a FROM AuditoriaSistema a ORDER BY a.fecha DESC")
    List<AuditoriaSistema> findAllOrderByFechaDesc();
    
    @Query("SELECT DISTINCT a.tablaAfectada FROM AuditoriaSistema a")
    List<String> findDistinctTablasAfectadas();
}