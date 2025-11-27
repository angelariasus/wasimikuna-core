package com.system.wasimikuna.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriaSistemaDTO {
    private Long auditoriaId;
    private String tablaAfectada;
    private String accion;
    private Long idRegistro;
    private String usuarioDb;
    private Timestamp fecha;
    private String datosAnteriores;
}