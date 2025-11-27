package com.system.wasimikuna.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleRecepcionDTO {
    private Long detRecepcionId;
    private Long recepcionId;
    private ProductoDTO producto;
    private String loteFabricacion;
    private LocalDate fechaVencimiento;
    private Integer cantidadRecibida;
    private Integer cantidadRechazada;
    private String motivoRechazo;
    private String evidenciaMimeType;
    private String evidenciaNombreArchivo;
}