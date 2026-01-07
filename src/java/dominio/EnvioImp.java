package dominio;

import com.google.gson.Gson;
import dto.DistanciaRespuesta;
import dto.Respuesta;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.net.URL;
import java.net.HttpURLConnection;
import modelo.mybatis.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import pojo.Envio;
import pojo.Cliente;
import pojo.Paquete;
import pojo.Sucursal;

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

   public static Respuesta registrar(Envio envio, List<Paquete> paquetes) {
        Respuesta respuesta = new Respuesta();
        
        if (envio.getIdSucursalOrigen() == null || envio.getIdCliente() == null) {
             respuesta.setError(true);
             respuesta.setMensaje("Faltan datos: Se requiere Sucursal de Origen y Cliente.");
             return respuesta;
        }

        Sucursal sucursalOrigen = SucursalImp.obtenerPorId(envio.getIdSucursalOrigen());
        
        if (sucursalOrigen == null || sucursalOrigen.getCodigoPostal() == null) {
            respuesta.setError(true);
            respuesta.setMensaje("La sucursal de origen no tiene un Código Postal configurado.");
            return respuesta;
        }
        String cpOrigen = sucursalOrigen.getCodigoPostal();

        Cliente clienteDestino = ClienteImp.obtenerPorId(envio.getIdCliente());
        
        if (clienteDestino == null) {
            respuesta.setError(true);
            respuesta.setMensaje("El cliente especificado no existe.");
            return respuesta;
        }
        
        if (clienteDestino.getCp()== null || clienteDestino.getCp().isEmpty()) {
            respuesta.setError(true);
            respuesta.setMensaje("El cliente destinatario no tiene Código Postal registrado. No se puede calcular el costo.");
            return respuesta;
        }
        
        String cpDestino = clienteDestino.getCp();

        Double distancia = obtenerDistanciaWS(cpOrigen, cpDestino);
        
        if (distancia == null) {
            respuesta.setError(true);
            respuesta.setMensaje("No se pudo calcular la distancia entre CP Origen: " + cpOrigen + " y CP Destino: " + cpDestino);
            return respuesta;
        }

        double costoTotal = calcularCostoEnvio(distancia, paquetes.size());
        envio.setCosto(costoTotal); 

        SqlSession conexionBD = MyBatisUtil.getSession();
        if (conexionBD != null) {
            try {
                int filasAfectadas = conexionBD.insert("envio.registrar", envio);
                
                for(Paquete p : paquetes){
                    p.setIdEnvio(envio.getId());
                    conexionBD.insert("paquete.registrar", p);
                }

                conexionBD.commit();
                if (filasAfectadas > 0) {
                    respuesta.setError(false);
                    respuesta.setMensaje("Envío registrado correctamente. Distancia: " + distancia + " km. Costo total: $" + costoTotal);
                } else {
                    respuesta.setError(true);
                    respuesta.setMensaje("No se pudo registrar la información del envío en la BD.");
                }
            } catch (Exception e) {
                respuesta.setError(true);
                respuesta.setMensaje("Error al guardar en base de datos: " + e.getMessage());
            } finally {
                conexionBD.close();
            }
        } else {
            respuesta.setError(true);
            respuesta.setMensaje("Error de conexión con la base de datos.");
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
    
    private static Double obtenerDistanciaWS(String cpOrigen, String cpDestino) {
        try {
            String urlStr = "http://sublimas.com.mx:8080/calculadora/api/envios/distancia/" + cpOrigen + "," + cpDestino;
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                System.out.println("API Distancia error HTTP: " + conn.getResponseCode());
                return null;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            StringBuilder sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            conn.disconnect();

            Gson gson = new Gson();
            DistanciaRespuesta response = gson.fromJson(sb.toString(), DistanciaRespuesta.class);

            if (!response.isError()) {
                return response.getDistanciaKM();
            } else {
                System.out.println("API Distancia error lógico: " + response.getMensaje());
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static double calcularCostoEnvio(double distancia, int numPaquetes) {
        double costoPorKm = 0.0;

        if (distancia >= 1 && distancia <= 200) {
            costoPorKm = 4.00;
        } else if (distancia > 200 && distancia <= 500) {
            costoPorKm = 3.00;
        } else if (distancia > 500 && distancia <= 1000) {
            costoPorKm = 2.00;
        } else if (distancia > 1000 && distancia <= 2000) {
            costoPorKm = 1.00;
        } else if (distancia > 2000) {
            costoPorKm = 0.50;
        }

        return (distancia * costoPorKm);
    }

    
}