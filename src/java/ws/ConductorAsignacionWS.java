package ws;

import com.google.gson.Gson;
import dominio.ConductorAsignacionImp;
import dto.Respuesta;
import pojo.ConductorAsignacion;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("conductor-asignacion")
public class ConductorAsignacionWS {

    @Path("obtener/{conductorId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ConductorAsignacion obtener(@PathParam("conductorId") int conductorId) {
        return ConductorAsignacionImp.obtenerPorConductor(conductorId);
    }

    @Path("asignar-vehiculo/{conductorId}")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Respuesta asignarVehiculo(@PathParam("conductorId") int conductorId, String json) {
        Gson gson = new Gson();
        AssignRequest req = gson.fromJson(json, AssignRequest.class);
        return ConductorAsignacionImp.asignarVehiculo(conductorId, req.vehiculoId);
    }

    @Path("desasignar-vehiculo/{conductorId}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Respuesta desasignarVehiculo(@PathParam("conductorId") int conductorId) {
        return ConductorAsignacionImp.desasignarVehiculo(conductorId);
    }

    @Path("asignar-envio/{conductorId}")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Respuesta asignarEnvio(@PathParam("conductorId") int conductorId, String json) {
        Gson gson = new Gson();
        AssignRequest req = gson.fromJson(json, AssignRequest.class);

        return ConductorAsignacionImp.asignarEnvio(conductorId, req.envioId, req.usuarioLogueadoId);
    }

    @Path("desasignar-envio/{conductorId}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Respuesta desasignarEnvio(@PathParam("conductorId") int conductorId) {
        return ConductorAsignacionImp.desasignarEnvio(conductorId);
    }

    private static class AssignRequest {

        Integer vehiculoId;
        Integer envioId;
        Integer usuarioLogueadoId;
    }

    @Path("desasignar-envio-por-envio/{envioId}")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Respuesta desasignarEnvioPorEnvio(@PathParam("envioId") int envioId, String json) {
        Gson gson = new Gson();
        AssignRequest req = gson.fromJson(json, AssignRequest.class);

        Integer usuarioId = null;
        if (req != null) {
            usuarioId = req.usuarioLogueadoId;
        }

        return ConductorAsignacionImp.desasignarEnvioPorEnvio(envioId, usuarioId);
    }
}
