package com.system.wasimikuna.repository;

import com.system.wasimikuna.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {
    
    Optional<Rol> findByNombre(String nombre);
    
    List<Rol> findByNivelAccesoGreaterThanEqual(Integer nivelAcceso);
    
    @Query("SELECT r FROM Rol r WHERE r.nivelAcceso <= ?1 ORDER BY r.nivelAcceso DESC")
    List<Rol> findRolesByMaxNivelAcceso(Integer maxNivelAcceso);
}