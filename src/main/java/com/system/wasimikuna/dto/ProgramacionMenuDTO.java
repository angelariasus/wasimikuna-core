package com.system.wasimikuna.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramacionMenuDTO {
    private Long programacionId;
    private InstitucionEducativaDTO institucion;
    private LocalDate fechaConsumo;
    private PlatoDTO plato;
    private Integer cantidadRaciones;
    private String estadoPreparacion;
    private LocalDate fechaRegistro;
}