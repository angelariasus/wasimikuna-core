package com.system.wasimikuna.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KardexDTO {
    private String colegio;
    private String producto;
    private String loteFabricacion;
    private LocalDate fechaVencimiento;
    private BigDecimal totalIngresado;
    private Timestamp ultimaEntrada;
}