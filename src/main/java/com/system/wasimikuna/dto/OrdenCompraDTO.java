package com.system.wasimikuna.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdenCompraDTO {
    private Long ordenCompraId;
    private AfiliadoDTO afiliado;
    private LocalDate fechaEmision;
    private Integer estado;
    private BigDecimal total;
    private LocalDate fechaEntregaPrevista;
    private UsuarioSistemaDTO usuarioCreacion;
    private LocalDate fechaModificacion;
    private List<DetalleOrdenDTO> detalles;
}