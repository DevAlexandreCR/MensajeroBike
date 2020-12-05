package mensajero.mensajerobike.Fragmentos;


import android.app.ActivityManager;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import mensajero.mensajerobike.Constantes.Constantes;
import mensajero.mensajerobike.Constantes.Mensajeros;
import mensajero.mensajerobike.LoginActivity;
import mensajero.mensajerobike.MainActivity;
import mensajero.mensajerobike.R;
import mensajero.mensajerobike.Servicios.ServicioEstadoConectado;

public class Fragment_perfil extends Fragment implements View.OnClickListener{
    public static final String TAG = "Mi Perfil";
    public SharedPreferences preferences;
    private TextInputEditText ET_nombre,ET_numero,ET_email;
    private Button cerrar_sesion;
    private String nombre, email, telefono;
    private ImageView image_perfil;


    public Fragment_perfil() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_perfil, container, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        //iniciar vistas
        ET_email = v.findViewById(R.id.perfil_email);
        ET_email.setFocusable(false);
        ET_nombre = v.findViewById(R.id.perfil_nombre);
        ET_nombre.setFocusable(false);
        ET_numero = v.findViewById(R.id.perfil_telefono);
        ET_numero.setFocusable(false);
        image_perfil = v.findViewById(R.id.imagen_perfil);
        cerrar_sesion = v.findViewById(R.id.boton_cerrar_sesion);
        cerrar_sesion.setOnClickListener(this);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Gson gson = new Gson(); //Instancia Gson.
        String json = preferences.getString(Constantes.BD_MENSAJERO, null);
        Mensajeros mensajero = gson.fromJson(json, Mensajeros.class);

        if (mensajero!=null) {
            ET_email.setText(mensajero.getEmail());
            ET_numero.setText(mensajero.getTelefono());
            ET_nombre.setText(mensajero.getNombre());
            String pathFoto = Constantes.URL_FOTO_PERFIL_CONDUCTOR+mensajero.getCodigo();

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference().child(pathFoto+"/foto_perfil");


            Glide.with(getActivity())
                    .using(new FirebaseImageLoader())
                    .load(storageReference)
                    .into(image_perfil);
        }else{
            if(MainActivity.isOnline(getActivity())){
                String id_user = FirebaseAuth.getInstance().getUid();

                if (id_user!=null) {
                    datos(id_user);
                }else{
                    Log.i("iduser perfil",id_user + "este es el id");
                }
            }else{
                Toast.makeText(getActivity(),"Verifique su conexión a internet",Toast.LENGTH_LONG).show();
                ET_numero.setText("000 000 0000");
                ET_nombre.setText("Nombre");
                ET_email.setText("minombre@ejemplo.com");
            }

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.boton_cerrar_sesion:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("¿Cerrar Sesión?")
                        .setPositiveButton("Cerrar Sesión", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //cerrar la sesion del usuario actual.

                                if (isMyServiceRunning(ServicioEstadoConectado.class)) {
                                    Toast.makeText(getActivity(),"Primero debes desconectarte",Toast.LENGTH_LONG).show();
                                    dialogInterface.dismiss();
                                } else {
                                    FirebaseAuth.getInstance().signOut();
                                    startActivity(new Intent(getActivity(), LoginActivity.class));
                                }
                            }
                        })
                        .setNegativeButton("Volver", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
                break;
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager)getActivity().getSystemService(Context.ACTIVITY_SERVICE);
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

    public void datos(String id){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference currenUser = database.getReference()
                .child(Constantes.BD_GERENTE).child(Constantes.BD_ADMIN)
                .child(Constantes.BD_MENSAJERO)
                .child(id);
        final Query query = currenUser.orderByKey();

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue()!=null) {
                    Mensajeros mensajero = dataSnapshot.getValue(Mensajeros.class);
                    nombre = mensajero.getNombre();
                    telefono = mensajero.getTelefono();
                    email = mensajero.getEmail();
                    ET_numero.setText(telefono);
                    ET_nombre.setText(nombre);
                    ET_email.setText(email);
                    String pathFoto = Constantes.URL_FOTO_PERFIL_CONDUCTOR+mensajero.getCodigo();

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageReference = storage.getReference()
                            .child(pathFoto+"/foto_perfil");


                    Glide.with(getActivity())
                            .using(new FirebaseImageLoader())
                            .load(storageReference)
                            .into(image_perfil);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
