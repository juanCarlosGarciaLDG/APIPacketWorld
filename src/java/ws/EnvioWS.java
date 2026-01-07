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
    import pojo.Paquete;

    @Path("envio")
    public class EnvioWS {

        private class ParametrosRegistroEnvio {
            private Envio envio;
            private List<Paquete> paquetes;

            public Envio getEnvio() { return envio; }
            public void setEnvio(Envio envio) { this.envio = envio; }
            public List<Paquete> getPaquetes() { return paquetes; }
            public void setPaquetes(List<Paquete> paquetes) { this.paquetes = paquetes; }
        }

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
                ParametrosRegistroEnvio params = gson.fromJson(json, ParametrosRegistroEnvio.class);

                if (params != null && params.getEnvio() != null && params.getPaquetes() != null) {
                    return EnvioImp.registrar(params.getEnvio(), params.getPaquetes());
                } else {
                    return new Respuesta(true, "JSON inválido: Faltan datos de envío o lista de paquetes.");
                }
            } catch (JsonSyntaxException ex) {
                return new Respuesta(true, "Error de sintaxis JSON: " + ex.getMessage());
            } catch (Exception ex) {
                return new Respuesta(true, "Error interno: " + ex.getMessage());
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

            Gson gson = new Gson();
            try {
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