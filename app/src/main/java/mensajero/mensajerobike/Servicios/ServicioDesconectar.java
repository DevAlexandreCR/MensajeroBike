package mensajero.mensajerobike.Servicios;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import mensajero.mensajerobike.Constantes.Constantes;
import mensajero.mensajerobike.MainActivity;
import mensajero.mensajerobike.R;


public class ServicioDesconectar extends Service {
    private NotificationManager notificationManager;

    public ServicioDesconectar() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mostratNotificacion();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    private void mostratNotificacion(){
        RemoteViews views = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notificacion_desconexion);
        views.setTextViewText(R.id.texto,"¿Permanecer Conectado?");
        views.setProgressBar(R.id.progressBarnotificacion,100,10,true);


        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Constantes.ACTION_CONECTAR_DESDE_NOTIFICACION);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        views.setOnClickPendingIntent(R.id.conectar, pendingIntent);

        Intent intent2 = new Intent(getApplicationContext(), MainActivity.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        intent2.setAction(Constantes.ACTION_DESCONECTAR);
        PendingIntent pendingIntent2 = PendingIntent.getActivity(getApplicationContext(), 2, intent2, PendingIntent.FLAG_ONE_SHOT);
        views.setOnClickPendingIntent(R.id.desconectar, pendingIntent2);

        Uri sonidoUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            Notification.Builder notificacion = new Notification.Builder(this,Constantes.ID_CANAL_DESCONECTAR);
            notificacion.setSmallIcon(R.mipmap.ic_launcher)
                    .setCustomContentView(views)
                    .setProgress(100,0,true)
                    .setAutoCancel(true);
            startForeground(Constantes.NOTIFICATION_ID.FOREGROUND_SERVICE_DESCEONECTAR,
                    notificacion.build());
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            NotificationCompat.Builder notificacion = new NotificationCompat.Builder(getApplicationContext());
            // versiones con android 6.0 o superior
            notificacion.setSmallIcon(R.mipmap.ic_launcher)
                    .setSound(sonidoUri)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setFullScreenIntent(pendingIntent,true)
                    .setCustomContentView(views)
                    .setAutoCancel(true);
            startForeground(Constantes.NOTIFICATION_ID.FOREGROUND_SERVICE_DESCEONECTAR,
                    notificacion.build());
        } else{
            // para versiones anteriores a android 6.0
            NotificationCompat.Builder notificacion = new NotificationCompat.Builder(this);
            notificacion.setSmallIcon(R.mipmap.ic_launcher)
                    .setSound(sonidoUri)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setFullScreenIntent(pendingIntent,true)
                    .setContentTitle("¿Desea seguir conectado?")
                    .addAction(R.mipmap.on,"Si",pendingIntent)
                    .addAction(R.mipmap.off,"No",pendingIntent2)
                    .setAutoCancel(true);
            startForeground(Constantes.NOTIFICATION_ID.FOREGROUND_SERVICE_DESCEONECTAR,
                    notificacion.build());
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
