package com.system.wasimikuna.service;

import com.system.wasimikuna.dto.RolDTO;
import com.system.wasimikuna.exception.DuplicateResourceException;
import com.system.wasimikuna.exception.ResourceNotFoundException;
import com.system.wasimikuna.model.Rol;
import com.system.wasimikuna.repository.RolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RolService {

    private final RolRepository rolRepository;

    @Transactional(readOnly = true)
    public List<RolDTO> findAll() {
        return rolRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RolDTO findById(Integer id) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", "id", id));
        return convertToDTO(rol);
    }

    @Transactional(readOnly = true)
    public RolDTO findByNombre(String nombre) {
        Rol rol = rolRepository.findByNombre(nombre)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", "nombre", nombre));
        return convertToDTO(rol);
    }

    @Transactional(readOnly = true)
    public List<RolDTO> findByNivelAccesoMinimo(Integer nivelMinimo) {
        return rolRepository.findByNivelAccesoGreaterThanEqual(nivelMinimo).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public RolDTO save(RolDTO rolDTO) {
        // Validar que no exista un rol con el mismo nombre
        if (rolDTO.getRolId() == null && rolRepository.findByNombre(rolDTO.getNombre()).isPresent()) {
            throw new DuplicateResourceException("Rol", "nombre", rolDTO.getNombre());
        }

        Rol rol = convertToEntity(rolDTO);
        Rol savedRol = rolRepository.save(rol);
        return convertToDTO(savedRol);
    }

    public RolDTO update(Integer id, RolDTO rolDTO) {
        Rol existingRol = rolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", "id", id));

        // Validar que no exista otro rol con el mismo nombre
        rolRepository.findByNombre(rolDTO.getNombre())
                .filter(rol -> !rol.getRolId().equals(id))
                .ifPresent(rol -> {
                    throw new DuplicateResourceException("Rol", "nombre", rolDTO.getNombre());
                });

        existingRol.setNombre(rolDTO.getNombre());
        existingRol.setDescripcion(rolDTO.getDescripcion());
        existingRol.setNivelAcceso(rolDTO.getNivelAcceso());

        Rol updatedRol = rolRepository.save(existingRol);
        return convertToDTO(updatedRol);
    }

    public void deleteById(Integer id) {
        if (!rolRepository.existsById(id)) {
            throw new ResourceNotFoundException("Rol", "id", id);
        }
        rolRepository.deleteById(id);
    }

    private RolDTO convertToDTO(Rol rol) {
        return new RolDTO(
                rol.getRolId(),
                rol.getNombre(),
                rol.getDescripcion(),
                rol.getNivelAcceso()
        );
    }

    private Rol convertToEntity(RolDTO rolDTO) {
        Rol rol = new Rol();
        rol.setRolId(rolDTO.getRolId());
        rol.setNombre(rolDTO.getNombre());
        rol.setDescripcion(rolDTO.getDescripcion());
        rol.setNivelAcceso(rolDTO.getNivelAcceso());
        return rol;
    }
}