package pe.qruta.trazabilidad.dominio.producto;

import java.time.LocalDateTime;


/** Datos que el productor completa al crear un producto (pollo, palta o banano). */
public class DatosProducto {

    private String nombre;

    private TipoProducto tipoProducto;

    private TipoProduccion tipoProduccion;

    private String lote;

    private LocalDateTime fechaProduccion;

    private String especie;

    private String codigoPlanta;

    private String certificadoSanitarioSenasa;

    private String variedad;

    private EstadoMaduracion estadoMaduracion;

    private Integer calidad;

    private String certificadoFitosanitario;


    public String getNombre() {
        return nombre;
    }


    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


    public TipoProducto getTipoProducto() {
        return tipoProducto;
    }


    public void setTipoProducto(TipoProducto tipoProducto) {
        this.tipoProducto = tipoProducto;
    }


    public TipoProduccion getTipoProduccion() {
        return tipoProduccion;
    }


    public void setTipoProduccion(TipoProduccion tipoProduccion) {
        this.tipoProduccion = tipoProduccion;
    }


    public String getLote() {
        return lote;
    }


    public void setLote(String lote) {
        this.lote = lote;
    }


    public LocalDateTime getFechaProduccion() {
        return fechaProduccion;
    }


    public void setFechaProduccion(LocalDateTime fechaProduccion) {
        this.fechaProduccion = fechaProduccion;
    }


    public String getEspecie() {
        return especie;
    }


    public void setEspecie(String especie) {
        this.especie = especie;
    }


    public String getCodigoPlanta() {
        return codigoPlanta;
    }


    public void setCodigoPlanta(String codigoPlanta) {
        this.codigoPlanta = codigoPlanta;
    }


    public String getCertificadoSanitarioSenasa() {
        return certificadoSanitarioSenasa;
    }


    public void setCertificadoSanitarioSenasa(String certificadoSanitarioSenasa) {
        this.certificadoSanitarioSenasa = certificadoSanitarioSenasa;
    }


    public String getVariedad() {
        return variedad;
    }


    public void setVariedad(String variedad) {
        this.variedad = variedad;
    }


    public EstadoMaduracion getEstadoMaduracion() {
        return estadoMaduracion;
    }


    public void setEstadoMaduracion(EstadoMaduracion estadoMaduracion) {
        this.estadoMaduracion = estadoMaduracion;
    }


    public Integer getCalidad() {
        return calidad;
    }


    public void setCalidad(Integer calidad) {
        this.calidad = calidad;
    }


    public String getCertificadoFitosanitario() {
        return certificadoFitosanitario;
    }


    public void setCertificadoFitosanitario(String certificadoFitosanitario) {
        this.certificadoFitosanitario = certificadoFitosanitario;
    }
}
