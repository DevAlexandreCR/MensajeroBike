package mensajero.mensajerobike.Servicios;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import mensajero.mensajerobike.Constantes.Constantes;
import mensajero.mensajerobike.R;


public class SonidoServicioNuevo extends Service {

    private MediaPlayer sonido;

    public SonidoServicioNuevo() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("Sonido","servicio creado");

        //reprodicir sonido
        sonido = MediaPlayer.create(this, R.raw.servicio_nuevo);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificar();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String action;
        if(intent != null){
            action = intent.getAction();
        }else{
            action = null;
        }


        if (action == null) {
            try {
                sonido.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        sonido.start();
                        sonido.setLooping(true);
                        Log.i("Sonido","sonido iniciado");
                    }
                });
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }


        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(sonido.isPlaying()){
            sonido.stop();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void notificar() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Notification.Builder notificacion = new Notification.Builder(this, Constantes.ID_CANAL_DESCONECTAR)
                    .setContentText("Servicio entrante")
                    .setSmallIcon(R.mipmap.logo_moto)
                    .setAutoCancel(true);

            try {
                startForeground(Constantes.NOTIFICATION_ID.FOREGROUND_SERVICE, notificacion.build());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
