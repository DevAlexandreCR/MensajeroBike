package mensajero.mensajerobike.Servicios;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.List;

import mensajero.mensajerobike.Constantes.Constantes;
import mensajero.mensajerobike.MainActivity;
import mensajero.mensajerobike.R;


/**
 * Created by equipo on 11/02/2018.
 */

public class JobEstadoConectado extends Job {

    public static final String TAG ="JOB";
    private DatabaseReference database;
    private DatabaseReference currenUserBD;
    Query query;
    private int contador = 0;
    private CountDownTimer countDownTimer,countnitifitimer;
    public ChildEventListener childEventListener = new ChildEventListener(){

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            contador = contador+1;

           if(contador>1){
               // if(isApplicationBroughtToBackground()) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setAction(Constantes.SERVICIO_NUEVO);
                    Log.i("servicionueco",dataSnapshot.toString());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(intent);
               // }else{
                   /* Handler mHandler = new Handler(Looper.getMainLooper());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity mainActivity = new
                                    MainActivity();
                            mainActivity.ServicioRecibido();
                        }
                    });*/
              // }
            }else{
                Log.i("primerdisparo",contador+"pum");
            }



        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
    public MainActivity mainActivity;



    public JobEstadoConectado(MainActivity activity, DatabaseReference database1, DatabaseReference currenuserdb) {
        super(new Params(Constantes.PRIORIDAD_ALTA)
                .requireNetwork()
                .singleInstanceBy(TAG)
                .addTags(TAG)
        );

        database=database1;
        currenUserBD = currenuserdb;
        mainActivity = activity;
    }

    @Override
    public void onAdded() {

        Log.i(TAG,"trabajo añadido");
        query = database.child(Constantes.PEDIDO).orderByKey().limitToLast(1);
            }

    @Override
    public void onRun() {
        Log.i(TAG,"servicio run");
        query.addChildEventListener(childEventListener);
       final Handler mHandler = new Handler(Looper.getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {

                countDownTimer = new CountDownTimer(420000,1000){
                    @Override
                    public void onTick(long millisUntilFinished) {
                        long segundos = millisUntilFinished / 1000;
                        Log.i("Servicio ", " " + segundos);

                       }

                    @Override
                    public void onFinish() {

                        mostratNotificacion();


                    }
                };
                countDownTimer.start();
    }
});


    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
        query.removeEventListener(childEventListener);
        countDownTimer.cancel();
        Log.i("Caneclar servicio","cancelado");
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }

    private boolean isApplicationBroughtToBackground() {
        ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(getApplicationContext().getPackageName())) {

                return true;
            }
        }
        Log.i("appforeground","no esta en backg");
        return false;
    }


    private void mostratNotificacion(){
        RemoteViews views = new RemoteViews(getApplicationContext().getPackageName(),R.layout.notificacion_desconexion);
        views.setTextViewText(R.id.texto,"¿Permanecer Conectado?");
        views.setProgressBar(R.id.progressBarnotificacion,100,10,true);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setAction(Constantes.ACTION_CONECTAR);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, intent, PendingIntent.FLAG_ONE_SHOT);
        views.setOnClickPendingIntent(R.id.conectar, pendingIntent);

        Intent intent2 = new Intent(getApplicationContext(), MainActivity.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent2.setAction(Constantes.ACTION_DESCONECTAR);
        PendingIntent pendingIntent2 = PendingIntent.getActivity(getApplicationContext(), 2, intent2, PendingIntent.FLAG_ONE_SHOT);
        views.setOnClickPendingIntent(R.id.desconectar, pendingIntent2);

        Uri sonidoUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

       PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0,
               new Intent(getApplicationContext(), MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);

        final NotificationManager notificationManager = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        final NotificationCompat.Builder notificacion= new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true)
                    .setSound(sonidoUri)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setCustomContentView(views)
                    .setFullScreenIntent(contentIntent,true)
                .setOnlyAlertOnce(true);


        final Handler mHandler = new Handler(Looper.getMainLooper());
        mHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        notificationManager.notify(0, notificacion.build());
                        countnitifitimer = new CountDownTimer(15000,1000){
                            @Override
                            public void onTick(long millisUntilFinished) {
                                long segundos = millisUntilFinished / 1000;
                                Log.i("contador notificacion ", " " + segundos);

                                if(!isApplicationBroughtToBackground()){
                                   notificationManager.cancelAll();
                                   this.cancel();
                                }
                            }

                            @Override
                            public void onFinish() {

                                notificacion.setContentText("Desconectado")
                                        // Quitar la barra de progreso
                                        .setProgress(0, 0, false);
                                notificationManager.cancelAll();


                                    currenUserBD.removeValue();
                                    query.removeEventListener(childEventListener);
                                    mainActivity.sharedPref.edit().putBoolean(Constantes.BD_ESTADO_MENSAJERO,false).apply();




                            }
                        };

                        countnitifitimer.start();


                    /*    mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mainActivity.aSwitchEstado.setChecked(false);
                            }
                        });*/
                        //mainActivity.EstaConectado(false);

                       // currenUserBD.removeValue();
                    }
                });

    }


}
