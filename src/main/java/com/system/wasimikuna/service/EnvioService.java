package com.system.wasimikuna.service;

import com.system.wasimikuna.dto.EnvioDTO;
import com.system.wasimikuna.dto.OrdenCompraDTO;
import com.system.wasimikuna.dto.InstitucionEducativaDTO;
import com.system.wasimikuna.dto.UsuarioSistemaDTO;
import com.system.wasimikuna.exception.BusinessLogicException;
import com.system.wasimikuna.exception.ResourceNotFoundException;
import com.system.wasimikuna.model.Envio;
import com.system.wasimikuna.model.Envio.EstadoEnvio;
import com.system.wasimikuna.model.OrdenCompra;
import com.system.wasimikuna.model.InstitucionEducativa;
import com.system.wasimikuna.model.UsuarioSistema;
import com.system.wasimikuna.repository.EnvioRepository;
import com.system.wasimikuna.repository.OrdenCompraRepository;
import com.system.wasimikuna.repository.InstitucionEducativaRepository;
import com.system.wasimikuna.repository.UsuarioSistemaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EnvioService {

    private final EnvioRepository envioRepository;
    private final OrdenCompraRepository ordenCompraRepository;
    private final InstitucionEducativaRepository institucionRepository;
    private final UsuarioSistemaRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<EnvioDTO> findAll() {
        return envioRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EnvioDTO findById(Long id) {
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Envío", "id", id));
        return convertToDTO(envio);
    }

    @Transactional(readOnly = true)
    public List<EnvioDTO> findByOrdenCompra(Long ordenCompraId) {
        return envioRepository.findByOrdenCompraOrdenCompraId(ordenCompraId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EnvioDTO> findByInstitucion(Long institucionId) {
        return envioRepository.findByInstitucionInstitucionId(institucionId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EnvioDTO> findByEstado(String estado) {
        EstadoEnvio estadoEnum = EstadoEnvio.valueOf(estado.toUpperCase());
        return envioRepository.findByEstadoEnvio(estadoEnum).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EnvioDTO> findEnviosActivos() {
        return envioRepository.findActiveShipments().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EnvioDTO> findByPlacaVehiculo(String placa) {
        return envioRepository.findByPlacaVehiculo(placa).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EnvioDTO> findByConductor(String nombreConductor) {
        return envioRepository.findByConductorNombreContaining(nombreConductor).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EnvioDTO> findByFechaRango(Timestamp fechaInicio, Timestamp fechaFin) {
        return envioRepository.findByFechaSalidaBetween(fechaInicio, fechaFin).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public EnvioDTO save(EnvioDTO envioDTO) {
        // Validar que la orden de compra existe y está en estado apropiado para envío
        OrdenCompra ordenCompra = ordenCompraRepository.findById(envioDTO.getOrdenCompra().getOrdenCompraId())
                .orElseThrow(() -> new ResourceNotFoundException("Orden de Compra", "id", envioDTO.getOrdenCompra().getOrdenCompraId()));

        if (ordenCompra.getEstado() < 1) {
            throw new BusinessLogicException("La orden de compra debe estar aprobada para poder crear un envío");
        }

        // Validar que la institución existe
        InstitucionEducativa institucion = institucionRepository.findById(envioDTO.getInstitucion().getInstitucionId())
                .orElseThrow(() -> new ResourceNotFoundException("Institución Educativa", "id", envioDTO.getInstitucion().getInstitucionId()));

        // Validar usuario despacho (opcional)
        UsuarioSistema usuarioDespacho = null;
        if (envioDTO.getUsuarioDespacho() != null) {
            usuarioDespacho = usuarioRepository.findById(envioDTO.getUsuarioDespacho().getUsuarioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", envioDTO.getUsuarioDespacho().getUsuarioId()));
        }

        Envio envio = convertToEntity(envioDTO, ordenCompra, institucion, usuarioDespacho);
        Envio savedEnvio = envioRepository.save(envio);
        return convertToDTO(savedEnvio);
    }

    public EnvioDTO update(Long id, EnvioDTO envioDTO) {
        Envio existingEnvio = envioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Envío", "id", id));

        // Solo permitir actualización si no está entregado
        if (existingEnvio.getEstadoEnvio() == EstadoEnvio.ENTREGADO) {
            throw new BusinessLogicException("No se puede modificar un envío que ya fue entregado");
        }

        existingEnvio.setConductorNombre(envioDTO.getConductorNombre());
        existingEnvio.setPlacaVehiculo(envioDTO.getPlacaVehiculo());

        // Actualizar usuario despacho si se proporciona
        if (envioDTO.getUsuarioDespacho() != null) {
            UsuarioSistema usuarioDespacho = usuarioRepository.findById(envioDTO.getUsuarioDespacho().getUsuarioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", envioDTO.getUsuarioDespacho().getUsuarioId()));
            existingEnvio.setUsuarioDespacho(usuarioDespacho);
        }

        Envio updatedEnvio = envioRepository.save(existingEnvio);
        return convertToDTO(updatedEnvio);
    }

    public EnvioDTO cambiarEstado(Long id, String nuevoEstado) {
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Envío", "id", id));

        EstadoEnvio estadoEnum = EstadoEnvio.valueOf(nuevoEstado.toUpperCase());

        // Validar transiciones de estado válidas
        if (!esTransicionValida(envio.getEstadoEnvio(), estadoEnum)) {
            throw new BusinessLogicException("Transición de estado inválida de " + envio.getEstadoEnvio() + " a " + estadoEnum);
        }

        envio.setEstadoEnvio(estadoEnum);

        // Si se marca como EN_RUTA, actualizar la fecha de salida si no está establecida
        if (estadoEnum == EstadoEnvio.EN_RUTA && envio.getFechaSalida() == null) {
            envio.setFechaSalida(new Timestamp(System.currentTimeMillis()));
        }

        Envio updatedEnvio = envioRepository.save(envio);
        return convertToDTO(updatedEnvio);
    }

    public EnvioDTO iniciarEnvio(Long id, String conductorNombre, String placaVehiculo) {
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Envío", "id", id));

        if (envio.getEstadoEnvio() != EstadoEnvio.PENDIENTE) {
            throw new BusinessLogicException("Solo se pueden iniciar envíos en estado PENDIENTE");
        }

        envio.setConductorNombre(conductorNombre);
        envio.setPlacaVehiculo(placaVehiculo);
        envio.setEstadoEnvio(EstadoEnvio.EN_RUTA);
        envio.setFechaSalida(new Timestamp(System.currentTimeMillis()));

        Envio updatedEnvio = envioRepository.save(envio);
        return convertToDTO(updatedEnvio);
    }

    public void deleteById(Long id) {
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Envío", "id", id));

        // Solo permitir eliminación si está pendiente
        if (envio.getEstadoEnvio() != EstadoEnvio.PENDIENTE) {
            throw new BusinessLogicException("Solo se pueden eliminar envíos en estado PENDIENTE");
        }

        envioRepository.deleteById(id);
    }

    private boolean esTransicionValida(EstadoEnvio estadoActual, EstadoEnvio nuevoEstado) {
        return switch (estadoActual) {
            case PENDIENTE -> nuevoEstado == EstadoEnvio.EN_RUTA;
            case EN_RUTA -> nuevoEstado == EstadoEnvio.ENTREGADO || nuevoEstado == EstadoEnvio.DEVUELTO;
            case ENTREGADO, DEVUELTO -> false; // Estados finales
        };
    }

    private EnvioDTO convertToDTO(Envio envio) {
        // Simplificar la conversión para evitar dependencias circulares
        OrdenCompraDTO ordenDTO = new OrdenCompraDTO();
        ordenDTO.setOrdenCompraId(envio.getOrdenCompra().getOrdenCompraId());
        ordenDTO.setFechaEmision(envio.getOrdenCompra().getFechaEmision());
        ordenDTO.setEstado(envio.getOrdenCompra().getEstado());

        InstitucionEducativaDTO institucionDTO = new InstitucionEducativaDTO();
        institucionDTO.setInstitucionId(envio.getInstitucion().getInstitucionId());
        institucionDTO.setNombre(envio.getInstitucion().getNombre());
        institucionDTO.setCodigoModular(envio.getInstitucion().getCodigoModular());

        UsuarioSistemaDTO usuarioDTO = null;
        if (envio.getUsuarioDespacho() != null) {
            usuarioDTO = new UsuarioSistemaDTO();
            usuarioDTO.setUsuarioId(envio.getUsuarioDespacho().getUsuarioId());
            usuarioDTO.setUsername(envio.getUsuarioDespacho().getUsername());
        }

        return new EnvioDTO(
                envio.getEnvioId(),
                ordenDTO,
                institucionDTO,
                envio.getConductorNombre(),
                envio.getPlacaVehiculo(),
                envio.getFechaSalida(),
                envio.getEstadoEnvio() != null ? envio.getEstadoEnvio().name() : null,
                usuarioDTO
        );
    }

    private Envio convertToEntity(EnvioDTO dto, OrdenCompra ordenCompra, InstitucionEducativa institucion, UsuarioSistema usuarioDespacho) {
        Envio envio = new Envio();
        envio.setEnvioId(dto.getEnvioId());
        envio.setOrdenCompra(ordenCompra);
        envio.setInstitucion(institucion);
        envio.setConductorNombre(dto.getConductorNombre());
        envio.setPlacaVehiculo(dto.getPlacaVehiculo());
        envio.setFechaSalida(dto.getFechaSalida());
        envio.setEstadoEnvio(dto.getEstadoEnvio() != null ? EstadoEnvio.valueOf(dto.getEstadoEnvio().toUpperCase()) : EstadoEnvio.PENDIENTE);
        envio.setUsuarioDespacho(usuarioDespacho);
        return envio;
    }
}
