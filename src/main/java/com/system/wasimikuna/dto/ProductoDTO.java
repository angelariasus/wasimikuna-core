package com.system.wasimikuna.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {
    private Long productoId;
    private String nombre;
    private String unidadMedida;
    private String categoria;
    private Integer vidaUtilDias;
    private Integer requiereRefrigeracion;
    private String descripcion;
}