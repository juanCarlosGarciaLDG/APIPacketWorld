package pojo;

public class ConductorAsignacion {
    private Integer conductorId;
    private Integer vehiculoId;
    private Integer envioId;
    private String fechaCreacion;
    private String fechaActualizacion;

    public ConductorAsignacion() {}

    public Integer getConductorId() { return conductorId; }
    public void setConductorId(Integer conductorId) { this.conductorId = conductorId; }

    public Integer getVehiculoId() { return vehiculoId; }
    public void setVehiculoId(Integer vehiculoId) { this.vehiculoId = vehiculoId; }

    public Integer getEnvioId() { return envioId; }
    public void setEnvioId(Integer envioId) { this.envioId = envioId; }

    public String getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public String getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(String fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
}