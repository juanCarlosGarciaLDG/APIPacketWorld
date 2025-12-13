package ws;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dominio.SucursalImp;
import dto.Respuesta;
import java.util.List;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import pojo.Sucursal;

/**
 *
 * @author Lenovo
 */
@Path("sucursal")
public class SucursalWS {

    // Obtener todos (devuelve la lista directamente, siguiendo la estructura de tu proyecto)
    @Path("obtener-todos")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Sucursal> obtenerTodos() {
        return SucursalImp.obtenerTodos();
    }

    @Path("obtener/{idSucursal}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Sucursal obtenerPorId(@PathParam("idSucursal") int idSucursal) {
        if (idSucursal > 0) {
            return SucursalImp.obtenerPorId(idSucursal);
        } else {
            throw new BadRequestException("ID inválido");
        }
    }

    @Path("registrar")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Respuesta registrar(String json) {
        Gson gson = new Gson();
        try {
            Sucursal s = gson.fromJson(json, Sucursal.class);
            if (s != null) {
                return SucursalImp.registrar(s);
            } else {
                throw new BadRequestException("El JSON enviado no es válido.");
            }
        } catch (JsonSyntaxException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Path("editar")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Respuesta editar(String json) {
        Gson gson = new Gson();
        try {
            Sucursal s = gson.fromJson(json, Sucursal.class);
            if (s != null && s.getIdSucursal() != null) {
                return SucursalImp.editar(s);
            } else {
                throw new BadRequestException("El objeto no tiene idSucursal o es nulo.");
            }
        } catch (JsonSyntaxException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    // Compatibilidad para front que no permita PUT
    @Path("editar")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Respuesta editarPorPost(String json) {
        return editar(json);
    }

    @Path("cambiar-estatus")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Respuesta cambiarEstatus(String json) {
        Gson gson = new Gson();
        try {
            // Espera JSON: { "idSucursal":3, "estatus":"Inactiva" }
            pojo.Sucursal s = gson.fromJson(json, pojo.Sucursal.class);
            if (s == null || s.getIdSucursal() == null || s.getEstatus() == null) {
                throw new BadRequestException("Falta idSucursal o estatus en el JSON.");
            }
            return SucursalImp.cambiarEstatus(s.getIdSucursal(), s.getEstatus());
        } catch (JsonSyntaxException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Path("eliminar/{idSucursal}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Respuesta eliminar(@PathParam("idSucursal") int idSucursal) {
        if (idSucursal > 0) {
            return SucursalImp.eliminar(idSucursal);
        } else {
            throw new BadRequestException("ID inválido");
        }
    }
}