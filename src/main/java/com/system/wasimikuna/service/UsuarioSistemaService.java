package com.system.wasimikuna.service;

import com.system.wasimikuna.dto.LoginRequestDTO;
import com.system.wasimikuna.dto.LoginResponseDTO;
import com.system.wasimikuna.dto.UsuarioSistemaDTO;
import com.system.wasimikuna.dto.RolDTO;
import com.system.wasimikuna.exception.BusinessLogicException;
import com.system.wasimikuna.exception.DuplicateResourceException;
import com.system.wasimikuna.exception.ResourceNotFoundException;
import com.system.wasimikuna.model.Rol;
import com.system.wasimikuna.model.UsuarioSistema;
import com.system.wasimikuna.repository.RolRepository;
import com.system.wasimikuna.repository.UsuarioSistemaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioSistemaService {

    private final UsuarioSistemaRepository usuarioRepository;
    private final RolRepository rolRepository;

    @Transactional(readOnly = true)
    public List<UsuarioSistemaDTO> findAll() {
        return usuarioRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioSistemaDTO findById(Long id) {
        UsuarioSistema usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        return convertToDTO(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioSistemaDTO findByUsername(String username) {
        UsuarioSistema usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "username", username));
        return convertToDTO(usuario);
    }

    @Transactional(readOnly = true)
    public List<UsuarioSistemaDTO> findByEstado(Integer estado) {
        return usuarioRepository.findByEstado(estado).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UsuarioSistemaDTO> findByRol(Integer rolId) {
        return usuarioRepository.findByRolRolId(rolId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        try {
            UsuarioSistema usuario = usuarioRepository.findActiveUserByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario", "username", loginRequest.getUsername()));

            // Verificar si está bloqueado
            if (usuario.getIntentosFallidos() >= 5) {
                throw new BusinessLogicException("Cuenta bloqueada por múltiples intentos fallidos");
            }

            // Verificar contraseña
            String hashedInputPassword = hashPassword(loginRequest.getPassword());
            if (!hashedInputPassword.equals(usuario.getPasswordHash())) {
                usuarioRepository.incrementFailedAttempts(usuario.getUsuarioId());
                throw new BusinessLogicException("Credenciales inválidas");
            }

            // Login exitoso
            usuarioRepository.updateLoginSuccess(usuario.getUsuarioId(), new Timestamp(System.currentTimeMillis()));
            
            UsuarioSistemaDTO usuarioDTO = convertToDTO(usuario);
            return new LoginResponseDTO(true, usuarioDTO);

        } catch (ResourceNotFoundException | BusinessLogicException e) {
            return new LoginResponseDTO(false, e.getMessage());
        }
    }

    public UsuarioSistemaDTO save(UsuarioSistemaDTO usuarioDTO, String password) {
        // Validaciones
        if (usuarioRepository.findByUsername(usuarioDTO.getUsername()).isPresent()) {
            throw new DuplicateResourceException("Usuario", "username", usuarioDTO.getUsername());
        }

        if (usuarioDTO.getEmail() != null && usuarioRepository.findByEmail(usuarioDTO.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Usuario", "email", usuarioDTO.getEmail());
        }

        // Buscar rol
        Rol rol = rolRepository.findById(usuarioDTO.getRol().getRolId())
                .orElseThrow(() -> new ResourceNotFoundException("Rol", "id", usuarioDTO.getRol().getRolId()));

        UsuarioSistema usuario = convertToEntity(usuarioDTO, rol);
        usuario.setPasswordHash(hashPassword(password));
        
        UsuarioSistema savedUsuario = usuarioRepository.save(usuario);
        return convertToDTO(savedUsuario);
    }

    public UsuarioSistemaDTO update(Long id, UsuarioSistemaDTO usuarioDTO) {
        UsuarioSistema existingUsuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        // Validar username único
        usuarioRepository.findByUsername(usuarioDTO.getUsername())
                .filter(u -> !u.getUsuarioId().equals(id))
                .ifPresent(u -> {
                    throw new DuplicateResourceException("Usuario", "username", usuarioDTO.getUsername());
                });

        // Validar email único
        if (usuarioDTO.getEmail() != null) {
            usuarioRepository.findByEmail(usuarioDTO.getEmail())
                    .filter(u -> !u.getUsuarioId().equals(id))
                    .ifPresent(u -> {
                        throw new DuplicateResourceException("Usuario", "email", usuarioDTO.getEmail());
                    });
        }

        // Buscar rol
        Rol rol = rolRepository.findById(usuarioDTO.getRol().getRolId())
                .orElseThrow(() -> new ResourceNotFoundException("Rol", "id", usuarioDTO.getRol().getRolId()));

        existingUsuario.setUsername(usuarioDTO.getUsername());
        existingUsuario.setEmail(usuarioDTO.getEmail());
        existingUsuario.setRol(rol);
        existingUsuario.setEstado(usuarioDTO.getEstado());

        UsuarioSistema updatedUsuario = usuarioRepository.save(existingUsuario);
        return convertToDTO(updatedUsuario);
    }

    public void changePassword(Long id, String newPassword) {
        UsuarioSistema usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        
        usuario.setPasswordHash(hashPassword(newPassword));
        usuario.setIntentosFallidos(0); // Reset intentos fallidos
        usuarioRepository.save(usuario);
    }

    public void toggleEstado(Long id) {
        UsuarioSistema usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        
        usuario.setEstado(usuario.getEstado() == 1 ? 0 : 1);
        usuarioRepository.save(usuario);
    }

    public void deleteById(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario", "id", id);
        }
        usuarioRepository.deleteById(id);
    }

    private UsuarioSistemaDTO convertToDTO(UsuarioSistema usuario) {
        RolDTO rolDTO = new RolDTO(
                usuario.getRol().getRolId(),
                usuario.getRol().getNombre(),
                usuario.getRol().getDescripcion(),
                usuario.getRol().getNivelAcceso()
        );

        return new UsuarioSistemaDTO(
                usuario.getUsuarioId(),
                usuario.getUsername(),
                usuario.getEmail(),
                rolDTO,
                usuario.getEstado(),
                usuario.getFotoMimeType(),
                usuario.getFotoNombre(),
                usuario.getUltimoAcceso(),
                usuario.getIntentosFallidos(),
                usuario.getFechaCreacion()
        );
    }

    private UsuarioSistema convertToEntity(UsuarioSistemaDTO dto, Rol rol) {
        UsuarioSistema usuario = new UsuarioSistema();
        usuario.setUsuarioId(dto.getUsuarioId());
        usuario.setUsername(dto.getUsername());
        usuario.setEmail(dto.getEmail());
        usuario.setRol(rol);
        usuario.setEstado(dto.getEstado());
        usuario.setFotoMimeType(dto.getFotoMimeType());
        usuario.setFotoNombre(dto.getFotoNombre());
        usuario.setIntentosFallidos(dto.getIntentosFallidos());
        usuario.setFechaCreacion(dto.getFechaCreacion());
        return usuario;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new BusinessLogicException("Error al procesar la contraseña", e);
        }
    }
}