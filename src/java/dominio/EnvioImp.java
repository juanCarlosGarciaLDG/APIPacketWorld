package dominio;

import com.google.gson.Gson;
import dto.DistanciaRespuesta;
import dto.Respuesta;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import modelo.mybatis.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import pojo.Cliente;
import pojo.Envio;
import pojo.Paquete;
import pojo.Sucursal;

public class EnvioImp {

    public static List<Envio> obtenerTodos() {
        List<Envio> lista = null;
        SqlSession conexionBD = MyBatisUtil.getSession();
        if (conexionBD != null) {
            try { lista = conexionBD.selectList("envio.obtener-todos"); } 
            catch (Exception e) { e.printStackTrace(); } finally { conexionBD.close(); }
        }
        return lista;
    }

    public static Envio obtenerPorId(int id) {
        Envio envio = null;
        SqlSession conexionBD = MyBatisUtil.getSession();
        if (conexionBD != null) {
            try { envio = conexionBD.selectOne("envio.obtener-por-id", id); } 
            catch (Exception e) { e.printStackTrace(); } finally { conexionBD.close(); }
        }
        return envio;
    }

    public static Envio obtenerPorGuia(String numGuia) {
        Envio envio = null;
        SqlSession conexionBD = MyBatisUtil.getSession();
        if (conexionBD != null) {
            try { envio = conexionBD.selectOne("envio.obtener-por-guia", numGuia); } 
            catch (Exception e) { e.printStackTrace(); } finally { conexionBD.close(); }
        }
        return envio;
    }

    public static Respuesta registrar(Envio envio, List<Paquete> paquetes) {
        Respuesta respuesta = new Respuesta();
        
        if (envio.getIdSucursalOrigen() == null || envio.getIdCliente() == null) {
             return new Respuesta(true, "Faltan datos: Sucursal Origen o Cliente.");
        }

        Sucursal sucursal = SucursalImp.obtenerPorId(envio.getIdSucursalOrigen());
        Cliente cliente = ClienteImp.obtenerPorId(envio.getIdCliente());

        if (sucursal == null || cliente == null || 
            sucursal.getCodigoPostal() == null || cliente.getCp() == null) {
            return new Respuesta(true, "Faltan códigos postales en sucursal o cliente.");
        }

        Double distancia = obtenerDistanciaWS(sucursal.getCodigoPostal(), cliente.getCp());
        if (distancia == null) {
            return new Respuesta(true, "No se pudo calcular la distancia con la API externa.");
        }

        int cantidadTotalPaquetes = 0;
        double pesoTotalDouble = 0.0;

        if (paquetes != null) {
            for (Paquete p : paquetes) {
                int q = (p.getCantidad() != null && p.getCantidad() > 0) ? p.getCantidad() : 1;
                cantidadTotalPaquetes += q;
                double pesoUnitario = (p.getPeso() != null) ? p.getPeso().doubleValue() : 0.0;
                pesoTotalDouble += (pesoUnitario * q);
            }
        }
        if (cantidadTotalPaquetes == 0) cantidadTotalPaquetes = 1;

        double costoTotal = calcularCostoLogica(distancia, cantidadTotalPaquetes);
        
        envio.setCosto(costoTotal);
        envio.setPeso(new BigDecimal(pesoTotalDouble));

        SqlSession conexionBD = MyBatisUtil.getSession();
        if (conexionBD != null) {
            try {
                int filas = conexionBD.insert("envio.registrar", envio);
                
                if (filas > 0) {
                    if (envio.getNumGuia() == null || envio.getNumGuia().trim().isEmpty()) {
                        String guiaGen = String.format("PW-%06d", envio.getId());
                        envio.setNumGuia(guiaGen);
                        conexionBD.update("envio.actualizar-guia", envio);
                    }
                }

                if (paquetes != null) {
                    for (Paquete p : paquetes) {
                        p.setIdEnvio(envio.getId());
                        if (p.getCantidad() == null || p.getCantidad() <= 0) p.setCantidad(1);
                        if (p.getValor() == null) p.setValor(BigDecimal.ZERO);
                        conexionBD.insert("paquete.registrar", p);
                    }
                }
                
                conexionBD.commit();
                respuesta.setError(false);
                respuesta.setMensaje("Envío registrado. Costo: $" + costoTotal);
            } catch (Exception e) {
                conexionBD.rollback();
                respuesta.setError(true);
                respuesta.setMensaje("Error BD: " + e.getMessage());
                e.printStackTrace();
            } finally {
                conexionBD.close();
            }
        } else {
            respuesta.setError(true);
            respuesta.setMensaje("Sin conexión a BD.");
        }
        return respuesta;
    }

    public static void recalcularCosto(int idEnvio) {
        SqlSession conexionBD = MyBatisUtil.getSession();
        if (conexionBD == null) return;

        try {
            Envio envio = conexionBD.selectOne("envio.obtener-por-id", idEnvio);
            if (envio == null) return;

            if (envio.getIdSucursalOrigen() == null || envio.getIdCliente() == null) {
                System.out.println("ERROR: El envío " + idEnvio + " tiene IDs nulos. Verifica el Mapper.");
                return;
            }

            List<Paquete> paquetes = conexionBD.selectList("paquete.obtener-por-envio", idEnvio);
            
            int totalPaquetes = 0;
            double totalPeso = 0.0;

            if (paquetes != null) {
                for (Paquete p : paquetes) {
                    int q = (p.getCantidad() != null && p.getCantidad() > 0) ? p.getCantidad() : 1;
                    totalPaquetes += q;
                    double pesoUnit = (p.getPeso() != null) ? p.getPeso().doubleValue() : 0.0;
                    totalPeso += (pesoUnit * q);
                }
            }
            if (totalPaquetes == 0) totalPaquetes = 1;

            Sucursal suc = SucursalImp.obtenerPorId(envio.getIdSucursalOrigen());
            Cliente cli = ClienteImp.obtenerPorId(envio.getIdCliente());
            
            if (suc != null && cli != null && suc.getCodigoPostal() != null && cli.getCp() != null) {
                Double distancia = obtenerDistanciaWS(suc.getCodigoPostal(), cli.getCp());
                
                if (distancia != null) {
                    double nuevoCosto = calcularCostoLogica(distancia, totalPaquetes);
                    
                    envio.setCosto(nuevoCosto);
                    envio.setPeso(new BigDecimal(totalPeso));
                    
                    conexionBD.update("envio.editar", envio);
                    conexionBD.commit();
                    System.out.println("Recálculo exitoso para Envío " + idEnvio + ": $" + nuevoCosto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            conexionBD.rollback();
        } finally {
            conexionBD.close();
        }
    }

    private static double calcularCostoLogica(double distancia, int numPaquetes) {
        double costoPorKm = 0.50; 

        if (distancia >= 1 && distancia <= 200) costoPorKm = 4.00;
        else if (distancia > 200 && distancia <= 500) costoPorKm = 3.00;
        else if (distancia > 500 && distancia <= 1000) costoPorKm = 2.00;
        else if (distancia > 1000 && distancia <= 2000) costoPorKm = 1.00;

        double costoBase = distancia * costoPorKm;

        double costoAdicional = 0.0;
        if (numPaquetes == 2) costoAdicional = 50.00;
        else if (numPaquetes == 3) costoAdicional = 80.00;
        else if (numPaquetes == 4) costoAdicional = 110.00;
        else if (numPaquetes >= 5) costoAdicional = 150.00;

        return costoBase + costoAdicional;
    }

    private static Double obtenerDistanciaWS(String cpOrigen, String cpDestino) {
        try {
            String urlStr = "http://sublimas.com.mx:8080/calculadora/api/envios/distancia/" + cpOrigen + "," + cpDestino;
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) return null;

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            StringBuilder sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) sb.append(output);
            conn.disconnect();

            Gson gson = new Gson();
            DistanciaRespuesta response = gson.fromJson(sb.toString(), DistanciaRespuesta.class);

            return (!response.isError()) ? response.getDistanciaKM() : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static Respuesta editar(Envio envio) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexionBD = MyBatisUtil.getSession();
        if (envio == null || envio.getId() == null) return new Respuesta(true, "Datos inválidos");
        if (conexionBD != null) {
            try {
                int filas = conexionBD.update("envio.editar", envio);
                conexionBD.commit();
                if (filas > 0) {
                    respuesta.setError(false);
                    respuesta.setMensaje("Envío actualizado.");
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
                int filas = conexionBD.delete("envio.eliminar", id);
                conexionBD.commit();
                if(filas>0) { respuesta.setError(false); respuesta.setMensaje("Eliminado"); }
                else { respuesta.setError(true); respuesta.setMensaje("No encontrado"); }
            } catch(Exception e){ e.printStackTrace(); } finally { conexionBD.close(); }
        }
        return respuesta;
    }
    public static Respuesta cambiarEstatus(int id, String estatus, Integer idColaborador) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexionBD = MyBatisUtil.getSession();
        if (id <= 0) return new Respuesta(true, "ID inválido");
        if (estatus == null) return new Respuesta(true, "Estatus inválido");
        if (conexionBD != null) {
            try {
                pojo.Envio e = new pojo.Envio();
                e.setId(id);
                e.setEstatus(estatus);
                e.setIdColaboradorActualizo(idColaborador);
                int filas = conexionBD.update("envio.cambiar-estatus", e);
                conexionBD.commit();
                if (filas > 0) { respuesta.setError(false); respuesta.setMensaje("Estatus actualizado"); }
                else { respuesta.setError(true); respuesta.setMensaje("No encontrado"); }
            } catch(Exception ex) { respuesta.setError(true); respuesta.setMensaje(ex.getMessage()); }
            finally { conexionBD.close(); }
        }
        return respuesta;
    }
}