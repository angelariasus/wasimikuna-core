package com.system.wasimikuna.repository;

import com.system.wasimikuna.model.Afiliado;
import com.system.wasimikuna.model.Afiliado.TipoAfiliado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface AfiliadoRepository extends JpaRepository<Afiliado, Long> {
    
    Optional<Afiliado> findByRuc(String ruc);
    
    List<Afiliado> findByTipo(TipoAfiliado tipo);
    
    List<Afiliado> findByEstado(Integer estado);
    
    @Query("SELECT a FROM Afiliado a WHERE a.razonSocial LIKE %:razonSocial%")
    List<Afiliado> findByRazonSocialContaining(@Param("razonSocial") String razonSocial);
    
    @Query("SELECT a FROM Afiliado a WHERE a.calificacionSanitaria >= :minCalificacion")
    List<Afiliado> findByCalificacionSanitariaGreaterThanEqual(@Param("minCalificacion") Integer minCalificacion);
    
    @Query("SELECT a FROM Afiliado a WHERE a.tipo = :tipo AND a.estado = 1")
    List<Afiliado> findActiveByTipo(@Param("tipo") TipoAfiliado tipo);
    
    Optional<Afiliado> findByUsuarioUsuarioId(Long usuarioId);
    
    @Query("SELECT a FROM Afiliado a WHERE a.contactoTelefono = :telefono")
    List<Afiliado> findByContactoTelefono(@Param("telefono") String telefono);
}