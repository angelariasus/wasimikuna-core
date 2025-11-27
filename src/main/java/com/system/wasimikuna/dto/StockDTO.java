package com.system.wasimikuna.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockDTO {
    private String colegio;
    private String producto;
    private BigDecimal totalIngresos;
    private BigDecimal totalSalidas;
    private BigDecimal stockActual;
}