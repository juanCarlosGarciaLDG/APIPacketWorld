package pojo;

import dto.Respuesta;
import java.math.BigDecimal;

public class Envio {
    private Integer id;
    private Integer idCliente;
    private String numGuia;
    private String direccionDestino;
    private String ciudadDestino;
    private String estadoDestino;
    private Integer idSucursalOrigen;
    private double costo;
    private String estatus; // 'recibido','procesado','en_transito','entregado','cancelado'
    private String fechaCreacion;
    private String destinatarioNombre;
    private String destinatarioTelefono;
    private BigDecimal peso;
    private String fechaActualizacion;
    private Integer idColaboradorActualizo;

    public Envio() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getIdCliente() { return idCliente; }
    public void setIdCliente(Integer idCliente) { this.idCliente = idCliente; }

    public String getNumGuia() { return numGuia; }
    public void setNumGuia(String numGuia) { this.numGuia = numGuia; }

    public String getDireccionDestino() { return direccionDestino; }
    public void setDireccionDestino(String direccionDestino) { this.direccionDestino = direccionDestino; }

    public String getCiudadDestino() { return ciudadDestino; }
    public void setCiudadDestino(String ciudadDestino) { this.ciudadDestino = ciudadDestino; }

    public String getEstadoDestino() { return estadoDestino; }
    public void setEstadoDestino(String estadoDestino) { this.estadoDestino = estadoDestino; }

    public Integer getIdSucursalOrigen() { return idSucursalOrigen; }
    public void setIdSucursalOrigen(Integer idSucursalOrigen) { this.idSucursalOrigen = idSucursalOrigen; }

    public double getCosto() { return costo; }
    public void setCosto(double costo) { this.costo = costo; }

    public String getEstatus() { return estatus; }
    public void setEstatus(String estatus) { this.estatus = estatus; }

    public String getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public String getDestinatarioNombre() { return destinatarioNombre; }
    public void setDestinatarioNombre(String destinatarioNombre) { this.destinatarioNombre = destinatarioNombre; }

    public String getDestinatarioTelefono() { return destinatarioTelefono; }
    public void setDestinatarioTelefono(String destinatarioTelefono) { this.destinatarioTelefono = destinatarioTelefono; }

    public BigDecimal getPeso() { return peso; }
    public void setPeso(BigDecimal peso) { this.peso = peso; }

    public String getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(String fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    public Integer getIdColaboradorActualizo() { return idColaboradorActualizo; }
    public void setIdColaboradorActualizo(Integer idColaboradorActualizo) { this.idColaboradorActualizo = idColaboradorActualizo; }
}