/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ws;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dominio.VehiculoImp;
import dto.Respuesta;
import java.util.List;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import pojo.Vehiculo;

/**
 *
 * @author Lenovo
 */
@Path("vehiculo")
public class VehiculoWS {

    @Path("obtener-todos")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Vehiculo> obtenerTodos() {
        return VehiculoImp.obtenerTodos();
    }

    @Path("obtener-disponibles")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Vehiculo> obtenerDisponibles() {
        return VehiculoImp.obtenerDisponibles();
    }

    @Path("obtener/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Vehiculo obtenerPorId(@PathParam("id") Integer id) {
        if (id != null && id > 0) {
            Vehiculo v = VehiculoImp.obtenerPorId(id);
            if (v != null) {
                return v;
            }
            throw new javax.ws.rs.NotFoundException();
        } else {
            throw new BadRequestException("ID inválido");
        }
    }

    @Path("buscar/{filtro}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Vehiculo> buscar(@PathParam("filtro") String filtro) {
        return VehiculoImp.buscar(filtro);
    }

    @Path("registrar")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Respuesta registrar(String json) {
        Gson gson = new Gson();
        try {
            Vehiculo v = gson.fromJson(json, Vehiculo.class);
            if (v.getVin() == null || v.getAnio() == null) {
                throw new BadRequestException("VIN y Año son obligatorios");
            }
            return VehiculoImp.registrar(v);
        } catch (JsonSyntaxException e) {
            throw new BadRequestException("JSON inválido");
        }
    }

    @Path("editar")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Respuesta editar(String json) {
        Gson gson = new Gson();
        try {
            Vehiculo v = gson.fromJson(json, Vehiculo.class);
            if (v.getId() == null) {
                throw new BadRequestException("El ID es obligatorio para editar");
            }
            return VehiculoImp.editar(v);
        } catch (JsonSyntaxException e) {
            throw new BadRequestException("JSON inválido");
        }
    }

    @Path("baja-vehiculo")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Respuesta darBaja(String json) {
        Gson gson = new Gson();
        try {
            Vehiculo v = gson.fromJson(json, Vehiculo.class);
            if (v.getId() == null || v.getMotivoBaja() == null || v.getMotivoBaja().trim().isEmpty()) {
                throw new BadRequestException("El ID y el motivo son obligatorios.");
            }
            return VehiculoImp.darBaja(v.getId(), v.getMotivoBaja());
        } catch (JsonSyntaxException e) {
            throw new BadRequestException("JSON inválido");
        }
    }
}
