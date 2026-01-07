/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

/**
 *
 * @author Lenovo
 */
public class DistanciaRespuesta {
    private Double distanciaKM;
    private boolean error;
    private String mensaje;

    public Double getDistanciaKM() { return distanciaKM; }
    public void setDistanciaKM(Double distanciaKM) { this.distanciaKM = distanciaKM; }
    public boolean isError() { return error; }
    public void setError(boolean error) { this.error = error; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}
