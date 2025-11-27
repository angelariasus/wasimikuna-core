package com.system.wasimikuna.service;

import com.system.wasimikuna.dto.ProgramacionMenuDTO;
import com.system.wasimikuna.dto.InstitucionEducativaDTO;
import com.system.wasimikuna.dto.PlatoDTO;
import com.system.wasimikuna.exception.BusinessLogicException;
import com.system.wasimikuna.exception.ResourceNotFoundException;
import com.system.wasimikuna.model.ProgramacionMenu;
import com.system.wasimikuna.model.ProgramacionMenu.EstadoPreparacion;
import com.system.wasimikuna.model.InstitucionEducativa;
import com.system.wasimikuna.model.Plato;
import com.system.wasimikuna.repository.ProgramacionMenuRepository;
import com.system.wasimikuna.repository.InstitucionEducativaRepository;
import com.system.wasimikuna.repository.PlatoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProgramacionMenuService {

    private final ProgramacionMenuRepository programacionRepository;
    private final InstitucionEducativaRepository institucionRepository;
    private final PlatoRepository platoRepository;

    @Transactional(readOnly = true)
    public List<ProgramacionMenuDTO> findAll() {
        return programacionRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProgramacionMenuDTO findById(Long id) {
        ProgramacionMenu programacion = programacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Programación Menu", "id", id));
        return convertToDTO(programacion);
    }

    @Transactional(readOnly = true)
    public List<ProgramacionMenuDTO> findByInstitucion(Long institucionId) {
        return programacionRepository.findByInstitucionInstitucionId(institucionId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProgramacionMenuDTO> findByFechaConsumo(LocalDate fecha) {
        return programacionRepository.findByFechaConsumo(fecha).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProgramacionMenuDTO> findByPlato(Long platoId) {
        return programacionRepository.findByPlatoPlatoId(platoId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProgramacionMenuDTO> findByEstadoPreparacion(String estado) {
        EstadoPreparacion estadoEnum = EstadoPreparacion.valueOf(estado.toUpperCase());
        return programacionRepository.findByEstadoPreparacion(estadoEnum).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProgramacionMenuDTO> findByInstitucionYFecha(Long institucionId, LocalDate fecha) {
        return programacionRepository.findByInstitucionInstitucionIdAndFechaConsumo(institucionId, fecha).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProgramacionMenuDTO> findByRangoFecha(LocalDate fechaInicio, LocalDate fechaFin) {
        return programacionRepository.findByFechaConsumoBetween(fechaInicio, fechaFin).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProgramacionMenuDTO> findByInstitucionYRangoFecha(Long institucionId, LocalDate fechaInicio, LocalDate fechaFin) {
        return programacionRepository.findByInstitucionInstitucionIdAndFechaConsumoBetween(institucionId, fechaInicio, fechaFin).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean existsProgramacionEnFecha(Long institucionId, LocalDate fecha, Long platoId) {
        return programacionRepository.existsByInstitucionInstitucionIdAndFechaConsumoAndPlatoPlatoId(
                institucionId, fecha, platoId);
    }

    public ProgramacionMenuDTO save(ProgramacionMenuDTO programacionDTO) {
        // Validar que la institución existe
        InstitucionEducativa institucion = institucionRepository.findById(programacionDTO.getInstitucion().getInstitucionId())
                .orElseThrow(() -> new ResourceNotFoundException("Institución Educativa", "id", programacionDTO.getInstitucion().getInstitucionId()));

        // Validar que el plato existe
        Plato plato = platoRepository.findById(programacionDTO.getPlato().getPlatoId())
                .orElseThrow(() -> new ResourceNotFoundException("Plato", "id", programacionDTO.getPlato().getPlatoId()));

        // Validar datos de la programación
        validarProgramacion(programacionDTO);

        // Verificar que no exista ya una programación para esta institución, fecha y plato
        if (existsProgramacionEnFecha(institucion.getInstitucionId(), 
                programacionDTO.getFechaConsumo(), 
                plato.getPlatoId())) {
            throw new BusinessLogicException("Ya existe una programación para esta fecha y plato en la institución");
        }

        ProgramacionMenu programacion = convertToEntity(programacionDTO, institucion, plato);
        ProgramacionMenu savedProgramacion = programacionRepository.save(programacion);
        return convertToDTO(savedProgramacion);
    }

    public ProgramacionMenuDTO update(Long id, ProgramacionMenuDTO programacionDTO) {
        ProgramacionMenu existingProgramacion = programacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Programación Menu", "id", id));

        // Validar que no se esté modificando una programación de fecha pasada (solo si no está servida)
        if (existingProgramacion.getFechaConsumo().isBefore(LocalDate.now()) && 
            existingProgramacion.getEstadoPreparacion() == EstadoPreparacion.SERVIDO) {
            throw new BusinessLogicException("No se puede modificar una programación ya servida");
        }

        // Validar datos
        validarProgramacion(programacionDTO);

        // Validar plato si se cambió
        if (programacionDTO.getPlato() != null && programacionDTO.getPlato().getPlatoId() != null) {
            Plato plato = platoRepository.findById(programacionDTO.getPlato().getPlatoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Plato", "id", programacionDTO.getPlato().getPlatoId()));
            existingProgramacion.setPlato(plato);
        }

        existingProgramacion.setFechaConsumo(programacionDTO.getFechaConsumo());
        existingProgramacion.setCantidadRaciones(programacionDTO.getCantidadRaciones());
        
        if (programacionDTO.getEstadoPreparacion() != null) {
            existingProgramacion.setEstadoPreparacion(EstadoPreparacion.valueOf(programacionDTO.getEstadoPreparacion().toUpperCase()));
        }

        ProgramacionMenu updatedProgramacion = programacionRepository.save(existingProgramacion);
        return convertToDTO(updatedProgramacion);
    }

    public ProgramacionMenuDTO cambiarEstadoPreparacion(Long id, String nuevoEstado) {
        ProgramacionMenu programacion = programacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Programación Menu", "id", id));

        EstadoPreparacion estadoEnum = EstadoPreparacion.valueOf(nuevoEstado.toUpperCase());
        
        // Validar transiciones válidas
        if (!esTransicionValida(programacion.getEstadoPreparacion(), estadoEnum)) {
            throw new BusinessLogicException("Transición de estado inválida de " + programacion.getEstadoPreparacion() + " a " + estadoEnum);
        }

        programacion.setEstadoPreparacion(estadoEnum);
        ProgramacionMenu updatedProgramacion = programacionRepository.save(programacion);
        return convertToDTO(updatedProgramacion);
    }

    public ProgramacionMenuDTO duplicarProgramacion(Long id, LocalDate nuevaFecha) {
        ProgramacionMenu programacionOriginal = programacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Programación Menu", "id", id));

        // Validar que la nueva fecha sea futura
        if (nuevaFecha.isBefore(LocalDate.now())) {
            throw new BusinessLogicException("La nueva fecha debe ser futura");
        }

        // Verificar que no exista programación para la nueva fecha
        if (existsProgramacionEnFecha(programacionOriginal.getInstitucion().getInstitucionId(),
                nuevaFecha, programacionOriginal.getPlato().getPlatoId())) {
            throw new BusinessLogicException("Ya existe una programación para la nueva fecha y plato");
        }

        // Crear nueva programación
        ProgramacionMenuDTO nuevaProgramacionDTO = convertToDTO(programacionOriginal);
        nuevaProgramacionDTO.setProgramacionId(null); // Para que sea una nueva entidad
        nuevaProgramacionDTO.setFechaConsumo(nuevaFecha);
        nuevaProgramacionDTO.setEstadoPreparacion(EstadoPreparacion.PLANIFICADO.name());

        return save(nuevaProgramacionDTO);
    }

    public List<ProgramacionMenuDTO> programarSemanaCompleta(Long institucionId, LocalDate inicioSemana, 
                                                           List<ProgramacionMenuDTO> programacionesSemana) {
        // Validar que la semana sea futura
        if (inicioSemana.isBefore(LocalDate.now())) {
            throw new BusinessLogicException("No se puede programar una semana pasada");
        }

        // Validar que la institución existe
        if (!institucionRepository.existsById(institucionId)) {
            throw new ResourceNotFoundException("Institución Educativa", "id", institucionId);
        }

        List<ProgramacionMenuDTO> programacionesGuardadas = new java.util.ArrayList<>();

        for (ProgramacionMenuDTO programacionDTO : programacionesSemana) {
            // Establecer la institución
            InstitucionEducativaDTO institucionDTO = new InstitucionEducativaDTO();
            institucionDTO.setInstitucionId(institucionId);
            programacionDTO.setInstitucion(institucionDTO);

            // Validar y guardar cada programación
            try {
                ProgramacionMenuDTO savedProgramacion = save(programacionDTO);
                programacionesGuardadas.add(savedProgramacion);
            } catch (BusinessLogicException e) {
                // Si ya existe, continuar con la siguiente
                if (!e.getMessage().contains("Ya existe una programación")) {
                    throw e; // Re-lanzar si es otro tipo de error
                }
            }
        }

        return programacionesGuardadas;
    }

    public void deleteById(Long id) {
        ProgramacionMenu programacion = programacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Programación Menu", "id", id));

        // Solo permitir eliminación si no está servido
        if (programacion.getEstadoPreparacion() == EstadoPreparacion.SERVIDO) {
            throw new BusinessLogicException("No se puede eliminar una programación ya servida");
        }

        programacionRepository.deleteById(id);
    }

    private boolean esTransicionValida(EstadoPreparacion estadoActual, EstadoPreparacion nuevoEstado) {
        return switch (estadoActual) {
            case PLANIFICADO -> nuevoEstado == EstadoPreparacion.COCINADO;
            case COCINADO -> nuevoEstado == EstadoPreparacion.SERVIDO;
            case SERVIDO -> false; // Estado final
        };
    }

    private void validarProgramacion(ProgramacionMenuDTO programacionDTO) {
        if (programacionDTO.getFechaConsumo() == null) {
            throw new BusinessLogicException("La fecha de consumo es obligatoria");
        }

        if (programacionDTO.getFechaConsumo().isBefore(LocalDate.now())) {
            throw new BusinessLogicException("La fecha de consumo debe ser futura o actual");
        }

        if (programacionDTO.getPlato() == null || programacionDTO.getPlato().getPlatoId() == null) {
            throw new BusinessLogicException("El plato es obligatorio");
        }

        if (programacionDTO.getCantidadRaciones() == null || programacionDTO.getCantidadRaciones() <= 0) {
            throw new BusinessLogicException("La cantidad de raciones debe ser mayor a cero");
        }
    }

    private ProgramacionMenuDTO convertToDTO(ProgramacionMenu programacion) {
        // Simplificar la conversión para evitar dependencias circulares
        InstitucionEducativaDTO institucionDTO = new InstitucionEducativaDTO();
        institucionDTO.setInstitucionId(programacion.getInstitucion().getInstitucionId());
        institucionDTO.setNombre(programacion.getInstitucion().getNombre());
        institucionDTO.setCodigoModular(programacion.getInstitucion().getCodigoModular());

        PlatoDTO platoDTO = new PlatoDTO();
        platoDTO.setPlatoId(programacion.getPlato().getPlatoId());
        platoDTO.setNombre(programacion.getPlato().getNombre());

        return new ProgramacionMenuDTO(
                programacion.getProgramacionId(),
                institucionDTO,
                programacion.getFechaConsumo(),
                platoDTO,
                programacion.getCantidadRaciones(),
                programacion.getEstadoPreparacion() != null ? programacion.getEstadoPreparacion().name() : null,
                programacion.getFechaRegistro()
        );
    }

    private ProgramacionMenu convertToEntity(ProgramacionMenuDTO dto, InstitucionEducativa institucion, Plato plato) {
        ProgramacionMenu programacion = new ProgramacionMenu();
        programacion.setProgramacionId(dto.getProgramacionId());
        programacion.setInstitucion(institucion);
        programacion.setFechaConsumo(dto.getFechaConsumo());
        programacion.setPlato(plato);
        programacion.setCantidadRaciones(dto.getCantidadRaciones());
        programacion.setEstadoPreparacion(dto.getEstadoPreparacion() != null ? 
                EstadoPreparacion.valueOf(dto.getEstadoPreparacion().toUpperCase()) : EstadoPreparacion.PLANIFICADO);
        return programacion;
    }
}
