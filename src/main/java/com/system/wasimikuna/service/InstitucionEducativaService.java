package com.system.wasimikuna.service;

import com.system.wasimikuna.dto.InstitucionEducativaDTO;
import com.system.wasimikuna.dto.UsuarioSistemaDTO;
import com.system.wasimikuna.exception.BusinessLogicException;
import com.system.wasimikuna.exception.DuplicateResourceException;
import com.system.wasimikuna.exception.ResourceNotFoundException;
import com.system.wasimikuna.model.InstitucionEducativa;
import com.system.wasimikuna.model.UsuarioSistema;
import com.system.wasimikuna.repository.InstitucionEducativaRepository;
import com.system.wasimikuna.repository.UsuarioSistemaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InstitucionEducativaService {

    private final InstitucionEducativaRepository institucionRepository;
    private final UsuarioSistemaRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<InstitucionEducativaDTO> findAll() {
        return institucionRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InstitucionEducativaDTO findById(Long id) {
        InstitucionEducativa institucion = institucionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Institución Educativa", "id", id));
        return convertToDTO(institucion);
    }

    @Transactional(readOnly = true)
    public InstitucionEducativaDTO findByCodigoModular(String codigoModular, String anexo) {
        InstitucionEducativa institucion = institucionRepository.findByCodigoModularAndAnexo(codigoModular, anexo)
                .orElseThrow(() -> new ResourceNotFoundException("Institución Educativa", "código modular", codigoModular + "-" + anexo));
        return convertToDTO(institucion);
    }

    @Transactional(readOnly = true)
    public List<InstitucionEducativaDTO> findByNombre(String nombre) {
        return institucionRepository.findByNombreContaining(nombre).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InstitucionEducativaDTO> findByDepartamento(String departamento) {
        return institucionRepository.findByDepartamento(departamento).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InstitucionEducativaDTO> findByProvincia(String provincia) {
        return institucionRepository.findByProvincia(provincia).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InstitucionEducativaDTO> findByDistrito(String distrito) {
        return institucionRepository.findByDistrito(distrito).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InstitucionEducativaDTO> findByUbigeo(String ubigeo) {
        return institucionRepository.findByUbigeo(ubigeo).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InstitucionEducativaDTO> findActivas() {
        return institucionRepository.findByEstadoActivo(1).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InstitucionEducativaDTO> findByDepartamentoYProvincia(String departamento, String provincia) {
        return institucionRepository.findByDepartamentoAndProvincia(departamento, provincia).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public InstitucionEducativaDTO save(InstitucionEducativaDTO institucionDTO) {
        // Validar código modular único
        if (institucionRepository.findByCodigoModularAndAnexo(
                institucionDTO.getCodigoModular(), 
                institucionDTO.getAnexo()).isPresent()) {
            throw new DuplicateResourceException("Institución Educativa", "código modular", 
                    institucionDTO.getCodigoModular() + "-" + institucionDTO.getAnexo());
        }

        // Validar que el usuario existe y no esté ya asociado
        UsuarioSistema usuario = usuarioRepository.findById(institucionDTO.getUsuario().getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", institucionDTO.getUsuario().getUsuarioId()));

        if (institucionRepository.findByUsuarioUsuarioId(usuario.getUsuarioId()).isPresent()) {
            throw new BusinessLogicException("El usuario ya está asociado a otra institución educativa");
        }

        InstitucionEducativa institucion = convertToEntity(institucionDTO, usuario);
        InstitucionEducativa savedInstitucion = institucionRepository.save(institucion);
        return convertToDTO(savedInstitucion);
    }

    public InstitucionEducativaDTO update(Long id, InstitucionEducativaDTO institucionDTO) {
        InstitucionEducativa existingInstitucion = institucionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Institución Educativa", "id", id));

        // Validar código modular único (excepto para sí misma)
        institucionRepository.findByCodigoModularAndAnexo(institucionDTO.getCodigoModular(), institucionDTO.getAnexo())
                .filter(i -> !i.getInstitucionId().equals(id))
                .ifPresent(i -> {
                    throw new DuplicateResourceException("Institución Educativa", "código modular", 
                            institucionDTO.getCodigoModular() + "-" + institucionDTO.getAnexo());
                });

        existingInstitucion.setCodigoModular(institucionDTO.getCodigoModular());
        existingInstitucion.setAnexo(institucionDTO.getAnexo());
        existingInstitucion.setNombre(institucionDTO.getNombre());
        existingInstitucion.setDireccion(institucionDTO.getDireccion());
        existingInstitucion.setDepartamento(institucionDTO.getDepartamento());
        existingInstitucion.setProvincia(institucionDTO.getProvincia());
        existingInstitucion.setDistrito(institucionDTO.getDistrito());
        existingInstitucion.setUbigeo(institucionDTO.getUbigeo());
        existingInstitucion.setEstadoActivo(institucionDTO.getEstadoActivo());

        InstitucionEducativa updatedInstitucion = institucionRepository.save(existingInstitucion);
        return convertToDTO(updatedInstitucion);
    }

    public void toggleEstado(Long id) {
        InstitucionEducativa institucion = institucionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Institución Educativa", "id", id));
        
        institucion.setEstadoActivo(institucion.getEstadoActivo() == 1 ? 0 : 1);
        institucionRepository.save(institucion);
    }

    public void deleteById(Long id) {
        if (!institucionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Institución Educativa", "id", id);
        }
        institucionRepository.deleteById(id);
    }

    private InstitucionEducativaDTO convertToDTO(InstitucionEducativa institucion) {
        UsuarioSistemaDTO usuarioDTO = new UsuarioSistemaDTO();
        usuarioDTO.setUsuarioId(institucion.getUsuario().getUsuarioId());
        usuarioDTO.setUsername(institucion.getUsuario().getUsername());
        usuarioDTO.setEmail(institucion.getUsuario().getEmail());

        return new InstitucionEducativaDTO(
                institucion.getInstitucionId(),
                usuarioDTO,
                institucion.getCodigoModular(),
                institucion.getAnexo(),
                institucion.getNombre(),
                institucion.getDireccion(),
                institucion.getDepartamento(),
                institucion.getProvincia(),
                institucion.getDistrito(),
                institucion.getUbigeo(),
                institucion.getFechaRegistro(),
                institucion.getEstadoActivo()
        );
    }

    private InstitucionEducativa convertToEntity(InstitucionEducativaDTO dto, UsuarioSistema usuario) {
        InstitucionEducativa institucion = new InstitucionEducativa();
        institucion.setInstitucionId(dto.getInstitucionId());
        institucion.setUsuario(usuario);
        institucion.setCodigoModular(dto.getCodigoModular());
        institucion.setAnexo(dto.getAnexo());
        institucion.setNombre(dto.getNombre());
        institucion.setDireccion(dto.getDireccion());
        institucion.setDepartamento(dto.getDepartamento());
        institucion.setProvincia(dto.getProvincia());
        institucion.setDistrito(dto.getDistrito());
        institucion.setUbigeo(dto.getUbigeo());
        institucion.setFechaRegistro(dto.getFechaRegistro());
        institucion.setEstadoActivo(dto.getEstadoActivo());
        return institucion;
    }
}