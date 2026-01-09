package dominio;

import dto.Respuesta;
import modelo.mybatis.MyBatisUtil;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import pojo.ConductorAsignacion;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementación de operaciones sobre conductor_asignacion y sincronización con envios. Usa los statement ids definidos en tus mappers XML: - conductorAsignacion.* (ConductorAsignacionMapper.xml) - envio.* (EnvioMapper.xml)
 */
public class ConductorAsignacionImp {

    /**
     * Obtener la fila de conductor_asignacion por conductor.
     */
    public static ConductorAsignacion obtenerPorConductor(int conductorId) {
        ConductorAsignacion ca = null;
        SqlSession session = MyBatisUtil.getSession();
        try {
            ca = session.selectOne("conductorAsignacion.obtenerPorConductor", conductorId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return ca;
    }

    /**
     * Asignar/actualizar vehículo para un conductor (persistencia en conductor_asignacion).
     */
    public static Respuesta asignarVehiculo(int conductorId, Integer vehiculoId) {
        Respuesta r = new Respuesta();
        SqlSession session = MyBatisUtil.getSession();
        try {
            ConductorAsignacion ca = new ConductorAsignacion();
            ca.setConductorId(conductorId);
            ca.setVehiculoId(vehiculoId);
            // Mantiene envioId si existía (insertOrUpdate respeta ON DUPLICATE KEY UPDATE)
            session.insert("conductorAsignacion.insertOrUpdate", ca);
            session.commit();
            r.setError(false);
            r.setMensaje("Vehículo asignado correctamente.");
        } catch (PersistenceException ex) {
            session.rollback();
            Throwable cause = ex.getCause();
            String msg = (cause != null) ? cause.getMessage() : ex.getMessage();
            r.setError(true);
            r.setMensaje("Error al asignar vehículo: " + msg);
        } catch (Exception e) {
            session.rollback();
            r.setError(true);
            r.setMensaje("Error inesperado: " + e.getMessage());
        } finally {
            session.close();
        }
        return r;
    }

    /**
     * Desasignar vehículo (poner NULL en vehiculo_id del registro conductor_asignacion).
     */
    public static Respuesta desasignarVehiculo(int conductorId) {
        Respuesta r = new Respuesta();
        SqlSession session = MyBatisUtil.getSession();
        try {
            int filas = session.update("conductorAsignacion.desasignarVehiculo", conductorId);
            session.commit();
            r.setError(false);
            r.setMensaje(filas > 0 ? "Vehículo desasignado." : "No existía asignación para este conductor.");
        } catch (Exception e) {
            session.rollback();
            r.setError(true);
            r.setMensaje("Error al desasignar: " + e.getMessage());
        } finally {
            session.close();
        }
        return r;
    }

    /**
     * Asignar un envío a un conductor (historial en conductor_asignacion + actualizar envios.id_colaborador_actualizo). Realiza validaciones: - el envío no debe estar asignado ya a otro conductor (conductorAsignacion.buscarPorEnvio) - el conductor no debe tener ya otro envío asignado (envio.findEnvioByConductor)
     */
    public static Respuesta asignarEnvio(Integer conductorId, Integer envioId, Integer usuarioLogueadoId) { // <--- CAMBIO AQUÍ
        Respuesta r = new Respuesta();
        if (conductorId == null || envioId == null) {
            r.setError(true);
            r.setMensaje("Parámetros inválidos");
            return r;
        }

        SqlSession session = MyBatisUtil.getSession();
        try {
            // 0) Validaciones
            Integer existingConductorForEnvio = session.selectOne("conductorAsignacion.buscarPorEnvio", envioId);
            if (existingConductorForEnvio != null) {
                r.setError(true);
                r.setMensaje("El envío ya está asignado a un conductor (ID: " + existingConductorForEnvio + ").");
                return r;
            }

            Integer existingEnvioForConductor = session.selectOne("envio.findEnvioByConductor", conductorId);
            if (existingEnvioForConductor != null && !existingEnvioForConductor.equals(envioId)) {
                r.setError(true);
                r.setMensaje("El conductor ya tiene asignado el envío ID: " + existingEnvioForConductor);
                return r;
            }

            // 1) Insertar o actualizar fila conductor_asignacion (histórico / registro por conductor)
            ConductorAsignacion ca = new ConductorAsignacion();
            ca.setConductorId(conductorId);
            ca.setEnvioId(envioId);
            ca.setVehiculoId(null);
            session.insert("conductorAsignacion.insertOrUpdate", ca);

            // 2) Actualizar tabla envios para reflejar asignación actual
            Map<String, Object> params = new HashMap<>();
            params.put("envioId", envioId);
            params.put("conductorId", conductorId);
            params.put("usuarioLogueadoId", usuarioLogueadoId);
            session.update("envio.updateEnvioSetColaborador", params);

            session.commit();
            r.setError(false);
            r.setMensaje("Envío asignado correctamente.");
            return r;

        } catch (PersistenceException ex) {
            try {
                session.rollback();
            } catch (Throwable ignore) {
            }
            Throwable cause = ex.getCause();
            String msg = (cause != null) ? cause.getMessage() : ex.getMessage();
            r.setError(true);
            r.setMensaje("Error de persistencia: " + msg);
            return r;
        } catch (Throwable ex) {
            try {
                session.rollback();
            } catch (Throwable ignore) {
            }
            ex.printStackTrace();
            r.setError(true);
            r.setMensaje("Error al asignar: " + ex.getMessage());
            return r;
        } finally {
            session.close();
        }
    }

    /**
     * Desasignar envío de un conductor: limpia conductor_asignacion.envio_id y envios.id_colaborador_actualizo.
     */
    public static Respuesta desasignarEnvio(Integer conductorId) {
        Respuesta r = new Respuesta();
        if (conductorId == null) {
            r.setError(true);
            r.setMensaje("conductorId inválido");
            return r;
        }
        SqlSession session = MyBatisUtil.getSession();
        try {
            // Obtener asignación actual (si existe) para conocer el envio asociado
            ConductorAsignacion ca = session.selectOne("conductorAsignacion.obtenerPorConductor", conductorId);
            Integer envioId = (ca == null) ? null : ca.getEnvioId();

            // Limpiar conductor_asignacion (poner envio_id = NULL)
            session.update("conductorAsignacion.desasignarEnvio", conductorId);

            // Si había un envío asociado, limpiar envios.id_colaborador_actualizo
            if (envioId != null) {
                session.update("envio.clearEnvioColaborador", envioId);
            }

            session.commit();
            r.setError(false);
            r.setMensaje("Desasignado correctamente.");
            return r;
        } catch (PersistenceException ex) {
            try {
                session.rollback();
            } catch (Throwable ignore) {
            }
            Throwable cause = ex.getCause();
            String msg = (cause != null) ? cause.getMessage() : ex.getMessage();
            r.setError(true);
            r.setMensaje("Error de persistencia: " + msg);
            return r;
        } catch (Throwable ex) {
            try {
                session.rollback();
            } catch (Throwable ignore) {
            }
            ex.printStackTrace();
            r.setError(true);
            r.setMensaje("Error al desasignar: " + ex.getMessage());
            return r;
        } finally {
            session.close();
        }
    }

    public static Respuesta desasignarEnvioPorEnvio(Integer envioId, Integer usuarioLogueadoId) {
        Respuesta r = new Respuesta();
        if (envioId == null) {
            r.setError(true);
            r.setMensaje("envioId inválido");
            return r;
        }
        SqlSession session = MyBatisUtil.getSession();
        try {
            pojo.Envio envio = session.selectOne("envio.obtener-por-id", envioId);

            Integer conductorId = null;

            if (envio != null && envio.getIdConductor() != null) {
                conductorId = envio.getIdConductor();
            } else {
                conductorId = session.selectOne("conductorAsignacion.buscarPorEnvio", envioId);
            }

            int deletedByEnvio = session.delete("conductorAsignacion.deleteByEnvio", envioId);

            if (conductorId != null) {
                session.update("conductorAsignacion.desasignarEnvio", conductorId);
            }

            Map<String, Object> params = new HashMap<>();
            params.put("envioId", envioId);
            params.put("usuarioLogueadoId", usuarioLogueadoId);

            session.update("envio.clearEnvioColaborador", params);
            // -----------------------------------------------------------

            session.commit();
            r.setError(false);
            r.setMensaje("Desasignado correctamente.");
            return r;

        } catch (Throwable ex) {
            try {
                session.rollback();
            } catch (Throwable ignore) {
            }
            ex.printStackTrace();
            r.setError(true);
            r.setMensaje("Error al desasignar: " + ex.getMessage());
            return r;
        } finally {
            session.close();
        }
    }
}
