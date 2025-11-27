package com.system.wasimikuna.service;

import com.system.wasimikuna.dto.AuditoriaSistemaDTO;
import com.system.wasimikuna.exception.BusinessLogicException;
import com.system.wasimikuna.exception.ResourceNotFoundException;
import com.system.wasimikuna.model.AuditoriaSistema;
import com.system.wasimikuna.repository.AuditoriaSistemaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuditoriaSistemaService {

    private final AuditoriaSistemaRepository auditoriaRepository;

    public List<AuditoriaSistemaDTO> findAll() {
        return auditoriaRepository.findAll(Sort.by(Sort.Direction.DESC, "fecha")).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Page<AuditoriaSistemaDTO> findAllPaginated(int page, int size, String sortBy, String sortDir) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") 
            ? Sort.Direction.DESC 
            : Sort.Direction.ASC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        return auditoriaRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    public AuditoriaSistemaDTO findById(Long id) {
        AuditoriaSistema auditoria = auditoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Auditoría Sistema", "id", id));
        return convertToDTO(auditoria);
    }

    public List<AuditoriaSistemaDTO> findByTablaAfectada(String tabla) {
        if (tabla == null || tabla.trim().isEmpty()) {
            throw new BusinessLogicException("El nombre de la tabla es obligatorio");
        }
        return auditoriaRepository.findByTablaAfectada(tabla).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AuditoriaSistemaDTO> findByAccion(String accion) {
        if (accion == null || accion.trim().isEmpty()) {
            throw new BusinessLogicException("La acción es obligatoria");
        }
        
        // Validar acciones válidas
        if (!accion.equalsIgnoreCase("INSERT") && 
            !accion.equalsIgnoreCase("UPDATE") && 
            !accion.equalsIgnoreCase("DELETE")) {
            throw new BusinessLogicException("Acción inválida. Valores permitidos: INSERT, UPDATE, DELETE");
        }
        
        return auditoriaRepository.findByAccion(accion.toUpperCase()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AuditoriaSistemaDTO> findByUsuario(String usuario) {
        if (usuario == null || usuario.trim().isEmpty()) {
            throw new BusinessLogicException("El usuario es obligatorio");
        }
        return auditoriaRepository.findByUsuarioDb(usuario).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AuditoriaSistemaDTO> findByIdRegistro(Long idRegistro) {
        if (idRegistro == null || idRegistro <= 0) {
            throw new BusinessLogicException("El ID de registro debe ser válido");
        }
        return auditoriaRepository.findByIdRegistro(idRegistro).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AuditoriaSistemaDTO> findByRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new BusinessLogicException("Las fechas de inicio y fin son obligatorias");
        }
        
        if (fechaInicio.isAfter(fechaFin)) {
            throw new BusinessLogicException("La fecha de inicio debe ser anterior a la fecha de fin");
        }

        Timestamp tsInicio = Timestamp.valueOf(fechaInicio);
        Timestamp tsFin = Timestamp.valueOf(fechaFin);
        
        return auditoriaRepository.findByFechaBetween(tsInicio, tsFin).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AuditoriaSistemaDTO> findByFechaHoy() {
        LocalDateTime inicio = LocalDate.now().atStartOfDay();
        LocalDateTime fin = inicio.plusDays(1).minusSeconds(1);
        return findByRangoFechas(inicio, fin);
    }

    public List<AuditoriaSistemaDTO> findByUltimaSemana() {
        LocalDateTime fin = LocalDateTime.now();
        LocalDateTime inicio = fin.minusDays(7);
        return findByRangoFechas(inicio, fin);
    }

    public List<AuditoriaSistemaDTO> findByTablaYRegistro(String tabla, Long idRegistro) {
        if (tabla == null || tabla.trim().isEmpty()) {
            throw new BusinessLogicException("El nombre de la tabla es obligatorio");
        }
        if (idRegistro == null || idRegistro <= 0) {
            throw new BusinessLogicException("El ID de registro debe ser válido");
        }
        
        return auditoriaRepository.findByTablaAndIdRegistro(tabla, idRegistro).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AuditoriaSistemaDTO> findByTablaYAccion(String tabla, String accion) {
        if (tabla == null || tabla.trim().isEmpty()) {
            throw new BusinessLogicException("El nombre de la tabla es obligatorio");
        }
        if (accion == null || accion.trim().isEmpty()) {
            throw new BusinessLogicException("La acción es obligatoria");
        }
        
        return auditoriaRepository.findByTablaAndAccion(tabla, accion.toUpperCase()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public AuditoriaSistemaDTO registrarAuditoria(String tabla, String accion, Long idRegistro, 
                                                 String usuario, String datosAnteriores) {
        validarDatosAuditoria(tabla, accion, idRegistro, usuario);
        
        AuditoriaSistema auditoria = new AuditoriaSistema();
        auditoria.setTablaAfectada(tabla);
        auditoria.setAccion(accion.toUpperCase());
        auditoria.setIdRegistro(idRegistro);
        auditoria.setUsuarioDb(usuario);
        auditoria.setDatosAnteriores(datosAnteriores);
        
        AuditoriaSistema savedAuditoria = auditoriaRepository.save(auditoria);
        return convertToDTO(savedAuditoria);
    }

    public Map<String, Long> getEstadisticasPorAccion() {
        return auditoriaRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                    AuditoriaSistema::getAccion,
                    Collectors.counting()
                ));
    }

    public Map<String, Long> getEstadisticasPorTabla() {
        return auditoriaRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                    AuditoriaSistema::getTablaAfectada,
                    Collectors.counting()
                ));
    }

    public Map<String, Long> getEstadisticasPorUsuario() {
        return auditoriaRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                    AuditoriaSistema::getUsuarioDb,
                    Collectors.counting()
                ));
    }

    public List<AuditoriaSistemaDTO> getHistorialCompleto(String tabla, Long idRegistro) {
        return findByTablaYRegistro(tabla, idRegistro);
    }

    public String generarReporteAuditoria(String tabla, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        List<AuditoriaSistemaDTO> auditorias = findByRangoFechas(fechaInicio, fechaFin);
        
        if (tabla != null && !tabla.trim().isEmpty()) {
            auditorias = auditorias.stream()
                    .filter(a -> a.getTablaAfectada().equals(tabla))
                    .collect(Collectors.toList());
        }
        
        StringBuilder reporte = new StringBuilder();
        reporte.append("=== REPORTE DE AUDITORÍA ===\n");
        reporte.append("Período: ").append(fechaInicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
               .append(" - ").append(fechaFin.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
        
        if (tabla != null) {
            reporte.append("Tabla: ").append(tabla).append("\n");
        }
        
        reporte.append("Total de registros: ").append(auditorias.size()).append("\n\n");
        
        Map<String, Long> estadisticasAccion = auditorias.stream()
                .collect(Collectors.groupingBy(AuditoriaSistemaDTO::getAccion, Collectors.counting()));
        
        reporte.append("Estadísticas por acción:\n");
        estadisticasAccion.forEach((accion, cantidad) -> 
            reporte.append("- ").append(accion).append(": ").append(cantidad).append("\n"));
        
        return reporte.toString();
    }

    private void validarDatosAuditoria(String tabla, String accion, Long idRegistro, String usuario) {
        if (tabla == null || tabla.trim().isEmpty()) {
            throw new BusinessLogicException("El nombre de la tabla es obligatorio");
        }
        
        if (accion == null || accion.trim().isEmpty()) {
            throw new BusinessLogicException("La acción es obligatoria");
        }
        
        if (!accion.equalsIgnoreCase("INSERT") && 
            !accion.equalsIgnoreCase("UPDATE") && 
            !accion.equalsIgnoreCase("DELETE")) {
            throw new BusinessLogicException("Acción inválida. Valores permitidos: INSERT, UPDATE, DELETE");
        }
        
        if (idRegistro == null || idRegistro <= 0) {
            throw new BusinessLogicException("El ID de registro debe ser válido");
        }
        
        if (usuario == null || usuario.trim().isEmpty()) {
            throw new BusinessLogicException("El usuario es obligatorio");
        }
    }

    private AuditoriaSistemaDTO convertToDTO(AuditoriaSistema auditoria) {
        return new AuditoriaSistemaDTO(
                auditoria.getAuditoriaId(),
                auditoria.getTablaAfectada(),
                auditoria.getAccion(),
                auditoria.getIdRegistro(),
                auditoria.getUsuarioDb(),
                auditoria.getFecha(),
                auditoria.getDatosAnteriores()
        );
    }
}