package ws;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dominio.EnvioImp;
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
import pojo.Envio;

@Path("envio")
public class EnvioWS {

    @Path("obtener-todos")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Envio> obtenerTodos() {
        return EnvioImp.obtenerTodos();
    }

    @Path("obtener/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Envio obtenerPorId(@PathParam("id") int id) {
        if (id > 0) {
            return EnvioImp.obtenerPorId(id);
        } else {
            throw new BadRequestException("ID inválido");
        }
    }

    @Path("obtener-por-guia/{guia}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Envio obtenerPorGuia(@PathParam("guia") String guia) {
        if (guia != null && !guia.trim().isEmpty()) {
            return EnvioImp.obtenerPorGuia(guia);
        } else {
            throw new BadRequestException("Guía inválida");
        }
    }

    @Path("registrar")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Respuesta registrar(String json) {
        Gson gson = new Gson();
        try {
            Envio e = gson.fromJson(json, Envio.class);
            if (e != null) {
                return EnvioImp.registrar(e);
            } else {
                throw new BadRequestException("JSON inválido");
            }
        } catch (JsonSyntaxException ex) {
            throw new BadRequestException(ex.getMessage());
        }
    }

    @Path("editar")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Respuesta editar(String json) {
        Gson gson = new Gson();
        try {
            Envio e = gson.fromJson(json, Envio.class);
            if (e != null && e.getId() != null) {
                return EnvioImp.editar(e);
            } else {
                throw new BadRequestException("Objeto inválido o sin id");
            }
        } catch (JsonSyntaxException ex) {
            throw new BadRequestException(ex.getMessage());
        }
    }

    // Compatibilidad si el front no permite PUT
    @Path("editar")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Respuesta editarPorPost(String json) {
        return editar(json);
    }

    @Path("cambiar-estatus")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Respuesta cambiarEstatus(String json) {
        /*
          Espera JSON:
          { "id": 12, "estatus": "en_transito", "idColaboradorActualizo": 3 }
        */
        Gson gson = new Gson();
        try {
            // usar pojo.Envio como receptor
            Envio e = gson.fromJson(json, Envio.class);
            if (e == null || e.getId() == null || e.getEstatus() == null) {
                throw new BadRequestException("Falta id o estatus en el JSON.");
            }
            return EnvioImp.cambiarEstatus(e.getId(), e.getEstatus(), e.getIdColaboradorActualizo());
        } catch (JsonSyntaxException ex) {
            throw new BadRequestException(ex.getMessage());
        }
    }

    @Path("eliminar/{id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Respuesta eliminar(@PathParam("id") int id) {
        if (id > 0) {
            return EnvioImp.eliminar(id);
        } else {
            throw new BadRequestException("ID inválido");
        }
    }
}