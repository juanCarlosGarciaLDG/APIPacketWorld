package dominio;

import dto.Respuesta;
import java.util.List;
import modelo.mybatis.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import pojo.Colaborador;

/**
 *
 * @author Lenovo
 */
public class ColaboradorImp {

    public static List<Colaborador> obtenerTodos() {
        List<Colaborador> colaboradores = null;
        SqlSession conexionBD = MyBatisUtil.getSession();
        if (colaboradores == null) {
            System.out.println("DEBUG: ColaboradorImp.obtenerTodos -> colaboradores == null");
        } else {
            System.out.println("DEBUG: ColaboradorImp.obtenerTodos -> size = " + colaboradores.size());
        }
        if (conexionBD != null) {
            try {
                colaboradores = conexionBD.selectList("colaborador.obtener-todos");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conexionBD.close();
            }
        }
        return colaboradores;
    }

    public static Respuesta registrar(Colaborador colaborador) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexionBD = MyBatisUtil.getSession();

        if (conexionBD != null) {
            try {
                int filasAfectadas = conexionBD.insert("colaborador.registrar", colaborador);
                conexionBD.commit();

                if (filasAfectadas > 0) {
                    respuesta.setError(false);
                    respuesta.setMensaje("Colaborador " + colaborador.getNombre() + " " + colaborador.getApellidoPaterno() + " registrado exitosamente.");
                } else {
                    respuesta.setError(true);
                    respuesta.setMensaje("Error al registrar al colaborador.");
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

    public static Respuesta editar(Colaborador colaborador) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexionBD = MyBatisUtil.getSession();

        if (conexionBD != null) {
            try {
                int filasAfectadas = conexionBD.update("colaborador.editar", colaborador);
                conexionBD.commit();

                if (filasAfectadas > 0) {
                    respuesta.setError(false);
                    respuesta.setMensaje("Datos del colaborador actualizados correctamente.");
                } else {
                    respuesta.setError(true);
                    respuesta.setMensaje("No se pudo editar al colaborador (verifique el ID).");
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

    public static Respuesta eliminar(int idColaborador) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexionBD = MyBatisUtil.getSession();

        if (idColaborador <= 0) {
            respuesta.setError(true);
            respuesta.setMensaje("El ID del colaborador no es válido.");
            return respuesta;
        }

        if (conexionBD != null) {
            try {
                int filasAfectadas = conexionBD.delete("colaborador.eliminar", idColaborador);
                conexionBD.commit();

                if (filasAfectadas > 0) {
                    respuesta.setError(false);
                    respuesta.setMensaje("Colaborador eliminado exitosamente");
                } else {
                    respuesta.setError(true);
                    respuesta.setMensaje("No se encontró el colaborador para eliminar.");
                }
            } catch (Exception e) {
                respuesta.setError(true);
                respuesta.setMensaje("Error al eliminar: " + e.getMessage());
            } finally {
                conexionBD.close();
            }
        } else {
            respuesta.setError(true);
            respuesta.setMensaje("No hubo conexión con la base de datos");
        }
        return respuesta;
    }

    public static Respuesta guardarFoto(int idColaborador, byte[] foto) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexionBD = MyBatisUtil.getSession();

        if (conexionBD != null) {
            try {
                Colaborador colaborador = new Colaborador();
                colaborador.setIdColaborador(idColaborador);
                colaborador.setFoto(foto);

                int filasAfectadas = conexionBD.update("colaborador.guardar-foto", colaborador);
                conexionBD.commit();

                if (filasAfectadas > 0) {
                    respuesta.setError(false);
                    respuesta.setMensaje("Fotografía del colaborador actualizada correctamente.");
                } else {
                    respuesta.setError(true);
                    respuesta.setMensaje("No se pudo actualizar la imagen (ID no encontrado).");
                }
            } catch (Exception e) {
                respuesta.setError(true);
                respuesta.setMensaje("Error en BD: " + e.getMessage());
            } finally {
                conexionBD.close();
            }
        } else {
            respuesta.setError(true);
            respuesta.setMensaje("No hay conexión con la base de datos.");
        }

        return respuesta;
    }

    public static Colaborador obtenerFoto(int idColaborador) {
        Colaborador colaborador = null;
        SqlSession conexionBD = MyBatisUtil.getSession();

        if (conexionBD != null) {
            try {
                colaborador = conexionBD.selectOne("colaborador.obtener-foto", idColaborador);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conexionBD.close();
            }
        }

        return colaborador;
    }
}
