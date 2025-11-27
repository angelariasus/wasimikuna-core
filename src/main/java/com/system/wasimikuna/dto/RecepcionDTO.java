package com.system.wasimikuna.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecepcionDTO {
    private Long recepcionId;
    private EnvioDTO envio;
    private ComiteGestionDTO comiteMiembro;
    private Timestamp fechaRecepcion;
    private String estadoConformidad;
    private String observacionesGenerales;
    private String actaMimeType;
    private String actaNombreArchivo;
    private List<DetalleRecepcionDTO> detalles;
}