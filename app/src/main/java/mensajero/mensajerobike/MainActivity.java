package mensajero.mensajerobike;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;
import com.birbit.android.jobqueue.log.CustomLogger;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import mensajero.mensajerobike.Constantes.AdapterServiciosPendientes;
import mensajero.mensajerobike.Constantes.Constantes;
import mensajero.mensajerobike.Constantes.Mensajeros;
import mensajero.mensajerobike.Constantes.Pedidos;
import mensajero.mensajerobike.Fragmentos.EnviarDocumentosFragm;
import mensajero.mensajerobike.Fragmentos.FragmentBloqueado;
import mensajero.mensajerobike.Fragmentos.FragmentChat;
import mensajero.mensajerobike.Fragmentos.Fragment_balance;
import mensajero.mensajerobike.Fragmentos.Fragment_perfil;
import mensajero.mensajerobike.Fragmentos.Historial;
import mensajero.mensajerobike.Servicios.ServicioCarrera;
import mensajero.mensajerobike.Servicios.ServicioCarrera.binderServicioCarrera;
import mensajero.mensajerobike.Servicios.ServicioDesconectar;
import mensajero.mensajerobike.Servicios.ServicioEstadoConectado;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
         View.OnClickListener, View.OnTouchListener {

    private static final int PERMISO_OVERLAYS = 156;
    private GoogleMap map;
    private boolean mapa_movido_po_el_usuario = false;
    private Marker marcador;
    private MapFragment mapFragment;
    private String token;
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    //este intent es para cada servicio nuevo que se recibe
    private Intent ServicioNuevo;
    private Boolean ActionServicioNuevo = false,
            ActionCalcularViaje = false;
    private String posicion_en_la_lista;
    private String id_lista;
    double valor = 0;
    int Distancia = 0;
    int Tiempo = 0;
    //variable para que el listener no se dispare en el oncreate()
    public int contador = 0;
    double lat = 0.0;
    double lng = 0.0;
    double lat_actualzada = 0.0;
    double lng_actualizada = 0.0;
    double lat_final = 0.0;
    double lng_final = 0.0;
    String Mensaje = "";
    String Direccion = "";
    public Switch aSwitchEstado;
    public String keyconectado = "";
    private boolean Conectado = false;
    public DatabaseReference database = FirebaseDatabase.getInstance().getReference()
            .child(Constantes.BD_GERENTE).child(Constantes.BD_ADMIN);
    public DatabaseReference currentUserDB;
    public String codigo, placa, estado_mensajero;
    public String id_mensajero, sessionId;
    public Mensajeros mensajero;
    public JobManager jobManager;
    private FirebaseAuth mAuth;
    public SharedPreferences sharedPref;
    public FragmentManager fragmentManager;
    public FragmentTransaction transaction;
    public FragmentBloqueado fragmentBloqueado;
    public EnviarDocumentosFragm documentosFragm;
    public FragmentChat fragmentChat;
    public Historial historial;
    public Button BotonIniciarCarrera;
    public EditText ETAgregarDestino;
    private Boolean SERVICIO_ACTIVO = false;
    //declaramos el frame para la carrera activa y sus componentes
    public FrameLayout frame_carrera_activa;
    public TextView textoDistancia, textoValor, textoVersion,texto_chat;
    public PolylineOptions lineOptions;
    public ArrayList<LatLng> points;
    public LatLng pocisionInicial, pocisionFinal;
    public LatLngBounds Popayan;
    LocationManager locationManager;
    public ServicioCarrera servicioCarrera;
    public Boolean servicioEnlazado = false;
    public Chronometer cronometro;
    //dialog para mostrar carrera terminada
    private Dialog dialog_terminar_viaje,dialog_Actualizar;
    private ReceptorMensajesServidor receptorMensajesServidor;
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            servicioEnlazado = false;
            cronometro.stop();
            cronometro.setBase(SystemClock.elapsedRealtime());
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binderServicioCarrera myBinder = (binderServicioCarrera) service;
            servicioCarrera = myBinder.getService();
            servicioEnlazado = true;
            cronometro.setBase(servicioCarrera.getBaseCronometro());
            cronometro.setOnChronometerTickListener(new OnChronometerTickListener() {
                @Override
                public void onChronometerTick(Chronometer chronometer) {
                    double tarifaminima = 2000;
                    if (pedido_aceptado != null) {
                        tarifaminima = modificadorDeTarifaminima(pedido_aceptado.getTipo_pedido());
                    }
                    double dist = servicioCarrera.getDistancia();
                    NumberFormat formatter = new DecimalFormat("#0.00");
                    long tiempo = servicioCarrera.getTiempoTranscurrido();
                    textoDistancia.setText(formatter.format(dist / 1000) + " km");
                    Log.i("sericeconected", "" + dist);
                    valor = (((dist/1000)* (350)) + (tiempo * (90))+ 1500);
                    if (valor <= tarifaminima) valor = tarifaminima;
                    textoValor.setText("$ " + valor);
                    points = servicioCarrera.getPoints();

                    if (points != null) {
                        PolylineOptions options = new PolylineOptions().width(17).color(getResources().getColor(R.color.colorPrimary)).geodesic(true);
                        for (int i = 0; i < points.size(); i++) {
                            LatLng point = points.get(i);
                            options.add(point);
                        }
                        if (map != null) {
                            map.addPolyline(options);
                            if (!mapa_movido_po_el_usuario) {
                                miUbicacion();
                            }
                        }

                    }
                }
            });

            cronometro.start();
        }
    };
    //las variables para el servicio que se acepta
    private String id_pedido_aceptado;
    private Pedidos pedido_aceptado;
    private Boolean dir_viaje = false;
    private Boolean dir_recoger = false;

    //estas son las variables del frame para recoger al cliente
    private FrameLayout frame_recoger_pasajero;
    private TextView recoger_nombre, recoger_numero, recoger_direccion, texto_nom_cliente, texto_num_cliente,
            texto_dir_cliente, text_cancelar, text_avisar,
            texto_tipo_servicio,texto_comentario_recoger, texto_comentario_iniciar;
    private ImageButton wase_boton, maps_boton, boton_info;
    private FloatingActionButton FAB_maps, FAB_wase, FAB_cancelar_servicio, FAB_avisar_llegada,FAB_Chat;

    //animaciones
    private Animation hacia_arriba, hacia_abajo;
    private LinearLayout layout_info;

    //Firebase Remote Config
    private FirebaseRemoteConfig configuracionRemotaFirebase;
    private FirebaseRemoteConfigSettings settingsConfRemote;

    //para activar la funcion de chat
    private FirebaseFunctions funtions;

    //para crear los canales de notificaciones
    private NotificationManager notificationManager;

    Dialog dialog_calculando;

    //listener para el pedido aceptado
    Query pedidoQuery;

    //Para mostrar servicios pendientes
    public RecyclerView reciclerPedidospendientes;
    public AdapterServiciosPendientes adapterServiciosPendientes;
    public List<Pedidos> pedidos_pendientes;
    public ConstraintLayout layout_pendientes;

    ValueEventListener listener_pedido_aceptado = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            pedido_aceptado = dataSnapshot.getValue(Pedidos.class);
            //guardamos el pedido en las preferencias
            if (pedido_aceptado != null) {
                layout_pendientes.setVisibility(View.INVISIBLE);
                Log.i("PEDIDO ACEPTADO", pedido_aceptado.toString());
                Gson gson = new Gson();
                String json = gson.toJson(pedido_aceptado);//convertimos el objeto a json
                final SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(Constantes.ACTION_RECOGER, dir_recoger);
                editor.putString(Constantes.PEDIDO, json);
                editor.apply();

                //Aquí vamos a ver que servicio solicitaron para saber qué hacer el la vista
                String tipo = pedido_aceptado.getTipo_pedido();
                String estado = pedido_aceptado.getEstado_pedido();

                switch (tipo){

                    case Constantes.COMPRAS_PEDIR_DOMICILIOS:
                        switch (estado){
                            case Constantes.ESTADO_EN_CURSO:
                                Log.i(Constantes.BD_ESTADO_PEDIDO,Constantes.ESTADO_EN_CURSO);
                                //mensajero toma el servicio y se dirige al lugar de la compra
                                //aparecemos el frame y le agregamos sus respectivos valores
                                FAB_Chat.show();
                                FAB_avisar_llegada.show();
                                texto_chat.setVisibility(View.VISIBLE);
                                text_avisar.setVisibility(View.VISIBLE);
                                    text_avisar.setText(Constantes.MENSAJERO_EN_TIENDA);
                                if (pedido_aceptado.getDir_inicial().isEmpty()) {
                                    recoger_direccion.setText("Dirección de Compra: Preguntar al usuario");
                                } else {
                                    recoger_direccion.setText("Comprar en: "+pedido_aceptado.getDir_inicial());
                                }
                                frame_carrera_activa.setVisibility(View.INVISIBLE);
                                    frame_recoger_pasajero.setVisibility(View.VISIBLE);
                                    recoger_nombre.setText(pedido_aceptado.getNombre());
                                    recoger_numero.setText(pedido_aceptado.getTelefono());
                                    recoger_numero.setLinksClickable(true);
                                    texto_tipo_servicio.setText(Constantes.TIPO_SERVICIO_COMPRAS_DOMICILIOS);
                                    texto_comentario_recoger.setText(pedido_aceptado.getComentario());
                                    //aqui vamos a agregar el punto al mapa para que detecte cuando esté cerca y vuelva a
                                    // Esta pantalla y continúe con el servicio

                                if (pedido_aceptado.getLat_dir_inicial() != 0) {
                                    if (map != null) {
                                        agregarMarcador(pedido_aceptado.getLat_dir_inicial(),
                                                pedido_aceptado.getLong_dir_inicial());
                                        pocisionFinal = new LatLng(pedido_aceptado.getLat_dir_inicial(),
                                                pedido_aceptado.getLong_dir_inicial());
                                        LatLngBounds bounds_ruta = LatLngBounds.builder()
                                                .include(pocisionInicial).include(pocisionFinal).build();
                                        //map.setPadding(0, 0, frame_recoger_pasajero.getHeight(), 0);
                                        try {
                                            map.animateCamera(CameraUpdateFactory
                                                    .newLatLngBounds(bounds_ruta, 100));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        Ruta(pocisionInicial, pocisionFinal);

                                    }
                                    View.OnClickListener clickListener = new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            switch (view.getId()) {
                                                case R.id.boton_waze:
                                                    Uri waseuri = Uri.parse("waze://?ll=" +
                                                            pedido_aceptado.getLat_dir_inicial() + "," +
                                                            pedido_aceptado.getLong_dir_inicial() + "&navigate=yes");
                                                    Intent waseintent = new Intent(Intent.ACTION_VIEW, waseuri);
                                                    if (waseintent.resolveActivity(getPackageManager()) != null) {
                                                        startActivity(waseintent);
                                                    } else {
                                                        toast("No tienes instalada la app");
                                                    }
                                                    break;
                                                case R.id.boton_maps:
                                                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" +
                                                            pedido_aceptado.getLat_dir_inicial() + "," +
                                                            pedido_aceptado.getLong_dir_inicial());
                                                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                                    mapIntent.setPackage("com.google.android.apps.maps");
                                                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                                                        startActivity(mapIntent);
                                                    } else {
                                                        toast("No tienes instalada la app");
                                                    }
                                                    break;
                                            }
                                        }
                                    };

                                    //le cargamos el metodo onclick
                                    wase_boton.setOnClickListener(clickListener);
                                    maps_boton.setOnClickListener(clickListener);
                                } else {
                                    wase_boton.setVisibility(View.INVISIBLE);
                                    maps_boton.setVisibility(View.INVISIBLE);
                                }

                                locationManager.removeUpdates(locationListener);

                                FAB_cancelar_servicio.show();
                                text_cancelar.setVisibility(View.VISIBLE);

                                id_pedido_aceptado = pedido_aceptado.getId_pedido();
                                Intent detenerDesconectar = new Intent();
                                detenerDesconectar.setAction(Constantes.ACTION_PEDIDO_ACEPTADO);
                                sendBroadcast(detenerDesconectar);

                                //**************************************************************************//


                                // Aqui iniciamos el servicio para que detecte cuando esté cerca del pasajero y
                                // aparezca la pantalla para iniciar la carrera con el pasajero
                                if (!dir_viaje && !isMyServiceRunning(ServicioCarrera.class)) {
                                    Intent recogerPasajero = new Intent(MainActivity.this,
                                            ServicioCarrera.class);
                                    recogerPasajero.setAction(Constantes.ACTION_RECOGER);
                                    if (pedido_aceptado.getLat_dir_inicial() != 0) {
                                        recogerPasajero.putExtra(Constantes.LAT_INI,
                                                pedido_aceptado.getLat_dir_inicial());
                                        recogerPasajero.putExtra(Constantes.LNG_INI,
                                                pedido_aceptado.getLong_dir_inicial());
                                    }else{
                                        recogerPasajero.putExtra("lat_ini", lat);
                                        recogerPasajero.putExtra("log_ini", lng);
                                    }

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        startForegroundService(recogerPasajero);
                                    } else {
                                        startService(recogerPasajero);
                                    }

                                }

                                break;
                            case Constantes.ESTADO_MENSAJERO_EN_TIENDA:
                                Log.i(Constantes.BD_ESTADO_PEDIDO,Constantes.ESTADO_MENSAJERO_EN_TIENDA);
                                //mensajero inicia a facturar en el momento en el que llega al lugar de la compra
                                //aparecemos el frame y le agregamos sus respectivos valores
                                frame_carrera_activa.setVisibility(View.VISIBLE);
                                frame_recoger_pasajero.setVisibility(View.INVISIBLE);
                                try {
                                    frame_carrera_activa.startAnimation(hacia_arriba);
                                    layout_info.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        boton_info.setBackground(getDrawable(android.R.drawable.arrow_down_float));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (map != null) {
                                    if (pedido_aceptado.getLat_dir_final() != 0) {
                                        agregarMarcador(pedido_aceptado.getLat_dir_final(), pedido_aceptado.getLong_dir_final());
                                        pocisionFinal = new LatLng(pedido_aceptado.getLat_dir_final(), pedido_aceptado.getLong_dir_final());
                                        LatLngBounds bounds_ruta = LatLngBounds.builder().include(pocisionInicial).include(pocisionFinal).build();
                                        //map.setPadding(0, 0, frame_recoger_pasajero.getHeight(), 0);
                                        try {
                                            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds_ruta, 100));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        Ruta(pocisionInicial, pocisionFinal);
                                    }
                                }
                                if (pedido_aceptado.getLat_dir_final() != 0) {
                                    View.OnClickListener clickListener = new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            switch (view.getId()) {
                                                case R.id.boton_waze:
                                                    Uri waseuri = Uri.parse("waze://?ll=" +
                                                            pedido_aceptado.getLat_dir_final() + "," +
                                                            pedido_aceptado.getLong_dir_final() + "&navigate=yes");
                                                    Intent waseintent = new Intent(Intent.ACTION_VIEW, waseuri);
                                                    if (waseintent.resolveActivity(getPackageManager()) != null) {
                                                        startActivity(waseintent);
                                                    } else {
                                                        toast("No tienes instalada la app");
                                                    }
                                                    break;
                                                case R.id.boton_maps:
                                                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" +
                                                            pedido_aceptado.getLat_dir_final() + "," +
                                                            pedido_aceptado.getLong_dir_final());
                                                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                                    mapIntent.setPackage("com.google.android.apps.maps");
                                                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                                                        startActivity(mapIntent);
                                                    } else {
                                                        toast("No tienes instalada la app");
                                                    }
                                                    break;
                                            }
                                        }
                                    };

                                    //le cargamos el metodo onclick
                                    wase_boton.setOnClickListener(clickListener);
                                    maps_boton.setOnClickListener(clickListener);
                                } else {
                                    wase_boton.setVisibility(View.INVISIBLE);
                                    maps_boton.setVisibility(View.INVISIBLE);
                                }
                                FAB_cancelar_servicio.show();
                                text_cancelar.setVisibility(View.VISIBLE);
                                frame_carrera_activa.setVisibility(View.VISIBLE);
                                FAB_avisar_llegada.show();
                                text_avisar.setVisibility(View.VISIBLE);
                                text_avisar.setText(Constantes.COMPRA_REALIZADA);
                                texto_chat.setVisibility(View.VISIBLE);
                                FAB_Chat.show();
                                FAB_wase.show();
                                FAB_maps.show();
                                texto_num_cliente.setText("Teléfono: " + pedido_aceptado.getTelefono());
                                texto_dir_cliente.setText("Dirección de Entrega: " + pedido_aceptado.getDir_final());
                                texto_nom_cliente.setText("Nombre: " + pedido_aceptado.getNombre());
                                texto_comentario_iniciar.setText(pedido_aceptado.getComentario());
                                Intent detenerDesconectar1 = new Intent();
                                detenerDesconectar1.setAction(Constantes.ACTION_PEDIDO_ACEPTADO);
                                sendBroadcast(detenerDesconectar1);
                                texto_comentario_recoger.setText(pedido_aceptado.getComentario());
                                //aqui vamos a agregar el punto al mapa para que detecte cuando esté cerca y vuelva a
                                // Esta pantalla y continúe con el servicio
                                if (!isMyServiceRunning(ServicioCarrera.class)) {
                                    Intent iniciarCarrera = new Intent(MainActivity.this,
                                            ServicioCarrera.class);
                                    iniciarCarrera.setAction(Constantes.ACTION_INICIAR_CARRERA);
                                    if (pedido_aceptado.getLat_dir_inicial() != 0) {
                                        iniciarCarrera.putExtra(Constantes.LAT_INI,
                                                pedido_aceptado.getLat_dir_inicial());
                                        iniciarCarrera.putExtra(Constantes.LNG_INI,
                                                pedido_aceptado.getLong_dir_inicial());
                                    }else{
                                        iniciarCarrera.putExtra("lat_ini", lat);
                                        iniciarCarrera.putExtra("log_ini", lng);
                                    }

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        startForegroundService(iniciarCarrera);
                                    } else {
                                        startService(iniciarCarrera);
                                    }
                                    SERVICIO_ACTIVO = true;
                                }
                                bindService(new Intent(MainActivity.this, ServicioCarrera.class),
                                        mServiceConnection, Context.BIND_AUTO_CREATE);
                                BotonIniciarCarrera.setText(Constantes.TERMINAR_SERVICIO);
                                break;
                            case Constantes.ESTADO_COMPRA_REALIZADA:
                                Log.i(Constantes.BD_ESTADO_PEDIDO,Constantes.ESTADO_COMPRA_REALIZADA);
                                //compra realizada, se notifica al usuario
                                //mensajero inicia a facturar en el momento en el que llega al lugar de la compra
                                //aparecemos el frame y le agregamos sus respectivos valores
                                frame_carrera_activa.setVisibility(View.VISIBLE);
                                frame_recoger_pasajero.setVisibility(View.INVISIBLE);
                                recoger_nombre.setText(pedido_aceptado.getNombre());
                                recoger_numero.setText(pedido_aceptado.getTelefono());
                                recoger_numero.setLinksClickable(true);
                                recoger_direccion.setText(pedido_aceptado.getDir_final());
                                FAB_cancelar_servicio.hide();
                                text_cancelar.setVisibility(View.INVISIBLE);
                                FAB_avisar_llegada.hide();
                                text_avisar.setVisibility(View.INVISIBLE);
                                texto_chat.setVisibility(View.INVISIBLE);
                                FAB_Chat.show();
                                FAB_wase.show();
                                FAB_maps.show();
                                texto_num_cliente.setText("Teléfono: " + pedido_aceptado.getTelefono());
                                texto_dir_cliente.setText("Dirección de Entrega: " + pedido_aceptado.getDir_final());
                                texto_nom_cliente.setText("Nombre: " + pedido_aceptado.getNombre());
                                Intent inteDEsc = new Intent();
                                inteDEsc.setAction(Constantes.ACTION_PEDIDO_ACEPTADO);
                                sendBroadcast(inteDEsc);
                                texto_comentario_iniciar.setText(pedido_aceptado.getComentario());
                                //aqui vamos a agregar el punto al mapa para que detecte cuando esté cerca y vuelva a
                                // Esta pantalla y continúe con el servicio
                                if (map != null) {
                                    agregarMarcador(pedido_aceptado.getLat_dir_inicial(),
                                            pedido_aceptado.getLong_dir_inicial());
                                    pocisionFinal = new LatLng(pedido_aceptado.getLat_dir_inicial(),
                                            pedido_aceptado.getLong_dir_inicial());
                                    LatLngBounds bounds_ruta = LatLngBounds.builder()
                                            .include(pocisionInicial).include(pocisionFinal).build();
                                    //map.setPadding(0, 0, frame_recoger_pasajero.getHeight(), 0);
                                    try {
                                        map.animateCamera(CameraUpdateFactory
                                                .newLatLngBounds(bounds_ruta, 100));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    Ruta(pocisionInicial, pocisionFinal);

                                }
                                if (pedido_aceptado.getLat_dir_final() != 0) {
                                    View.OnClickListener clickListener = new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            switch (view.getId()) {
                                                case R.id.boton_waze:
                                                    Uri waseuri = Uri.parse("waze://?ll=" +
                                                            pedido_aceptado.getLat_dir_final() + "," +
                                                            pedido_aceptado.getLong_dir_final() + "&navigate=yes");
                                                    Intent waseintent = new Intent(Intent.ACTION_VIEW, waseuri);
                                                    if (waseintent.resolveActivity(getPackageManager()) != null) {
                                                        startActivity(waseintent);
                                                    } else {
                                                        toast("No tienes instalada la app");
                                                    }
                                                    break;
                                                case R.id.boton_maps:
                                                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" +
                                                            pedido_aceptado.getLat_dir_final() + "," +
                                                            pedido_aceptado.getLong_dir_final());
                                                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                                    mapIntent.setPackage("com.google.android.apps.maps");
                                                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                                                        startActivity(mapIntent);
                                                    } else {
                                                        toast("No tienes instalada la app");
                                                    }
                                                    break;
                                            }
                                        }
                                    };

                                    //le cargamos el metodo onclick
                                    wase_boton.setOnClickListener(clickListener);
                                    maps_boton.setOnClickListener(clickListener);
                                } else {
                                    wase_boton.setVisibility(View.INVISIBLE);
                                    maps_boton.setVisibility(View.INVISIBLE);
                                }
                                try {
                                    frame_carrera_activa.startAnimation(hacia_arriba);
                                    layout_info.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        boton_info.setBackground(getDrawable(android.R.drawable.arrow_down_float));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                id_pedido_aceptado = pedido_aceptado.getId_pedido();
                                Intent intnetdes = new Intent();
                                intnetdes.setAction(Constantes.ACTION_PEDIDO_ACEPTADO);
                                sendBroadcast(intnetdes);

                                //**************************************************************************//


                                // Aqui iniciamos el servicio para que detecte cuando esté cerca del pasajero y
                                // aparezca la pantalla para iniciar la carrera con el pasajero

                                if (!isMyServiceRunning(ServicioCarrera.class)) {
                                    Intent iniciarCarrera = new Intent(MainActivity.this,
                                            ServicioCarrera.class);
                                    iniciarCarrera.setAction(Constantes.ACTION_INICIAR_CARRERA);
                                    if (pedido_aceptado.getLat_dir_final() != 0) {
                                        iniciarCarrera.putExtra(Constantes.LAT_INI,
                                                pedido_aceptado.getLat_dir_final());
                                        iniciarCarrera.putExtra(Constantes.LNG_INI,
                                                pedido_aceptado.getLong_dir_final());
                                    }else{
                                        iniciarCarrera.putExtra("lat_ini", lat);
                                        iniciarCarrera.putExtra("log_ini", lng);
                                    }

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        startForegroundService(iniciarCarrera);
                                    } else {
                                        startService(iniciarCarrera);
                                    }
                                    SERVICIO_ACTIVO = true;
                                }
                                    bindService(new Intent(MainActivity.this, ServicioCarrera.class)
                                            , mServiceConnection, Context.BIND_AUTO_CREATE);

                                BotonIniciarCarrera.setText(Constantes.TERMINAR_SERVICIO);
                                break;
                            case Constantes.ESTADO_TERMINADO:
                                Log.i(Constantes.BD_ESTADO_PEDIDO,Constantes.ESTADO_TERMINADO);
                                //servicio terminado
                                layout_pendientes.setVisibility(View.VISIBLE);
                                servicioEnlazado = false;
                                SERVICIO_ACTIVO = false;
                                cronometro.stop();
                                cronometro.setBase(SystemClock.elapsedRealtime());
                                textoDistancia.setText("");
                                BotonIniciarCarrera.setText(Constantes.INICIAR_SERVICIO);
                                texto_num_cliente.setText("Teléfono: ");
                                texto_dir_cliente.setText("Dirección: ");
                                texto_nom_cliente.setText("Nombre: ");
                                textoDistancia.setText("Distancia Recorrida: ");
                                textoValor.setText("Valor: ");
                                texto_comentario_iniciar.setText("");
                                frame_carrera_activa.setVisibility(View.INVISIBLE);
                                map.clear();
                                points = null;
                                FAB_maps.hide();
                                FAB_wase.hide();
                                FAB_avisar_llegada.hide();
                                text_avisar.setVisibility(View.INVISIBLE);
                                FAB_Chat.hide();
                                texto_chat.setVisibility(View.INVISIBLE);
                                FAB_cancelar_servicio.hide();
                                text_cancelar.setVisibility(View.INVISIBLE);
                                dir_viaje = false;
                                dir_recoger = false;
                                pedido_aceptado = null;
                                id_pedido_aceptado = null;
                                editor.putBoolean(Constantes.ACTION_INICIAR_CARRERA, dir_viaje);
                                editor.putBoolean(Constantes.ACTION_RECOGER, dir_recoger);
                                editor.putString(Constantes.BD_ID_PEDIDO_ACEPTADO, id_pedido_aceptado);
                                editor.apply();
                                Intent iniciarDesconectar = new Intent();
                                iniciarDesconectar.setAction(Constantes.ACTION_TERMINAR_CARRERA);
                                sendBroadcast(iniciarDesconectar);
                                stopService(new Intent(MainActivity.this, ServicioCarrera.class));
                                pedidoQuery.removeEventListener(this);
                                pedido_aceptado = null;

                                break;
                            case Constantes.ESTADO_CANCELADO:
                                Log.i(Constantes.BD_ESTADO_PEDIDO,Constantes.ESTADO_CANCELADO);
                                //servicio cancelado
                                Intent iniciarDesconectar2 = new Intent();
                                layout_pendientes.setVisibility(View.VISIBLE);
                                iniciarDesconectar2.setAction(Constantes.ACTION_TERMINAR_CARRERA);
                                sendBroadcast(iniciarDesconectar2);
                                stopService(new Intent(MainActivity.this, ServicioCarrera.class));
                                pedido_aceptado = null;
                                frame_carrera_activa.setVisibility(View.INVISIBLE);
                                frame_recoger_pasajero.setVisibility(View.INVISIBLE);
                                FAB_Chat.hide();
                                FAB_maps.hide();
                                FAB_wase.hide();
                                texto_chat.setVisibility(View.INVISIBLE);
                                dir_viaje = false;
                                dir_recoger = false;
                                id_pedido_aceptado = null;
                                if(fragmentChat.isAdded()){
                                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                                    ft.remove(fragmentChat);
                                    ft.commit();
                                }
                                FAB_avisar_llegada.hide();
                                text_avisar.setVisibility(View.INVISIBLE);
                                Log.i("AcTION PEDACEPT value", "dir_recoger= " + dir_recoger);
                                ActivityManager activityManager = (ActivityManager) getApplication().getSystemService(Context.ACTIVITY_SERVICE);
                                List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
                                for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                                    if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                                        Log.i("Foreground App", appProcess.processName);
                                        if (appProcess.processName.equals(getApplication().getPackageName())) {
                                            Log.i("CANCELADO", "APP EN PRIMER PLANO");
                                            try {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                                builder.setMessage("El servicio ha sido cancelado")
                                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                Log.i("dialog click", "int " + i);
                                                                pedido_aceptado = null;
                                                                dir_viaje = false;
                                                                dir_recoger = false;
                                                                editor.putBoolean(Constantes.ACTION_RECOGER, dir_recoger);
                                                                editor.putBoolean(Constantes.ACTION_INICIAR_CARRERA, dir_viaje);
                                                                editor.putString(Constantes.BD_ID_PEDIDO_ACEPTADO, id_pedido_aceptado);
                                                                editor.apply();
                                                                Log.i("AcTION PEDACEPT value", "dir_recoger= " + dir_recoger);

                                                                reiniciarApp(MainActivity.this, null);
                                                            }
                                                        })
                                                        .setCancelable(false).show();


                                            } catch (Exception e) {

                                                e.printStackTrace();
                                                editor.putBoolean(Constantes.ACTION_INICIAR_CARRERA, dir_viaje);
                                                editor.putBoolean(Constantes.ACTION_RECOGER, dir_recoger);
                                                editor.putString(Constantes.BD_ID_PEDIDO_ACEPTADO, id_pedido_aceptado);
                                                editor.apply();
                                                reiniciarApp(MainActivity.this, true);
                                            }
                                            break;
                                        }
                                    } else {
                                        Log.i("CANCELADO", " APP EN SEGUNDO PLANO");
                                        try {
                                            reiniciarApp(MainActivity.this, true);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                pedidoQuery.removeEventListener(this);

                                break;


                            default:

                                break;
                        }
                        break;

                    case Constantes.ENCOMIENDAS:
                        switch (estado){
                            case Constantes.ESTADO_EN_CURSO:
                                Log.i(Constantes.BD_ESTADO_PEDIDO,Constantes.ESTADO_EN_CURSO);
                                //mensajero toma el servicio y se dirige al lugar de recoger la encomiienda
                                //aparecemos el frame y le agregamos sus respectivos valores
                                FAB_Chat.show();
                                if (dir_viaje) {
                                    frame_carrera_activa.setVisibility(View.VISIBLE);
                                    frame_recoger_pasajero.setVisibility(View.INVISIBLE);
                                    try {
                                        frame_carrera_activa.startAnimation(hacia_arriba);
                                        layout_info.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            boton_info.setBackground(getDrawable(android.R.drawable.arrow_down_float));
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    texto_num_cliente.setText("Teléfono: " + pedido_aceptado.getTelefono());
                                    if (pedido_aceptado.getDir_final() != null) {
                                        texto_dir_cliente.setText("Dirección: " + pedido_aceptado.getDir_final());
                                    } else {
                                        texto_dir_cliente.setText("Dirección: " + "Preguntar al usuario");
                                    }
                                    texto_nom_cliente.setText("Nombre: " + pedido_aceptado.getNombre());
                                    texto_comentario_iniciar.setText(pedido_aceptado.getComentario());
                                    if (map != null) {
                                        if (pedido_aceptado.getLat_dir_final() != 0) {
                                            agregarMarcador(pedido_aceptado.getLat_dir_final(), pedido_aceptado.getLong_dir_final());
                                            pocisionFinal = new LatLng(pedido_aceptado.getLat_dir_final(), pedido_aceptado.getLong_dir_final());
                                            LatLngBounds bounds_ruta = LatLngBounds.builder().include(pocisionInicial).include(pocisionFinal).build();
                                            //map.setPadding(0, 0, frame_recoger_pasajero.getHeight(), 0);
                                            try {
                                                map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds_ruta, 100));
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            Ruta(pocisionInicial, pocisionFinal);
                                        }
                                    }
                                    if (pedido_aceptado.getLat_dir_inicial() != 0) {
                                        View.OnClickListener clickListener = new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                switch (view.getId()) {
                                                    case R.id.boton_waze:
                                                        Uri waseuri = Uri.parse("waze://?ll=" +
                                                                pedido_aceptado.getLat_dir_inicial() + "," +
                                                                pedido_aceptado.getLong_dir_inicial() + "&navigate=yes");
                                                        Intent waseintent = new Intent(Intent.ACTION_VIEW, waseuri);
                                                        if (waseintent.resolveActivity(getPackageManager()) != null) {
                                                            startActivity(waseintent);
                                                        } else {
                                                            toast("No tienes instalada la app");
                                                        }
                                                        break;
                                                    case R.id.boton_maps:
                                                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" +
                                                                pedido_aceptado.getLat_dir_inicial() + "," +
                                                                pedido_aceptado.getLong_dir_inicial());
                                                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                                        mapIntent.setPackage("com.google.android.apps.maps");
                                                        if (mapIntent.resolveActivity(getPackageManager()) != null) {
                                                            startActivity(mapIntent);
                                                        } else {
                                                            toast("No tienes instalada la app");
                                                        }
                                                        break;
                                                }
                                            }
                                        };

                                        //le cargamos el metodo onclick
                                        wase_boton.setOnClickListener(clickListener);
                                        maps_boton.setOnClickListener(clickListener);
                                    } else {
                                        wase_boton.setVisibility(View.INVISIBLE);
                                        maps_boton.setVisibility(View.INVISIBLE);
                                    }

                                } else {
                                    FAB_avisar_llegada.show();
                                    texto_chat.setVisibility(View.VISIBLE);
                                    text_avisar.setVisibility(View.VISIBLE);
                                    texto_tipo_servicio.setText(Constantes.TIPO_SERVICIO_ENCOMIENDAS);
                                    text_avisar.setText(Constantes.MENSAJERO_EN_LUGAR_DE_RECOGIDA);
                                    recoger_direccion.setText(pedido_aceptado.getDir_inicial());
                                    frame_carrera_activa.setVisibility(View.INVISIBLE);
                                    frame_recoger_pasajero.setVisibility(View.VISIBLE);
                                    recoger_nombre.setText(pedido_aceptado.getNombre());
                                    recoger_numero.setText(pedido_aceptado.getTelefono());
                                    recoger_numero.setLinksClickable(true);
                                    texto_comentario_recoger.setText(pedido_aceptado.getComentario());
                                    //aqui vamos a agregar el punto al mapa para que detecte cuando esté cerca y vuelva a
                                    // Esta pantalla y continúe con el servicio
                                    if (pedido_aceptado.getLat_dir_inicial() != 0) {
                                        if (map != null) {
                                            agregarMarcador(pedido_aceptado.getLat_dir_inicial(),
                                                    pedido_aceptado.getLong_dir_inicial());
                                            pocisionFinal = new LatLng(pedido_aceptado.getLat_dir_inicial(),
                                                    pedido_aceptado.getLong_dir_inicial());
                                            LatLngBounds bounds_ruta = LatLngBounds.builder()
                                                    .include(pocisionInicial).include(pocisionFinal).build();
                                            //map.setPadding(0, 0, frame_recoger_pasajero.getHeight(), 0);
                                            try {
                                                map.animateCamera(CameraUpdateFactory
                                                        .newLatLngBounds(bounds_ruta, 100));
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            Ruta(pocisionInicial, pocisionFinal);

                                        }

                                        View.OnClickListener clickListener = new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                switch (view.getId()) {
                                                    case R.id.boton_waze:
                                                        Uri waseuri = Uri.parse("waze://?ll=" +
                                                                pedido_aceptado.getLat_dir_inicial() + "," +
                                                                pedido_aceptado.getLong_dir_inicial() + "&navigate=yes");
                                                        Intent waseintent = new Intent(Intent.ACTION_VIEW, waseuri);
                                                        if (waseintent.resolveActivity(getPackageManager()) != null) {
                                                            startActivity(waseintent);
                                                        } else {
                                                            toast("No tienes instalada la app");
                                                        }
                                                        break;
                                                    case R.id.boton_maps:
                                                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" +
                                                                pedido_aceptado.getLat_dir_inicial() + "," +
                                                                pedido_aceptado.getLong_dir_inicial());
                                                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                                        mapIntent.setPackage("com.google.android.apps.maps");
                                                        if (mapIntent.resolveActivity(getPackageManager()) != null) {
                                                            startActivity(mapIntent);
                                                        } else {
                                                            toast("No tienes instalada la app");
                                                        }
                                                        break;
                                                }
                                            }
                                        };

                                        //le cargamos el metodo onclick
                                        wase_boton.setOnClickListener(clickListener);
                                        maps_boton.setOnClickListener(clickListener);
                                    } else {
                                        wase_boton.setVisibility(View.INVISIBLE);
                                        maps_boton.setVisibility(View.INVISIBLE);
                                    }
                                }

                                locationManager.removeUpdates(locationListener);

                                FAB_cancelar_servicio.show();
                                text_cancelar.setVisibility(View.VISIBLE);

                                id_pedido_aceptado = pedido_aceptado.getId_pedido();
                                Intent detenerDesconectar = new Intent();
                                detenerDesconectar.setAction(Constantes.ACTION_PEDIDO_ACEPTADO);
                                sendBroadcast(detenerDesconectar);

                                //**************************************************************************//


                                // Aqui iniciamos el servicio para que detecte cuando esté cerca del pasajero y
                                // aparezca la pantalla para iniciar la carrera con el pasajero
                                if (!dir_viaje && !isMyServiceRunning(ServicioCarrera.class)) {
                                    Intent recogerPasajero = new Intent(MainActivity.this,
                                            ServicioCarrera.class);
                                    recogerPasajero.setAction(Constantes.ACTION_RECOGER);
                                    if (pedido_aceptado.getLat_dir_inicial() != 0) {
                                        recogerPasajero.putExtra(Constantes.LAT_INI,
                                                pedido_aceptado.getLat_dir_inicial());
                                        recogerPasajero.putExtra(Constantes.LNG_INI,
                                                pedido_aceptado.getLong_dir_inicial());
                                    }else{
                                        recogerPasajero.putExtra("lat_ini", lat);
                                        recogerPasajero.putExtra("log_ini", lng);
                                    }

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        startForegroundService(recogerPasajero);
                                    } else {
                                        startService(recogerPasajero);
                                    }
                                }
                                break;
                            case Constantes.ESTADO_PAQUETE_RECOGIDO:
                                Log.i(Constantes.BD_ESTADO_PEDIDO,Constantes.ESTADO_PAQUETE_RECOGIDO);
                                //mensajeor en el lugar de recogida, enmpieza a facturar
                                //aparecemos el frame y le agregamos sus respectivos valores
                                //compra realizada, se notifica al usuario
                                //mensajero inicia a facturar en el momento en el que llega al lugar de la compra
                                //aparecemos el frame y le agregamos sus respectivos valores
                                frame_carrera_activa.setVisibility(View.VISIBLE);
                                frame_recoger_pasajero.setVisibility(View.INVISIBLE);
                                texto_tipo_servicio.setText(Constantes.TIPO_SERVICIO_ENCOMIENDAS);
                                recoger_nombre.setText(pedido_aceptado.getNombre());
                                recoger_numero.setText(pedido_aceptado.getTelefono());
                                recoger_numero.setLinksClickable(true);
                                recoger_direccion.setText(pedido_aceptado.getDir_final());
                                FAB_cancelar_servicio.hide();
                                text_cancelar.setVisibility(View.INVISIBLE);
                                text_avisar.setText(Constantes.PAQUETE_ENTREGADO);
                                FAB_avisar_llegada.show();
                                text_avisar.setVisibility(View.VISIBLE);
                                texto_chat.setVisibility(View.VISIBLE);
                                FAB_Chat.show();
                                FAB_wase.show();
                                FAB_maps.show();
                                texto_num_cliente.setText("Teléfono: " + pedido_aceptado.getTelefono());
                                if (pedido_aceptado.getDir_final() != null) {
                                    texto_dir_cliente.setText("Dirección: " + pedido_aceptado.getDir_final());
                                } else {
                                    texto_dir_cliente.setText("Dirección: " + "Preguntar al usuario");
                                }
                                try {
                                    frame_carrera_activa.startAnimation(hacia_arriba);
                                    layout_info.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        boton_info.setBackground(getDrawable(android.R.drawable.arrow_down_float));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                texto_nom_cliente.setText("Nombre: " + pedido_aceptado.getNombre());
                                Intent detenerDesconectar1 = new Intent();
                                detenerDesconectar1.setAction(Constantes.ACTION_PEDIDO_ACEPTADO);
                                sendBroadcast(detenerDesconectar1);
                                texto_comentario_iniciar.setText(pedido_aceptado.getComentario());
                                //aqui vamos a agregar el punto al mapa para que detecte cuando esté cerca y vuelva a
                                // Esta pantalla y continúe con el servicio
                                if (map != null && pedido_aceptado.getLat_dir_inicial() != 0) {
                                    agregarMarcador(pedido_aceptado.getLat_dir_inicial(),
                                            pedido_aceptado.getLong_dir_inicial());

                                    pocisionFinal = new LatLng(pedido_aceptado.getLat_dir_inicial(),
                                            pedido_aceptado.getLong_dir_inicial());
                                    LatLngBounds bounds_ruta = LatLngBounds.builder()
                                            .include(pocisionInicial).include(pocisionFinal).build();
                                    //map.setPadding(0, 0, frame_recoger_pasajero.getHeight(), 0);
                                        map.animateCamera(CameraUpdateFactory
                                                .newLatLngBounds(bounds_ruta, 100));
                                    Ruta(pocisionInicial, pocisionFinal);

                                }
                                id_pedido_aceptado = pedido_aceptado.getId_pedido();
                                Intent donectar = new Intent();
                                donectar.setAction(Constantes.ACTION_PEDIDO_ACEPTADO);
                                sendBroadcast(donectar);

                                //**************************************************************************//
                                if (!isMyServiceRunning(ServicioCarrera.class)) {
                                    Log.i("Servicio no corriendo", " ver aqui");
                                    Intent iniciarCarrera = new Intent(MainActivity.this,
                                            ServicioCarrera.class);
                                    iniciarCarrera.setAction(Constantes.ACTION_INICIAR_CARRERA);
                                    if (pedido_aceptado.getLat_dir_inicial() != 0) {
                                        iniciarCarrera.putExtra(Constantes.LAT_INI,
                                                pedido_aceptado.getLat_dir_inicial());
                                        iniciarCarrera.putExtra(Constantes.LNG_INI,
                                                pedido_aceptado.getLong_dir_inicial());
                                    }else{
                                        iniciarCarrera.putExtra("lat_ini", lat);
                                        iniciarCarrera.putExtra("log_ini", lng);
                                    }

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        startForegroundService(iniciarCarrera);
                                    } else {
                                        startService(iniciarCarrera);
                                    }
                                    SERVICIO_ACTIVO = true;
                                }
                                bindService(new Intent(MainActivity.this, ServicioCarrera.class),
                                        mServiceConnection, Context.BIND_AUTO_CREATE);
                                    BotonIniciarCarrera.setText(Constantes.TERMINAR_SERVICIO);

                                break;
                            case Constantes.ESTADO_PAQUETE_ENTREGADO:
                                Log.i(Constantes.BD_ESTADO_PEDIDO,Constantes.ESTADO_PAQUETE_ENTREGADO);
                                //mensajeor en el lugar de entrega, aqui seguirá facturando hasta que se termine el viaje, ésto por
                                //si el mensajero debe dirigirse a otro lugar
                                //mensajeor en el lugar de recogida, enpieza a facturar
                                //aparecemos el frame y le agregamos sus respectivos valores
                                //compra realizada, se notifica al usuario
                                //mensajero inicia a facturar en el momento en el que llega al lugar de la compra
                                //aparecemos el frame y le agregamos sus respectivos valores
                                frame_carrera_activa.setVisibility(View.VISIBLE);
                                frame_recoger_pasajero.setVisibility(View.INVISIBLE);
                                recoger_nombre.setText(pedido_aceptado.getNombre());
                                recoger_numero.setText(pedido_aceptado.getTelefono());
                                recoger_numero.setLinksClickable(true);
                                recoger_direccion.setText(pedido_aceptado.getDir_final());
                                FAB_cancelar_servicio.hide();
                                text_cancelar.setVisibility(View.INVISIBLE);
                                FAB_avisar_llegada.hide();
                                texto_tipo_servicio.setText(Constantes.TIPO_SERVICIO_ENCOMIENDAS);
                                text_avisar.setVisibility(View.INVISIBLE);
                                texto_chat.setVisibility(View.INVISIBLE);
                                FAB_Chat.show();
                                FAB_maps.hide();
                                FAB_wase.hide();
                                texto_num_cliente.setText("Teléfono: " + pedido_aceptado.getTelefono());
                                if (pedido_aceptado.getDir_final() != null) {
                                    texto_dir_cliente.setText("Dirección: " + pedido_aceptado.getDir_final());
                                } else {
                                    texto_dir_cliente.setText("Dirección: " + "Preguntar al usuario");
                                }
                                texto_nom_cliente.setText("Nombre: " + pedido_aceptado.getNombre());
                                Intent detenerDesconectar00 = new Intent();
                                detenerDesconectar00.setAction(Constantes.ACTION_PEDIDO_ACEPTADO);
                                sendBroadcast(detenerDesconectar00);
                                texto_comentario_iniciar.setText(pedido_aceptado.getComentario());
                                //aqui vamos a agregar el punto al mapa para que detecte cuando esté cerca y vuelva a
                                // Esta pantalla y continúe con el servicio
                                if (map != null) {
                                    agregarMarcador(pedido_aceptado.getLat_dir_inicial(),
                                            pedido_aceptado.getLong_dir_inicial());
                                    pocisionFinal = new LatLng(pedido_aceptado.getLat_dir_inicial(),
                                            pedido_aceptado.getLong_dir_inicial());
                                    LatLngBounds bounds_ruta = LatLngBounds.builder()
                                            .include(pocisionInicial).include(pocisionFinal).build();
                                    //map.setPadding(0, 0, frame_recoger_pasajero.getHeight(), 0);
                                    try {
                                        map.animateCamera(CameraUpdateFactory
                                                .newLatLngBounds(bounds_ruta, 100));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        frame_carrera_activa.startAnimation(hacia_arriba);
                                        layout_info.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            boton_info.setBackground(getDrawable(android.R.drawable.arrow_down_float));
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    Ruta(pocisionInicial, pocisionFinal);

                                }
                                id_pedido_aceptado = pedido_aceptado.getId_pedido();
                                Intent donectar00 = new Intent();
                                donectar00.setAction(Constantes.ACTION_PEDIDO_ACEPTADO);
                                sendBroadcast(donectar00);

                                //**************************************************************************//

                                if (!isMyServiceRunning(ServicioCarrera.class)) {
                                    Intent iniciarCarrera = new Intent(MainActivity.this,
                                            ServicioCarrera.class);
                                    iniciarCarrera.setAction(Constantes.ACTION_INICIAR_CARRERA);
                                    if (pedido_aceptado.getLat_dir_inicial() != 0) {
                                        iniciarCarrera.putExtra(Constantes.LAT_INI,
                                                pedido_aceptado.getLat_dir_inicial());
                                        iniciarCarrera.putExtra(Constantes.LNG_INI,
                                                pedido_aceptado.getLong_dir_inicial());
                                    }else{
                                        iniciarCarrera.putExtra("lat_ini", lat);
                                        iniciarCarrera.putExtra("log_ini", lng);
                                    }

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        startForegroundService(iniciarCarrera);
                                    } else {
                                        startService(iniciarCarrera);
                                    }
                                    SERVICIO_ACTIVO = true;
                                }
                                bindService(new Intent(MainActivity.this, ServicioCarrera.class),
                                        mServiceConnection, Context.BIND_AUTO_CREATE);
                                BotonIniciarCarrera.setText(Constantes.TERMINAR_SERVICIO);


                                break;
                            case Constantes.ESTADO_TERMINADO:
                                Log.i(Constantes.BD_ESTADO_PEDIDO,Constantes.ESTADO_TERMINADO);
                                //servicio terminado
                                servicioEnlazado = false;
                                SERVICIO_ACTIVO = false;
                                layout_pendientes.setVisibility(View.VISIBLE);
                                cronometro.stop();
                                cronometro.setBase(SystemClock.elapsedRealtime());
                                textoDistancia.setText("");
                                BotonIniciarCarrera.setText(Constantes.INICIAR_SERVICIO);
                                texto_num_cliente.setText("Teléfono: ");
                                texto_dir_cliente.setText("Dirección: ");
                                texto_nom_cliente.setText("Nombre: ");
                                textoDistancia.setText("Distancia Recorrida: ");
                                textoValor.setText("Valor: ");
                                map.clear();
                                FAB_avisar_llegada.hide();
                                text_avisar.setVisibility(View.INVISIBLE);
                                points = null;
                                FAB_maps.hide();
                                FAB_Chat.hide();
                                texto_chat.setVisibility(View.INVISIBLE);
                                FAB_wase.hide();
                                frame_carrera_activa.setVisibility(View.INVISIBLE);
                                FAB_cancelar_servicio.hide();
                                text_cancelar.setVisibility(View.INVISIBLE);
                                dir_viaje = false;
                                dir_recoger = false;
                                pedido_aceptado = null;
                                id_pedido_aceptado = null;
                                editor.putBoolean(Constantes.ACTION_INICIAR_CARRERA, dir_viaje);
                                editor.putBoolean(Constantes.ACTION_RECOGER, dir_recoger);
                                editor.putString(Constantes.BD_ID_PEDIDO_ACEPTADO, id_pedido_aceptado);
                                editor.apply();
                                Intent iniciarDesconectar = new Intent();
                                iniciarDesconectar.setAction(Constantes.ACTION_TERMINAR_CARRERA);
                                sendBroadcast(iniciarDesconectar);
                                stopService(new Intent(MainActivity.this, ServicioCarrera.class));
                                pedidoQuery.removeEventListener(this);
                                pedido_aceptado = null;

                                break;
                            case Constantes.ESTADO_CANCELADO:
                                Log.i(Constantes.BD_ESTADO_PEDIDO,Constantes.ESTADO_CANCELADO);
                                //servicio cancelado
                                BotonIniciarCarrera.setText(Constantes.INICIAR_SERVICIO);
                                Intent iniciarDesconectar4 = new Intent();
                                iniciarDesconectar4.setAction(Constantes.ACTION_TERMINAR_CARRERA);
                                sendBroadcast(iniciarDesconectar4);
                                stopService(new Intent(MainActivity.this, ServicioCarrera.class));
                                pedido_aceptado = null;
                                FAB_Chat.hide();
                                FAB_maps.hide();
                                layout_pendientes.setVisibility(View.VISIBLE);
                                FAB_wase.hide();
                                frame_carrera_activa.setVisibility(View.INVISIBLE);
                                frame_recoger_pasajero.setVisibility(View.INVISIBLE);
                                texto_chat.setVisibility(View.INVISIBLE);
                                dir_viaje = false;
                                dir_recoger = false;
                                id_pedido_aceptado = null;
                                if(fragmentChat.isAdded()){
                                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                                    ft.remove(fragmentChat);
                                    ft.commit();
                                }
                                FAB_avisar_llegada.hide();
                                text_avisar.setVisibility(View.INVISIBLE);
                                Log.i("AcTION PEDACEPT value", "dir_recoger= " + dir_recoger);
                                ActivityManager activityManager = (ActivityManager) getApplication().getSystemService(Context.ACTIVITY_SERVICE);
                                List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
                                for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                                    if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                                        Log.i("Foreground App", appProcess.processName);
                                        if (appProcess.processName.equals(getApplication().getPackageName())) {
                                            Log.i("CANCELADO", "APP EN PRIMER PLANO");
                                            try {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                                builder.setMessage("El servicio ha sido cancelado")
                                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                Log.i("dialog click", "int " + i);
                                                                pedido_aceptado = null;
                                                                dir_viaje = false;
                                                                dir_recoger = false;
                                                                editor.putBoolean(Constantes.ACTION_RECOGER, dir_recoger);
                                                                editor.putBoolean(Constantes.ACTION_INICIAR_CARRERA, dir_viaje);
                                                                editor.putString(Constantes.BD_ID_PEDIDO_ACEPTADO, id_pedido_aceptado);
                                                                editor.apply();
                                                                Log.i("AcTION PEDACEPT value", "dir_recoger= " + dir_recoger);

                                                                reiniciarApp(MainActivity.this, null);
                                                            }
                                                        })
                                                        .setCancelable(false).show();


                                            } catch (Exception e) {

                                                e.printStackTrace();
                                                editor.putBoolean(Constantes.ACTION_INICIAR_CARRERA, dir_viaje);
                                                editor.putBoolean(Constantes.ACTION_RECOGER, dir_recoger);
                                                editor.putString(Constantes.BD_ID_PEDIDO_ACEPTADO, id_pedido_aceptado);
                                                editor.apply();
                                                reiniciarApp(MainActivity.this, true);
                                            }
                                            break;
                                        }
                                    } else {
                                        Log.i("CANCELADO", " APP EN SEGUNDO PLANO");
                                        try {
                                            reiniciarApp(MainActivity.this, true);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                pedidoQuery.removeEventListener(this);

                                break;
                            default:

                                break;
                        }
                        break;

                    case Constantes.FACTURAS_TRAMITES:
                        switch (estado){
                            case Constantes.ESTADO_EN_CURSO:
                                Log.i(Constantes.BD_ESTADO_PEDIDO,Constantes.ESTADO_EN_CURSO);
                                FAB_Chat.show();
                                if (dir_viaje) {
                                    frame_carrera_activa.setVisibility(View.VISIBLE);
                                    frame_recoger_pasajero.setVisibility(View.INVISIBLE);
                                    try {
                                        frame_carrera_activa.startAnimation(hacia_arriba);
                                        layout_info.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            boton_info.setBackground(getDrawable(android.R.drawable.arrow_down_float));
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    texto_num_cliente.setText("Teléfono: " + pedido_aceptado.getTelefono());
                                    if (pedido_aceptado.getDir_final() != null) {
                                        texto_dir_cliente.setText("Dirección: " + pedido_aceptado.getDir_final());
                                    } else {
                                        texto_dir_cliente.setText("Dirección final: " + "Preguntar al usuario");
                                    }
                                    texto_nom_cliente.setText("Nombre: " + pedido_aceptado.getNombre());
                                    texto_comentario_iniciar.setText(pedido_aceptado.getComentario());
                                    if (map != null) {
                                        if (pedido_aceptado.getLat_dir_final() != 0) {
                                            agregarMarcador(pedido_aceptado.getLat_dir_final(), pedido_aceptado.getLong_dir_final());
                                            pocisionFinal = new LatLng(pedido_aceptado.getLat_dir_final(), pedido_aceptado.getLong_dir_final());
                                            LatLngBounds bounds_ruta = LatLngBounds.builder().include(pocisionInicial).include(pocisionFinal).build();
                                            //map.setPadding(0, 0, frame_recoger_pasajero.getHeight(), 0);
                                            try {
                                                map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds_ruta, 100));
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            Ruta(pocisionInicial, pocisionFinal);
                                        }
                                    }

                                } else {
                                    FAB_avisar_llegada.show();
                                    texto_chat.setVisibility(View.VISIBLE);
                                    text_avisar.setVisibility(View.VISIBLE);
                                    recoger_direccion.setText(pedido_aceptado.getDir_inicial());
                                    texto_tipo_servicio.setText(Constantes.TIPO_SERVICIO_TRAMITES_FACTURAS);
                                    frame_carrera_activa.setVisibility(View.INVISIBLE);
                                    frame_recoger_pasajero.setVisibility(View.VISIBLE);
                                    recoger_nombre.setText(pedido_aceptado.getNombre());
                                    recoger_numero.setText(pedido_aceptado.getTelefono());
                                    recoger_numero.setLinksClickable(true);
                                    texto_comentario_recoger.setText(pedido_aceptado.getComentario());
                                    //aqui vamos a agregar el punto al mapa para que detecte cuando esté cerca y vuelva a
                                    // Esta pantalla y continúe con el servicio
                                    if (pedido_aceptado.getLat_dir_inicial() != 0) {
                                        if (map != null) {

                                            agregarMarcador(pedido_aceptado.getLat_dir_inicial(),
                                                    pedido_aceptado.getLong_dir_inicial());
                                            pocisionFinal = new LatLng(pedido_aceptado.getLat_dir_inicial(),
                                                    pedido_aceptado.getLong_dir_inicial());
                                            LatLngBounds bounds_ruta = LatLngBounds.builder()
                                                    .include(pocisionInicial).include(pocisionFinal).build();
                                            //map.setPadding(0, 0, frame_recoger_pasajero.getHeight(), 0);
                                            try {
                                                map.animateCamera(CameraUpdateFactory
                                                        .newLatLngBounds(bounds_ruta, 100));
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            Ruta(pocisionInicial, pocisionFinal);

                                        }

                                        View.OnClickListener clickListener = new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                switch (view.getId()) {
                                                    case R.id.boton_waze:
                                                        Uri waseuri = Uri.parse("waze://?ll=" +
                                                                pedido_aceptado.getLat_dir_inicial() + "," +
                                                                pedido_aceptado.getLong_dir_inicial() + "&navigate=yes");
                                                        Intent waseintent = new Intent(Intent.ACTION_VIEW, waseuri);
                                                        if (waseintent.resolveActivity(getPackageManager()) != null) {
                                                            startActivity(waseintent);
                                                        } else {
                                                            toast("No tienes instalada la app");
                                                        }
                                                        break;
                                                    case R.id.boton_maps:
                                                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" +
                                                                pedido_aceptado.getLat_dir_inicial() + "," +
                                                                pedido_aceptado.getLong_dir_inicial());
                                                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                                        mapIntent.setPackage("com.google.android.apps.maps");
                                                        if (mapIntent.resolveActivity(getPackageManager()) != null) {
                                                            startActivity(mapIntent);
                                                        } else {
                                                            toast("No tienes instalada la app");
                                                        }
                                                        break;
                                                }
                                            }
                                        };

                                        //le cargamos el metodo onclick
                                        wase_boton.setOnClickListener(clickListener);
                                        maps_boton.setOnClickListener(clickListener);
                                        // Aqui iniciamos el servicio para que detecte cuando esté cerca del pasajero y
                                        // aparezca la pantalla para iniciar la carrera con el pasajero
                                        if (!dir_viaje && !isMyServiceRunning(ServicioCarrera.class)) {
                                            Intent recogerPasajero = new Intent(MainActivity.this,
                                                    ServicioCarrera.class);
                                            recogerPasajero.setAction(Constantes.ACTION_RECOGER);
                                            if (pedido_aceptado.getLat_dir_inicial() != 0) {
                                                recogerPasajero.putExtra(Constantes.LAT_INI,
                                                        pedido_aceptado.getLat_dir_inicial());
                                                recogerPasajero.putExtra(Constantes.LNG_INI,
                                                        pedido_aceptado.getLong_dir_inicial());
                                            }

                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                startForegroundService(recogerPasajero);
                                            } else {
                                                startService(recogerPasajero);
                                            }
                                        }
                                    } else {
                                        wase_boton.setVisibility(View.INVISIBLE);
                                        maps_boton.setVisibility(View.INVISIBLE);
                                    }
                                }

                                locationManager.removeUpdates(locationListener);

                                FAB_cancelar_servicio.show();
                                text_cancelar.setVisibility(View.VISIBLE);

                                id_pedido_aceptado = pedido_aceptado.getId_pedido();
                                Intent detenerDesconectar = new Intent();
                                detenerDesconectar.setAction(Constantes.ACTION_PEDIDO_ACEPTADO);
                                sendBroadcast(detenerDesconectar);

                                //**************************************************************************//


                                break;
                            case Constantes.ESTADO_SERVICIO_INICIADO:
                                Log.i(Constantes.BD_ESTADO_PEDIDO,Constantes.ESTADO_SERVICIO_INICIADO);
                                //aparecemos el frame y le agregamos sus respectivos valores
                                frame_carrera_activa.setVisibility(View.VISIBLE);
                                frame_recoger_pasajero.setVisibility(View.INVISIBLE);
                                recoger_nombre.setText(pedido_aceptado.getNombre());
                                recoger_numero.setText(pedido_aceptado.getTelefono());
                                recoger_numero.setLinksClickable(true);
                                texto_tipo_servicio.setText(Constantes.TIPO_SERVICIO_TRAMITES_FACTURAS);
                                recoger_direccion.setText(pedido_aceptado.getDir_final());
                                FAB_cancelar_servicio.hide();
                                text_cancelar.setVisibility(View.INVISIBLE);
                                FAB_avisar_llegada.hide();
                                text_avisar.setVisibility(View.INVISIBLE);
                                texto_chat.setVisibility(View.INVISIBLE);
                                FAB_Chat.hide();
                                FAB_wase.show();
                                FAB_maps.show();
                                texto_num_cliente.setText("Teléfono: " + pedido_aceptado.getTelefono());
                                if (pedido_aceptado.getDir_final() != null) {
                                    texto_dir_cliente.setText("Dirección: " + pedido_aceptado.getDir_final());
                                } else {
                                    texto_dir_cliente.setText("Dirección final: " + "Preguntar al usuario");
                                }
                                texto_nom_cliente.setText("Nombre: " + pedido_aceptado.getNombre());
                                Intent detenerDesconectar5 = new Intent();
                                detenerDesconectar5.setAction(Constantes.ACTION_PEDIDO_ACEPTADO);
                                sendBroadcast(detenerDesconectar5);

                                if (!isMyServiceRunning(ServicioCarrera.class)) {
                                    Intent iniciarCarrera = new Intent(MainActivity.this,
                                        ServicioCarrera.class);
                                iniciarCarrera.setAction(Constantes.ACTION_INICIAR_CARRERA);
                                    if (pedido_aceptado.getLat_dir_inicial() != 0) {
                                        iniciarCarrera.putExtra(Constantes.LAT_INI,
                                                pedido_aceptado.getLat_dir_inicial());
                                        iniciarCarrera.putExtra(Constantes.LNG_INI,
                                                pedido_aceptado.getLong_dir_inicial());
                                    }

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    startForegroundService(iniciarCarrera);
                                } else {
                                    startService(iniciarCarrera);

                                }
                                    SERVICIO_ACTIVO = true;
                            }
                                bindService(new Intent(MainActivity.this, ServicioCarrera.class),
                                        mServiceConnection, Context.BIND_AUTO_CREATE);
                            BotonIniciarCarrera.setText(Constantes.TERMINAR_SERVICIO);
                                try {
                                    frame_carrera_activa.startAnimation(hacia_arriba);
                                    layout_info.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        boton_info.setBackground(getDrawable(android.R.drawable.arrow_down_float));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                break;
                            case Constantes.ESTADO_TERMINADO:
                                Log.i(Constantes.BD_ESTADO_PEDIDO,Constantes.ESTADO_TERMINADO);
                                //servicio terminado
                                servicioEnlazado = false;
                                SERVICIO_ACTIVO = false;
                                cronometro.setBase(SystemClock.elapsedRealtime());
                                cronometro.stop();
                                textoDistancia.setText("");
                                BotonIniciarCarrera.setText(Constantes.INICIAR_SERVICIO);
                                texto_num_cliente.setText("Teléfono: ");
                                texto_dir_cliente.setText("Dirección: ");
                                texto_nom_cliente.setText("Nombre: ");
                                textoDistancia.setText("Distancia Recorrida: ");
                                textoValor.setText("Valor: ");
                                frame_carrera_activa.setVisibility(View.INVISIBLE);
                                frame_recoger_pasajero.setVisibility(View.INVISIBLE);
                                map.clear();
                                points = null;
                                FAB_maps.hide();
                                FAB_Chat.hide();
                                layout_pendientes.setVisibility(View.VISIBLE);
                                texto_chat.setVisibility(View.INVISIBLE);
                                FAB_wase.hide();
                                FAB_cancelar_servicio.hide();
                                text_cancelar.setVisibility(View.INVISIBLE);
                                dir_viaje = false;
                                dir_recoger = false;
                                pedido_aceptado = null;
                                id_pedido_aceptado = null;
                                editor.putBoolean(Constantes.ACTION_INICIAR_CARRERA, dir_viaje);
                                editor.putBoolean(Constantes.ACTION_RECOGER, dir_recoger);
                                editor.putString(Constantes.BD_ID_PEDIDO_ACEPTADO, id_pedido_aceptado);
                                editor.apply();
                                Intent iniciarDesconectar = new Intent();
                                iniciarDesconectar.setAction(Constantes.ACTION_TERMINAR_CARRERA);
                                sendBroadcast(iniciarDesconectar);
                                pedidoQuery.removeEventListener(this);
                                pedido_aceptado = null;
                                break;
                            case Constantes.ESTADO_CANCELADO:
                                Log.i(Constantes.BD_ESTADO_PEDIDO,Constantes.ESTADO_CANCELADO);
                                BotonIniciarCarrera.setText(Constantes.INICIAR_SERVICIO);
                                Intent iniciarDesconecta = new Intent();
                                iniciarDesconecta.setAction(Constantes.ACTION_TERMINAR_CARRERA);
                                sendBroadcast(iniciarDesconecta);
                                stopService(new Intent(MainActivity.this, ServicioCarrera.class));
                                pedido_aceptado = null;
                                FAB_Chat.hide();
                                FAB_maps.hide();
                                layout_pendientes.setVisibility(View.VISIBLE);
                                FAB_wase.hide();
                                frame_carrera_activa.setVisibility(View.INVISIBLE);
                                frame_recoger_pasajero.setVisibility(View.INVISIBLE);
                                texto_chat.setVisibility(View.INVISIBLE);
                                dir_viaje = false;
                                dir_recoger = false;
                                id_pedido_aceptado = null;
                                if(fragmentChat.isAdded()){
                                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                                    ft.remove(fragmentChat);
                                    ft.commit();
                                }
                                FAB_avisar_llegada.hide();
                                text_avisar.setVisibility(View.INVISIBLE);
                                Log.i("AcTION PEDACEPT value", "dir_recoger= " + dir_recoger);
                                ActivityManager activityManager = (ActivityManager) getApplication().getSystemService(Context.ACTIVITY_SERVICE);
                                List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
                                for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                                    if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                                        Log.i("Foreground App", appProcess.processName);
                                        if (appProcess.processName.equals(getApplication().getPackageName())) {
                                            Log.i("CANCELADO", "APP EN PRIMER PLANO");
                                            try {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                                builder.setMessage("El servicio ha sido cancelado")
                                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                Log.i("dialog click", "int " + i);
                                                                pedido_aceptado = null;
                                                                dir_viaje = false;
                                                                dir_recoger = false;
                                                                editor.putBoolean(Constantes.ACTION_RECOGER, dir_recoger);
                                                                editor.putBoolean(Constantes.ACTION_INICIAR_CARRERA, dir_viaje);
                                                                editor.putString(Constantes.BD_ID_PEDIDO_ACEPTADO, id_pedido_aceptado);
                                                                editor.apply();
                                                                Log.i("AcTION PEDACEPT value", "dir_recoger= " + dir_recoger);

                                                                reiniciarApp(MainActivity.this, null);
                                                            }
                                                        })
                                                        .setCancelable(false).show();


                                            } catch (Exception e) {

                                                e.printStackTrace();
                                                editor.putBoolean(Constantes.ACTION_INICIAR_CARRERA, dir_viaje);
                                                editor.putBoolean(Constantes.ACTION_RECOGER, dir_recoger);
                                                editor.putString(Constantes.BD_ID_PEDIDO_ACEPTADO, id_pedido_aceptado);
                                                editor.apply();
                                                reiniciarApp(MainActivity.this, true);
                                            }
                                            break;
                                        }
                                    } else {
                                        Log.i("CANCELADO", " APP EN SEGUNDO PLANO");
                                        try {
                                            reiniciarApp(MainActivity.this, true);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                pedidoQuery.removeEventListener(this);

                                break;

                            default:

                                break;
                        }
                        break;

                    case Constantes.SOLICITUD_RAPIDA:
                        switch (estado){
                            case Constantes.ESTADO_EN_CURSO:
                                Log.i(Constantes.BD_ESTADO_PEDIDO,Constantes.ESTADO_EN_CURSO);
                                //el mensajero ya tomo el servicio
                                //aparecemos el frame y le agregamos sus respectivos valores
                                FAB_Chat.show();
                                if (dir_viaje) {
                                    frame_carrera_activa.setVisibility(View.VISIBLE);
                                    frame_recoger_pasajero.setVisibility(View.INVISIBLE);
                                    try {
                                        frame_carrera_activa.startAnimation(hacia_arriba);
                                        layout_info.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            boton_info.setBackground(getDrawable(android.R.drawable.arrow_down_float));
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    if (pedido_aceptado.getComentario() != null) {
                                        texto_comentario_recoger.setText(pedido_aceptado.getComentario());
                                    } else {
                                        texto_comentario_iniciar.setText("Recibe el domicilio y ve a entregarlo, no olvides pedirle la dirección de entrega al despachador");
                                    }

                                    texto_num_cliente.setText("Teléfono: " + pedido_aceptado.getTelefono());
                                    texto_dir_cliente.setText("Dirección: " + "Preguntar al despachador");
                                    texto_nom_cliente.setText("Nombre: " + pedido_aceptado.getNombre());
                                    BotonIniciarCarrera.setText(Constantes.AGREGAR_VALOR_SERVICIO);



                                    if (map != null) {
                                        if (pedido_aceptado.getLat_dir_final() != 0) {
                                            agregarMarcador(pedido_aceptado.getLat_dir_final(), pedido_aceptado.getLong_dir_final());
                                            pocisionFinal = new LatLng(pedido_aceptado.getLat_dir_final(), pedido_aceptado.getLong_dir_final());
                                            LatLngBounds bounds_ruta = LatLngBounds.builder().include(pocisionInicial).include(pocisionFinal).build();
                                            //map.setPadding(0, 0, frame_recoger_pasajero.getHeight(), 0);
                                            try {
                                                map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds_ruta, 100));
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            Ruta(pocisionInicial, pocisionFinal);
                                        }
                                    }

                                } else {
                                    FAB_avisar_llegada.show();
                                    texto_chat.setVisibility(View.VISIBLE);
                                    text_avisar.setVisibility(View.VISIBLE);
                                    texto_tipo_servicio.setText(Constantes.TIPO_SERVICIO_SOLICITUD_RAPIDA);
                                    recoger_direccion.setText(pedido_aceptado.getDir_inicial());
                                    frame_carrera_activa.setVisibility(View.INVISIBLE);
                                    frame_recoger_pasajero.setVisibility(View.VISIBLE);
                                    if (pedido_aceptado.getComentario() != null) {
                                        texto_comentario_recoger.setText(pedido_aceptado.getComentario());
                                    } else {
                                        texto_comentario_recoger.setText("Dirígete lo antes posible");
                                    }
                                    recoger_nombre.setText(pedido_aceptado.getNombre());
                                    recoger_numero.setText(pedido_aceptado.getTelefono() + " click para llamar");
                                    recoger_numero.setLinksClickable(true);
                                    //aqui vamos a agregar el punto al mapa para que detecte cuando esté cerca y vuelva a
                                    // Esta pantalla y continúe con el servicio
                                    if (map != null) {
                                        if(pedido_aceptado.getLat_dir_inicial() != 0){
                                            agregarMarcador(pedido_aceptado.getLat_dir_inicial(),
                                                    pedido_aceptado.getLong_dir_inicial());
                                            pocisionFinal = new LatLng(pedido_aceptado.getLat_dir_inicial(),
                                                    pedido_aceptado.getLong_dir_inicial());
                                            LatLngBounds bounds_ruta = LatLngBounds.builder()
                                                    .include(pocisionInicial).include(pocisionFinal).build();
                                            //map.setPadding(0, 0, frame_recoger_pasajero.getHeight(), 0);
                                            try {
                                                map.animateCamera(CameraUpdateFactory
                                                        .newLatLngBounds(bounds_ruta, 100));
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            Ruta(pocisionInicial, pocisionFinal);
                                        }else{
                                            FAB_maps.hide();
                                            FAB_wase.hide();
                                            maps_boton.setVisibility(View.INVISIBLE);
                                            wase_boton.setVisibility(View.INVISIBLE);
                                        }
                                    }

                                    View.OnClickListener clickListener = new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            switch (view.getId()) {
                                                case R.id.boton_waze:
                                                    if (pedido_aceptado.getLat_dir_inicial() != 0) {
                                                        Uri waseuri = Uri.parse("waze://?ll=" +
                                                                pedido_aceptado.getLat_dir_inicial() + "," +
                                                                pedido_aceptado.getLong_dir_inicial() + "&navigate=yes");
                                                        Intent waseintent = new Intent(Intent.ACTION_VIEW, waseuri);
                                                        if (waseintent.resolveActivity(getPackageManager()) != null) {
                                                            startActivity(waseintent);
                                                        } else {
                                                            toast("No tienes instalada la app");
                                                        }
                                                    }
                                                    break;
                                                case R.id.boton_maps:
                                                    if (pedido_aceptado.getLat_dir_inicial() != 0) {
                                                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" +
                                                                pedido_aceptado.getLat_dir_inicial() + "," +
                                                                pedido_aceptado.getLong_dir_inicial());
                                                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                                        mapIntent.setPackage("com.google.android.apps.maps");
                                                        if (mapIntent.resolveActivity(getPackageManager()) != null) {
                                                            startActivity(mapIntent);
                                                        } else {
                                                            toast("No tienes instalada la app");
                                                        }
                                                    }
                                                    break;
                                            }
                                        }
                                    };

                                    //le cargamos el metodo onclick
                                    wase_boton.setOnClickListener(clickListener);
                                    maps_boton.setOnClickListener(clickListener);
                                }

                                locationManager.removeUpdates(locationListener);

                                FAB_cancelar_servicio.show();
                                text_cancelar.setVisibility(View.VISIBLE);

                                id_pedido_aceptado = pedido_aceptado.getId_pedido();
                                Intent detenerDesconectar = new Intent();
                                detenerDesconectar.setAction(Constantes.ACTION_PEDIDO_ACEPTADO);
                                sendBroadcast(detenerDesconectar);

                                //**************************************************************************//


                                // Aqui iniciamos el servicio para que detecte cuando esté cerca del pasajero y
                                // aparezca la pantalla para iniciar la carrera con el pasajero
                                if (!dir_viaje && !isMyServiceRunning(ServicioCarrera.class)) {
                                    if(pedido_aceptado.getLat_dir_inicial() != 0) {
                                        Intent recogerPasajero = new Intent(MainActivity.this,
                                                ServicioCarrera.class);
                                        recogerPasajero.setAction(Constantes.ACTION_RECOGER);
                                        recogerPasajero.putExtra(Constantes.LAT_INI,
                                                pedido_aceptado.getLat_dir_inicial());
                                        recogerPasajero.putExtra(Constantes.LNG_INI,
                                                pedido_aceptado.getLong_dir_inicial());

                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            startForegroundService(recogerPasajero);
                                        } else {
                                            startService(recogerPasajero);
                                        }
                                    }
                                }


                                break;
                            case Constantes.ESTADO_SERVICIO_INICIADO:
                                Log.i(Constantes.BD_ESTADO_PEDIDO,Constantes.ESTADO_SERVICIO_INICIADO);
                                //el mensajero ya tomo el servicio

                                //aparecemos el frame y le agregamos sus respectivos valores
                                frame_carrera_activa.setVisibility(View.VISIBLE);
                                frame_recoger_pasajero.setVisibility(View.INVISIBLE);
                                recoger_nombre.setText(pedido_aceptado.getNombre());
                                recoger_numero.setText(pedido_aceptado.getTelefono());
                                recoger_numero.setLinksClickable(true);
                                recoger_direccion.setText(pedido_aceptado.getDir_final());
                                texto_comentario_iniciar.setText("Entrega el domicilio");
                                FAB_cancelar_servicio.hide();
                                text_cancelar.setVisibility(View.INVISIBLE);
                                FAB_avisar_llegada.hide();
                                text_avisar.setVisibility(View.INVISIBLE);
                                texto_chat.setVisibility(View.INVISIBLE);
                                FAB_Chat.hide();
                                FAB_wase.show();
                                FAB_maps.show();
                                texto_num_cliente.setText("Teléfono: " + pedido_aceptado.getTelefono());
                                if (pedido_aceptado.getDir_final() != null) {
                                    texto_dir_cliente.setText("Dirección: " + pedido_aceptado.getDir_final());
                                } else {
                                    texto_dir_cliente.setText("Dirección: " + "Preguntar al usuario");
                                }
                                texto_nom_cliente.setText("Nombre: " + pedido_aceptado.getNombre());
                                Intent detenerDesconectar6 = new Intent();
                                detenerDesconectar6.setAction(Constantes.ACTION_PEDIDO_ACEPTADO);
                                sendBroadcast(detenerDesconectar6);
                                if (!isMyServiceRunning(ServicioCarrera.class)) {
                                    Intent iniciarCarrera = new Intent(MainActivity.this,
                                            ServicioCarrera.class);
                                        iniciarCarrera.putExtra("lat_ini", lat);
                                        iniciarCarrera.putExtra("log_ini", lng);

                                    iniciarCarrera.setAction(Constantes.ACTION_INICIAR_CARRERA);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        startForegroundService(iniciarCarrera);
                                    } else {
                                        startService(iniciarCarrera);
                                    }
                                    SERVICIO_ACTIVO = true;
                                }
                                bindService(new Intent(MainActivity.this, ServicioCarrera.class),
                                        mServiceConnection, Context.BIND_AUTO_CREATE);
                                BotonIniciarCarrera.setText(Constantes.TERMINAR_SERVICIO);
                                try {
                                    frame_carrera_activa.startAnimation(hacia_arriba);
                                    layout_info.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        boton_info.setBackground(getDrawable(android.R.drawable.arrow_down_float));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case Constantes.ESTADO_TERMINADO:
                                Log.i(Constantes.BD_ESTADO_PEDIDO,Constantes.ESTADO_TERMINADO);
                                servicioEnlazado = false;
                                SERVICIO_ACTIVO = false;
                                cronometro.stop();
                                cronometro.setBase(SystemClock.elapsedRealtime());
                                textoDistancia.setText("");
                                BotonIniciarCarrera.setText(Constantes.INICIAR_SERVICIO);
                                frame_carrera_activa.setVisibility(View.INVISIBLE);
                                texto_num_cliente.setText("Teléfono: ");
                                texto_dir_cliente.setText("Dirección: ");
                                texto_nom_cliente.setText("Nombre: ");
                                textoDistancia.setText("Distancia Recorrida: ");
                                textoValor.setText("Valor: ");
                                map.clear();
                                FAB_avisar_llegada.hide();
                                text_avisar.setVisibility(View.INVISIBLE);
                                points = null;
                                FAB_maps.hide();
                                FAB_Chat.hide();
                                texto_chat.setVisibility(View.INVISIBLE);
                                FAB_wase.hide();
                                FAB_cancelar_servicio.hide();
                                text_cancelar.setVisibility(View.INVISIBLE);
                                dir_viaje = false;
                                dir_recoger = false;
                                layout_pendientes.setVisibility(View.VISIBLE);
                                pedido_aceptado = null;
                                id_pedido_aceptado = null;
                                editor.putBoolean(Constantes.ACTION_INICIAR_CARRERA, dir_viaje);
                                editor.putBoolean(Constantes.ACTION_RECOGER, dir_recoger);
                                editor.putString(Constantes.BD_ID_PEDIDO_ACEPTADO, id_pedido_aceptado);
                                editor.apply();
                                Intent iniciarDesconectar = new Intent();
                                iniciarDesconectar.setAction(Constantes.ACTION_TERMINAR_CARRERA);
                                sendBroadcast(iniciarDesconectar);
                                stopService(new Intent(MainActivity.this, ServicioCarrera.class));
                                pedidoQuery.removeEventListener(this);
                                pedido_aceptado = null;

                                break;
                            case Constantes.ESTADO_CANCELADO:
                                Log.i(Constantes.BD_ESTADO_PEDIDO,Constantes.ESTADO_CANCELADO);
                                Intent iniciarDesconect = new Intent();
                                iniciarDesconect.setAction(Constantes.ACTION_TERMINAR_CARRERA);
                                sendBroadcast(iniciarDesconect);
                                stopService(new Intent(MainActivity.this, ServicioCarrera.class));
                                pedido_aceptado = null;
                                FAB_Chat.hide();
                                BotonIniciarCarrera.setText(Constantes.INICIAR_SERVICIO);
                                texto_chat.setVisibility(View.INVISIBLE);
                                dir_viaje = false;
                                dir_recoger = false;
                                id_pedido_aceptado = null;
                                layout_pendientes.setVisibility(View.VISIBLE);
                                if(fragmentChat.isAdded()){
                                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                                    ft.remove(fragmentChat);
                                    ft.commit();
                                }
                                FAB_avisar_llegada.hide();
                                text_avisar.setVisibility(View.INVISIBLE);
                                Log.i("AcTION PEDACEPT value", "dir_recoger= " + dir_recoger);
                                ActivityManager activityManager = (ActivityManager) getApplication().getSystemService(Context.ACTIVITY_SERVICE);
                                List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
                                for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                                    if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                                        Log.i("Foreground App", appProcess.processName);
                                        if (appProcess.processName.equals(getApplication().getPackageName())) {
                                            Log.i("CANCELADO", "APP EN PRIMER PLANO");
                                            try {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                                builder.setMessage("El servicio ha sido cancelado")
                                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                Log.i("dialog click", "int " + i);
                                                                pedido_aceptado = null;
                                                                dir_viaje = false;
                                                                dir_recoger = false;
                                                                editor.putBoolean(Constantes.ACTION_RECOGER, dir_recoger);
                                                                editor.putBoolean(Constantes.ACTION_INICIAR_CARRERA, dir_viaje);
                                                                editor.putString(Constantes.BD_ID_PEDIDO_ACEPTADO, id_pedido_aceptado);
                                                                editor.apply();
                                                                Log.i("AcTION PEDACEPT value", "dir_recoger= " + dir_recoger);

                                                                reiniciarApp(MainActivity.this, null);
                                                            }
                                                        })
                                                        .setCancelable(false).show();


                                            } catch (Exception e) {

                                                e.printStackTrace();
                                                editor.putBoolean(Constantes.ACTION_INICIAR_CARRERA, dir_viaje);
                                                editor.putBoolean(Constantes.ACTION_RECOGER, dir_recoger);
                                                editor.putString(Constantes.BD_ID_PEDIDO_ACEPTADO, id_pedido_aceptado);
                                                editor.apply();
                                                reiniciarApp(MainActivity.this, true);
                                            }
                                            break;
                                        }
                                    } else {
                                        Log.i("CANCELADO", " APP EN SEGUNDO PLANO");
                                        try {
                                            reiniciarApp(MainActivity.this, true);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                pedidoQuery.removeEventListener(this);

                                break;
                            default:

                                break;
                        }
                        break;

                }
            } else {
                FAB_Chat.hide();
                texto_chat.setVisibility(View.INVISIBLE);
                Log.i("PEDIDO ACEPTADO", "PEDIDO NULO");
                dir_recoger = false;
                dir_viaje = false;
                id_pedido_aceptado = null;
                pedidoQuery.removeEventListener(this);
                stopService(new Intent(MainActivity.this, ServicioCarrera.class));
            }


        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    //fragmentos del menu
    Fragment_perfil fragment_perfil;
    Fragment_balance fragment_balance;
    private DrawerLayout drawer;

    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;

    public Query query_pedidos_pendientes;
    public TextView texto_vacio_pendientes;
    public ValueEventListener listener_pedidos_pendientes = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            pedidos_pendientes.clear();
            if (dataSnapshot.hasChildren()) {
                for(DataSnapshot snapshot:
                        dataSnapshot.getChildren()){
                    Pedidos pedido = snapshot.getValue(Pedidos.class);
                    if (pedido != null) {
                        if(pedido.getCodigo_mensajero().equals(Constantes.CODIGO_CENTRAL_MENSAJERO) && !pedido.getEstado_pedido().equals(Constantes.ESTADO_TERMINADO)
                                && !pedido.getEstado_pedido().equals(Constantes.ESTADO_CANCELADO)){
                            pedidos_pendientes.add(pedido);
                        }else if(pedido.getCodigo_mensajero().equals(codigo) && !pedido.getEstado_pedido().equals(Constantes.ESTADO_TERMINADO)
                                && !pedido.getEstado_pedido().equals(Constantes.ESTADO_CANCELADO) && id_pedido_aceptado == null){
                            id_pedido_aceptado = pedido.getId_pedido();
                            pedidoQuery = database.child(Constantes.PEDIDO).child(id_pedido_aceptado);
                            pedidoQuery.addValueEventListener(listener_pedido_aceptado);

                        }else{
                            for (int i = 0; i < pedidos_pendientes.size(); i++) {
                                if(!pedidos_pendientes.get(i).getCodigo_mensajero().equals(Constantes.CODIGO_CENTRAL_MENSAJERO)){
                                    pedidos_pendientes.remove(i);
                                }
                            }

                        }
                    }
                }

            }

            if(pedidos_pendientes.size() < 1){
                texto_vacio_pendientes.setVisibility(View.VISIBLE);
            }else{
                texto_vacio_pendientes.setVisibility(View.INVISIBLE);
                if (pedido_aceptado == null) {
                    layout_pendientes.setVisibility(View.VISIBLE);
                } else{
                    layout_pendientes.setVisibility(View.INVISIBLE);
                }
            }
            adapterServiciosPendientes.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //iniciar el dialogo para cuandop se está esperando el valor del servicio
        dialog_calculando = new Dialog(this, R.style.Theme_AppCompat_DialogWhenLarge);

        //esto es para crear los canales de notificaciones si aún no están creados
        notificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CrearCanalesDeNOtificaciones(notificationManager);
        }
        //creando el dialogo confirmacion
        dialog_terminar_viaje = new Dialog(this, R.style.Theme_AppCompat_DialogWhenLarge);
        dialog_terminar_viaje.setCancelable(false);

        //inicializar los fragmentos
        fragment_balance = new Fragment_balance();
        fragment_perfil = new Fragment_perfil();
        fragmentBloqueado = new FragmentBloqueado();
        documentosFragm = new EnviarDocumentosFragm();
        historial = new Historial();
        fragmentChat = new FragmentChat();

        //cargar el cuadrante de popayan
        Popayan = new LatLngBounds.Builder().
                include(new LatLng(2.419813, -76.659250))
                .include(new LatLng(2.494589, -76.556597)).build();

        //cargar animaciones
        hacia_abajo = AnimationUtils.loadAnimation(this, R.anim.aparecer_abajo_arriba);
        hacia_arriba = AnimationUtils.loadAnimation(this, R.anim.aparecer);

        //pantalla siempre encendida
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //iniciar locationllistener
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        points = new ArrayList<LatLng>();

        //inicializamos el frame para la carrera activa y sus componentes
        texto_comentario_iniciar = findViewById(R.id.text_comentario_iniciar);
        layout_info = findViewById(R.id.L_info);
        frame_carrera_activa = findViewById(R.id.frame_carrera_activa);
        frame_carrera_activa.setVisibility(View.INVISIBLE);
        textoDistancia = findViewById(R.id.textViewDistancia);
        textoValor = findViewById(R.id.textViewValor);
        cronometro = findViewById(R.id.cronometro);
        texto_dir_cliente = findViewById(R.id.texto_dir_final);
        texto_nom_cliente = findViewById(R.id.texto_nom_cliente);
        texto_num_cliente = findViewById(R.id.texto_tel_cliente);
        texto_num_cliente.setOnClickListener(this);
        texto_chat = findViewById(R.id.texto_chat);
        textoVersion = findViewById(R.id.textViewversionApp);
        //********************************
        //aqui vamos a inicializar el frame que indica los datos para recoger al cliente
        texto_tipo_servicio = findViewById(R.id.tipo_servicio);
        frame_recoger_pasajero = findViewById(R.id.frame_servicio_aceptado);
        recoger_direccion = findViewById(R.id.dir_recoger_pasajero);
        recoger_nombre = findViewById(R.id.nom_recoger_pasajero);
        recoger_numero = findViewById(R.id.num_recoger_pasajero);
        wase_boton = findViewById(R.id.boton_waze);
        maps_boton = findViewById(R.id.boton_maps);
        text_cancelar = findViewById(R.id.text_cancelar);
        text_avisar = findViewById(R.id.text_avisar_llegada);
        texto_comentario_recoger = findViewById(R.id.text_comentario);

        //widgets de la pantalla botones y demas
        BotonIniciarCarrera = findViewById(R.id.btiniciarcarrera);
        ETAgregarDestino = findViewById(R.id.ETAgregarDestino);
        BotonIniciarCarrera.setOnClickListener(this);
        boton_info = findViewById(R.id.boton_info);
        boton_info.setOnClickListener(this);
        FAB_maps = findViewById(R.id.floating_maps);
        FAB_wase = findViewById(R.id.floating_waze);
        FAB_cancelar_servicio = findViewById(R.id.floating_cancelar_servicio);
        FAB_avisar_llegada = findViewById(R.id.floating_avisar_llegue);
        FAB_avisar_llegada.setOnClickListener(this);
        FAB_cancelar_servicio.setOnClickListener(this);
        FAB_wase.setOnClickListener(this);
        FAB_maps.setOnClickListener(this);
        FAB_wase.hide();
        FAB_maps.hide();
        text_cancelar.setVisibility(View.INVISIBLE);
        text_avisar.setVisibility(View.INVISIBLE);
        recoger_numero.setOnClickListener(this);
        FAB_avisar_llegada.hide();
        FAB_cancelar_servicio.hide();
        FAB_Chat = findViewById(R.id.fabChat);
        FAB_Chat.setOnClickListener(this);
        FAB_Chat.hide();
        texto_chat.setVisibility(View.INVISIBLE);
        //pra guardar datos
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //para pedidos pendientes
        reciclerPedidospendientes = findViewById(R.id.recycler_servicios);
        reciclerPedidospendientes.setLayoutManager(new LinearLayoutManager(this));
        pedidos_pendientes = new ArrayList<>();
        adapterServiciosPendientes = new AdapterServiciosPendientes(pedidos_pendientes,this);
        reciclerPedidospendientes.setAdapter(adapterServiciosPendientes);
        reciclerPedidospendientes.setItemAnimator(new DefaultItemAnimator());
        query_pedidos_pendientes = database.child(Constantes.PEDIDO).limitToLast(50);
        texto_vacio_pendientes = findViewById(R.id.text_servicios_vacio);
        layout_pendientes = findViewById(R.id.layout_pendientes);

        mAuth = FirebaseAuth.getInstance();

        funtions = FirebaseFunctions.getInstance();
        configuracionRemotaFirebase = FirebaseRemoteConfig.getInstance();
        settingsConfRemote = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        configuracionRemotaFirebase.setConfigSettings(settingsConfRemote);
        configuracionRemotaFirebase.setDefaults(R.xml.remote_config_defaults);
        long cacheExpiration = 3600;
        if (configuracionRemotaFirebase.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }
        configuracionRemotaFirebase.fetch(cacheExpiration).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.i("FETCH CONFG", "cargados correctamente");
                    configuracionRemotaFirebase.activateFetched();


                } else {
                    Log.i("FETCH CONFG", "error al cargar");
                    Log.e("FETCH CONFG", task.getException() + "");
                }

                String mensje = configuracionRemotaFirebase.getString("mensaje_bienvenida");
                //toast(mensje);
            }
        });


        //boton para conectar y desconectar
        aSwitchEstado = findViewById(R.id.switch1);
        aSwitchEstado.setOnCheckedChangeListener(checkedChangeListener);
        //cambiarEstado(aSwitchEstado);//escucha para el cambio de estado

        sessionId = UUID.randomUUID().toString();


        //Configuracion para el trabajo de mantener el listener de la base de datos en background
        // configureJobManager();


        //obtener soporte para el mapa
        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //validamos si la app se inicia desde el servicio
        try {
            String actionNotificacion = "";
            if (getIntent() != null) {
                actionNotificacion = getIntent().getAction();
            }
            Log.i("accion", actionNotificacion+ "accion");
            switch (actionNotificacion) {
                case Constantes.ACTION_INICIO_SESION:
                    Log.i("ACTION", "INICIO DE SESION");
                    //este es el caso en el que el usuario inicia sesion por primero vez o
                    // abre la app con la sesion ya activa

                    id_mensajero = getIntent().getStringExtra(Constantes.BD_ID_USUARIO);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(Constantes.BD_ID_PEDIDO, id_mensajero);
                    editor.apply();

                    Gson gson = new Gson(); //Instancia Gson.
                    String json = sharedPref.getString(Constantes.BD_MENSAJERO, null);
                    mensajero = gson.fromJson(json, Mensajeros.class);


                    if (id_mensajero != null) {
                        DatosMensajero(id_mensajero);
                        Log.i("INICIO DE SESION", id_mensajero);
                    } else {

                        if (mAuth.getCurrentUser() != null) {
                            DatosMensajero(mAuth.getCurrentUser().getUid());
                        }

                    }
                    /*
                     Aqui lo que hacemos es verificar el estado del mensjaero
                     y de acuerdo a eso aparecerá la pantalla correspondiente
                     - ESTADO_VERIFICAR == es cuando ya sube las imagenes pero aun hay que verificarlas.
                     - ESTADO_SUBIR_IMAGENES == es el estado con el que queda el mensajero cuando recien se registra
                                                para que aparezca la pantalla de subir las imagenes
                     - ESTADO_ACTIVO == para el mensajero que puede iniciar sin problemas
                     - ESTADO_BLOQUEADO == para el mensajero que se encuantra bloqueado por alguna razon
                    */
                    break;
                case Constantes.ACTION_DESCONECTAR:
                    Log.i("ACTION", "DESCONECTAR");
                    setConectado(false);
                    Intent deconectar = new Intent(MainActivity.this, ServicioDesconectar.class);
                    stopService(deconectar);
                    try {
                        finishAffinity();
                    } catch (Exception e) {
                        e.printStackTrace();
                        finish();
                    }
                    break;
                case Constantes.ACTION_CONECTAR:
                    Log.i("ACTION", "CONECTAR");
                    Intent conectar = new Intent(MainActivity.this, ServicioDesconectar.class);
                    stopService(conectar);
                    if (getIntent().getBooleanExtra(Constantes.ESTADO_CANCELADO, false)) {
                        toast("El cliente canceló el servicio");
                        dir_recoger = false;
                        dir_viaje = false;
                        editor = sharedPref.edit();
                        editor.putBoolean(Constantes.ACTION_INICIAR_CARRERA, dir_viaje);
                        editor.putBoolean(Constantes.ACTION_RECOGER, dir_recoger);
                        editor.apply();

                    }
                    setConectado(true);
                    break;
                case Constantes.ACTION_CONECTAR_DESDE_NOTIFICACION:
                    Log.i("ACTION", "CONECTAR_desde_motificacion");
                    try {
                        if (isMyServiceRunning(ServicioDesconectar.class)) {
                            Intent desconectar = new Intent(MainActivity.this,
                                    ServicioDesconectar.class);
                            stopService(desconectar);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    aSwitchEstado.setChecked(true);

                    break;
                case Constantes.SERVICIO_NUEVO:
                    Log.i("ACTION", "PEDIDO NUEVO");
                    String id_pedido = getIntent().getStringExtra(Constantes.BD_ID_PEDIDO);
                    posicion_en_la_lista = getIntent()
                            .getStringExtra(Constantes.BD_POSICION_EN_LA_LISTA);
                    id_lista = getIntent().getStringExtra(Constantes.ID_LISTA);
                    Log.i("Id_pedido", id_pedido);
                    ServicioRecibido(id_pedido);
                    ActionServicioNuevo = true;
                    dir_viaje = false;
                    dir_recoger = false;
                    editor = sharedPref.edit();
                    editor.putBoolean(Constantes.BD_ESTADO_MENSAJERO, true);
                    editor.apply();
                    try {
                        ProgressDialog.Builder builder = new ProgressDialog.Builder(this);
                        builder.setMessage("Servicio entrante...")
                                .setCancelable(true)
                                .show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case Constantes.ACTION_PEDIDO_ACEPTADO:
                    Intent detenerDesc = new Intent();
                    detenerDesc.setAction(Constantes.ACTION_PEDIDO_ACEPTADO);
                    sendBroadcast(detenerDesc);
                    editor = sharedPref.edit();
                    stopService(new Intent(this,ServicioCarrera.class));
                    ETAgregarDestino.setVisibility(View.INVISIBLE);
                    layout_pendientes.setVisibility(View.INVISIBLE);
                    editor.putBoolean(Constantes.BD_ESTADO_MENSAJERO, true);
                    editor.apply();
                    Log.i("ACTION", "PEDIDO ECPTADO");
                    FAB_maps.hide();
                    FAB_wase.hide();
                    FAB_cancelar_servicio.show();
                    FAB_avisar_llegada.show();
                    text_avisar.setVisibility(View.VISIBLE);
                    text_cancelar.setVisibility(View.VISIBLE);
                    dir_recoger = true;
                    Log.i("AcTION PEDIDOACEPT", "dir_recoger= " + dir_recoger);
                    id_pedido_aceptado = getIntent().getStringExtra(Constantes.BD_ID_PEDIDO);
                    editor = sharedPref.edit();
                    editor.putString(Constantes.BD_ID_PEDIDO_ACEPTADO, id_pedido_aceptado);
                    editor.apply();
                    pedidoQuery = database.child(Constantes.PEDIDO).child(id_pedido_aceptado);
                    pedidoQuery.addValueEventListener(listener_pedido_aceptado);

                    break;
                case Constantes.ACTION_INICIAR_CARRERA:
                    dir_viaje = true;
                    dir_recoger = false;
                    frame_carrera_activa.setVisibility(View.VISIBLE);
                    editor = sharedPref.edit();
                    editor.putBoolean(Constantes.BD_ESTADO_MENSAJERO, true);
                    editor.putBoolean(Constantes.ACTION_INICIAR_CARRERA, dir_viaje);
                    editor.putBoolean(Constantes.ACTION_RECOGER, dir_recoger);
                    editor.apply();
                    FAB_maps.show();
                    FAB_wase.show();
                    FAB_Chat.show();
                    FAB_cancelar_servicio.show();
                    text_cancelar.setVisibility(View.VISIBLE);
                    Gson ggson = new Gson(); //Instancia Gson.
                    String jjson = sharedPref.getString(Constantes.PEDIDO, null);
                    pedido_aceptado = ggson.fromJson(jjson, Pedidos.class);
                    Log.i("INICIAR CARRERA", "pedido=" + pedido_aceptado.getNombre());
                    //aqui vamos a agregar el punto al mapa a donde el cliente va a ir
                    //en caso del que el cliente no haya puesto un punto se deja el mapa en blanco

                    if (pedido_aceptado != null) {
                        frame_carrera_activa.startAnimation(hacia_arriba);
                        layout_info.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            boton_info.setBackground(getDrawable(android.R.drawable.arrow_down_float));
                        }

                    } else {
                        Log.i("INICIAR CARRERA", "pedido nulo");
                    }
                    break;

                case Constantes.INICIAR_DE_NOTIFICACION:
                    if(getIntent().getBooleanExtra(Constantes.MENSAJE_CHAT,false)){
                        Log.i("INICIAR_DE_NOTIFICACION", "Mensaje chat true");
                        Gson gson1 = new Gson(); //Instancia Gson.
                        String json1 = sharedPref.getString(Constantes.BD_MENSAJERO, null);
                        mensajero = gson1.fromJson(json1, Mensajeros.class);
                        fragmentChat = new FragmentChat();
                        Bundle bundle = new Bundle();
                        id_pedido_aceptado = sharedPref.getString(Constantes.BD_ID_PEDIDO_ACEPTADO,null);
                        bundle.putString(Constantes.BD_NOMBRE_USUARIO,mensajero.getNombre());
                        bundle.putString(Constantes.BD_ID_PEDIDO,id_pedido_aceptado);
                        fragmentChat.setArguments(bundle);
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.main_cont, fragmentChat, FragmentChat.TAG);
                        ft.commit();
                    }
                    break;
                default:
                    //reiniciarApp(this);
                    break;
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        //ponemos en pantalla el texto de la version de la app
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = "v " + info.versionName;
            textoVersion.setText(version);
            //VerificarActualizaciones verificarActualizaciones = new VerificarActualizaciones(MainActivity.this, info.versionName);
            //verificarActualizaciones.execute();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else if (fragment_perfil.isAdded()) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.remove(fragment_perfil);
            ft.commit();
            if (map == null) {
                Log.i("onBackpresed", "mapa nulo..");
            } else {
                Log.i("onBackpresed", "mapa OK..");
            }
        } else if (fragment_balance.isAdded()) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.remove(fragment_balance);
            ft.commit();
            if (map == null) {
                Log.i("onBackpresed", "mapa nulo..");
            } else {
                Log.i("onBackpresed", "mapa OK..");
            }
        } else if (historial.isAdded()) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.remove(historial);
            ft.commit();

        }else if(fragmentChat.isAdded()){
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.remove(fragmentChat);
            ft.commit();
        } else {

            if (isConectado()) {
                toast("permancerás conectado en segundo plano");
            }
            try {
                finishAffinity();
            } catch (Exception e) {
                e.printStackTrace();
                finish();
            }
        }



    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_perfil) {
            fragment_perfil = new Fragment_perfil();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.main_cont, fragment_perfil, Fragment_perfil.TAG);
            ft.commit();

        } else if (id == R.id.nav_balance) {
            if (codigo != null) {
                Bundle bundle = new Bundle();
                bundle.putString(Constantes.BD_CODIGO_MENSAJERO, codigo);
                fragment_balance = new Fragment_balance();
                fragment_balance.setArguments(bundle);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.main_cont, fragment_balance, Fragment_balance.TAG);
                ft.commit();
            } else {
                if (id_mensajero != null) {
                    DatosMensajero(id_mensajero);
                }
            }

        } else if (id == R.id.nav_cerrar) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("¿ Seguro desea cerrar sesión ?")
                    .setCancelable(true)
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).setPositiveButton("Si", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    if (!isConectado()) {
                        mAuth.signOut();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    } else {
                        toast("Primero debes desconectarte");
                        dialogInterface.dismiss();
                    }

                }
            }).show();


        } else if (id == R.id.nav_invitar) {
            String id_user = "";
            if (mAuth.getCurrentUser() != null) {
                id_user = mAuth.getCurrentUser().getUid();
            }
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            final String URL_DESCARGA = "http://cort.as/-7d6j";
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Hola éste es mi código de referido " +
                    "de Mensajero:    " + id_user + "              descargala aqui...    " + URL_DESCARGA);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }else if(id == R.id.nav_historial){
            historial = new Historial();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.main_cont, historial, Historial.TAG);
            ft.commit();
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        miUbicacion();
        int hora = 0;
        Calendar horadeldia = Calendar.getInstance();
        hora = horadeldia.get(Calendar.HOUR_OF_DAY);
        if (hora <= 5 || hora >= 18) {
            map.setMapStyle(MapStyleOptions.loadRawResourceStyle(MainActivity.this, R.raw.stylo_noche));
        } else {
            map.setMapStyle(MapStyleOptions.loadRawResourceStyle(MainActivity.this, R.raw.stylo_gris));
        }
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {

            return;
        } else {
            map.setMyLocationEnabled(true);
        }

        map.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {

            }
        });
        map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {

            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        } else {
            map.setMyLocationEnabled(true);
            map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    mapa_movido_po_el_usuario = false;
                    miUbicacion();
                    return true;
                }
            });
        }
        map.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {

                if(i == REASON_GESTURE){
                    mapa_movido_po_el_usuario = true;
                }
            }
        });

    }

    private void activarLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!gpsEnabled) {
            Intent intentSetings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intentSetings);
        }
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
    }

    public void obtenerDireccion(Location location) {
        if (location.getLatitude() != 0.0 && location.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        location.getLatitude(), location.getLongitude(), 1
                );
                if (!list.isEmpty()) {
                    Address address = list.get(0);
                    Direccion = (address.getAddressLine(0));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void agregarMarcador(double lat, double lng) {
        LatLng coordenadas = new LatLng(lat, lng);
        if (marcador != null) marcador.remove();
        try {
            marcador = map.addMarker(new MarkerOptions()
                    .position(coordenadas)
                    .title(Direccion)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher_foreground)));
        } catch (Exception e) {
            e.printStackTrace();
            marcador = map.addMarker(new MarkerOptions()
                    .position(coordenadas)
                    .title(Direccion));
        }

    }

    private void ActualizarUbicacion(Location location) {
        if (location != null) {
            location.setAccuracy(3);
            if (SERVICIO_ACTIVO) {
                lat_actualzada = location.getLatitude();
                lng_actualizada = location.getLongitude();
                pocisionFinal = new LatLng(lat_actualzada, lng_actualizada);
                //agregarMarcador(lat_actualzada, lng_actualizada);
                pocisionInicial = new LatLng(lat_actualzada, lng_actualizada);
                if (map != null && !mapa_movido_po_el_usuario) {
                    PosicionCamara(map, location);
                }
            } else {
                lat = location.getLatitude();
                lng = location.getLongitude();
                pocisionInicial = new LatLng(lat, lng);
                LatLng coordenadas = new LatLng(lat, lng);
                CameraUpdate ubicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 17);
                if (map != null && !mapa_movido_po_el_usuario) {
                    PosicionCamara(map, location);
                }
            }

        }
    }
    private void PosicionCamara(GoogleMap map, Location location){
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(),location.getLongitude()))
                .zoom(19)// Sets the orientation of the camera to east
                .bearing(location.getBearing())
                .tilt(90)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            // ComprobarConexion(MainActivity.this);
            ActualizarUbicacion(location);
            //obtenerDireccion(location);

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {
            String mensaje = "GPS Desactivado";
            activarLocation();
            toast(mensaje);
        }
    };

    private void miUbicacion() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            int PEDIR_PERMISO_LOCATION = 101;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PEDIR_PERMISO_LOCATION);
            return;
        } else {
            //comprovamos el estado del gps
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                AlertaNoGps();
            } else {
                //permiso de localizacion

                try {
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location == null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }
                    if (location == null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                    pocisionInicial = new LatLng(location.getLatitude(), location.getLongitude());
                    ActualizarUbicacion(location);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            2000, 10, locationListener);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    toast("Error de ubicacion, reinicie la app");
                    //reiniciarApp(this,null);
                }

            }

        }

    }

    public void toast(String mensaje) {
        Toast toast = Toast.makeText(this, mensaje, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void DatosMensajero(final String id_mensajero) {
        //solicitamos token del mensjaero
        final AlertDialog builder = new AlertDialog.Builder(this).create();
        builder.setTitle("Cargando...");
        builder.setMessage("Si ésto tarda más de 10 segundos por favor verifica tu conexión a internet y vuelve a abrir la app");
        builder.setCancelable(false);
        builder.show();

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        token = instanceIdResult.getToken();
                    }
                });
        final Query query = database.child(Constantes.BD_MENSAJERO).child(id_mensajero);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Mensajeros.class) != null) {
                    Mensajeros mensajero_prov = dataSnapshot.getValue(Mensajeros.class);
                    String id = "nulo";

                    try {
                        id = mensajero_prov.getId_mensajero();
                        Log.i("id mensajero", id);
                    } catch (NullPointerException e) {
                        id = "nulo";
                    }
                    mensajero = mensajero_prov;
                    Log.i("mensjaero",mensajero_prov.toString());
                    codigo = mensajero_prov.getCodigo();
                    placa = mensajero_prov.getPlaca();
                    estado_mensajero = mensajero_prov.getEstado();
                    TextView nombre = findViewById(R.id.texto_nombre);
                    TextView correo = findViewById(R.id.texto_correo);
                    ImageView imagen_perfil = findViewById(R.id.imagen_perfil);
                    nombre.setText(mensajero.getNombre());
                    correo.setText(mensajero.getEmail());
                    String pathFoto = Constantes.URL_FOTO_PERFIL_CONDUCTOR + mensajero.getCodigo();

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageReference = storage.getReference().child(pathFoto + "/foto_perfil");


                    Glide.with(MainActivity.this)
                            .using(new FirebaseImageLoader())
                            .load(storageReference)
                            .into(imagen_perfil);


                    try {
                        mensajero.setToken(token);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //guardamos los datos del mensajero en las preferencias
                    Gson gson = new Gson();
                    String json = gson.toJson(mensajero);//convertimos el objeto a json
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(Constantes.BD_MENSAJERO, json);
                    editor.apply();
                    builder.dismiss();

                    IniciarOSolicitarCompletar(estado_mensajero);
                    Log.i("estado mensajero", estado_mensajero);
                }else{
                    toast("Hay un problema con tu cuenta, contacta a soporte");
                    builder.dismiss();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                toast("verifica tu conexión a internet");
                builder.dismiss();
                aSwitchEstado.setEnabled(false);
            }
        });
    }

    public static void reiniciarApp(Activity actividad, @Nullable Boolean cancelar) {
        Log.i("reiniciando", actividad.getLocalClassName());
        Intent intent = new Intent(actividad, MainActivity.class);
        intent.setAction(Constantes.ACTION_CONECTAR);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (cancelar != null) {
            intent.putExtra(Constantes.ESTADO_CANCELADO, cancelar);

        }
        //finalizamos la actividad actual
        try {
            actividad.finishAffinity();
        } catch (Exception e) {
            e.printStackTrace();
            actividad.finish();
        }
        //llamamos a la actividad
        actividad.startActivity(intent);
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


    @Override
    protected void onStart() {
        super.onStart();
        if (fragmentBloqueado.isAdded() || documentosFragm.isAdded()) {
            if (isConectado()) aSwitchEstado.setChecked(false);
            aSwitchEstado.setVisibility(View.INVISIBLE);
            toolbar.setVisibility(View.INVISIBLE);
        } else {
            aSwitchEstado.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.VISIBLE);
            toolbar.setTitle("");
        }
            if (isMyServiceRunning(ServicioEstadoConectado.class)) {
                setConectado(true);
                ETAgregarDestino.setVisibility(View.INVISIBLE);
                texto_num_cliente.setText("Teléfono: ");
                texto_dir_cliente.setText("Dirección: ");
                texto_nom_cliente.setText("Nombre: ");
                textoDistancia.setText("Distancia Recorrida: ");
                textoValor.setText("Valor: ");
            } else{
                setConectado(sharedPref.getBoolean(Constantes.BD_ESTADO_MENSAJERO, false));

            }


        ActionCalcularViaje = false;

        codigo = sharedPref.getString(Constantes.BD_CODIGO, null);
        SERVICIO_ACTIVO = sharedPref.getBoolean(Constantes.SERVICIO_ACTIVO, false);
        id_pedido_aceptado = sharedPref.getString(Constantes.BD_ID_PEDIDO_ACEPTADO, null);
        if (!dir_viaje) {
            dir_viaje = sharedPref.getBoolean(Constantes.ACTION_INICIAR_CARRERA, false);
        }
        if (!dir_recoger) {
            dir_recoger = sharedPref.getBoolean(Constantes.ACTION_RECOGER, false);
        }
        EstaConectado(isConectado());

        if (id_pedido_aceptado != null) {
            layout_pendientes.setVisibility(View.INVISIBLE);
            Log.i("OnStart ", id_pedido_aceptado);
            pedidoQuery = database.child(Constantes.PEDIDO).child(id_pedido_aceptado);
            pedidoQuery.addValueEventListener(listener_pedido_aceptado);
            }else{
            Log.i("OnStart ", "pedido nulo");
        }

        sessionId = UUID.randomUUID().toString();

    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.i("on stop()", "dir_recoger = " + dir_recoger);
        Log.i("OnStop()", "dir_viaje= " + dir_viaje);
        if (dir_viaje) dir_recoger = false;
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constantes.BD_ID_PEDIDO_ACEPTADO, id_pedido_aceptado);
        //editor.putBoolean(Constantes.BD_ESTADO_MENSAJERO, Conectado);
        editor.putBoolean(Constantes.SERVICIO_ACTIVO, SERVICIO_ACTIVO);
        editor.putString(Constantes.BD_CODIGO, codigo);
        editor.putBoolean(Constantes.ACTION_INICIAR_CARRERA, dir_viaje);
        editor.putBoolean(Constantes.ACTION_RECOGER, dir_recoger);
        editor.apply();
        cronometro.stop();

        if (pedidoQuery != null && listener_pedido_aceptado != null) {
            pedidoQuery.removeEventListener(listener_pedido_aceptado);
        }

        if (servicioEnlazado) {
            unbindService(mServiceConnection);
            servicioEnlazado = false;
        }
    }

    public String getSessionId() {
        return sessionId;
    }

    @Override
    protected void onDestroy() {
        // toast("on destoy");
        Log.i("On Destroy()", "dir_recoger= " + dir_recoger);
        Log.i("OnDestroy()", "dir_viaje= " + dir_viaje);
        if (dir_viaje) dir_recoger = false;
        if (sharedPref != null) {
            SharedPreferences.Editor editor = sharedPref.edit();
            //editor.putBoolean(Constantes.BD_ESTADO_MENSAJERO, Conectado);
            editor.putBoolean(Constantes.SERVICIO_ACTIVO, SERVICIO_ACTIVO);
            editor.putString(Constantes.BD_CODIGO, codigo);
            editor.putBoolean(Constantes.ACTION_INICIAR_CARRERA, dir_viaje);
            editor.putBoolean(Constantes.ACTION_RECOGER, dir_recoger);
            editor.apply();
        }

        super.onDestroy();

    }

    @Override
    protected void onResume() {
        setConectado(isConectado());

        receptorMensajesServidor = new ReceptorMensajesServidor();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constantes.ACTION_CONFIRMAR_VALOR_VIAJE);
        intentFilter.addAction(Constantes.ACTION_MENSAJE_CHAT);
        intentFilter.addAction(Constantes.ACTION_ACTUALIZAR);
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        intentFilter.addAction(Intent.ACTION_MEDIA_BUTTON);
        registerReceiver(receptorMensajesServidor, intentFilter);

        Gson gson = new Gson(); //Instancia Gson.
        String json = sharedPref.getString(Constantes.BD_MENSAJERO, null);
        mensajero = gson.fromJson(json, Mensajeros.class);

        if (documentosFragm != null) {
            if (documentosFragm.isAdded()) {
                aSwitchEstado.setVisibility(View.INVISIBLE);
            }
        }
        if (fragmentBloqueado != null) {
            if (fragmentBloqueado.isAdded()) {
                aSwitchEstado.setVisibility(View.INVISIBLE);
            }
        }

        super.onResume();
    }

    @Override
    protected void onPause() {
        // toast("on pause");
        if (isConectado()) {

            cronometro.stop();
            query_pedidos_pendientes.removeEventListener(listener_pedidos_pendientes);
        }

        try {
            //guardamos los datos del mensajero en las preferencias
            Gson gson = new Gson();
            String json = gson.toJson(mensajero);//convertimos el objeto a json
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(Constantes.BD_MENSAJERO, json);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (receptorMensajesServidor != null) {
            unregisterReceiver(receptorMensajesServidor);
        }
        super.onPause();
    }

    private void configureJobManager() {
        Configuration configuration = new Configuration.Builder(this)
                .customLogger(new CustomLogger() {
                    private static final String TAG = "JOBS";

                    @Override
                    public boolean isDebugEnabled() {
                        return true;
                    }

                    @Override
                    public void d(String text, Object... args) {
                        Log.d(TAG, String.format(text, args));
                    }

                    @Override
                    public void e(Throwable t, String text, Object... args) {
                        Log.e(TAG, String.format(text, args), t);
                    }

                    @Override
                    public void e(String text, Object... args) {
                        Log.e(TAG, String.format(text, args));
                    }

                    @Override
                    public void v(String text, Object... args) {

                    }
                })
                .maxConsumerCount(1)//up to 3 consumers at a time
                .loadFactor(1)//3 jobs per consumer
                .consumerKeepAlive(60)//wait 2 minute
                .build();
        jobManager = new JobManager(configuration);
    }


    public void toastrecibido(Context context, String mensaje) {
        Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show();
    }

    private void IniciarOSolicitarCompletar(String estado) {

        switch (estado) {
            case Constantes.ESTADO_ACTIVO:
                Log.i(Constantes.BD_ESTADO_MENSAJERO, Constantes.ESTADO_ACTIVO);
                aSwitchEstado.setVisibility(View.VISIBLE);
                aSwitchEstado.setEnabled(true);
                toolbar.setVisibility(View.VISIBLE);
                break;
            case Constantes.ESTADO_BLOQUEADO:
                try {
                    Log.i(Constantes.BD_ESTADO_MENSAJERO, Constantes.ESTADO_BLOQUEADO);
                    ETAgregarDestino.setVisibility(View.INVISIBLE);
                    aSwitchEstado.setVisibility(View.INVISIBLE);
                    aSwitchEstado.setEnabled(false);
                    toolbar.setVisibility(View.INVISIBLE);
                    drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
                        @Override
                        public void onDrawerSlide(View drawerView, float slideOffset) {
                            drawer.closeDrawer(GravityCompat.START);
                        }

                        @Override
                        public void onDrawerOpened(View drawerView) {

                        }

                        @Override
                        public void onDrawerClosed(View drawerView) {

                        }

                        @Override
                        public void onDrawerStateChanged(int newState) {

                        }
                    });
                    fragmentBloqueado = new FragmentBloqueado();
                    Bundle bundle = new Bundle();
                    bundle.putString(Constantes.BD_ESTADO_MENSAJERO,Constantes.ESTADO_BLOQUEADO);
                    fragmentBloqueado.setArguments(bundle);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.main_cont, fragmentBloqueado, FragmentBloqueado.TAG);
                    ft.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                    reiniciarApp(this, null);
                }
                break;
            case Constantes.ESTADO_SUBIR_IMAGENES:
                toolbar.setVisibility(View.INVISIBLE);
                drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        drawer.closeDrawer(GravityCompat.START);
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {

                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {

                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {

                    }
                });
                Log.i(Constantes.BD_ESTADO_MENSAJERO, Constantes.ESTADO_SUBIR_IMAGENES);
                if (codigo != null) {

                    try {
                        documentosFragm = new EnviarDocumentosFragm(codigo);
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.main_cont, documentosFragm, EnviarDocumentosFragm.TAG);
                        ft.commit();
                        aSwitchEstado.setVisibility(View.INVISIBLE);
                        aSwitchEstado.setEnabled(false);
                        ETAgregarDestino.setVisibility(View.INVISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                        reiniciarApp(this, null);
                    }
                } else {
                    finish();
                }

                break;
            case Constantes.ESTADO_VERIFICAR:
                try {
                    Log.i(Constantes.BD_ESTADO_MENSAJERO, Constantes.ESTADO_VERIFICAR);
                    fragmentBloqueado = new FragmentBloqueado();
                    Bundle bundle = new Bundle();
                    bundle.putString(Constantes.BD_ESTADO_MENSAJERO,Constantes.ESTADO_VERIFICAR);
                    fragmentBloqueado.setArguments(bundle);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.main, fragmentBloqueado, FragmentBloqueado.TAG);
                    ft.commit();
                    ETAgregarDestino.setVisibility(View.INVISIBLE);
                    aSwitchEstado.setVisibility(View.INVISIBLE);
                    aSwitchEstado.setEnabled(false);
                } catch (Exception e) {
                    e.printStackTrace();
                    reiniciarApp(this, null);
                }
                break;
            case Constantes.ESTADO_VERIFICADO:
                try {
                    Log.i(Constantes.BD_ESTADO_MENSAJERO, Constantes.ESTADO_VERIFICADO);
                    fragmentBloqueado = new FragmentBloqueado();
                    Bundle bundle = new Bundle();
                    bundle.putString(Constantes.BD_ESTADO_MENSAJERO,Constantes.ESTADO_VERIFICADO);
                    fragmentBloqueado.setArguments(bundle);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.main, fragmentBloqueado, FragmentBloqueado.TAG);
                    ft.commit();
                    ETAgregarDestino.setVisibility(View.INVISIBLE);
                    aSwitchEstado.setVisibility(View.INVISIBLE);
                    aSwitchEstado.setEnabled(false);
                } catch (Exception e) {
                    e.printStackTrace();
                    reiniciarApp(this, null);
                }
                break;
            default:

                break;
        }

    }

    public void EstaConectado(boolean conectado) {
        if (conectado) {
            aSwitchEstado.setChecked(true);
        } else {
            aSwitchEstado.setChecked(false);
        }
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }

    public boolean isConectado() {
        return Conectado;
    }

    public void setConectado(boolean conectado) {
        Conectado = conectado;
        if(conectado){
            query_pedidos_pendientes.addValueEventListener(listener_pedidos_pendientes);
        }else{
            layout_pendientes.setVisibility(View.INVISIBLE);
            query_pedidos_pendientes.removeEventListener(listener_pedidos_pendientes);
        }
    }

    public boolean DatosActivos() {

        //https://developer.android.com/training/monitoring-device-state/connectivity-monitoring?hl=es-419

        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo actNetInfo = connectivityManager.getActiveNetworkInfo();

        return (actNetInfo != null && actNetInfo.isConnected());
    }


    public Boolean HayConexionInternet() {

        try {
            Process p = Runtime.getRuntime().exec("ping -c 1 www.google.es");

            int val = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public void ComprobarConexion(Context context) {
        if (!DatosActivos()) {
            AlertaNoInternet();
        } else if (!HayConexionInternet()) {
            Log.i("sininternet", "sin conexion a internet");
        }
    }

    private void AlertaNoInternet() {

        AlertDialog alert;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("El acceso a internet está desactivado y es necesario para el uso de la aplication. ¿Desea activarlo?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });

        alert = builder.create();
        alert.show();

    }

    private void AlertaNoGps() {

        AlertDialog alert;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Es necesario activar el GPS y/o configurarlo en modo de alta precisión " +
                "con Wi-fi y Redes móviles. ¿Desea activarlo?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });

        alert = builder.create();
        alert.show();
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id) {
            case R.id.btiniciarcarrera:
                if (isConectado()) {
                    String texto_boton = BotonIniciarCarrera.getText().toString();

                    switch (texto_boton) {
                        case Constantes.INICIAR_SERVICIO:
                            dir_recoger = false;
                            FAB_cancelar_servicio.hide();
                            text_cancelar.setVisibility(View.INVISIBLE);
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setMessage("¿Iniciar Servicio?")
                                    .setCancelable(false)
                                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                try {
                                                    if (pedido_aceptado.getTipo_pedido().equals(Constantes.FACTURAS_TRAMITES) || pedido_aceptado.getTipo_pedido().equals(Constantes.SOLICITUD_RAPIDA)) {
                                                        if (isOnline(MainActivity.this)) {
                                                            currentUserDB = database.child(Constantes.PEDIDO).child(pedido_aceptado.getId_pedido());
                                                            currentUserDB.child(Constantes.BD_ESTADO_PEDIDO).setValue(Constantes.ESTADO_SERVICIO_INICIADO)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            toast("viaje iniciado correctamente");
                                                                            Intent IntentCarreraNueva = new Intent(MainActivity.this, ServicioCarrera.class);
                                                                            IntentCarreraNueva.putExtra("lat_ini", lat);
                                                                            IntentCarreraNueva.putExtra("log_ini", lng);
                                                                            IntentCarreraNueva.setAction(Constantes.ACTION_INICIAR_CARRERA);

                                                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                                                startForegroundService(IntentCarreraNueva);
                                                                            } else {
                                                                                startService(IntentCarreraNueva);
                                                                            }
                                                                            bindService(IntentCarreraNueva, mServiceConnection, Context.BIND_AUTO_CREATE);
                                                                            SERVICIO_ACTIVO = true;
                                                                            BotonIniciarCarrera.setText(Constantes.TERMINAR_SERVICIO);
                                                                            if (map != null) {
                                                                                map.clear();
                                                                            }
                                                                        }
                                                                    });
                                                        } else {
                                                            toast("Verifique su conexión a internet e intente nuevamente");
                                                        }
                                                    } else {
                                                        currentUserDB = database.child(Constantes.PEDIDO).child(pedido_aceptado.getId_pedido());
                                                        String estado = Constantes.ESTADO_SERVICIO_INICIADO;
                                                        if(pedido_aceptado.getTipo_pedido().equals(Constantes.ENCOMIENDAS)){
                                                            estado = Constantes.ESTADO_PAQUETE_RECOGIDO;
                                                        }else if(pedido_aceptado.getTipo_pedido().equals(Constantes.COMPRAS_PEDIR_DOMICILIOS)){
                                                            estado = Constantes.ESTADO_MENSAJERO_EN_TIENDA;
                                                        }
                                                        currentUserDB.child(Constantes.BD_ESTADO_PEDIDO).setValue(estado).addOnCompleteListener(
                                                                new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                        if(task.isSuccessful()){
                                                                            toast("viaje iniciado correctamente");
                                                                            Intent IntentCarreraNueva = new Intent(MainActivity.this, ServicioCarrera.class);
                                                                            IntentCarreraNueva.putExtra("lat_ini", lat);
                                                                            IntentCarreraNueva.putExtra("log_ini", lng);
                                                                            IntentCarreraNueva.setAction(Constantes.ACTION_INICIAR_CARRERA);
                                                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                                                startForegroundService(IntentCarreraNueva);
                                                                            } else {
                                                                                startService(IntentCarreraNueva);
                                                                            }
                                                                            bindService(IntentCarreraNueva, mServiceConnection, Context.BIND_AUTO_CREATE);
                                                                            SERVICIO_ACTIVO = true;
                                                                            BotonIniciarCarrera.setText(Constantes.TERMINAR_SERVICIO);
                                                                            if (map != null) {
                                                                                map.clear();
                                                                            }
                                                                        }else{
                                                                            toast("ocurrió un error, intente nuevamente");
                                                                        }

                                                                    }
                                                                }
                                                        );

                                                    }

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    toast("Ha ocurrido un error, intente más tarde");
                                                }
                                        }
                                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();

                            break;
                        case Constantes.TERMINAR_SERVICIO:

                            String mensaje = "¿Terminar Servicio?";
                            if(!pedido_aceptado.getTipo_pedido().equals(Constantes.SOLICITUD_RAPIDA))mensaje = "¿Terminar Servicio? $"+valor;
                                AlertDialog.Builder builderTerm = new AlertDialog.Builder(MainActivity.this);
                                builderTerm.setMessage(mensaje)
                                        .setCancelable(false)
                                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                if (isOnline(MainActivity.this)) {
                                                    if (!pedido_aceptado.isServicio_empresa() && pedido_aceptado.getId_usuario() != null) {
                                                        try {
                                                        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                                                        View view = inflater.inflate(R.layout.layout_calculando, null);
                                                        dialog_calculando.setContentView(view);
                                                        dialog_calculando.setCancelable(false);
                                                        dialog_calculando.show();
                                                        //distancia en metros
                                                        Double dist = servicioCarrera.getDistancia();
                                                        //tiempo den minutos
                                                        long tiempo = servicioCarrera.getTiempoTranscurrido();
                                                        Log.i("terminar viaje", "tiempo " + tiempo + "distancia " + dist);
                                                        final String id_pedido = pedido_aceptado.getId_pedido();
                                                        String codigo_mensajero = mensajero.getCodigo();
                                                        String id_usuario = pedido_aceptado.getId_usuario();

                                                        TerminarViaje(tiempo, dist, id_pedido, codigo_mensajero, id_usuario, pedido_aceptado.getTipo_pedido())
                                                                .addOnCompleteListener(new OnCompleteListener<String>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<String> task) {
                                                                        if (task.isSuccessful()) {
                                                                            Log.i("terminar viaje", "OK");
                                                                            toast("Servicio terminado exitosamente");
                                                                            servicioEnlazado = false;
                                                                            SERVICIO_ACTIVO = false;
                                                                            cronometro.stop();
                                                                            cronometro.setBase(SystemClock.elapsedRealtime());
                                                                            textoDistancia.setText("");
                                                                            BotonIniciarCarrera.setText(Constantes.INICIAR_SERVICIO);
                                                                            frame_carrera_activa.setVisibility(View.INVISIBLE);
                                                                            texto_num_cliente.setText("Teléfono: ");
                                                                            texto_dir_cliente.setText("Dirección: ");
                                                                            texto_nom_cliente.setText("Nombre: ");
                                                                            textoDistancia.setText("Distancia Recorrida: ");
                                                                            texto_comentario_iniciar.setText("");
                                                                            textoValor.setText("Valor: ");
                                                                            map.clear();
                                                                            points = null;
                                                                            FAB_maps.hide();
                                                                            FAB_wase.hide();
                                                                            FAB_cancelar_servicio.hide();
                                                                            text_cancelar.setVisibility(View.INVISIBLE);
                                                                            dir_viaje = false;
                                                                            dir_recoger = false;
                                                                            SharedPreferences.Editor editor = sharedPref.edit();
                                                                            editor.putBoolean(Constantes.ACTION_INICIAR_CARRERA, dir_viaje);
                                                                            editor.putBoolean(Constantes.SERVICIO_ACTIVO, SERVICIO_ACTIVO);
                                                                            editor.putBoolean(Constantes.ACTION_RECOGER, dir_recoger);
                                                                            editor.apply();
                                                                            Intent IntentTerminarCarrera = new Intent(MainActivity.this, ServicioCarrera.class);
                                                                            stopService(IntentTerminarCarrera);
                                                                            unbindService(mServiceConnection);
                                                                        } else {
                                                                            if (task.getException()!=null) {
                                                                                Log.i("terminar viaje", task.getException().getLocalizedMessage());
                                                                            }
                                                                            toast("Intenta nuevamente");
                                                                            dialog_calculando.dismiss();
                                                                            //
                                                                        }
                                                                    }
                                                                });

                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                        toast("Ha ocurrido un error, intente nuevamente");
                                                        dialog_calculando.dismiss();
                                                    }
                                                        pedido_aceptado = null;
                                                        try {
                                                            sharedPref.edit().remove(Constantes.PEDIDO).apply();
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    } else {
                                                        DatabaseReference data = FirebaseDatabase.getInstance().getReference()
                                                                .child(Constantes.BD_GERENTE).child(Constantes.BD_ADMIN)
                                                                .child(Constantes.PEDIDO).child(pedido_aceptado.getId_pedido());

                                                        if(!pedido_aceptado.getTipo_pedido().equals(Constantes.SOLICITUD_RAPIDA)){
                                                            data.child(Constantes.BD_VALOR_PEDIDO).setValue(valor);
                                                        }
                                                            data.child(Constantes.BD_ESTADO_PEDIDO).setValue(Constantes.ESTADO_TERMINADO)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if(task.isSuccessful()){
                                                                                toast("Servicio terminado exitosamente");
                                                                                servicioEnlazado = false;
                                                                                SERVICIO_ACTIVO = false;
                                                                                cronometro.stop();
                                                                                cronometro.setBase(SystemClock.elapsedRealtime());
                                                                                textoDistancia.setText("");
                                                                                BotonIniciarCarrera.setText(Constantes.INICIAR_SERVICIO);
                                                                                frame_carrera_activa.setVisibility(View.INVISIBLE);
                                                                                texto_num_cliente.setText("Teléfono: ");
                                                                                texto_dir_cliente.setText("Dirección: ");
                                                                                texto_nom_cliente.setText("Nombre: ");
                                                                                textoDistancia.setText("Distancia Recorrida: ");
                                                                                texto_comentario_iniciar.setText("");
                                                                                textoValor.setText("Valor: ");
                                                                                map.clear();
                                                                                points = null;
                                                                                FAB_maps.hide();
                                                                                FAB_wase.hide();
                                                                                FAB_cancelar_servicio.hide();
                                                                                text_cancelar.setVisibility(View.INVISIBLE);
                                                                                dir_viaje = false;
                                                                                dir_recoger = false;
                                                                                SharedPreferences.Editor editor = sharedPref.edit();
                                                                                editor.putBoolean(Constantes.ACTION_INICIAR_CARRERA, dir_viaje);
                                                                                editor.putBoolean(Constantes.SERVICIO_ACTIVO, SERVICIO_ACTIVO);
                                                                                editor.putBoolean(Constantes.ACTION_RECOGER, dir_recoger);
                                                                                editor.apply();
                                                                                Intent IntentTerminarCarrera = new Intent(MainActivity.this, ServicioCarrera.class);
                                                                                stopService(IntentTerminarCarrera);
                                                                                unbindService(mServiceConnection);
                                                                            }else{
                                                                                toast("intente nuevamente");
                                                                            }
                                                                        }
                                                                    });

                                                    }
                                                } else {
                                                    toast("verifique su conexión a internet e intente nuevamente");
                                                }


                                            }
                                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).show();

                            break;
                        case Constantes.AGREGAR_VALOR_SERVICIO:

                            //creando el dialogo confirmacion
                            final Dialog dialogo_valor_servicio =  new Dialog(this, R.style.Theme_AppCompat_DialogWhenLarge);
                            dialogo_valor_servicio.setCancelable(true);
                            LayoutInflater inflater = this.getLayoutInflater();
                            View view = inflater.inflate(R.layout.agregar_precio_servicio, null);
                            dialogo_valor_servicio.setContentView(view);
                            Button confirmar = view.findViewById(R.id.buttonConfirmarValor);
                            final EditText texto_valor_servicio = view.findViewById(R.id.editTextValorViaje);

                            confirmar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(texto_valor_servicio.getText().toString().length()>=4){
                                        if(!texto_valor_servicio.getText().toString().contains(".")&& !texto_valor_servicio.getText().toString().contains(",")){
                                            int valor_pedido = Integer.valueOf(texto_valor_servicio.getText().toString());
                                            DatabaseReference data = FirebaseDatabase.getInstance().getReference()
                                                    .child(Constantes.BD_GERENTE).child(Constantes.BD_ADMIN)
                                                    .child(Constantes.PEDIDO).child(pedido_aceptado.getId_pedido())
                                                    .child(Constantes.BD_VALOR_PEDIDO);
                                            data.setValue(valor_pedido).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        BotonIniciarCarrera.setText(Constantes.INICIAR_SERVICIO);
                                                        dialogo_valor_servicio.dismiss();
                                                        toast("agregado correctamente");
                                                        dir_recoger = false;
                                                        dir_viaje = true;
                                                    }else{
                                                        toast("intenta nuevamente");
                                                    }
                                                }
                                            });
                                        }else{
                                            toast("No incluya comas ni puntos");
                                        }

                                    }else{
                                        toast("escriba un valor");
                                    }
                                }
                            });
                            dialogo_valor_servicio.show();
                            break;
                    }
                } else {
                    toast("primero debes conectarte");
                }
                break;
            case R.id.boton_info:

                if (layout_info.getHeight() == 0) {

                    frame_carrera_activa.startAnimation(hacia_arriba);
                    layout_info.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        boton_info.setBackground(getDrawable(android.R.drawable.arrow_down_float));
                    }
                } else {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        boton_info.setBackground(getDrawable(android.R.drawable.arrow_up_float));
                    }
                    frame_carrera_activa.startAnimation(hacia_abajo);
                    layout_info.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
                }
                break;
            case R.id.floating_maps:
                if (pocisionFinal != null) {
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + pocisionFinal.latitude + "," + pocisionFinal.longitude);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(mapIntent);
                    } else {
                        toast("No tienes instalada la app");
                    }
                }
                break;
            case R.id.floating_waze:
                if (pocisionFinal != null) {
                    Uri waseuri = Uri.parse("waze://?ll=" + pocisionFinal.latitude + "," + pocisionFinal.longitude + "&navigate=yes");
                    Intent waseintent = new Intent(Intent.ACTION_VIEW, waseuri);
                    if (waseintent.resolveActivity(getPackageManager()) != null) {
                        startActivity(waseintent);
                    } else {
                        toast("No tienes instalada la app");
                    }
                }
                break;
            case R.id.fabChat:
                fragmentChat = new FragmentChat();
                Bundle bundle = new Bundle();
                bundle.putString(Constantes.BD_NOMBRE_USUARIO,mensajero.getNombre());
                bundle.putString(Constantes.BD_ID_PEDIDO,pedido_aceptado.getId_pedido());
                fragmentChat.setArguments(bundle);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.main_cont, fragmentChat, FragmentChat.TAG);
                ft.commit();
                break;
            case R.id.ETAgregarDestino:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(ETAgregarDestino.getWindowToken(), 0);
                if (isConectado()) {
                    try {

                        if (Popayan == null) {
                            //cargar el cuadrante de popayan
                            Popayan = new LatLngBounds.Builder().
                                    include(new LatLng(2.419813, -76.659250))
                                    .include(new LatLng(2.494589, -76.556597)).build();
                        }

                        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
                                .build();
                        Intent intent = new PlaceAutocomplete
                                .IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                .setFilter(typeFilter)
                                .setBoundsBias(Popayan)
                                .build(this);
                        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                    } catch (GooglePlayServicesRepairableException e) {
                        // TODO: Handle the error.
                    } catch (GooglePlayServicesNotAvailableException e) {
                        // TODO: Handle the error.
                    }
                } else {
                    toast("No estás conectado");
                }
                break;
            case R.id.texto_tel_cliente:
                Log.i("Click telefono", "click ");
                try {
                    if (pedido_aceptado != null) {
                        Intent llamar = new Intent(Intent.ACTION_DIAL);
                        llamar.setData(Uri.parse("tel:" + pedido_aceptado.getTelefono()));
                        startActivity(llamar);
                    } else {
                        toast("Telefono vacío");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    toast("Telefono vacío");
                }
                break;
            case R.id.num_recoger_pasajero:
                Log.i("Click telefono", "click ");
                if (pedido_aceptado == null) {
                    if (id_pedido_aceptado != null) {
                        pedidoQuery = database.child(Constantes.PEDIDO).child(id_pedido_aceptado);
                        pedidoQuery.addValueEventListener(listener_pedido_aceptado);

                    }
                }
                    if (pedido_aceptado.getTelefono() != null) {
                        Intent llamar = new Intent(Intent.ACTION_DIAL);
                        llamar.setData(Uri.parse("tel:" + pedido_aceptado.getTelefono()));
                        startActivity(llamar);
                    } else {
                        toast("Ha ocurrido un error, intente más tarde");
                    }

                break;
            case R.id.floating_cancelar_servicio:
                AlertDialog.Builder builderTerm = new AlertDialog.Builder(MainActivity.this);
                builderTerm.setMessage("¿Seguro desea liberar el servicio?")
                        .setCancelable(false)
                        .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try {
                                    Log.i("click en si", "click");
                                    currentUserDB = database.child(Constantes.PEDIDO).child(pedido_aceptado.getId_pedido());
                                    currentUserDB.child(Constantes.BD_CODIGO_MENSAJERO).setValue(Constantes.CODIGO_CENTRAL_MENSAJERO);
                                    stopService(new Intent(MainActivity.this, ServicioCarrera.class));
                                    id_pedido_aceptado = null;
                                    pedido_aceptado = null;
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putString(Constantes.BD_ID_PEDIDO_ACEPTADO, id_pedido_aceptado);
                                    editor.apply();
                                    stopService(new Intent(MainActivity.this, ServicioCarrera.class));
                                    reiniciarApp(MainActivity.this, true);
                                    if (pedidoQuery!=null ){
                                        pedidoQuery.removeEventListener(listener_pedido_aceptado);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    toast("Ha ocurrido un error intente nuevamente");
                                    dialogInterface.dismiss();

                                }

                            }
                        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Log.i("click en no", "click");
                    }
                }).show();
                break;
            case R.id.floating_avisar_llegue:
                if (pedido_aceptado != null) {
                    DatabaseReference estadodata = database.child(Constantes.PEDIDO).child(id_pedido_aceptado)
                            .child(Constantes.BD_ESTADO_PEDIDO);
                    switch (text_avisar.getText().toString()){
                        case Constantes.MENSAJERO_EN_TIENDA:

                            estadodata.setValue(Constantes.ESTADO_MENSAJERO_EN_TIENDA);
                            break;
                        case Constantes.AVISA_QUE_LLEGASTE:
                            if (pedido_aceptado.getToken() != null) {
                                ChatServicio(Constantes.CONFIRMAR_LLEGADA, pedido_aceptado.getToken());
                            }
                            stopService(new Intent(MainActivity.this, ServicioCarrera.class));
                            SharedPreferences.Editor editor = sharedPref.edit();
                            dir_viaje = true;
                            dir_recoger = false;
                            editor.putBoolean(Constantes.ACTION_INICIAR_CARRERA, dir_viaje);
                            editor.putBoolean(Constantes.ACTION_RECOGER, dir_recoger);
                            editor.putString(Constantes.BD_ID_PEDIDO_ACEPTADO, id_pedido_aceptado);
                            editor.apply();

                            reiniciarApp(MainActivity.this, null);

                            break;
                        case Constantes.COMPRA_REALIZADA:

                            estadodata.setValue(Constantes.ESTADO_COMPRA_REALIZADA);
                            break;
                        case Constantes.PAQUETE_RECOGIDO:

                            estadodata.setValue(Constantes.ESTADO_PAQUETE_RECOGIDO);
                            break;
                        case Constantes.PAQUETE_ENTREGADO:

                            estadodata.setValue(Constantes.ESTADO_PAQUETE_ENTREGADO);
                            break;
                        default:
                            toast("revisar");
                            break;
                    }

                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(Constantes.BD_ID_PEDIDO_ACEPTADO, pedido_aceptado.getId_pedido());
                    editor.apply();

                }else{
                    FAB_avisar_llegada.hide();
                }

                break;

        }
    }

    private String obtenerDireccionesURL(LatLng origin, LatLng dest) {

        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        String sensor = "sensor=false";

        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters+ "&key="+
                getResources().getString(R.string.google_maps_key_PROD);

        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creamos una conexion http
            urlConnection = (HttpURLConnection) url.openConnection();

            // Conectamos
            urlConnection.connect();

            // Leemos desde URL
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
            Log.i("error de Conexion", "sin internet");

        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //some code....
                break;
            case MotionEvent.ACTION_UP:
                view.performClick();
                break;
            case MotionEvent.ACTION_MOVE:
                float orientacion = motionEvent.getOrientation();

                break;
            default:
                break;
        }
        return true;
    }


    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("ERRORALOBTENERINFODELWS", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            parserTask.execute(result);

        }
    }

    public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            if (result != null) {
                points = null;

                lineOptions = new PolylineOptions();

                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<LatLng>();

                    List<HashMap<String, String>> path = result.get(i);

                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        if (j == 0) {
                            pocisionInicial = new LatLng(lat, lng);
                        } else if (j == path.size() - 1) {
                            pocisionFinal = new LatLng(lat, lng);
                        }

                        points.add(position);

                    }

                    lineOptions.addAll(points);
                    lineOptions.width(15);
                    lineOptions.jointType(JointType.ROUND);
                    lineOptions.startCap(new RoundCap());
                    lineOptions.endCap(new RoundCap());
                    lineOptions.color(Color.rgb(41, 135, 189));

                    if (frame_recoger_pasajero.getVisibility() == View.INVISIBLE) {
                        FAB_maps.show();
                        FAB_wase.show();
                    }
                }
                if (lineOptions != null && map != null) {
                    map.clear();
                    map.addPolyline(lineOptions);
                    LatLngBounds rutazoom = new LatLngBounds.Builder()
                            .include(pocisionInicial)
                            .include(pocisionFinal)
                            .build();
                    map.animateCamera(CameraUpdateFactory.newLatLngBounds(rutazoom, 18));

                }
            }

        }
    }

    public class DirectionsJSONParser {

        public List<List<HashMap<String, String>>> parse(JSONObject jObject) {

            List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
            JSONArray jRoutes = null;
            JSONArray jLegs = null;
            JSONArray jSteps = null;


            try {

                jRoutes = jObject.getJSONArray("routes");

                for (int i = 0; i < jRoutes.length(); i++) {
                    jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                    List path = new ArrayList<HashMap<String, String>>();

                    for (int j = 0; j < jLegs.length(); j++) {
                        jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                        for (int k = 0; k < jSteps.length(); k++) {
                            String polyline = "";
                            polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                            List<LatLng> list = decodePoly(polyline);

                            for (int l = 0; l < list.size(); l++) {
                                HashMap<String, String> hm = new HashMap<String, String>();
                                hm.put("lat", Double.toString((list.get(l)).latitude));
                                hm.put("lng", Double.toString((list.get(l)).longitude));
                                path.add(hm);
                            }
                        }
                        routes.add(path);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
            }

            return routes;
        }

        private List<LatLng> decodePoly(String encoded) {

            List<LatLng> poly = new ArrayList<>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
                poly.add(p);
            }

            return poly;
        }
    }

    public class DownloadTaskDistancia extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("ERRORALOBTENERINFODELWS", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTaskDistancia parserTask = new ParserTaskDistancia();

            parserTask.execute(result);


        }
    }

    public class ParserTaskDistancia extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... jsonData) {

            JSONObject jObject = null;


            try {
                jObject = new JSONObject(jsonData[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jObject;
        }

        @Override
        protected void onPostExecute(JSONObject result) {

            String distanciatexto = "";
            String tiempotexto = "";
            int distancia = 0;
            int tiempo = 0;

            try {
                JSONArray rowsArray = result.getJSONArray("rows");
                Log.i("Jsonelement", rowsArray.toString());
                JSONObject rowsOBJ = null;
                rowsOBJ = rowsArray.getJSONObject(0);
                JSONArray elementArray = rowsOBJ.getJSONArray("elements");
                //para la distancia
                JSONObject elementOBJ = elementArray.getJSONObject(0);
                JSONObject distance = elementOBJ.getJSONObject("distance");
                //para el tiempo
                JSONObject tiempoOBJ = elementOBJ.getJSONObject("duration");

                Log.i("Jsondistance", distance.toString());
                distanciatexto = distance.getString("text");
                distancia = distance.getInt("value");
                tiempotexto = tiempoOBJ.getString("text");
                tiempo = tiempoOBJ.getInt("value");

                Tiempo = tiempo;
                Distancia = distancia;

                //esto es cuando se recibe una carrera de la app, para verificar la distancia a la que
                //se enucuentra el cliente
                if (ActionServicioNuevo) {
                    try {
                        ServicioNuevo.putExtra(Constantes.TIEMPO, Tiempo);
                        ServicioNuevo.putExtra(Constantes.DISTANCIA, Distancia);
                        ServicioNuevo.putExtra(Constantes.BD_POSICION_EN_LA_LISTA, posicion_en_la_lista);
                        ServicioNuevo.putExtra(Constantes.ID_LISTA, id_lista);
                        startActivity(ServicioNuevo);
                        ActionServicioNuevo = false;
                        posicion_en_la_lista = null;
                        id_lista = null;
                        try {
                            finishAffinity();
                        } catch (Exception e) {
                            e.printStackTrace();
                            finish();
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                } else if (ActionCalcularViaje) {
                    //esto es para calcular un viaje a realizar
                    Log.i("ACTIONCALCULARVIAJE", "OK");
                    ActionCalcularViaje = false;
                    double tarifaminima = modificadorDeTarifaminima(Constantes.ENCOMIENDAS);
                    textoDistancia.setText("Distancia aprox: " + Distancia / 1000 + " km");
                    valor = (((Distancia/1000)* (350)) + (tiempo * (90))+ 1500);
                    if (valor <= tarifaminima) valor = tarifaminima;
                    textoValor.setText("Valor aprox: $" + valor);
                    cronometro.setOnChronometerTickListener(null);
                    cronometro.setBase(SystemClock.elapsedRealtime() - Tiempo * 1000);
                }


            } catch (Exception e) {
                e.printStackTrace();
                //esto es cuando se recibe una carrera de la app, para verificar la distancia a la que
                //se enucuentra el cliente
                if (ActionServicioNuevo) {
                    try {
                        ServicioNuevo.putExtra(Constantes.TIEMPO, 0.5);
                        ServicioNuevo.putExtra(Constantes.DISTANCIA, 0.5);
                        ServicioNuevo.putExtra(Constantes.BD_POSICION_EN_LA_LISTA, posicion_en_la_lista);
                        ServicioNuevo.putExtra(Constantes.ID_LISTA, id_lista);
                        startActivity(ServicioNuevo);
                        ActionServicioNuevo = false;
                        posicion_en_la_lista = null;
                        id_lista = null;
                        try {
                            finishAffinity();
                        } catch (Exception lle) {
                            lle.printStackTrace();
                            finish();
                        }
                    } catch (NullPointerException ex) {
                        ex.printStackTrace();
                    }

                } else if (ActionCalcularViaje) {
                    //esto es para calcular un viaje a realizar
                    Log.i("ACTIONCALCULARVIAJE", "OK");
                    ActionCalcularViaje = false;
                    toast("Ha ocurrido un error, intenta nuevamente");
                }
            }

            Log.i("resultJson", result.toString());

        }
    }

    // crear URl para la solicitud de distancia y tiempo
    private String obtenerdistanciaURL(LatLng origin, LatLng dest) {

        String str_origin = origin.latitude + "," + origin.longitude;

        String str_dest = dest.latitude + "," + dest.longitude;

        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins=" + str_origin + "&destinations="
                + str_dest+ "&key="+
                getResources().getString(R.string.google_maps_key_PROD);

        return url;
    }

    public void Distancia(LatLng posInicial, LatLng posFinal) {
        String urldistancia = obtenerdistanciaURL(posInicial, posFinal);
        DownloadTaskDistancia downloadTaskDistancia = new DownloadTaskDistancia();
        downloadTaskDistancia.execute(urldistancia);
    }

    public void Ruta(LatLng inicial, LatLng fin) {
        String url = obtenerDireccionesURL(inicial, fin);
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);
    }

    public void ServicioRecibido(String id_pedido) {

        DatabaseReference database = FirebaseDatabase.getInstance().getReference()
                .child(Constantes.BD_GERENTE).child(Constantes.BD_ADMIN).child(Constantes.PEDIDO)
                .child(id_pedido);

        Query query = database.orderByKey();


        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Pedidos pedido = dataSnapshot.getValue(Pedidos.class);
                Log.i("Servicio Recibido", dataSnapshot.getValue().toString());
                ServicioNuevo = new Intent(MainActivity.this, ServicioNuevoActivity.class);
                try {
                    Double lat = 1.0, lng = 1.0;
                    lat = pedido.getLat_dir_inicial();
                    lng = pedido.getLong_dir_inicial();
                    LatLng pocisioncliente = new LatLng(lat, lng);
                    Distancia(pocisionInicial, pocisioncliente);
                    ServicioNuevo.putExtra(Constantes.BD_ID_PEDIDO, pedido.getId_pedido());
                    ServicioNuevo.putExtra(Constantes.BD_DIR_INICIAL, pedido.getDir_inicial());
                    ServicioNuevo.putExtra(Constantes.LAT_INI, lat);
                    ServicioNuevo.putExtra(Constantes.LNG_INI, lng);

                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public double modificadorDeTarifaminima(String tipo_pedido) {

        double tarifa_minima;

        switch(tipo_pedido){
            case Constantes.COMPRAS_PEDIR_DOMICILIOS:
                tarifa_minima = 3000;
                break;
            case Constantes.FACTURAS_TRAMITES:
                tarifa_minima = 5000;
                break;
            case Constantes.ENCOMIENDAS:
                tarifa_minima = 2000;
                break;
            default:
                tarifa_minima = 2000;
                break;
        }


        Log.i("tipo pedido  ",tipo_pedido + "  minima "+tarifa_minima);
        return tarifa_minima;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                ETAgregarDestino.setText(place.getName());
                pocisionFinal = place.getLatLng();
                ActionCalcularViaje = true;
                Ruta(pocisionInicial, pocisionFinal);
                Distancia(pocisionInicial, pocisionFinal);
            } else if (requestCode == PERMISO_OVERLAYS) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(MainActivity.this)) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, PERMISO_OVERLAYS);
                    } else {
                        Intent mantenerConectado = new Intent(MainActivity.this, ServicioEstadoConectado.class);
                        mantenerConectado.putExtra(Constantes.BD_CODIGO_MENSAJERO, codigo);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            Log.i("OncompleteListener()", "starforegroundservice");
                            startForegroundService(mantenerConectado);
                        } else {
                            Log.i("OncompleteListener()", "starservice");
                            startService(mantenerConectado);
                        }
                    }
                }
            }
        }
    }

    public static float getImageRotation(Uri path, Context context) {
        try {
            String[] projection = {MediaStore.Images.ImageColumns.ORIENTATION};

            Cursor cursor = context.getContentResolver().query(path, projection, null, null, null);

            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            cursor.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            return 0f;
        }

        return 0f;
    }

    private Task<String> ChatServicio(String mensaje, String token_usuario) {
        Map<String, Object> data = new HashMap<>();
        data.put("clave",Constantes.CONFIRMAR_LLEGADA);
        data.put("text", mensaje);
        data.put("token_usuario", token_usuario);

        return funtions
                .getHttpsCallable("ChatServicio")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        String result = (String) task.getResult().getData();
                        Log.i("ChatServicio", result);
                        return result;
                    }
                });
    }

    private Task<String> TerminarViaje(double tiempo, double distancia, String id_pedido,
                                       String codigo_mensajero, String id_usuario, String tipo_pedido) {
        Map<String, Object> data = new HashMap<>();

        token = mensajero.getToken();
        data.put("tiempo", tiempo);
        data.put("distancia", distancia);
        data.put("id_usuario", id_usuario);
        data.put("codigo_mensajero", codigo_mensajero);
        data.put("id_pedido", id_pedido);
        data.put("token", token);
        data.put("tipo_pedido", tipo_pedido);

        return funtions
                .getHttpsCallable("TerminarViajeMoto")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        String result = (String) task.getResult().getData();
                        return result;
                    }
                });
    }

    //metodo para crear canales de notificaciones
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void CrearCanalesDeNOtificaciones(NotificationManager notificationManager) {


        try {
            Uri sonidoUri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.tin);            //-------- canal para el servicio conectado --------------------
            NotificationChannel canalEstadoConectado =
                    new NotificationChannel(Constantes.ID_CANAL_CONECTADO, Constantes.NOMBRE_CANAL_CONECTADO
                            , NotificationManager.IMPORTANCE_DEFAULT);
            canalEstadoConectado.setLightColor(Color.MAGENTA);
            canalEstadoConectado.setSound(null, null);
            canalEstadoConectado.setBypassDnd(false);
            canalEstadoConectado.enableLights(true);
            canalEstadoConectado.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(canalEstadoConectado);
            //------- canal creado ---------------

            //-------- canal para el servicio desconectar --------------------
            NotificationChannel canalDesconestar =
                    new NotificationChannel(Constantes.ID_CANAL_DESCONECTAR, Constantes.NOMBRE_CANAL_DESCONECTAR
                            , NotificationManager.IMPORTANCE_HIGH);
            canalDesconestar.setLightColor(Color.MAGENTA);
            canalDesconestar.setSound(sonidoUri, Notification.AUDIO_ATTRIBUTES_DEFAULT);
            canalDesconestar.setBypassDnd(true);
            canalDesconestar.setImportance(NotificationManager.IMPORTANCE_HIGH);
            canalDesconestar.enableVibration(true);
            canalDesconestar.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(canalDesconestar);
            //------- canal creado ---------------

            //-------- canal para el servicio carrera --------------------
            NotificationChannel canalServicioCarrera =
                    new NotificationChannel(Constantes.ID_CANAL_SERVICIO_CARRERA, Constantes.NOMBRE_CANAL_SERVICIO_CARRERA
                            , NotificationManager.IMPORTANCE_DEFAULT);
            canalServicioCarrera.setLightColor(Color.MAGENTA);
            canalServicioCarrera.setSound(null, null);
            canalServicioCarrera.setBypassDnd(false);
            canalServicioCarrera.enableVibration(false);
            canalServicioCarrera.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(canalServicioCarrera);
            //------- canal creado ---------------
            //-------- canal para el servicio conectado --------------------
            NotificationChannel canalDefault =
                    new NotificationChannel(Constantes.ID_CANAL_DEFAULT, Constantes.NOMBRE_CANAL_DEFAULT
                            , NotificationManager.IMPORTANCE_DEFAULT);
            canalDefault.setLightColor(Color.MAGENTA);
            canalDefault.setSound(sonidoUri, Notification.AUDIO_ATTRIBUTES_DEFAULT);
            canalDefault.setBypassDnd(false);
            canalDefault.enableLights(true);
            canalDefault.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(canalDefault);
            //------- canal creado ---------------

            //-------- canal para el servicio desconectar --------------------
            NotificationChannel canalChat =
                    new NotificationChannel(Constantes.ID_CANAL_CHAT, Constantes.NOMBRE_CANAL_CHAT
                            , NotificationManager.IMPORTANCE_HIGH);
            canalChat.setLightColor(Color.MAGENTA);
            canalChat.setSound(sonidoUri, Notification.AUDIO_ATTRIBUTES_DEFAULT);
            canalChat.setBypassDnd(true);
            canalChat.setImportance(NotificationManager.IMPORTANCE_HIGH);
            canalChat.enableVibration(true);
            canalChat.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(canalChat);
            //------- canal creado ---------------
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void MostrarDetalles(String valor_viaje, String valor_cobrar, String descuentos_u, String saldos_u) {
        try {
            LayoutInflater inflater = this.getLayoutInflater();
            View v = inflater.inflate(R.layout.layout_valor_viaje, null);
            dialog_terminar_viaje.setContentView(v);

            Button volver = v.findViewById(R.id.boton_confirmar_term_viaje);
            TextView texto_valor_a_cobrar = v.findViewById(R.id.term_viaje_valor);
            TextView texto_valor_total = v.findViewById(R.id.term_viaje_total);
            TextView texto_saldo_u = v.findViewById(R.id.term_viaje_saldo_u);
            TextView texto_descuentos_u = v.findViewById(R.id.term_viaje_descuentos_u);

            texto_valor_a_cobrar.setText(valor_cobrar);
            texto_valor_total.setText("$ " + valor_viaje);
            texto_descuentos_u.setText("$ " + descuentos_u);
            texto_saldo_u.setText("$ " + saldos_u);

            volver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog_terminar_viaje.dismiss();
                   // toast("servicio terminado con éxito");
                }
            });

            try {
                dialog_terminar_viaje.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void MostrarActualizar() {
        try {
            //creando el dialogo de actualizacion
            dialog_Actualizar = new Dialog(this, R.style.Theme_AppCompat_DialogWhenLarge);
            dialog_Actualizar.setCancelable(true);
            LayoutInflater inflater = this.getLayoutInflater();
            View v = inflater.inflate(R.layout.layout_actualizar, null);
            Button cerrar = v.findViewById(R.id.cerrar_actualizar);
            Button actualizar = v.findViewById(R.id.actualizar);
            cerrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog_Actualizar.dismiss();
                }
            });
            actualizar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openPlayStore();
                    dialog_Actualizar.dismiss();
                }
            });
            dialog_Actualizar.setContentView(v);
            dialog_Actualizar.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class ReceptorMensajesServidor extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.i("RECEPTOR", "MENSAJE RECIBIDO");

            String action = "action";

            if (intent != null) {
                action = intent.getAction();
            }

            if (action != null) {
                switch (action) {
                    case Constantes.ACTION_CONFIRMAR_VALOR_VIAJE:
                        dialog_calculando.dismiss();
                        Bundle bundle = intent.getExtras();
                        String valor_cobrar = "0";
                        String valor_total = "0";
                        String descuentos_u = "0";
                        String saldo_u = "0";
                        if (bundle != null) {
                            valor_cobrar = bundle.getString("valor_cobrar");
                            valor_total = bundle.getString("valor_viaje");
                            descuentos_u = bundle.getString("descuentos_usuario");
                            saldo_u = bundle.getString("saldo_pendiente_usuario");
                        }
                        MostrarDetalles(valor_total, valor_cobrar, descuentos_u, saldo_u);
                        break;
                    case Constantes.ACTION_ACTUALIZAR:
                        Log.i("ACTION_ACTUALIZAR", "HAY UNA ACTUALÑIZACION DISPONIBLE");
                        MostrarActualizar();
                        break;
                    case Constantes.ACTION_MENSAJE_CHAT:
                        if (!fragmentChat.isAdded()) {
                            fragmentChat = new FragmentChat();
                            Bundle bundle1 = new Bundle();
                            bundle1.putString(Constantes.BD_NOMBRE_USUARIO,mensajero.getNombre());
                            bundle1.putString(Constantes.BD_ID_PEDIDO,pedido_aceptado.getId_pedido());
                            fragmentChat.setArguments(bundle1);
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.replace(R.id.main_cont, fragmentChat, FragmentChat.TAG);
                            ft.commit();
                        }
                        break;
                    case Intent.ACTION_HEADSET_PLUG:
                        int state = intent.getIntExtra("state", -1);
                        switch (state)
                        {
                            case 0:
                                break;
                            case 1:
                                toast("Auricular conectado");
                                break;
                            default:
                                toast("Estado desconocido");
                                break;
                        }
                        break;
                    case Intent.ACTION_MEDIA_BUTTON:
                            KeyEvent event = intent .getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                        Log.i("KEY EVENT", "multiple");
                        if (event == null) {
                            return;
                        }

                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            Log.i("KEY EVENT", "DOWN");
                        }else if(event.getAction() == KeyEvent.ACTION_UP){
                            Log.i("KEY EVENT", "up");
                        }else if(event.getAction() == KeyEvent.ACTION_MULTIPLE){
                            Log.i("KEY EVENT", "multiple");
                        }
                        break;
                }
            }

        }
    }

    private void openPlayStore(){
        final String appPackageName = getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    private CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                if (isOnline(MainActivity.this)) {
                    if (isMyServiceRunning(ServicioEstadoConectado.class)) {
                        Log.i("OncompleteListener()", "el servicio ya estaba corriendo");
                        buttonView.setText(Constantes.ESTADO_CONECTADO);
                    } else {
                        Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.animacion_switch);
                        buttonView.setAnimation(anim);
                        buttonView.setText("Conectando...");
                        buttonView.animate();

                        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
                            id_mensajero = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            Log.i("idmensjaero", id_mensajero);
                        }else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setMessage("Por favor vuelve a iniciar sesión")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                            try {
                                                finishAffinity();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                finish();
                                            }
                                        }
                                    }).setCancelable(false).show();
                        }


                        if (mensajero == null) {
                            Gson gson = new Gson(); //Instancia Gson.
                            String json = sharedPref.getString(Constantes.BD_MENSAJERO, null);
                            mensajero = gson.fromJson(json, Mensajeros.class);
                            id_mensajero = sharedPref.getString(Constantes.BD_ID_USUARIO, null);
                            if (mensajero == null) {
                                Log.i("CAMBIAR ESTADO", "mensajero null en sPref");
                                DatosMensajero(id_mensajero);
                            }

                        }

                        if (mensajero != null) {
                            try {
                                mensajero.setLat_dir_ini(lat);
                                mensajero.setLgn_dir_ini(lng);
                            } catch (Exception e) {
                                e.printStackTrace();
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setMessage("Por favor vuelve a iniciar sesión")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                                finish();
                                            }
                                        }).setCancelable(false).show();
                            }
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setMessage("Por favor vuelve a iniciar sesión")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                            finish();
                                        }
                                    }).setCancelable(false).show();
                        }
                        //aqui se registran los datos del ususario en la base de datos
                        try {
                            if (codigo == null) {
                                codigo = mensajero.getCodigo();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setMessage("Por favor vuelve a iniciar sesión")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                            finish();
                                        }
                                    }).setCancelable(false).show();
                        }

                        Log.i("switch conectar ", codigo + "");
                        FirebaseInstanceId.getInstance().getInstanceId()
                                .addOnSuccessListener(MainActivity.this,
                                        new OnSuccessListener<InstanceIdResult>() {
                                            @Override
                                            public void onSuccess(InstanceIdResult instanceIdResult) {

                                                token = instanceIdResult.getToken();
                                                mensajero.setToken(token);
                                                database.child(Constantes.BD_MENSAJERO).child(codigo)
                                                        .child(Constantes.BD_TOKEN).setValue(token);
                                                currentUserDB = database.child(Constantes.BD_MENSAJERO_CONECTADO).child(codigo);
                                                keyconectado = currentUserDB.getKey();
                                                //guardamos los datos del mensajero en las preferencias
                                                Gson gson = new Gson();
                                                String json = gson.toJson(mensajero);//convertimos el objeto a json
                                                SharedPreferences.Editor editor = sharedPref.edit();
                                                editor.putString(Constantes.BD_MENSAJERO, json);
                                                editor.apply();
                                                SimpleDateFormat hora = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                                Calendar calendar = Calendar.getInstance();
                                                mensajero.setHora_conexion(hora.format(calendar.getTime()));
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    if (!Settings.canDrawOverlays(MainActivity.this)) {
                                                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                                                Uri.parse("package:" + getPackageName()));
                                                        startActivityForResult(intent, PERMISO_OVERLAYS);
                                                        buttonView.setChecked(false);
                                                    }else {
                                                        currentUserDB.setValue(mensajero, new DatabaseReference.CompletionListener() {
                                                            @Override
                                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                                                if (databaseError == null) {
                                                                    if(pedido_aceptado!=null){

                                                                        if(pedido_aceptado.getEstado_pedido().equals(Constantes.ESTADO_CANCELADO)) {
                                                                            mensajero.setOcupado(false);
                                                                            layout_pendientes.setVisibility(View.VISIBLE);
                                                                        }else if(pedido_aceptado.getEstado_pedido().equals(Constantes.ESTADO_TERMINADO)) {
                                                                            mensajero.setOcupado(false);
                                                                            layout_pendientes.setVisibility(View.VISIBLE);
                                                                        }else {
                                                                            mensajero.setOcupado(true);
                                                                            layout_pendientes.setVisibility(View.INVISIBLE);
                                                                        }
                                                                    }else{
                                                                        layout_pendientes.setVisibility(View.VISIBLE);
                                                                    }
                                                                    buttonView.setText(Constantes.ESTADO_CONECTADO);
                                                                    buttonView.clearAnimation();
                                                                    setConectado(true);
                                                                    Log.i("OncompleteListener()", "el servicio no estaba corriendo");
                                                                    Intent mantenerConectado = new Intent(MainActivity.this, ServicioEstadoConectado.class);
                                                                    mantenerConectado.putExtra(Constantes.BD_CODIGO_MENSAJERO, codigo);

                                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                                        Log.i("OncompleteListener()", "starforegroundservice");
                                                                        startForegroundService(mantenerConectado);
                                                                    } else {
                                                                        Log.i("OncompleteListener()", "starservice");
                                                                        startService(mantenerConectado);
                                                                    }

                                                                    contador = 0;
                                                                } else {
                                                                    buttonView.setChecked(false);
                                                                    if (isMyServiceRunning(ServicioEstadoConectado.class)) {
                                                                        Log.i("OncompleteListener()", "el servicio ya estaba corriendo pero no se pudo volver a escribir " +
                                                                                "en la base de datos, quiza sea porque no hay internet");
                                                                        Intent IntentCarreraNueva = new Intent(MainActivity.this, ServicioEstadoConectado.class);
                                                                        stopService(IntentCarreraNueva);
                                                                    }
                                                                }
                                                            }
                                                        });
                                                    }
                                                }else{
                                                    currentUserDB.setValue(mensajero, new DatabaseReference.CompletionListener() {
                                                        @Override
                                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                                            if (databaseError == null) {
                                                                buttonView.setText(Constantes.ESTADO_CONECTADO);
                                                                buttonView.clearAnimation();
                                                                setConectado(true);
                                                                Log.i("OncompleteListener()", "el servicio no estaba corriendo");
                                                                Intent mantenerConectado = new Intent(MainActivity.this, ServicioEstadoConectado.class);
                                                                mantenerConectado.putExtra(Constantes.BD_CODIGO_MENSAJERO, codigo);


                                                                    Log.i("OncompleteListener()", "starservice");
                                                                    startService(mantenerConectado);
                                                                contador = 0;
                                                            } else {
                                                                buttonView.setChecked(false);
                                                                if (isMyServiceRunning(ServicioEstadoConectado.class)) {
                                                                    Log.i("OncompleteListener()", "el servicio ya estaba corriendo pero no se pudo volver a escribir " +
                                                                            "en la base de datos, quiza sea porque no hay internet");
                                                                    Intent IntentCarreraNueva = new Intent(MainActivity.this, ServicioEstadoConectado.class);
                                                                    stopService(IntentCarreraNueva);
                                                                }
                                                            }
                                                        }
                                                    });
                                                }
                                                }

                                        });
                    }
                } else {
                    toast("Verifique su conexión a internet");
                    setConectado(false);
                    buttonView.setChecked(false);
                    buttonView.setText(Constantes.ESTADO_DESCONECTADO);
                    buttonView.clearAnimation();
                }

            } else {
                setConectado(false);
                buttonView.setChecked(false);
                buttonView.setText(Constantes.ESTADO_DESCONECTADO);
                buttonView.clearAnimation();

                //aqui se registran los datos del ususario en la base de datos
                if (codigo != null) {
                    currentUserDB = database.child(Constantes.BD_MENSAJERO_CONECTADO)
                            .child(codigo);
                    Log.i("SET CONECTADO", "quitar de la lista");
                    currentUserDB.removeValue();
                }
                Intent IntentCarreraNueva = new Intent(MainActivity.this,
                        ServicioEstadoConectado.class);
                stopService(IntentCarreraNueva);
                layout_pendientes.setVisibility(View.INVISIBLE);
            }
        }


    };

}





























