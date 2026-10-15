package pe.qruta.trazabilidad.controlador;

import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import pe.qruta.trazabilidad.controlador.Dtos.MotivoSolicitud;
import pe.qruta.trazabilidad.controlador.Dtos.ProductoRespuesta;
import pe.qruta.trazabilidad.controlador.Dtos.UsuarioRespuesta;
import pe.qruta.trazabilidad.dominio.OperacionNoAutorizadaException;
import pe.qruta.trazabilidad.dominio.Rol;
import pe.qruta.trazabilidad.dominio.Usuario;
import pe.qruta.trazabilidad.repositorio.UsuarioRepositorio;
import pe.qruta.trazabilidad.servicio.ProductoService;
import pe.qruta.trazabilidad.servicio.RegulacionService;


/** Control sanitario del regulador: bloqueo, desbloqueo, suspensión y reporte (CU-13, CU-14, CU-03). */
@RestController
public class RegulacionController {

    private final RegulacionService regulacionService;

    private final ProductoService productoService;

    private final UsuarioRepositorio usuarios;


    public RegulacionController(
            RegulacionService regulacionService,
            ProductoService productoService,
            UsuarioRepositorio usuarios
    ) {
        this.regulacionService = regulacionService;
        this.productoService = productoService;
        this.usuarios = usuarios;
    }


    @PostMapping("/api/productos/{id}/bloquear")
    public ProductoRespuesta bloquear(
            @RequestAttribute(InterceptorDeSesion.USUARIO) Usuario usuario,
            @PathVariable Long id,
            @RequestBody MotivoSolicitud solicitud
    ) {

        regulacionService.bloquear(id, usuario, solicitud.motivo());

        return ProductoRespuesta.desde(productoService.buscarPorId(id));
    }


    @PostMapping("/api/productos/{id}/desbloquear")
    public ProductoRespuesta desbloquear(
            @RequestAttribute(InterceptorDeSesion.USUARIO) Usuario usuario,
            @PathVariable Long id,
            @RequestBody MotivoSolicitud solicitud
    ) {

        regulacionService.desbloquear(id, usuario, solicitud.motivo());

        return ProductoRespuesta.desde(productoService.buscarPorId(id));
    }


    @PostMapping("/api/usuarios/{nombreUsuario}/suspender")
    public UsuarioRespuesta suspender(
            @RequestAttribute(InterceptorDeSesion.USUARIO) Usuario usuario,
            @PathVariable String nombreUsuario
    ) {

        exigirRegulador(usuario);

        regulacionService.suspender(nombreUsuario, usuario);

        return UsuarioRespuesta.desde(usuarios.buscarPorNombreUsuario(nombreUsuario).orElseThrow());
    }


    /** RE-36: el reporte sanitario se descarga como archivo CSV. */
    @GetMapping("/api/reportes/salubridad.csv")
    public ResponseEntity<byte[]> reporteSanitario(
            @RequestAttribute(InterceptorDeSesion.USUARIO) Usuario usuario
    ) {

        exigirRegulador(usuario);

        // La marca BOM al inicio hace que Excel reconozca el UTF-8 y muestre bien las tildes.
        byte[] contenido =
            ("﻿" + regulacionService.reporteSanitarioCsv(usuario)).getBytes(StandardCharsets.UTF_8);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"salubridad.csv\"")
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(contenido);
    }


    private void exigirRegulador(Usuario usuario) {

        if (usuario.getRol() != Rol.REGULADOR) {
            throw new OperacionNoAutorizadaException(
                "Solo el regulador puede realizar esta operación"
            );
        }
    }
}
