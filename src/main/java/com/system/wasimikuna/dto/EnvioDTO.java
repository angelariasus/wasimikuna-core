package com.system.wasimikuna.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnvioDTO {
    private Long envioId;
    private OrdenCompraDTO ordenCompra;
    private InstitucionEducativaDTO institucion;
    private String conductorNombre;
    private String placaVehiculo;
    private Timestamp fechaSalida;
    private String estadoEnvio;
    private UsuarioSistemaDTO usuarioDespacho;
}