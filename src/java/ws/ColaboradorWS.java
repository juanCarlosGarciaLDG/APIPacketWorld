package ws;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dominio.ColaboradorImp;
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
import pojo.Colaborador;
import com.google.gson.JsonObject; 
import com.google.gson.JsonParser;
import java.util.HashMap;

/**
 *
 * @author Lenovo
 */
@Path("colaborador")
public class ColaboradorWS {

    @Path("obtener-todos")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Colaborador> obtenerTodos() {
        return ColaboradorImp.obtenerTodos();
    }
    @Path("registrar")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Respuesta registrar(String json) {
        Gson gson = new Gson();
        try {
            Colaborador colaborador = gson.fromJson(json, Colaborador.class);

            if (colaborador != null) {
                return ColaboradorImp.registrar(colaborador);
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
            Colaborador colaborador = gson.fromJson(json, Colaborador.class);
            if (colaborador != null && colaborador.getIdColaborador() != null) {
                return ColaboradorImp.editar(colaborador);
            } else {
                throw new BadRequestException("El objeto no tiene ID o es nulo.");
            }
        } catch (JsonSyntaxException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Path("eliminar/{idColaborador}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Respuesta eliminar(@PathParam("idColaborador") int idColaborador) {
        if (idColaborador > 0) {
            try {
                return ColaboradorImp.eliminar(idColaborador);
            } catch (Exception e) {
                throw new BadRequestException(e.getMessage());
            }
        } else {
            throw new BadRequestException("El ID debe ser mayor a 0");
        }
    }

    @Path("subir-foto/{idColaborador}")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Respuesta subirFoto(@PathParam("idColaborador") Integer idColaborador, byte[] foto) {
        if (idColaborador != null && idColaborador > 0 && foto != null && foto.length > 0) {
            return ColaboradorImp.guardarFoto(idColaborador, foto);
        } else {
            throw new BadRequestException();
        }
    }

    @Path("obtener-foto/{idColaborador}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Colaborador obtenerFoto(@PathParam("idColaborador") Integer idColaborador) {
        if (idColaborador != null && idColaborador > 0) {
            return ColaboradorImp.obtenerFoto(idColaborador);
        } else {
            throw new BadRequestException();
        }
    }
    
    @Path("login")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String login(String json) {
        Gson gson = new Gson();
        HashMap<String, Object> respuesta = new HashMap<>(); 
        
        try {
            JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
            String noPersonal = jsonObject.get("noPersonal").getAsString();
            String contrasena = jsonObject.get("contrasena").getAsString(); 

            Colaborador colaborador = ColaboradorImp.login(noPersonal, contrasena);

            if (colaborador != null) {
                respuesta.put("error", false);
                respuesta.put("mensaje", "Bienvenido(a) " + colaborador.getNombre());
                respuesta.put("colaborador", colaborador);
            } else {
                respuesta.put("error", true);
                respuesta.put("mensaje", "Credenciales incorrectas.");
            }
        } catch (Exception e) {
            respuesta.put("error", true);
            respuesta.put("mensaje", "Error en el servicio: " + e.getMessage());
        }
        
        return gson.toJson(respuesta);
    }

}