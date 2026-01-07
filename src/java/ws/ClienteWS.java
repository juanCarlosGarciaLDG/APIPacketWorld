package ws;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dominio.ClienteImp;
import dto.Respuesta;
import java.util.List;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import pojo.Cliente;

@Path("cliente")
public class ClienteWS {

   

    @Path("obtener-todos")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Cliente> obtenerTodos() {
        // Devuelve la lista directamente; puede ser [] si no hay clientes
        List<Cliente> lista = ClienteImp.obtenerTodos();
        if (lista == null) {
            return java.util.Collections.emptyList();
        }
        return lista;
    }

    @GET
    @Path("obtener/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Cliente obtenerPorId(@PathParam("id") Integer id) {
        if (id != null && id > 0) {
            Cliente cliente = ClienteImp.obtenerPorId(id);
            if (cliente != null) {
                return cliente;
            }
        }
        throw new BadRequestException("No se encontró un cliente con el ID proporcionado.");
    }
    
    // ... el resto de métodos (registrar, editar, eliminar, verificar) se mantienen igual
    @Path("registrar")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Respuesta registrar(String json) {
        Gson gson = new Gson();
        try {
            Cliente cliente = gson.fromJson(json, Cliente.class);
            return ClienteImp.registrar(cliente);
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
            Cliente cliente = gson.fromJson(json, Cliente.class);
            return ClienteImp.editar(cliente);
        } catch (JsonSyntaxException e) {
            Respuesta respuesta = new Respuesta();
            respuesta.setError(true);
            respuesta.setMensaje("Error al parsear JSON: " + e.getMessage());
            return respuesta;
        } catch (Exception e) {
            Respuesta respuesta = new Respuesta();
            respuesta.setError(true);
            respuesta.setMensaje("Error al editar el cliente: " + e.getMessage());
            return respuesta;
        }
    }

    @DELETE
    @Path("eliminar/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Respuesta eliminar(@PathParam("id") int idCliente) {
        try {
            return ClienteImp.eliminar(idCliente);
        } catch (Exception e) {
            Respuesta respuesta = new Respuesta();
            respuesta.setError(true);
            respuesta.setMensaje("Error al eliminar el cliente: " + e.getMessage());
            return respuesta;
        }
    }

    @Path("verificar_correo")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Respuesta verificarCorreo(@FormParam("correo") String correo) {
        if (correo != null && !correo.trim().isEmpty()) {
            return ClienteImp.verificarCorreo(correo);
        } else {
            throw new BadRequestException("El correo es requerido.");
        }
    }
}