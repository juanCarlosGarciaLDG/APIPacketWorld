package dominio;

import dto.Respuesta;
import java.util.HashMap;
import java.util.Map;
import modelo.mybatis.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.exceptions.PersistenceException;
import pojo.ConductorAsignacion;

public class ConductorAsignacionImp {

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

    public static Respuesta asignarVehiculo(int conductorId, Integer vehiculoId) {
        Respuesta r = new Respuesta();
        SqlSession session = MyBatisUtil.getSession();
        try {
            // intenta insertar o actualizar la fila (crea si no existe)
            ConductorAsignacion ca = new ConductorAsignacion();
            ca.setConductorId(conductorId);
            ca.setVehiculoId(vehiculoId);
            // No tocamos envioId aquí (se mantiene)
            session.insert("conductorAsignacion.insertOrUpdate", ca);
            session.commit();
            r.setError(false);
            r.setMensaje("Vehículo asignado correctamente.");
        } catch (PersistenceException ex) {
            session.rollback();
            // si fue constraint (vehiculo ya asignado a otro), reporta conflicto
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

    public static Respuesta asignarEnvio(int conductorId, Integer envioId) {
        Respuesta r = new Respuesta();
        SqlSession session = MyBatisUtil.getSession();
        try {
            ConductorAsignacion ca = new ConductorAsignacion();
            ca.setConductorId(conductorId);
            ca.setEnvioId(envioId);
            session.insert("conductorAsignacion.insertOrUpdate", ca);
            session.commit();
            r.setError(false);
            r.setMensaje("Envío asignado correctamente.");
        } catch (PersistenceException ex) {
            session.rollback();
            Throwable cause = ex.getCause();
            String msg = (cause != null) ? cause.getMessage() : ex.getMessage();
            r.setError(true);
            r.setMensaje("Error al asignar envío: " + msg);
        } catch (Exception e) {
            session.rollback();
            r.setError(true);
            r.setMensaje("Error inesperado: " + e.getMessage());
        } finally {
            session.close();
        }
        return r;
    }

    public static Respuesta desasignarEnvio(int conductorId) {
        Respuesta r = new Respuesta();
        SqlSession session = MyBatisUtil.getSession();
        try {
            int filas = session.update("conductorAsignacion.desasignarEnvio", conductorId);
            session.commit();
            r.setError(false);
            r.setMensaje(filas > 0 ? "Envío desasignado." : "No existía asignación de envío para este conductor.");
        } catch (Exception e) {
            session.rollback();
            r.setError(true);
            r.setMensaje("Error al desasignar: " + e.getMessage());
        } finally {
            session.close();
        }
        return r;
    }
}