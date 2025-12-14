/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dominio;

import dto.Respuesta;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import modelo.mybatis.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import pojo.Vehiculo;

/**
 *
 * @author Lenovo
 */
public class VehiculoImp {

    public static List<Vehiculo> obtenerTodos() {
        List<Vehiculo> lista = null;
        SqlSession conexionBD = MyBatisUtil.getSession();
        if (conexionBD != null) {
            try {
                lista = conexionBD.selectList("vehiculo.obtener-todos");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conexionBD.close();
            }
        }
        return lista;
    }

    public static List<Vehiculo> buscar(String filtro) {
        List<Vehiculo> lista = null;
        SqlSession conexionBD = MyBatisUtil.getSession();
        if (conexionBD != null) {
            try {
                lista = conexionBD.selectList("vehiculo.buscar", filtro);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conexionBD.close();
            }
        }
        return lista;
    }

    public static Respuesta registrar(Vehiculo vehiculo) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexionBD = MyBatisUtil.getSession();
        if (conexionBD != null) {
            try {

                if (!validarTipoUnidad(vehiculo.getTipoUnidad())) {
                    respuesta.setError(true);
                    respuesta.setMensaje("Tipo de unidad inválido. Valores permitidos: Gasolina, Diesel, Eléctrica, Híbrida");
                    return respuesta;
                }
                String nii = generarNII(vehiculo.getAnio(), vehiculo.getVin());
                vehiculo.setNii(nii);

                int filas = conexionBD.insert("vehiculo.registrar", vehiculo);
                conexionBD.commit();
                if (filas > 0) {
                    respuesta.setError(false);
                    respuesta.setMensaje("Vehículo registrado. NII asignado: " + nii);
                } else {
                    respuesta.setError(true);
                    respuesta.setMensaje("No se pudo registrar el vehículo.");
                }
            } catch (Exception e) {
                respuesta.setError(true);
                respuesta.setMensaje("Error al registrar: " + e.getMessage());
            } finally {
                conexionBD.close();
            }
        }
        return respuesta;
    }

    public static Respuesta editar(Vehiculo vehiculo) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexionBD = MyBatisUtil.getSession();
        if (conexionBD != null) {
            try {

                if (!validarTipoUnidad(vehiculo.getTipoUnidad())) {
                    respuesta.setError(true);
                    respuesta.setMensaje("Tipo de unidad inválido. Valores permitidos: Gasolina, Diesel, Eléctrica, Híbrida");
                    return respuesta;
                }

                Vehiculo original = conexionBD.selectOne("vehiculo.obtener-por-id", vehiculo.getId());

                if (original != null) {
                    String nii = generarNII(vehiculo.getAnio(), original.getVin());
                    vehiculo.setNii(nii);

                    int filas = conexionBD.update("vehiculo.editar", vehiculo);
                    conexionBD.commit();
                    if (filas > 0) {
                        respuesta.setError(false);
                        respuesta.setMensaje("Vehículo actualizado correctamente.");
                    } else {
                        respuesta.setError(true);
                        respuesta.setMensaje("Error al actualizar.");
                    }
                } else {
                    respuesta.setError(true);
                    respuesta.setMensaje("El vehículo no existe.");
                }
            } catch (Exception e) {
                respuesta.setError(true);
                respuesta.setMensaje("Error al editar: " + e.getMessage());
            } finally {
                conexionBD.close();
            }
        }
        return respuesta;
    }

    public static Respuesta darBaja(int id, String motivo) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexionBD = MyBatisUtil.getSession();
        if (conexionBD != null) {
            try {
                Map<String, Object> params = new HashMap<>();
                params.put("id", id);
                params.put("motivo", motivo);

                int filas = conexionBD.update("vehiculo.dar-baja", params);
                conexionBD.commit();
                if (filas > 0) {
                    respuesta.setError(false);
                    respuesta.setMensaje("Vehículo dado de baja.");
                } else {
                    respuesta.setError(true);
                    respuesta.setMensaje("No se encontró el vehículo.");
                }
            } catch (Exception e) {
                respuesta.setError(true);
                respuesta.setMensaje("Error al dar de baja: " + e.getMessage());
            } finally {
                conexionBD.close();
            }
        }
        return respuesta;
    }

    private static String generarNII(Integer anio, String vin) {
        if (anio == null || vin == null || vin.length() < 4) {
            return "N/A";
        }
        return anio.toString() + vin.substring(0, 4);
    }

    private static boolean validarTipoUnidad(String tipo) {
        if (tipo == null) {
            return false;
        }
        return tipo.equals("Gasolina") || tipo.equals("Diesel")
                || tipo.equals("Eléctrica") || tipo.equals("Híbrida");
    }
}
