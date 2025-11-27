package com.system.wasimikuna.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "Producto")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "producto_id")
    private Long productoId;
    
    @Column(name = "nombre", length = 150, nullable = false)
    private String nombre;
    
    @Column(name = "unidad_medida", length = 10)
    private String unidadMedida;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "categoria", length = 20)
    private CategoriaProducto categoria;
    
    @Column(name = "vida_util_dias")
    private Integer vidaUtilDias;
    
    @Column(name = "requiere_refrigeracion")
    private Integer requiereRefrigeracion = 0;
    
    @Column(name = "descripcion", length = 200)
    private String descripcion;
    
    public enum CategoriaProducto {
        PERECIBLE, NO_PERECIBLE, CARNE_FRESCA, VERDURA, LACTEO
    }
}