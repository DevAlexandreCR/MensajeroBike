package mensajero.mensajerobike;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;

import java.text.DecimalFormat;

import mensajero.mensajerobike.Constantes.Constantes;
import mensajero.mensajerobike.Constantes.Mensajeros;
import mensajero.mensajerobike.Servicios.SonidoServicioNuevo;

public class ServicioNuevoActivity extends AppCompatActivity implements View.OnClickListener{

    public LottieAnimationView lottieRecibido;
    public String id_pedido="",direccion_inicial="",token_conductor;
    public Double tiempo,distancia;
    public TextView textotiempo,textodistancia;
    public TextView cronoregresivo;
    private final String TAG = "ServicioNuevoActivity";
    private CountDownTimer count;
    private String posicion_en_la_lista,id_lista;
    private SharedPreferences sharedPref ;
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference()
            .child(Constantes.BD_GERENTE).child(Constantes.BD_ADMIN);
    private Boolean cancelado = false;
    private Button botonRechazar;
    private Query query;
    private ValueEventListener valueEventListener = new ValueEventListener(){

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.getValue()!=null) {
                String estado = dataSnapshot.getValue().toString();
                if(estado!=null && estado.equals(Constantes.ESTADO_CANCELADO)){
                    cancelado = true;
                    count.onFinish();
                    query.removeEventListener(this);
                    stopService(new Intent(ServicioNuevoActivity.this,SonidoServicioNuevo.class));
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_servicio_nuevo);

        unlockScreen();
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnSuccessListener(ServicioNuevoActivity.this, new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        token_conductor = instanceIdResult.getToken();
                    }
                });

        //pra guardar datos
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        textodistancia = findViewById(R.id.textViewdistcliente);
        textotiempo = findViewById(R.id.textViewtiempocliente);
        cronoregresivo = findViewById(R.id.cronotiemposervicio);
        botonRechazar = findViewById(R.id.botonRechazar);
        //inicializamos la animacion
        lottieRecibido = findViewById(R.id.lotiierecibido);
        lottieRecibido.loop(true);

        try{
            id_pedido = getIntent().getStringExtra(Constantes.BD_ID_PEDIDO);
            tiempo = getIntent().getIntExtra(Constantes.TIEMPO,300)/60.0;
            distancia = getIntent().getIntExtra(Constantes.DISTANCIA,2000)/1000.0;
            posicion_en_la_lista = getIntent().getStringExtra(Constantes.BD_POSICION_EN_LA_LISTA);
            id_lista = getIntent().getStringExtra(Constantes.ID_LISTA);
            DecimalFormat format = new DecimalFormat("#.#");
            format.setMaximumFractionDigits(1);

            Log.i(TAG,"tiempo: "+format.format(tiempo)+" distancia: "+format.format(distancia));
            textodistancia.setText(format.format(distancia)+"  km");
            textotiempo.setText(format.format(tiempo)+" min");


        }catch (NullPointerException e){
            e.printStackTrace();
        }

        //aqui vamos a crear un listener para saber si el usuario cancela el servicio antes de se complete el tiempo
        query = database.child(Constantes.PEDIDO).child(id_pedido).child(Constantes.BD_ESTADO_PEDIDO);
        query.addValueEventListener(valueEventListener);

        count = new CountDownTimer(13000,1000){

            @Override
            public void onTick(long millisUntilFinished) {
                cronoregresivo.setText(" "+millisUntilFinished/1000 + " ");
            }

            @Override
            public void onFinish() {
                Log.d(TAG, "La cuenta terminó");
                database = FirebaseDatabase.getInstance().getReference()
                        .child(Constantes.BD_GERENTE).child(Constantes.BD_ADMIN).child(Constantes.BD_MENSAJEROS_CERCA_ENV_MENSAJE)
                        .child(id_lista);
                database.child(posicion_en_la_lista).removeValue();
                query.removeEventListener(valueEventListener);
                stopService(new Intent(ServicioNuevoActivity.this, SonidoServicioNuevo.class));
                Intent volversinservicio = new Intent(ServicioNuevoActivity.this,LoginActivity.class);
                volversinservicio.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                volversinservicio.setAction(Constantes.ACTION_CONECTAR);
                volversinservicio.putExtra(Constantes.ESTADO_CANCELADO,cancelado);
                startActivity(volversinservicio);
                try {
                    finishAffinity();
                } catch (Exception e) {
                    e.printStackTrace();
                    finish();
                }
            }
        };
        count.start();


        botonRechazar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count.onFinish();
                Log.d(TAG, "HEMOS RECHAZADO EL PEDIDO");
            }
        });
        lottieRecibido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "HEMOS ACEPTADO EL PEDIDO");
                count.cancel();
                stopService(new Intent(ServicioNuevoActivity.this, SonidoServicioNuevo.class));
                DatabaseReference database = FirebaseDatabase.getInstance().getReference()
                        .child(Constantes.BD_GERENTE).child(Constantes.BD_ADMIN).child(Constantes.BD_MENSAJEROS_CERCA_ENV_MENSAJE)
                        .child(id_lista);
                //borramos la lista para que no envíe más mensajes
                database.removeValue();

                Mensajeros mensajero;
                Gson gson = new Gson(); //Instancia Gson.
                String json = sharedPref.getString(Constantes.BD_MENSAJERO, null);
                mensajero = gson.fromJson(json, Mensajeros.class);
                String codigo = mensajero.getCodigo();


                final DatabaseReference databaseasignarmensajero = FirebaseDatabase.getInstance().getReference()
                        .child(Constantes.BD_GERENTE).child(Constantes.BD_ADMIN).child(Constantes.PEDIDO)
                        .child(id_pedido);
                databaseasignarmensajero.child(Constantes.BD_CODIGO_MENSAJERO).setValue(codigo)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                databaseasignarmensajero.child(Constantes.BD_ESTADO_PEDIDO).setValue(Constantes.ESTADO_EN_CURSO);
                                databaseasignarmensajero.child(Constantes.TOKEN_CONDUCTOR).setValue(token_conductor);
                            }
                        });

                Intent pedidoAceptado = new Intent(ServicioNuevoActivity.this,MainActivity.class);
                pedidoAceptado.setAction(Constantes.ACTION_PEDIDO_ACEPTADO);
                pedidoAceptado.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pedidoAceptado.putExtra(Constantes.BD_ID_PEDIDO,id_pedido);
                startActivity(pedidoAceptado);

                try {
                    finishAffinity();
                } catch (Exception e) {
                    e.printStackTrace();
                    finish();
                }
            }
        });
        }

    @Override
    protected void onStop() {
        super.onStop();
        lottieRecibido.cancelAnimation();
        if (query!=null&&valueEventListener!=null) {
            query.removeEventListener(valueEventListener);
        }
        stopService(new Intent(ServicioNuevoActivity.this,SonidoServicioNuevo.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        lottieRecibido.playAnimation();
        Log.i("iniciar sonido ", "iniciando sonido");
        Intent iniciarSonido = new Intent(ServicioNuevoActivity.this, SonidoServicioNuevo.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(iniciarSonido);
        } else {
            startService(iniciarSonido);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        switch (id) {
            case (R.id.lotiierecibido):

            case R.id.botonRechazar:

                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    private void unlockScreen() {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_HEADSETHOOK){

            Log.d(TAG, "HEMOS ACEPTADO EL PEDIDO");
            count.cancel();
            stopService(new Intent(ServicioNuevoActivity.this, SonidoServicioNuevo.class));
            DatabaseReference database = FirebaseDatabase.getInstance().getReference()
                    .child(Constantes.BD_GERENTE).child(Constantes.BD_ADMIN).child(Constantes.BD_MENSAJEROS_CERCA_ENV_MENSAJE)
                    .child(id_lista);
            //borramos la lista para que no envíe más mensajes
            database.removeValue();

            Mensajeros mensajero;
            Gson gson = new Gson(); //Instancia Gson.
            String json = sharedPref.getString(Constantes.BD_MENSAJERO, null);
            mensajero = gson.fromJson(json, Mensajeros.class);
            String codigo = mensajero.getCodigo();


            final DatabaseReference databaseasignarmensajero = FirebaseDatabase.getInstance().getReference()
                    .child(Constantes.BD_GERENTE).child(Constantes.BD_ADMIN).child(Constantes.PEDIDO)
                    .child(id_pedido);
            databaseasignarmensajero.child(Constantes.BD_CODIGO_MENSAJERO).setValue(codigo)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            databaseasignarmensajero.child(Constantes.BD_ESTADO_PEDIDO).setValue(Constantes.ESTADO_EN_CURSO);
                            databaseasignarmensajero.child(Constantes.TOKEN_CONDUCTOR).setValue(token_conductor);
                        }
                    });

            Intent pedidoAceptado = new Intent(ServicioNuevoActivity.this,MainActivity.class);
            pedidoAceptado.setAction(Constantes.ACTION_PEDIDO_ACEPTADO);
            pedidoAceptado.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pedidoAceptado.putExtra(Constantes.BD_ID_PEDIDO,id_pedido);
            startActivity(pedidoAceptado);

            try {
                finishAffinity();
            } catch (Exception e) {
                e.printStackTrace();
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
