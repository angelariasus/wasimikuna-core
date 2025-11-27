package com.system.wasimikuna.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecetaProductoDTO {
    private Long recetaId;
    private PlatoDTO plato;
    private ProductoDTO producto;
    private BigDecimal cantidadPorRacion;
}