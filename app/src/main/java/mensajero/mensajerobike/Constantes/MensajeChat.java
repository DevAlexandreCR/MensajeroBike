package mensajero.mensajerobike.Constantes;

import java.util.Date;

public class MensajeChat {

    private String mensaje;
    private String mensajeSender;
    private String nombreSender;
    private long mensajeTiempo;

    public MensajeChat(String mensaje, String mensajeSender, String nombreSender) {
        this.mensaje = mensaje;
        this.mensajeSender = mensajeSender;
        this.nombreSender = nombreSender;

        mensajeTiempo = new Date().getTime();
    }

    public MensajeChat(){


    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensajeSender() {
        return mensajeSender;
    }

    public void setMensajeSender(String mensajeSender) {
        this.mensajeSender = mensajeSender;
    }

    public long getMensajeTiempo() {
        return mensajeTiempo;
    }

    public void setMensajeTiempo(long mensajeTiempo) {
        this.mensajeTiempo = mensajeTiempo;
    }

    public String getNombreSender() {
        return nombreSender;
    }

    public void setNombreSender(String nombreSender) {
        this.nombreSender = nombreSender;
    }
}
