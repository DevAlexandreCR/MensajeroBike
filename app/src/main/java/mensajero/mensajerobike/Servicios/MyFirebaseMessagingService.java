package mensajero.mensajerobike.Servicios;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import mensajero.mensajerobike.Constantes.Constantes;
import mensajero.mensajerobike.Constantes.Mensajeros;
import mensajero.mensajerobike.MainActivity;
import mensajero.mensajerobike.R;

/**
 * Created by equipo on 13/02/2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private final String RECIBIDO = "Solicitud recibida,en un momento se te asignará un mensajero";
    private final String TAG = "Servicio Mensaje";
    String id_mensajero;
    String codigo;
    String token;


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        token = s;

        FirebaseAuth mAuth= FirebaseAuth.getInstance();
        try {
            id_mensajero = mAuth.getCurrentUser().getUid();
            //aqui se registran los datos del ususario en la base de datos
            final DatabaseReference database = FirebaseDatabase.getInstance().getReference()
                    .child(Constantes.BD_GERENTE).child(Constantes.BD_ADMIN).child(Constantes.BD_MENSAJERO);

            Query query = database.orderByKey();

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot snapshot :
                            dataSnapshot.getChildren()) {
                        Mensajeros mensajero_prov = snapshot.getValue(Mensajeros.class);
                        String id = "nulo";

                        try {
                            id = mensajero_prov.getId_mensajero();
                            Log.i("id mensajero", id);
                        } catch (NullPointerException e) {

                            id = "nulo";
                            //Log.i("nulo",id);
                            // return;
                        }

                        if (id.equals(id_mensajero)) {
                            codigo = mensajero_prov.getCodigo();

                            DatabaseReference currentmensajero = database.child(codigo)
                                    .child(Constantes.BD_TOKEN);
                            currentmensajero.setValue(token);
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }catch (Exception e){
            Log.i("Error Auth.getid",e.toString());
        }
    }

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String from = remoteMessage.getFrom();
        Log.i("onMensajeReceiver"," mensaje recibido de "+ from);

        if(remoteMessage.getNotification()!=null){
            Log.d("notificacion",remoteMessage.getNotification().getBody());
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                mostratNotificacion(remoteMessage.getNotification().getTitle(),
                        remoteMessage.getNotification().getBody(), Constantes.ID_CANAL_DEFAULT);
            }else{
                mostratNotificacion(remoteMessage.getNotification().getTitle(),
                        remoteMessage.getNotification().getBody(),null);
            }

        }else if(remoteMessage.getData().size()>0){
            String datos = remoteMessage.getData().toString();
            Log.d("MENSAJE RECIBIDO",datos+"");
            if(datos.contains(Constantes.CONFIRMAR_VALOR_VIAJE)){
                Log.d("datosdel mensaje confir", "" + remoteMessage.getData());
                String valor_cobrar = remoteMessage.getData().get("valor_cobrar");
                String valor_total = remoteMessage.getData().get("valor_viaje");
                String descuentos_u = remoteMessage.getData().get("descuentos_usuario");
                String saldo_u = remoteMessage.getData().get("saldo_pendiente_usuario");
                Intent confirmar_llegada = new Intent();
                confirmar_llegada.setAction(Constantes.ACTION_CONFIRMAR_VALOR_VIAJE);
                confirmar_llegada.putExtra("valor_cobrar",valor_cobrar);
                confirmar_llegada.putExtra("valor_viaje",valor_total);
                confirmar_llegada.putExtra("descuentos_usuario",descuentos_u);
                confirmar_llegada.putExtra("saldo_pendiente_usuario",saldo_u);
                sendBroadcast(confirmar_llegada);
                Log.d("sendbroadc","MENSAJE ENVIADO AL RECEPTOR");
            }else if(datos.contains(Constantes.BD_ID_PEDIDO)) {
                Log.d("datosdel mensaje", "" + remoteMessage.getData());
                Log.i(TAG, remoteMessage.getData().get(Constantes.BD_ID_PEDIDO));
                String id_pedido = remoteMessage.getData().get(Constantes.BD_ID_PEDIDO);
                String posicion_en_la_lista = remoteMessage.getData().get(Constantes.BD_POSICION_EN_LA_LISTA);
                String id_lista = remoteMessage.getData().get(Constantes.ID_LISTA);
                Intent servicionuevo = new Intent(this, MainActivity.class);
                servicionuevo.setAction(Constantes.SERVICIO_NUEVO);
                servicionuevo.putExtra(Constantes.BD_ID_PEDIDO, id_pedido);
                servicionuevo.putExtra(Constantes.BD_POSICION_EN_LA_LISTA, posicion_en_la_lista);
                servicionuevo.putExtra(Constantes.ID_LISTA, id_lista);
                if (!isMyServiceRunning(SonidoServicioNuevo.class)) {
                    try {
                        servicionuevo.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(servicionuevo);
                    } catch (Exception e) {
                        e.printStackTrace();
                        servicionuevo.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(servicionuevo);
                    }
                }else{
                    Log.i(TAG,"ya hay un servicio entrante");
                   DatabaseReference database = FirebaseDatabase.getInstance().getReference()
                            .child(Constantes.BD_GERENTE).child(Constantes.BD_ADMIN).child(Constantes.BD_MENSAJEROS_CERCA_ENV_MENSAJE)
                            .child(id_lista);
                    database.child(posicion_en_la_lista).removeValue();
                }


            }else if(datos.contains(Constantes.MENSAJE_CHAT)){
                JSONObject DatosMensaje = null;

                try {
                    try {
                        datos = datos.replace(" ","_");
                        DatosMensaje = new JSONObject(datos);
                        Log.i("Datos Mensaje", DatosMensaje.toString());
                        String mensaje = DatosMensaje.get("_mensaje").toString();
                        mensaje = mensaje.replace("_"," ");
                        Log.i("datos mensaje",DatosMensaje.toString());

                        ActivityManager activityManager = (ActivityManager) getApplication().getSystemService( Context.ACTIVITY_SERVICE );
                        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
                        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                                Log.i("Foreground App", appProcess.processName);
                                if (appProcess.processName.equals(getApplication().getPackageName())) {
                                    Log.i(TAG, " APP EN PRIMER PLANO");
                                    Intent chat = new Intent();
                                    chat.putExtra(Constantes.MENSAJE_CHAT,mensaje);
                                    chat.setAction(Constantes.ACTION_MENSAJE_CHAT);
                                    sendBroadcast(chat);
                                    break;
                                }
                            } else {
                                Log.i(TAG, appProcess.processName);
                                Log.i(TAG, "APP EN SEGUNDO PLANO");
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    mostratNotificacion("Mensaje del usuario!", mensaje, Constantes.ID_CANAL_CHAT);
                                }else{
                                    mostratNotificacion("Mensaje del usuario!", mensaje, null);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

            }

        }
    }

    private void mostratNotificacion(String title, String body, @Nullable String canal){
        try {
        Intent intent = new Intent(this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Constantes.INICIAR_DE_NOTIFICACION);
        if (title.contains("Mensaje")) {
            intent.putExtra(Constantes.MENSAJE_CHAT,true);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0,intent,PendingIntent.FLAG_ONE_SHOT);

            Uri sonidoUri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.tin);

        if(canal!= null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Notification.Builder notificacion = new Notification.Builder(this, canal)
                        .setSmallIcon(R.drawable.ic_notificacion)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setSound(sonidoUri)
                        .setContentIntent(pendingIntent);
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0, notificacion.build());
            }
        }else{
            NotificationCompat.Builder notificacion = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_notificacion)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(sonidoUri)
                    .setContentIntent(pendingIntent);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notificacion.build());
            notificationManager.notify(0,notificacion.build());
        }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        try {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return false;
    }
}
