package mensajero.mensajerobike.Constantes;

import android.support.annotation.Nullable;

/**
 * Created by equipo on 03/10/2017.
 */

public class Mensajeros {

    private String nombre;
    private float calificacion;
    private String placa;
    private String urlFoto;
    private String email;
    private String telefono;
    private String token;
    private String contraseña;
    private String id_mensajero;
    private String codigo;
    private String estado;
    private double lat_dir_ini;
    private double lgn_dir_ini;
    private double saldo;
    private double ingreso;
    private boolean ocupado;
    private String hora_conexion;

    public Mensajeros(){

    }

    public Mensajeros(String codigo, String id_mensajero, String nombre, String urlFoto, float calificacion,
                      String placa, String email, String telefono, String constraseña,
                      String token, String estado, @Nullable Double lat_dir_ini,@Nullable Double lgn_dir_ini){

        this.nombre = nombre;
        this.calificacion = calificacion;
        this.urlFoto = urlFoto;
        this.placa = placa;
        this.telefono = telefono;
        this.email = email;
        this.contraseña = constraseña;
        this.token = token;
        this.id_mensajero = id_mensajero;
        this.estado = estado;
        this.codigo = codigo;
        this.lat_dir_ini = lat_dir_ini;
        this.lgn_dir_ini = lgn_dir_ini;
        }

    public double getIngreso() {
        return ingreso;
    }

    public boolean isOcupado() {
        return ocupado;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public void setIngreso(double ingreso) {
        this.ingreso = ingreso;
    }

    public String getHora_conexion() {
        return hora_conexion;
    }

    public void setHora_conexion(String hora_conexion) {
        this.hora_conexion = hora_conexion;
    }

    public void setOcupado(boolean ocupado) {
        this.ocupado = ocupado;
    }

    public Double getLat_dir_ini() {
        return lat_dir_ini;
    }

    public void setLat_dir_ini(Double lat_dir_ini) {
        this.lat_dir_ini = lat_dir_ini;
    }

    public Double getLgn_dir_ini() {
        return lgn_dir_ini;
    }

    public void setLgn_dir_ini(Double lgn_dir_ini) {
        this.lgn_dir_ini = lgn_dir_ini;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    public String getId_mensajero() {
        return id_mensajero;
    }

    public void setId_mensajero(String id_mensajero) {
        this.id_mensajero = id_mensajero;
    }
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }


    public String getNombre() {
        return nombre;
    }


    public float getCalificacion() {
        return calificacion;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCalificacion(float calificacion) {
        this.calificacion = calificacion;
    }
    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }



    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }


    public double getSaldo() {
        return saldo;
    }
}
