package dominio;

import dto.Respuesta;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import modelo.mybatis.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import pojo.Paquete;

public class PaqueteImp {
    // En dominio.PaqueteImp (backend)
public static List<Paquete> obtenerTodos() {
        List<Paquete> lista = null;
        SqlSession conexionBD = MyBatisUtil.getSession();
        if (conexionBD != null) {
            try {
                lista = conexionBD.selectList("paquete.obtener-todos");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conexionBD.close();
            }
        }
        return lista;
    }

    public static List<Paquete> obtenerPorEnvio(int idEnvio) {
        List<Paquete> lista = null;
        SqlSession conexionBD = MyBatisUtil.getSession();
        if (conexionBD != null) {
            try {
                lista = conexionBD.selectList("paquete.obtener-por-envio", idEnvio);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conexionBD.close();
            }
        }
        return lista;
    }

    public static Paquete obtenerPorId(int id) {
        Paquete p = null;
        SqlSession conexionBD = MyBatisUtil.getSession();
        if (conexionBD != null) {
            try {
                p = conexionBD.selectOne("paquete.obtener-por-id", id);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conexionBD.close();
            }
        }
        return p;
    }

    public static Respuesta registrar(Paquete paquete) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexionBD = MyBatisUtil.getSession();

        if (paquete == null) {
            respuesta.setError(true);
            respuesta.setMensaje("Objeto paquete nulo.");
            return respuesta;
        }
        if (paquete.getIdEnvio() == null || paquete.getIdEnvio() <= 0) {
            respuesta.setError(true);
            respuesta.setMensaje("El paquete debe pertenecer a un envío válido (idEnvio).");
            return respuesta;
        }

        if (conexionBD != null) {
            try {
                int filas = conexionBD.insert("paquete.registrar", paquete);
                conexionBD.commit();
                if (filas > 0) {
                    respuesta.setError(false);
                    respuesta.setMensaje("Paquete registrado correctamente. ID: " + paquete.getId());
                } else {
                    respuesta.setError(true);
                    respuesta.setMensaje("No se pudo registrar el paquete.");
                }
            } catch (Exception e) {
                respuesta.setError(true);
                respuesta.setMensaje("Error en base de datos: " + e.getMessage());
            } finally {
                conexionBD.close();
            }
        } else {
            respuesta.setError(true);
            respuesta.setMensaje("No hubo conexión con la Base de Datos");
        }

        return respuesta;
    }

    public static Respuesta editar(Paquete paquete) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexionBD = MyBatisUtil.getSession();

        if (paquete == null || paquete.getId() == null) {
            respuesta.setError(true);
            respuesta.setMensaje("Paquete inválido o sin id.");
            return respuesta;
        }

        if (conexionBD != null) {
            try {
                int filas = conexionBD.update("paquete.editar", paquete);
                conexionBD.commit();
                if (filas > 0) {
                    respuesta.setError(false);
                    respuesta.setMensaje("Paquete actualizado correctamente.");
                } else {
                    respuesta.setError(true);
                    respuesta.setMensaje("No se encontró el paquete para actualizar.");
                }
            } catch (Exception e) {
                respuesta.setError(true);
                respuesta.setMensaje("Error al editar: " + e.getMessage());
            } finally {
                conexionBD.close();
            }
        } else {
            respuesta.setError(true);
            respuesta.setMensaje("No hubo conexión con la Base de Datos.");
        }

        return respuesta;
    }

    public static Respuesta eliminar(int id) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexionBD = MyBatisUtil.getSession();

        if (id <= 0) {
            respuesta.setError(true);
            respuesta.setMensaje("ID inválido.");
            return respuesta;
        }

        if (conexionBD != null) {
            try {
                int filas = conexionBD.delete("paquete.eliminar", id);
                conexionBD.commit();
                if (filas > 0) {
                    respuesta.setError(false);
                    respuesta.setMensaje("Paquete eliminado correctamente.");
                } else {
                    respuesta.setError(true);
                    respuesta.setMensaje("No se encontró el paquete para eliminar.");
                }
            } catch (Exception e) {
                respuesta.setError(true);
                respuesta.setMensaje("Error al eliminar: " + e.getMessage());
            } finally {
                conexionBD.close();
            }
        } else {
            respuesta.setError(true);
            respuesta.setMensaje("No hubo conexión con la Base de Datos.");
        }

        return respuesta;
    }
}