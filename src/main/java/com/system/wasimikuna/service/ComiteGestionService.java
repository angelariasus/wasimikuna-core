package com.system.wasimikuna.service;

import com.system.wasimikuna.dto.ComiteGestionDTO;
import com.system.wasimikuna.dto.InstitucionEducativaDTO;
import com.system.wasimikuna.exception.BusinessLogicException;
import com.system.wasimikuna.exception.DuplicateResourceException;
import com.system.wasimikuna.exception.ResourceNotFoundException;
import com.system.wasimikuna.model.ComiteGestion;
import com.system.wasimikuna.model.ComiteGestion.CargoComite;
import com.system.wasimikuna.model.InstitucionEducativa;
import com.system.wasimikuna.repository.ComiteGestionRepository;
import com.system.wasimikuna.repository.InstitucionEducativaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ComiteGestionService {

    private final ComiteGestionRepository comiteRepository;
    private final InstitucionEducativaRepository institucionRepository;

    @Transactional(readOnly = true)
    public List<ComiteGestionDTO> findAll() {
        return comiteRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ComiteGestionDTO findById(Long id) {
        ComiteGestion miembro = comiteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Miembro del Comité", "id", id));
        return convertToDTO(miembro);
    }

    @Transactional(readOnly = true)
    public List<ComiteGestionDTO> findByInstitucion(Long institucionId) {
        return comiteRepository.findByInstitucionInstitucionId(institucionId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ComiteGestionDTO> findActivosByInstitucion(Long institucionId) {
        return comiteRepository.findActiveMembersByInstitucion(institucionId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ComiteGestionDTO findByDniAndInstitucion(String dni, Long institucionId) {
        ComiteGestion miembro = comiteRepository.findByDniAndInstitucionInstitucionId(dni, institucionId)
                .orElseThrow(() -> new ResourceNotFoundException("Miembro del Comité", "DNI", dni));
        return convertToDTO(miembro);
    }

    @Transactional(readOnly = true)
    public List<ComiteGestionDTO> findByCargo(String cargo) {
        CargoComite cargoEnum = CargoComite.valueOf(cargo.toUpperCase());
        return comiteRepository.findByCargo(cargoEnum).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ComiteGestionDTO findByInstitucionAndCargo(Long institucionId, String cargo) {
        CargoComite cargoEnum = CargoComite.valueOf(cargo.toUpperCase());
        ComiteGestion miembro = comiteRepository.findActiveByInstitucionAndCargo(institucionId, cargoEnum)
                .orElseThrow(() -> new ResourceNotFoundException("Miembro del Comité", "cargo", cargo + " en institución " + institucionId));
        return convertToDTO(miembro);
    }

    @Transactional(readOnly = true)
    public List<ComiteGestionDTO> findMiembrosVencidos() {
        return comiteRepository.findExpiredMembers(LocalDate.now()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ComiteGestionDTO> findByNombre(String nombre) {
        return comiteRepository.findByNombreCompletoContaining(nombre).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ComiteGestionDTO save(ComiteGestionDTO comiteDTO) {
        // Validar que la institución existe
        InstitucionEducativa institucion = institucionRepository.findById(comiteDTO.getInstitucion().getInstitucionId())
                .orElseThrow(() -> new ResourceNotFoundException("Institución Educativa", "id", comiteDTO.getInstitucion().getInstitucionId()));

        // Validar que no existe otro miembro con el mismo DNI en la institución
        if (comiteRepository.findByDniAndInstitucionInstitucionId(comiteDTO.getDni(), institucion.getInstitucionId()).isPresent()) {
            throw new DuplicateResourceException("Miembro del Comité", "DNI", comiteDTO.getDni() + " en la institución");
        }

        // Validar que no existe otro miembro activo con el mismo cargo en la institución
        CargoComite cargo = CargoComite.valueOf(comiteDTO.getCargo().toUpperCase());
        if (comiteRepository.findActiveByInstitucionAndCargo(institucion.getInstitucionId(), cargo).isPresent()) {
            throw new BusinessLogicException("Ya existe un miembro activo con el cargo " + cargo + " en esta institución");
        }

        ComiteGestion miembro = convertToEntity(comiteDTO, institucion);
        ComiteGestion savedMiembro = comiteRepository.save(miembro);
        return convertToDTO(savedMiembro);
    }

    public ComiteGestionDTO update(Long id, ComiteGestionDTO comiteDTO) {
        ComiteGestion existingMiembro = comiteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Miembro del Comité", "id", id));

        // Validar DNI único en la institución (excepto para sí mismo)
        comiteRepository.findByDniAndInstitucionInstitucionId(comiteDTO.getDni(), existingMiembro.getInstitucion().getInstitucionId())
                .filter(m -> !m.getMiembroId().equals(id))
                .ifPresent(m -> {
                    throw new DuplicateResourceException("Miembro del Comité", "DNI", comiteDTO.getDni());
                });

        // Validar cargo único en la institución si está activo y cambió el cargo
        CargoComite nuevoCargo = CargoComite.valueOf(comiteDTO.getCargo().toUpperCase());
        if (!existingMiembro.getCargo().equals(nuevoCargo) && comiteDTO.getEstadoActivo() == 1) {
            comiteRepository.findActiveByInstitucionAndCargo(existingMiembro.getInstitucion().getInstitucionId(), nuevoCargo)
                    .ifPresent(m -> {
                        throw new BusinessLogicException("Ya existe un miembro activo con el cargo " + nuevoCargo + " en esta institución");
                    });
        }

        existingMiembro.setDni(comiteDTO.getDni());
        existingMiembro.setNombreCompleto(comiteDTO.getNombreCompleto());
        existingMiembro.setCargo(nuevoCargo);
        existingMiembro.setTelefono(comiteDTO.getTelefono());
        existingMiembro.setFechaFinVigencia(comiteDTO.getFechaFinVigencia());
        existingMiembro.setEstadoActivo(comiteDTO.getEstadoActivo());

        ComiteGestion updatedMiembro = comiteRepository.save(existingMiembro);
        return convertToDTO(updatedMiembro);
    }

    public ComiteGestionDTO extenderVigencia(Long id, LocalDate nuevaFechaFin) {
        ComiteGestion miembro = comiteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Miembro del Comité", "id", id));

        if (nuevaFechaFin.isBefore(LocalDate.now())) {
            throw new BusinessLogicException("La fecha de fin de vigencia no puede ser anterior a la fecha actual");
        }

        miembro.setFechaFinVigencia(nuevaFechaFin);
        if (miembro.getEstadoActivo() == 0) {
            miembro.setEstadoActivo(1); // Reactivar si estaba inactivo
        }

        ComiteGestion updatedMiembro = comiteRepository.save(miembro);
        return convertToDTO(updatedMiembro);
    }

    public void finalizarVigencia(Long id) {
        ComiteGestion miembro = comiteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Miembro del Comité", "id", id));

        miembro.setFechaFinVigencia(LocalDate.now());
        miembro.setEstadoActivo(0);
        comiteRepository.save(miembro);
    }

    public void toggleEstado(Long id) {
        ComiteGestion miembro = comiteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Miembro del Comité", "id", id));

        // Si se está activando, validar que no haya otro miembro activo con el mismo cargo
        if (miembro.getEstadoActivo() == 0) {
            comiteRepository.findActiveByInstitucionAndCargo(miembro.getInstitucion().getInstitucionId(), miembro.getCargo())
                    .ifPresent(m -> {
                        throw new BusinessLogicException("Ya existe un miembro activo con el cargo " + miembro.getCargo() + " en esta institución");
                    });
        }

        miembro.setEstadoActivo(miembro.getEstadoActivo() == 1 ? 0 : 1);
        comiteRepository.save(miembro);
    }

    public void deleteById(Long id) {
        if (!comiteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Miembro del Comité", "id", id);
        }
        comiteRepository.deleteById(id);
    }

    @Transactional
    public void procesarMiembrosVencidos() {
        List<ComiteGestion> miembrosVencidos = comiteRepository.findExpiredMembers(LocalDate.now());
        for (ComiteGestion miembro : miembrosVencidos) {
            miembro.setEstadoActivo(0);
            comiteRepository.save(miembro);
        }
    }

    private ComiteGestionDTO convertToDTO(ComiteGestion miembro) {
        InstitucionEducativaDTO institucionDTO = new InstitucionEducativaDTO();
        institucionDTO.setInstitucionId(miembro.getInstitucion().getInstitucionId());
        institucionDTO.setNombre(miembro.getInstitucion().getNombre());
        institucionDTO.setCodigoModular(miembro.getInstitucion().getCodigoModular());

        return new ComiteGestionDTO(
                miembro.getMiembroId(),
                institucionDTO,
                miembro.getDni(),
                miembro.getNombreCompleto(),
                miembro.getCargo() != null ? miembro.getCargo().name() : null,
                miembro.getTelefono(),
                miembro.getFechaInicioVigencia(),
                miembro.getFechaFinVigencia(),
                miembro.getEstadoActivo()
        );
    }

    private ComiteGestion convertToEntity(ComiteGestionDTO dto, InstitucionEducativa institucion) {
        ComiteGestion miembro = new ComiteGestion();
        miembro.setMiembroId(dto.getMiembroId());
        miembro.setInstitucion(institucion);
        miembro.setDni(dto.getDni());
        miembro.setNombreCompleto(dto.getNombreCompleto());
        miembro.setCargo(dto.getCargo() != null ? CargoComite.valueOf(dto.getCargo().toUpperCase()) : null);
        miembro.setTelefono(dto.getTelefono());
        miembro.setFechaInicioVigencia(dto.getFechaInicioVigencia());
        miembro.setFechaFinVigencia(dto.getFechaFinVigencia());
        miembro.setEstadoActivo(dto.getEstadoActivo());
        return miembro;
    }
}
