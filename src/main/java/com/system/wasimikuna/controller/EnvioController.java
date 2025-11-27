package com.system.wasimikuna.controller;

import com.system.wasimikuna.dto.EnvioDTO;
import com.system.wasimikuna.service.EnvioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/envios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EnvioController {

    private final EnvioService envioService;

    @GetMapping
    public ResponseEntity<List<EnvioDTO>> getAllEnvios() {
        List<EnvioDTO> envios = envioService.findAll();
        return ResponseEntity.ok(envios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnvioDTO> getEnvioById(@PathVariable Long id) {
        EnvioDTO envio = envioService.findById(id);
        return ResponseEntity.ok(envio);
    }

    @GetMapping("/orden/{ordenCompraId}")
    public ResponseEntity<List<EnvioDTO>> getEnviosByOrden(@PathVariable Long ordenCompraId) {
        List<EnvioDTO> envios = envioService.findByOrdenCompra(ordenCompraId);
        return ResponseEntity.ok(envios);
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<EnvioDTO>> getEnviosByEstado(@PathVariable String estado) {
        List<EnvioDTO> envios = envioService.findByEstado(estado);
        return ResponseEntity.ok(envios);
    }

    @PostMapping
    public ResponseEntity<EnvioDTO> createEnvio(@RequestBody EnvioDTO envioDTO) {
        EnvioDTO nuevoEnvio = envioService.save(envioDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoEnvio);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EnvioDTO> updateEnvio(@PathVariable Long id, @RequestBody EnvioDTO envioDTO) {
        EnvioDTO envioActualizado = envioService.update(id, envioDTO);
        return ResponseEntity.ok(envioActualizado);
    }

    @PatchMapping("/{id}/entregar")
    public ResponseEntity<EnvioDTO> marcarComoEntregado(@PathVariable Long id) {
        EnvioDTO envio = envioService.cambiarEstado(id, "ENTREGADO");
        return ResponseEntity.ok(envio);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnvio(@PathVariable Long id) {
        envioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}