package com.system.wasimikuna.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlatoDTO {
    private Long platoId;
    private String nombre;
    private BigDecimal aporteCalorico;
    private BigDecimal aporteProteico;
    private BigDecimal aporteHierro;
    private String regionOrigen;
    private String recetaTexto;
}