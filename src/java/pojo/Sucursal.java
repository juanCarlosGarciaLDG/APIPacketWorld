package pojo;

import static dominio.SucursalImp.cambiarEstatus;
import dto.Respuesta;

public class Sucursal {
    private Integer idSucursal;
    private String codigoUnico;
    private String nombre;
    private String calle;
    private String numero;
    private String colonia;
    private String codigoPostal;
    private String ciudad;
    private String estado;
    private String estatus; // "Activa" o "Inactiva"
    private String fechaCreacion;

    public Sucursal() {}

    public Integer getIdSucursal() { return idSucursal; }
    public void setIdSucursal(Integer idSucursal) { this.idSucursal = idSucursal; }

    public String getCodigoUnico() { return codigoUnico; }
    public void setCodigoUnico(String codigoUnico) { this.codigoUnico = codigoUnico; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCalle() { return calle; }
    public void setCalle(String calle) { this.calle = calle; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getColonia() { return colonia; }
    public void setColonia(String colonia) { this.colonia = colonia; }

    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getEstatus() { return estatus; }
    public void setEstatus(String estatus) { this.estatus = estatus; }
        public static Respuesta darBaja(int idSucursal) {
        return cambiarEstatus(idSucursal, "Inactiva");
    }

    public static Respuesta reactivar(int idSucursal) {
        return cambiarEstatus(idSucursal, "Activa");
    }

    public String getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}