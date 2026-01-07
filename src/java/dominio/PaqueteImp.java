package dominio;

import dto.Respuesta;
import java.util.List;
import modelo.mybatis.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import pojo.Paquete;

public class PaqueteImp {
    public static List<Paquete> obtenerTodos() {
        List<Paquete> lista = null;
        SqlSession conexionBD = MyBatisUtil.getSession();
        if (conexionBD != null) {
            try { lista = conexionBD.selectList("paquete.obtener-todos"); } 
            catch (Exception e) { e.printStackTrace(); } finally { conexionBD.close(); }
        }
        return lista;
    }
    public static List<Paquete> obtenerPorEnvio(int idEnvio) {
        List<Paquete> lista = null;
        SqlSession conexionBD = MyBatisUtil.getSession();
        if (conexionBD != null) {
            try { lista = conexionBD.selectList("paquete.obtener-por-envio", idEnvio); } 
            catch (Exception e) { e.printStackTrace(); } finally { conexionBD.close(); }
        }
        return lista;
    }
    public static Paquete obtenerPorId(int id) {
        Paquete p = null;
        SqlSession conexionBD = MyBatisUtil.getSession();
        if (conexionBD != null) {
            try { p = conexionBD.selectOne("paquete.obtener-por-id", id); } 
            catch (Exception e) { e.printStackTrace(); } finally { conexionBD.close(); }
        }
        return p;
    }

    public static Respuesta registrar(Paquete paquete) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexionBD = MyBatisUtil.getSession();

        if (paquete == null || paquete.getIdEnvio() == null) {
            return new Respuesta(true, "Datos inválidos.");
        }

        if (conexionBD != null) {
            try {
                int filas = conexionBD.insert("paquete.registrar", paquete);
                conexionBD.commit(); 

                if (filas > 0) {
                    EnvioImp.recalcularCosto(paquete.getIdEnvio());
                    respuesta.setError(false);
                    respuesta.setMensaje("Paquete registrado y costo actualizado.");
                } else {
                    respuesta.setError(true);
                    respuesta.setMensaje("Error al registrar.");
                }
            } catch (Exception e) {
                respuesta.setError(true);
                respuesta.setMensaje("Error BD: " + e.getMessage());
            } finally {
                conexionBD.close();
            }
        }
        return respuesta;
    }

    public static Respuesta editar(Paquete paquete) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexionBD = MyBatisUtil.getSession();

        if (paquete == null || paquete.getId() == null) return new Respuesta(true, "Datos inválidos.");

        if (conexionBD != null) {
            try {
                Paquete old = conexionBD.selectOne("paquete.obtener-por-id", paquete.getId());
                int filas = conexionBD.update("paquete.editar", paquete);
                conexionBD.commit();

                if (filas > 0) {
                    if (paquete.getIdEnvio() != null) EnvioImp.recalcularCosto(paquete.getIdEnvio());
                    if (old != null && !old.getIdEnvio().equals(paquete.getIdEnvio())) {
                        EnvioImp.recalcularCosto(old.getIdEnvio());
                    }
                    respuesta.setError(false);
                    respuesta.setMensaje("Paquete actualizado.");
                } else {
                    respuesta.setError(true);
                    respuesta.setMensaje("No encontrado.");
                }
            } catch (Exception e) {
                respuesta.setError(true);
                respuesta.setMensaje("Error: " + e.getMessage());
            } finally { conexionBD.close(); }
        }
        return respuesta;
    }

    public static Respuesta eliminar(int id) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexionBD = MyBatisUtil.getSession();
        if (id <= 0) return new Respuesta(true, "ID inválido");
        if (conexionBD != null) {
            try {
                Paquete p = conexionBD.selectOne("paquete.obtener-por-id", id);
                int filas = conexionBD.delete("paquete.eliminar", id);
                conexionBD.commit();
                if (filas > 0) {
                    if (p != null) EnvioImp.recalcularCosto(p.getIdEnvio());
                    respuesta.setError(false);
                    respuesta.setMensaje("Eliminado y recalculado.");
                } else {
                    respuesta.setError(true);
                    respuesta.setMensaje("No encontrado.");
                }
            } catch (Exception e) {
                respuesta.setError(true);
                respuesta.setMensaje("Error: " + e.getMessage());
            } finally { conexionBD.close(); }
        }
        return respuesta;
    }
}