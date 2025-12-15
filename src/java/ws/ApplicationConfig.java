package ws;

import java.util.Set;

@javax.ws.rs.ApplicationPath("api")
public class ApplicationConfig extends javax.ws.rs.core.Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

   
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(ws.ClienteWS.class);
        resources.add(ws.ColaboradorWS.class);
        resources.add(ws.ConductorAsignacionWS.class);
        resources.add(ws.EnvioWS.class);
        resources.add(ws.PaqueteWS.class);
        resources.add(ws.SucursalWS.class);
        resources.add(ws.VehiculoWS.class);
    }
    
}
