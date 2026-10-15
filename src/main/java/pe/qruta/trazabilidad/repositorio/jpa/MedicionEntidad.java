package pe.qruta.trazabilidad.repositorio.jpa;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import pe.qruta.trazabilidad.dominio.evaluacion.Medicion;


@Entity
@Table(name = "mediciones")
public class MedicionEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal temperatura;

    @Column(precision = 5, scale = 2)
    private BigDecimal humedad;

    @ManyToOne(optional = false)
    private UsuarioEntidad registradaPor;


    protected MedicionEntidad() {
    }


    public MedicionEntidad(
            Medicion medicion,
            UsuarioEntidad registradaPor
    ) {
        this.fechaHora = medicion.fechaHora();
        this.temperatura = medicion.temperatura();
        this.humedad = medicion.humedad();
        this.registradaPor = registradaPor;
    }


    public Medicion aDominio() {
        return new Medicion(
            fechaHora,
            temperatura.stripTrailingZeros(),
            humedad == null ? null : humedad.stripTrailingZeros(),
            registradaPor.aDominio()
        );
    }
}
