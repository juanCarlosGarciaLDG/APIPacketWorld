package dominio;

import Utilidades.Constantes;
import dto.Respuesta;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import modelo.mybatis.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import pojo.Cliente;

public class ClienteImp {

    public static List<Cliente> obtenerTodos() {
    List<Cliente> clientes = java.util.Collections.emptyList();
    SqlSession conexionBD = null;
    try {
        conexionBD = MyBatisUtil.getSession();
        if (conexionBD != null) {
            // llamar con el id EXACTO que MyBatis cargó (con guion)
            List<Cliente> tmp = conexionBD.selectList("cliente.obtener-todos");
            if (tmp != null) clientes = tmp;
        }
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        if (conexionBD != null) conexionBD.close();
    }
    return clientes;
}

    public static Respuesta registrar(Cliente cliente) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexionBD = MyBatisUtil.getSession();

        if (conexionBD != null) {
            try {
                int filasAfectadas = conexionBD.insert("cliente.registrar", cliente);
                conexionBD.commit();

                if (filasAfectadas > 0) {
                    respuesta.setError(false);
                    respuesta.setMensaje("Cliente " + cliente.getNombre() + " registrado exitosamente.");
                } else {
                    respuesta.setError(true);
                    respuesta.setMensaje("Error al registrar el cliente.");
                }
            } catch (Exception e) {
                respuesta.setError(true);
                respuesta.setMensaje(e.getMessage());
            } finally {
                conexionBD.close();
            }
        } else {
            respuesta.setError(true);
            respuesta.setMensaje(Constantes.MSJ_ERROR_BD);
        }

        return respuesta;
    }

    public static Respuesta editar(Cliente cliente) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexionBD = MyBatisUtil.getSession();

        if (conexionBD != null) {
            try {
                // aquí MyBatis tiene "cliente.obtener_por_id" con underscore según tu listado
                Cliente existente = conexionBD.selectOne("cliente.obtener_por_id", cliente.getId());
                if (existente == null) {
                    respuesta.setError(true);
                    respuesta.setMensaje("Cliente no encontrado.");
                    return respuesta;
                }

                Map<String, Object> params = new HashMap<>();
                params.put("correo", cliente.getCorreo());
                params.put("id", cliente.getId());
                Integer existe = conexionBD.selectOne("cliente.verificar_correo_editar", params);
                if (existe != null && existe > 0) {
                    respuesta.setError(true);
                    respuesta.setMensaje("Correo ya registrado en otro cliente.");
                    return respuesta;
                }

                int filasAfectadas = conexionBD.update("cliente.editar", cliente);
                conexionBD.commit();

                if (filasAfectadas > 0) {
                    respuesta.setError(false);
                    respuesta.setMensaje("Cliente actualizado correctamente.");
                } else {
                    respuesta.setError(true);
                    respuesta.setMensaje("No se pudo actualizar el cliente.");
                }
            } catch (Exception e) {
                conexionBD.rollback();
                respuesta.setError(true);
                respuesta.setMensaje("Error al actualizar el cliente: " + e.getMessage());
                e.printStackTrace();
            } finally {
                conexionBD.close();
            }
        } else {
            respuesta.setError(true);
            respuesta.setMensaje("No se pudo establecer la conexión a la base de datos.");
        }
        return respuesta;
    }

    public static Respuesta eliminar(int idCliente) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexionBD = MyBatisUtil.getSession();

        if (conexionBD != null) {
            try {
                int filasAfectadas = conexionBD.delete("cliente.eliminar", idCliente);
                conexionBD.commit();

                if (filasAfectadas > 0) {
                    respuesta.setError(false);
                    respuesta.setMensaje("Cliente eliminado correctamente.");
                } else {
                    respuesta.setError(true);
                    respuesta.setMensaje("No se pudo eliminar el cliente.");
                }
            } catch (Exception e) {
                conexionBD.rollback();
                respuesta.setError(true);
                respuesta.setMensaje("Error al eliminar el cliente: " + e.getMessage());
                e.printStackTrace();
            } finally {
                conexionBD.close();
            }
        } else {
            respuesta.setError(true);
            respuesta.setMensaje("No se pudo establecer la conexión a la base de datos.");
        }
        return respuesta;
    }

    public static Respuesta verificarCorreo(String correo) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexionBD = MyBatisUtil.getSession();

        if (conexionBD != null) {
            try {
                Integer existe = conexionBD.selectOne("cliente.verificar_correo", correo);
                if (existe != null && existe > 0) {
                    respuesta.setError(true);
                    respuesta.setMensaje("El correo ya existe.");
                } else {
                    respuesta.setError(false);
                    respuesta.setMensaje("El correo está disponible.");
                }
            } catch (Exception e) {
                respuesta.setError(true);
                respuesta.setMensaje("Error al verificar correo: " + e.getMessage());
            } finally {
                conexionBD.close();
            }
        } else {
            respuesta.setError(true);
            respuesta.setMensaje(Constantes.MSJ_ERROR_BD);
        }

        return respuesta;
    }
}