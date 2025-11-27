package com.system.wasimikuna.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncidenciaSanitariaDTO {
    private Long incidenciaId;
    private InstitucionEducativaDTO institucion;
    private ProductoDTO producto;
    private String loteAfectado;
    private String tipoRiesgo;
    private String descripcionDetallada;
    private Timestamp fechaReporte;
    private String estadoAtencion;
    private String accionTomada;
    private ComiteGestionDTO usuarioReportante;
    private UsuarioSistemaDTO monitorAsignado;
}