package com.system.wasimikuna.service;

import com.system.wasimikuna.dto.AfiliadoDTO;
import com.system.wasimikuna.dto.UsuarioSistemaDTO;
import com.system.wasimikuna.exception.BusinessLogicException;
import com.system.wasimikuna.exception.DuplicateResourceException;
import com.system.wasimikuna.exception.ResourceNotFoundException;
import com.system.wasimikuna.model.Afiliado;
import com.system.wasimikuna.model.Afiliado.TipoAfiliado;
import com.system.wasimikuna.model.UsuarioSistema;
import com.system.wasimikuna.repository.AfiliadoRepository;
import com.system.wasimikuna.repository.UsuarioSistemaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AfiliadoService {

    private final AfiliadoRepository afiliadoRepository;
    private final UsuarioSistemaRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<AfiliadoDTO> findAll() {
        return afiliadoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AfiliadoDTO findById(Long id) {
        Afiliado afiliado = afiliadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Afiliado", "id", id));
        return convertToDTO(afiliado);
    }

    @Transactional(readOnly = true)
    public AfiliadoDTO findByRuc(String ruc) {
        Afiliado afiliado = afiliadoRepository.findByRuc(ruc)
                .orElseThrow(() -> new ResourceNotFoundException("Afiliado", "RUC", ruc));
        return convertToDTO(afiliado);
    }

    @Transactional(readOnly = true)
    public List<AfiliadoDTO> findByTipo(String tipo) {
        TipoAfiliado tipoAfiliado = TipoAfiliado.valueOf(tipo.toUpperCase());
        return afiliadoRepository.findByTipo(tipoAfiliado).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AfiliadoDTO> findActivosByTipo(String tipo) {
        TipoAfiliado tipoAfiliado = TipoAfiliado.valueOf(tipo.toUpperCase());
        return afiliadoRepository.findActiveByTipo(tipoAfiliado).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AfiliadoDTO> findByRazonSocial(String razonSocial) {
        return afiliadoRepository.findByRazonSocialContaining(razonSocial).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AfiliadoDTO> findByCalificacionMinima(Integer calificacionMinima) {
        return afiliadoRepository.findByCalificacionSanitariaGreaterThanEqual(calificacionMinima).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public AfiliadoDTO save(AfiliadoDTO afiliadoDTO) {
        // Validar RUC único
        if (afiliadoRepository.findByRuc(afiliadoDTO.getRuc()).isPresent()) {
            throw new DuplicateResourceException("Afiliado", "RUC", afiliadoDTO.getRuc());
        }

        // Validar que el usuario existe y no esté ya asociado
        UsuarioSistema usuario = usuarioRepository.findById(afiliadoDTO.getUsuario().getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", afiliadoDTO.getUsuario().getUsuarioId()));

        if (afiliadoRepository.findByUsuarioUsuarioId(usuario.getUsuarioId()).isPresent()) {
            throw new BusinessLogicException("El usuario ya está asociado a otro afiliado");
        }

        Afiliado afiliado = convertToEntity(afiliadoDTO, usuario);
        Afiliado savedAfiliado = afiliadoRepository.save(afiliado);
        return convertToDTO(savedAfiliado);
    }

    public AfiliadoDTO update(Long id, AfiliadoDTO afiliadoDTO) {
        Afiliado existingAfiliado = afiliadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Afiliado", "id", id));

        // Validar RUC único (excepto para sí mismo)
        afiliadoRepository.findByRuc(afiliadoDTO.getRuc())
                .filter(a -> !a.getAfiliadoId().equals(id))
                .ifPresent(a -> {
                    throw new DuplicateResourceException("Afiliado", "RUC", afiliadoDTO.getRuc());
                });

        existingAfiliado.setTipo(afiliadoDTO.getTipo() != null ? 
            TipoAfiliado.valueOf(afiliadoDTO.getTipo().toUpperCase()) : null);
        existingAfiliado.setRuc(afiliadoDTO.getRuc());
        existingAfiliado.setRazonSocial(afiliadoDTO.getRazonSocial());
        existingAfiliado.setDireccion(afiliadoDTO.getDireccion());
        existingAfiliado.setContactoNombre(afiliadoDTO.getContactoNombre());
        existingAfiliado.setContactoTelefono(afiliadoDTO.getContactoTelefono());
        existingAfiliado.setEstado(afiliadoDTO.getEstado());
        existingAfiliado.setCalificacionSanitaria(afiliadoDTO.getCalificacionSanitaria());

        Afiliado updatedAfiliado = afiliadoRepository.save(existingAfiliado);
        return convertToDTO(updatedAfiliado);
    }

    public AfiliadoDTO updateCalificacionSanitaria(Long id, Integer nuevaCalificacion) {
        if (nuevaCalificacion < 0 || nuevaCalificacion > 100) {
            throw new BusinessLogicException("La calificación sanitaria debe estar entre 0 y 100");
        }

        Afiliado afiliado = afiliadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Afiliado", "id", id));

        afiliado.setCalificacionSanitaria(nuevaCalificacion);
        Afiliado updatedAfiliado = afiliadoRepository.save(afiliado);
        return convertToDTO(updatedAfiliado);
    }

    public void toggleEstado(Long id) {
        Afiliado afiliado = afiliadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Afiliado", "id", id));
        
        afiliado.setEstado(afiliado.getEstado() == 1 ? 0 : 1);
        afiliadoRepository.save(afiliado);
    }

    public void deleteById(Long id) {
        if (!afiliadoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Afiliado", "id", id);
        }
        afiliadoRepository.deleteById(id);
    }

    private AfiliadoDTO convertToDTO(Afiliado afiliado) {
        UsuarioSistemaDTO usuarioDTO = new UsuarioSistemaDTO();
        usuarioDTO.setUsuarioId(afiliado.getUsuario().getUsuarioId());
        usuarioDTO.setUsername(afiliado.getUsuario().getUsername());
        usuarioDTO.setEmail(afiliado.getUsuario().getEmail());

        return new AfiliadoDTO(
                afiliado.getAfiliadoId(),
                usuarioDTO,
                afiliado.getTipo() != null ? afiliado.getTipo().name() : null,
                afiliado.getRuc(),
                afiliado.getRazonSocial(),
                afiliado.getDireccion(),
                afiliado.getContactoNombre(),
                afiliado.getContactoTelefono(),
                afiliado.getEstado(),
                afiliado.getCalificacionSanitaria(),
                afiliado.getFechaCreacion()
        );
    }

    private Afiliado convertToEntity(AfiliadoDTO dto, UsuarioSistema usuario) {
        Afiliado afiliado = new Afiliado();
        afiliado.setAfiliadoId(dto.getAfiliadoId());
        afiliado.setUsuario(usuario);
        afiliado.setTipo(dto.getTipo() != null ? TipoAfiliado.valueOf(dto.getTipo().toUpperCase()) : null);
        afiliado.setRuc(dto.getRuc());
        afiliado.setRazonSocial(dto.getRazonSocial());
        afiliado.setDireccion(dto.getDireccion());
        afiliado.setContactoNombre(dto.getContactoNombre());
        afiliado.setContactoTelefono(dto.getContactoTelefono());
        afiliado.setEstado(dto.getEstado());
        afiliado.setCalificacionSanitaria(dto.getCalificacionSanitaria());
        afiliado.setFechaCreacion(dto.getFechaCreacion());
        return afiliado;
    }
}