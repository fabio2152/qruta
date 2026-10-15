package pe.qruta.trazabilidad.repositorio.jpa;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import pe.qruta.trazabilidad.dominio.evaluacion.Evaluacion;
import pe.qruta.trazabilidad.dominio.evaluacion.NivelRiesgo;


@Entity
@Table(name = "evaluaciones")
public class EvaluacionEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal temperaturaEvaluada;

    private long vidaUtilRestanteHoras;

    private boolean rupturaCadenaFrio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private NivelRiesgo nivelRiesgo;

    private boolean aptoConsumo;


    protected EvaluacionEntidad() {
    }


    public EvaluacionEntidad(Evaluacion evaluacion) {
        this.fechaHora = evaluacion.fechaHora();
        this.temperaturaEvaluada = evaluacion.temperaturaEvaluada();
        this.vidaUtilRestanteHoras = evaluacion.vidaUtilRestanteHoras();
        this.rupturaCadenaFrio = evaluacion.rupturaCadenaFrio();
        this.nivelRiesgo = evaluacion.nivelRiesgo();
        this.aptoConsumo = evaluacion.aptoConsumo();
    }


    public Evaluacion aDominio() {
        return new Evaluacion(
            fechaHora,
            temperaturaEvaluada.stripTrailingZeros(),
            vidaUtilRestanteHoras,
            rupturaCadenaFrio,
            nivelRiesgo,
            aptoConsumo
        );
    }
}
