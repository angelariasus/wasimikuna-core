package com.system.wasimikuna.repository;

import com.system.wasimikuna.model.Producto;
import com.system.wasimikuna.model.Producto.CategoriaProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    List<Producto> findByCategoria(CategoriaProducto categoria);
    
    @Query("SELECT p FROM Producto p WHERE p.nombre LIKE CONCAT('%', :nombre, '%')")
    List<Producto> findByNombreContaining(@Param("nombre") String nombre);
    
    List<Producto> findByUnidadMedida(String unidadMedida);
    
    List<Producto> findByRequiereRefrigeracion(Integer requiereRefrigeracion);
    
    @Query("SELECT p FROM Producto p WHERE p.vidaUtilDias <= :diasMaximos")
    List<Producto> findByVidaUtilMenorIgual(@Param("diasMaximos") Integer diasMaximos);
    
    @Query("SELECT p FROM Producto p WHERE p.categoria IN :categorias")
    List<Producto> findByCategoriaIn(@Param("categorias") List<CategoriaProducto> categorias);
    
    @Query("SELECT p FROM Producto p WHERE p.categoria = :categoria AND p.requiereRefrigeracion = :requiereRefrigeracion")
    List<Producto> findByCategoriaAndRequiereRefrigeracion(@Param("categoria") CategoriaProducto categoria, @Param("requiereRefrigeracion") Integer requiereRefrigeracion);
}