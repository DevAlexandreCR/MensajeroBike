package mensajero.mensajerobike.Servicios;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Chronometer;
import android.widget.RemoteViews;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;

import mensajero.mensajerobike.Constantes.Constantes;
import mensajero.mensajerobike.MainActivity;
import mensajero.mensajerobike.R;


public class ServicioCarrera extends Service {

    LocationManager locationManager;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private LatLng pocisionInicial, pocisionFinal, pocisionActual;
    private static int PEDIR_PERMISO_LOCATION = 101;
    NotificationManager notificationManager;
    Double distancia = 0.0;
    int tiempo = 0;
    int precio = 0;
    RemoteViews views;
    private IBinder mBinder = new binderServicioCarrera();
    private Chronometer cronometro;
    private long tiempoTranscurrido;
    private boolean corriendo = false;
    Location locationInicial, locationRecogida;
    private ArrayList<LatLng> points;
    private static final String LOG_TAG = "ServicioCarrera";
    private static final String CHANNEL_ID = "canal_02";

    public ServicioCarrera() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        views = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notificacion_carrera);
        views.setChronometer(R.id.crononotificacion, SystemClock.elapsedRealtime(), "hh:mm:ss", true);
        views.setTextViewText(R.id.txt_tiempo, "Tiempo");
        points = new ArrayList<LatLng>();
        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        locationRequest = new LocationRequest()
                .setInterval(4000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setSmallestDisplacement(5)
                .setFastestInterval(5000);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());


        cronometro = new Chronometer(this);
        corriendo = true;
        miUbicacion();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String action = null;
        if (intent != null) {
            action = intent.getAction();
        }

        if (action != null) {
            switch (action) {
                case Constantes.ACTION_RECOGER:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Intent notificationIntent = new Intent(this, MainActivity.class);
                        notificationIntent.setAction(Constantes.ACTION_CONECTAR);
                        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        Notification.Builder notificacion = new Notification.Builder(this, Constantes.ID_CANAL_SERVICIO_CARRERA);
                        notificacion.setContentTitle("Recoger Cliente")
                                .setSmallIcon(R.mipmap.logo_moto)
                                .setContentText("Toque para regresar a la app")
                                .setContentIntent(pendingIntent)
                                .setUsesChronometer(true)
                        ;
                        try {
                            startForeground(Constantes.NOTIFICATION_ID.FOREGROUND_SERVICE, notificacion.build());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    miUbicacion();
                    try {
                        locationRecogida = new Location(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
                    } catch (Exception e) {
                        e.printStackTrace();
                        stopSelf();
                    }
                    try {
                        locationRecogida.setLatitude(intent.getDoubleExtra(Constantes.LAT_INI, 0));
                        locationRecogida.setLongitude(intent.getDoubleExtra(Constantes.LNG_INI, 0));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    locationCallback = new LocationCallback(){
                        @Override
                        public void onLocationResult(LocationResult locationResult) {

                            if (locationResult.getLastLocation()!=null) {
                                int distancia = (int) locationResult.getLastLocation().distanceTo(locationRecogida);
                                Log.i(Constantes.DISTANCIA, distancia + "");

                                if (distancia < 15) {
                                    fusedLocationProviderClient.removeLocationUpdates(this);
                                    Intent iniciarCarrera = new Intent(ServicioCarrera.this, MainActivity.class);

                                    iniciarCarrera.setAction(Constantes.ACTION_INICIAR_CARRERA);
                                    try {
                                        iniciarCarrera.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(iniciarCarrera);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        iniciarCarrera.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(iniciarCarrera);
                                    }

                                    stopSelf();
                                }
                            }
                        }
                    };

                    fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());



                    break;
                case Constantes.ACTION_INICIAR_CARRERA:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Intent notificationIntent = new Intent(this, MainActivity.class);
                        notificationIntent.setAction(Constantes.ACTION_CONECTAR);
                        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        Notification.Builder notificacion = new Notification.Builder(this, Constantes.ID_CANAL_SERVICIO_CARRERA)
                                .setContentIntent(pendingIntent)
                                .setSmallIcon(R.mipmap.logo_moto)
                                .setContentTitle("Servicio En Curso")
                                .setContentText("Toque para regresar a la app")
                                .setUsesChronometer(true)
                                .setOngoing(true);
                        startForeground(Constantes.NOTIFICATION_ID.FOREGROUND_SERVICE,
                                notificacion.build());
                    } else {

                        Intent notificationIntent = new Intent(this, MainActivity.class);
                        notificationIntent.setAction(Constantes.ACTION_CONECTAR);
                        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                                notificationIntent, 0);
                        NotificationCompat.Builder notificacion = new NotificationCompat.Builder(this)
                                .setContentIntent(pendingIntent)
                                .setSmallIcon(R.mipmap.logo_moto)
                                .setContentTitle("Servicio En Curso")
                                .setContentText("Toque para regresar a la app")
                                .setUsesChronometer(true)
                                .setOngoing(true);
                        startForeground(Constantes.NOTIFICATION_ID.FOREGROUND_SERVICE,
                                notificacion.build());
                    }
                    locationCallback = new LocationCallback(){
                        @Override
                        public void onLocationResult(LocationResult locationResult) {

                            if (locationResult.getLastLocation()!=null) {
                                pocisionActual = new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
                                points.add(pocisionActual);
                            }
                        }
                    };
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,Looper.myLooper());
                    if (intent.getExtras() != null) {
                        pocisionInicial = new LatLng(intent.getDoubleExtra("lat_ini", 0), intent.getDoubleExtra("log_ini", 0));
                    } else {
                        pocisionInicial = pocisionActual;
                    }
                    pocisionActual = pocisionInicial;
                    cronometro.setBase(SystemClock.elapsedRealtime());
                    cronometro.start();


                    break;
            }
        } else {
            stopSelf();
        }


        return START_STICKY;

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationCallback != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
            locationCallback = null;
        }
        cronometro.stop();
        corriendo = false;
        Log.i(LOG_TAG, "servicio terminado");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.i(LOG_TAG, "servicio ontastremoved");
    }

    @Override
    protected void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        super.dump(fd, writer, args);
        Log.i(LOG_TAG, "servicio dump");
    }

    private void miUbicacion() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            locationInicial = location;


    }
    @Override
    public void onRebind(Intent intent) {
        Log.v(LOG_TAG, "in onRebind");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(LOG_TAG, "in onUnbind");
        return false;
    }

    public class binderServicioCarrera extends Binder {
       public ServicioCarrera getService() {
            return ServicioCarrera.this;
        }

    }

    public long getTiempoTranscurrido() {
        long elapsedMillis = SystemClock.elapsedRealtime()
                - cronometro.getBase();
        tiempoTranscurrido =  elapsedMillis/ 60000;

        return tiempoTranscurrido ;
    }

    public long getBaseCronometro(){
        return cronometro.getBase();
    }

    public boolean isActivo(){
        return corriendo;
    }

    public Double getDistancia() {
        distancia = 0.0;
        if (points!=null) {
            for(int i=0;i<points.size();i++){
                if (i<(points.size()-1)) {
                    distancia = distancia +Distancia(points.get(i),points.get(i+1));
                }
            }
        }
        return distancia;
    }

    public static double Distancia(LatLng StartP, LatLng EndP) {
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return 6366000 * c;
    }

    public ArrayList<LatLng> getPoints() {
        return points;
    }
}
