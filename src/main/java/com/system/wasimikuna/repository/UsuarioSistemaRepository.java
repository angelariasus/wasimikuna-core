package com.system.wasimikuna.repository;

import com.system.wasimikuna.model.UsuarioSistema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.List;

@Repository
public interface UsuarioSistemaRepository extends JpaRepository<UsuarioSistema, Long> {
    
    Optional<UsuarioSistema> findByUsername(String username);
    
    Optional<UsuarioSistema> findByEmail(String email);
    
    List<UsuarioSistema> findByEstado(Integer estado);
    
    List<UsuarioSistema> findByRolRolId(Integer rolId);
    
    @Query("SELECT u FROM UsuarioSistema u WHERE u.username = :username AND u.estado = 1")
    Optional<UsuarioSistema> findActiveUserByUsername(@Param("username") String username);
    
    @Modifying
    @Query("UPDATE UsuarioSistema u SET u.ultimoAcceso = :timestamp, u.intentosFallidos = 0 WHERE u.usuarioId = :usuarioId")
    void updateLoginSuccess(@Param("usuarioId") Long usuarioId, @Param("timestamp") Timestamp timestamp);
    
    @Modifying
    @Query("UPDATE UsuarioSistema u SET u.intentosFallidos = u.intentosFallidos + 1 WHERE u.usuarioId = :usuarioId")
    void incrementFailedAttempts(@Param("usuarioId") Long usuarioId);
    
    List<UsuarioSistema> findByIntentosFallidosGreaterThanEqual(Integer intentos);
}