package com.system.wasimikuna.repository;

import com.system.wasimikuna.model.Plato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PlatoRepository extends JpaRepository<Plato, Long> {
    
    @Query("SELECT p FROM Plato p WHERE p.nombre LIKE CONCAT('%', :nombre, '%')")
    List<Plato> findByNombreContaining(@Param("nombre") String nombre);
    
    List<Plato> findByRegionOrigen(String regionOrigen);
    
    @Query("SELECT p FROM Plato p WHERE p.aporteCalorico >= :minCalorias")
    List<Plato> findByAporteCaloricoGreaterThanEqual(@Param("minCalorias") BigDecimal minCalorias);
    
    @Query("SELECT p FROM Plato p WHERE p.aporteProteico >= :minProteina")
    List<Plato> findByAporteProteicoGreaterThanEqual(@Param("minProteina") BigDecimal minProteina);
    
    @Query("SELECT p FROM Plato p WHERE p.aporteHierro >= :minHierro")
    List<Plato> findByAporteHierroGreaterThanEqual(@Param("minHierro") BigDecimal minHierro);
    
    @Query("SELECT p FROM Plato p WHERE p.aporteCalorico BETWEEN :minCalorias AND :maxCalorias")
    List<Plato> findByAporteCaloricoRange(@Param("minCalorias") BigDecimal minCalorias, @Param("maxCalorias") BigDecimal maxCalorias);
}