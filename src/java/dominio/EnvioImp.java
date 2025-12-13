package dominio;

import dto.Respuesta;
import java.util.List;
import java.math.BigDecimal;
import modelo.mybatis.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import pojo.Envio;

public class EnvioImp {

    public static List<Envio> obtenerTodos() {
        List<Envio> lista = null;
        SqlSession conexionBD = MyBatisUtil.getSession();
        if (conexionBD != null) {
            try {
                lista = conexionBD.selectList("envio.obtener-todos");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conexionBD.close();
            }
        }
        return lista;
    }

    public static Envio obtenerPorId(int id) {
        Envio envio = null;
        SqlSession conexionBD = MyBatisUtil.getSession();
        if (conexionBD != null) {
            try {
                envio = conexionBD.selectOne("envio.obtener-por-id", id);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conexionBD.close();
            }
        }
        return envio;
    }

    public static Envio obtenerPorGuia(String numGuia) {
        Envio envio = null;
        SqlSession conexionBD = MyBatisUtil.getSession();
        if (conexionBD != null) {
            try {
                envio = conexionBD.selectOne("envio.obtener-por-guia", numGuia);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conexionBD.close();
            }
        }
        return envio;
    }

    public static Respuesta registrar(Envio envio) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexionBD = MyBatisUtil.getSession();
        if (envio == null) {
            respuesta.setError(true);
            respuesta.setMensaje("Objeto envio nulo.");
            return respuesta;
        }
        if (conexionBD != null) {
            try {
                // Insert (useGeneratedKeys -> llena envio.id)
                int filas = conexionBD.insert("envio.registrar", envio);
                conexionBD.commit();

                if (filas > 0) {
                    // Si numGuia no fue proporcionado por la app, generarlo a partir del id
                    if (envio.getNumGuia() == null || envio.getNumGuia().trim().isEmpty()) {
                        String gen = String.format("PW-%06d", envio.getId());
                        envio.setNumGuia(gen);
                        conexionBD.update("envio.actualizar-guia", envio);
                        conexionBD.commit();
                    }
                    respuesta.setError(false);
                    respuesta.setMensaje("Envío registrado correctamente. Guía: " + envio.getNumGuia());
                } else {
                    respuesta.setError(true);
                    respuesta.setMensaje("No se pudo registrar el envío.");
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

    public static Respuesta editar(Envio envio) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexionBD = MyBatisUtil.getSession();
        if (envio == null || envio.getId() == null) {
            respuesta.setError(true);
            respuesta.setMensaje("Envio inválido o sin id.");
            return respuesta;
        }
        if (conexionBD != null) {
            try {
                int filas = conexionBD.update("envio.editar", envio);
                conexionBD.commit();
                if (filas > 0) {
                    respuesta.setError(false);
                    respuesta.setMensaje("Envío actualizado correctamente.");
                } else {
                    respuesta.setError(true);
                    respuesta.setMensaje("No se encontró el envío para actualizar.");
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

    public static Respuesta cambiarEstatus(int id, String estatus, Integer idColaborador) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexionBD = MyBatisUtil.getSession();
        if (id <= 0) {
            respuesta.setError(true);
            respuesta.setMensaje("ID inválido.");
            return respuesta;
        }
        if (estatus == null || estatus.trim().isEmpty()) {
            respuesta.setError(true);
            respuesta.setMensaje("Estatus inválido.");
            return respuesta;
        }
        if (conexionBD != null) {
            try {
                pojo.Envio e = new pojo.Envio();
                e.setId(id);
                e.setEstatus(estatus);
                e.setIdColaboradorActualizo(idColaborador);

                int filas = conexionBD.update("envio.cambiar-estatus", e);
                conexionBD.commit();
                if (filas > 0) {
                    respuesta.setError(false);
                    respuesta.setMensaje("Estatus actualizado a: " + estatus);
                } else {
                    respuesta.setError(true);
                    respuesta.setMensaje("No se encontró el envío para actualizar estado.");
                }
            } catch (Exception ex) {
                respuesta.setError(true);
                respuesta.setMensaje("Error al actualizar estatus: " + ex.getMessage());
            } finally {
                conexionBD.close();
            }
        } else {
            respuesta.setError(true);
            respuesta.setMensaje("No hubo conexión con la base de datos.");
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
                int filas = conexionBD.delete("envio.eliminar", id);
                conexionBD.commit();
                if (filas > 0) {
                    respuesta.setError(false);
                    respuesta.setMensaje("Envío eliminado correctamente.");
                } else {
                    respuesta.setError(true);
                    respuesta.setMensaje("No se encontró el envío para eliminar.");
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