package dominio;

import dto.Respuesta;
import java.util.List;
import modelo.mybatis.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import pojo.Sucursal;

/**
 *
 * @author Lenovo
 */
public class SucursalImp {

    public static List<Sucursal> obtenerTodos() {
        List<Sucursal> sucursales = null;
        SqlSession conexionBD = MyBatisUtil.getSession();
        if (sucursales == null) {
            System.out.println("DEBUG: SucursalImp.obtenerTodos -> sucursales == null");
        } else {
            System.out.println("DEBUG: SucursalImp.obtenerTodos -> size = " + sucursales.size());
        }
        if (conexionBD != null) {
            try {
                sucursales = conexionBD.selectList("sucursal.obtener-todos");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conexionBD.close();
            }
        }
        return sucursales;
    }

    public static Sucursal obtenerPorId(int idSucursal) {
        Sucursal sucursal = null;
        SqlSession conexionBD = MyBatisUtil.getSession();

        if (conexionBD != null) {
            try {
                sucursal = conexionBD.selectOne("sucursal.obtener-por-id", idSucursal);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conexionBD.close();
            }
        }
        return sucursal;
    }

    public static Respuesta registrar(Sucursal sucursal) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexionBD = MyBatisUtil.getSession();

        if (conexionBD != null) {
            try {
                int filasAfectadas = conexionBD.insert("sucursal.registrar", sucursal);
                conexionBD.commit();

                if (filasAfectadas > 0) {
                    respuesta.setError(false);
                    respuesta.setMensaje("Sucursal " + sucursal.getNombre() + " registrada exitosamente.");
                } else {
                    respuesta.setError(true);
                    respuesta.setMensaje("Error al registrar la sucursal.");
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

    public static Respuesta editar(Sucursal sucursal) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexionBD = MyBatisUtil.getSession();

        if (conexionBD != null) {
            try {
                int filasAfectadas = conexionBD.update("sucursal.editar", sucursal);
                conexionBD.commit();

                if (filasAfectadas > 0) {
                    respuesta.setError(false);
                    respuesta.setMensaje("Datos de la sucursal actualizados correctamente.");
                } else {
                    respuesta.setError(true);
                    respuesta.setMensaje("No se pudo editar la sucursal (verifique el ID).");
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

        public static Respuesta cambiarEstatus(int idSucursal, String estatus) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexionBD = MyBatisUtil.getSession();

        if (idSucursal <= 0) {
            respuesta.setError(true);
            respuesta.setMensaje("ID inválido.");
            return respuesta;
        }

        if (estatus == null || !(estatus.equals("Activa") || estatus.equals("Inactiva"))) {
            respuesta.setError(true);
            respuesta.setMensaje("Estatus inválido. Valores permitidos: 'Activa' o 'Inactiva'.");
            return respuesta;
        }

        if (conexionBD != null) {
            try {
                pojo.Sucursal s = new pojo.Sucursal();
                s.setIdSucursal(idSucursal);
                s.setEstatus(estatus);

                int filasAfectadas = conexionBD.update("sucursal.cambiar-estatus", s);
                conexionBD.commit();

                if (filasAfectadas > 0) {
                    respuesta.setError(false);
                    respuesta.setMensaje("Estatus actualizado a '" + estatus + "'.");
                } else {
                    respuesta.setError(true);
                    respuesta.setMensaje("No se encontró la sucursal.");
                }
            } catch (Exception e) {
                respuesta.setError(true);
                respuesta.setMensaje("Error al actualizar estatus: " + e.getMessage());
            } finally {
                conexionBD.close();
            }
        } else {
            respuesta.setError(true);
            respuesta.setMensaje("No hubo conexión con la base de datos.");
        }
        return respuesta;
    }

    public static Respuesta eliminar(int idSucursal) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexionBD = MyBatisUtil.getSession();

        if (idSucursal <= 0) {
            respuesta.setError(true);
            respuesta.setMensaje("El ID de la sucursal no es válido.");
            return respuesta;
        }

        if (conexionBD != null) {
            try {
                int filasAfectadas = conexionBD.delete("sucursal.eliminar", idSucursal);
                conexionBD.commit();

                if (filasAfectadas > 0) {
                    respuesta.setError(false);
                    respuesta.setMensaje("Sucursal eliminada exitosamente");
                } else {
                    respuesta.setError(true);
                    respuesta.setMensaje("No se encontró la sucursal para eliminar.");
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
}