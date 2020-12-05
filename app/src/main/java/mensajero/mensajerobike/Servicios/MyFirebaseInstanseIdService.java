package mensajero.mensajerobike.Servicios;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;

import mensajero.mensajerobike.Constantes.Constantes;
import mensajero.mensajerobike.Constantes.Mensajeros;


/**
 * Created by equipo on 13/02/2018.
 */

public class MyFirebaseInstanseIdService extends FirebaseInstanceIdService {

    String id_mensajero;
    String codigo;
    SharedPreferences sharedPref;

    public String getToken() {
        return token;
    }

    String token;


    @Override
    public void onTokenRefresh() {
        //pra guardar datos
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        super.onTokenRefresh();

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                token = instanceIdResult.getToken();
            }
        });

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
                            mensajero_prov.setToken(token);

                            //guardamos los datos del mensajero en las preferencias
                            Gson gson = new Gson();
                            String json = gson.toJson(mensajero_prov);//convertimos el objeto a json
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString(Constantes.BD_MENSAJERO, json);
                            editor.apply();
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
}
