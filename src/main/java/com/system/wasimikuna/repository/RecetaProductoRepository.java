package com.system.wasimikuna.repository;

import com.system.wasimikuna.model.RecetaProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface RecetaProductoRepository extends JpaRepository<RecetaProducto, Long> {
    
    List<RecetaProducto> findByPlatoPlatoId(Long platoId);
    
    List<RecetaProducto> findByProductoProductoId(Long productoId);
    
    @Query("SELECT r FROM RecetaProducto r WHERE r.cantidadPorRacion >= :cantidadMinima")
    List<RecetaProducto> findByCantidadPorRacionGreaterThanEqual(@Param("cantidadMinima") BigDecimal cantidadMinima);
    
    @Query("SELECT r FROM RecetaProducto r WHERE r.plato.platoId = :platoId AND r.producto.categoria = :categoria")
    List<RecetaProducto> findByPlatoAndProductoCategoria(@Param("platoId") Long platoId, @Param("categoria") com.system.wasimikuna.model.Producto.CategoriaProducto categoria);
    
    @Query("SELECT SUM(r.cantidadPorRacion) FROM RecetaProducto r WHERE r.plato.platoId = :platoId")
    BigDecimal getTotalIngredientsByPlato(@Param("platoId") Long platoId);
    
    @Query("SELECT DISTINCT r.plato FROM RecetaProducto r WHERE r.producto.productoId = :productoId")
    List<com.system.wasimikuna.model.Plato> findPlatosByProducto(@Param("productoId") Long productoId);
    
    @Query("SELECT r FROM RecetaProducto r WHERE r.producto.requiereRefrigeracion = 1 AND r.plato.platoId = :platoId")
    List<RecetaProducto> findRefrigeratedIngredientsByPlato(@Param("platoId") Long platoId);
}