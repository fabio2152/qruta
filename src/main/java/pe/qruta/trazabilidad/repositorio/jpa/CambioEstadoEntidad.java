package pe.qruta.trazabilidad.repositorio.jpa;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import pe.qruta.trazabilidad.dominio.CambioEstado;
import pe.qruta.trazabilidad.dominio.EstadoProducto;


@Entity
@Table(name = "cambios_estado")
public class CambioEstadoEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoProducto estadoAnterior;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoProducto estadoNuevo;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @ManyToOne(optional = false)
    private UsuarioEntidad usuario;

    @Column(length = 200)
    private String observacion;


    protected CambioEstadoEntidad() {
    }


    public CambioEstadoEntidad(
            CambioEstado cambio,
            UsuarioEntidad usuario
    ) {
        this.estadoAnterior = cambio.estadoAnterior();
        this.estadoNuevo = cambio.estadoNuevo();
        this.fechaHora = cambio.fechaHora();
        this.usuario = usuario;
        this.observacion = cambio.observacion();
    }


    public CambioEstado aDominio() {
        return new CambioEstado(
            estadoAnterior,
            estadoNuevo,
            fechaHora,
            usuario.aDominio(),
            observacion
        );
    }
}
