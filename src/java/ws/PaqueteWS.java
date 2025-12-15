package ws;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dominio.PaqueteImp;
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
import pojo.Paquete;

@Path("paquete")
public class PaqueteWS {

    @Path("obtener-por-envio/{idEnvio}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Paquete> obtenerPorEnvio(@PathParam("idEnvio") int idEnvio) {
        if (idEnvio <= 0) throw new BadRequestException("idEnvio inválido");
        return PaqueteImp.obtenerPorEnvio(idEnvio);
    }

    @Path("obtener/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Paquete obtenerPorId(@PathParam("id") int id) {
        if (id <= 0) throw new BadRequestException("ID inválido");
        return PaqueteImp.obtenerPorId(id);
    }

    @Path("registrar")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Respuesta registrar(String json) {
        Gson gson = new Gson();
        try {
            Paquete p = gson.fromJson(json, Paquete.class);
            return PaqueteImp.registrar(p);
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
            Paquete p = gson.fromJson(json, Paquete.class);
            if (p == null || p.getId() == null) throw new BadRequestException("Objeto inválido o sin id");
            return PaqueteImp.editar(p);
        } catch (JsonSyntaxException ex) {
            throw new BadRequestException(ex.getMessage());
        }
    }

    // Compatibilidad si front no soporta PUT
    @Path("editar")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Respuesta editarPorPost(String json) {
        return editar(json);
    }

    @Path("eliminar/{id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Respuesta eliminar(@PathParam("id") int id) {
        if (id <= 0) throw new BadRequestException("ID inválido");
        return PaqueteImp.eliminar(id);
    }
}