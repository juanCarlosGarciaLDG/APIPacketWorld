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
        return ConductorAsignacionImp.asignarEnvio(conductorId, req.envioId);
    }

    @Path("desasignar-envio/{conductorId}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Respuesta desasignarEnvio(@PathParam("conductorId") int conductorId) {
        return ConductorAsignacionImp.desasignarEnvio(conductorId);
    }

    // clase interna para request simple { "vehiculoId": 1 } o { "envioId": 2 }
    private static class AssignRequest {
        Integer vehiculoId;
        Integer envioId;
    }
    @Path("desasignar-envio-por-envio/{envioId}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Respuesta desasignarEnvioPorEnvio(@PathParam("envioId") int envioId) {
        return ConductorAsignacionImp.desasignarEnvioPorEnvio(envioId);
    }
}