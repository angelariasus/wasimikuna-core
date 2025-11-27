package com.system.wasimikuna.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComiteGestionDTO {
    private Long miembroId;
    private InstitucionEducativaDTO institucion;
    private String dni;
    private String nombreCompleto;
    private String cargo;
    private String telefono;
    private LocalDate fechaInicioVigencia;
    private LocalDate fechaFinVigencia;
    private Integer estadoActivo;
}