package com.system.wasimikuna.repository;

import com.system.wasimikuna.model.InstitucionEducativa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface InstitucionEducativaRepository extends JpaRepository<InstitucionEducativa, Long> {
    
    Optional<InstitucionEducativa> findByCodigoModularAndAnexo(String codigoModular, String anexo);
    
    List<InstitucionEducativa> findByCodigoModular(String codigoModular);
    
    List<InstitucionEducativa> findByDepartamento(String departamento);
    
    List<InstitucionEducativa> findByProvincia(String provincia);
    
    List<InstitucionEducativa> findByDistrito(String distrito);
    
    List<InstitucionEducativa> findByUbigeo(String ubigeo);
    
    List<InstitucionEducativa> findByEstadoActivo(Integer estadoActivo);
    
    @Query("SELECT ie FROM InstitucionEducativa ie WHERE ie.nombre LIKE %:nombre%")
    List<InstitucionEducativa> findByNombreContaining(@Param("nombre") String nombre);
    
    @Query("SELECT ie FROM InstitucionEducativa ie WHERE ie.departamento = :departamento AND ie.provincia = :provincia")
    List<InstitucionEducativa> findByDepartamentoAndProvincia(@Param("departamento") String departamento, @Param("provincia") String provincia);
    
    Optional<InstitucionEducativa> findByUsuarioUsuarioId(Long usuarioId);
}