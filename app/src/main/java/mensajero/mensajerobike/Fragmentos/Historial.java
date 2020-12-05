package mensajero.mensajerobike.Fragmentos;


import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import mensajero.mensajerobike.Constantes.AdaptadorHistorial;
import mensajero.mensajerobike.Constantes.Constantes;
import mensajero.mensajerobike.Constantes.Pedidos;
import mensajero.mensajerobike.MainActivity;
import mensajero.mensajerobike.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Historial extends Fragment {

    public static final String TAG = "Historial";

    RecyclerView recyclerView;
    List<Pedidos> pedidos;
    AdaptadorHistorial adaptadorHistorial;
    TextView texto_vacio;
    FirebaseAuth auth;
    private ProgressBar progressBar;
    SharedPreferences sharedPref;
    String codigo;



    public Historial() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewRoot = inflater.inflate(R.layout.fragment_historial, container, false);
        try {
            ((MainActivity)getActivity()).getSupportActionBar().setTitle(TAG);
        } catch (Exception e) {
            e.printStackTrace();
        }

        recyclerView = viewRoot.findViewById(R.id.recicler_historial);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        progressBar = viewRoot.findViewById(R.id.progressBar_historial);
        texto_vacio = viewRoot.findViewById(R.id.texto_vacio);
        pedidos = new ArrayList<>();
        adaptadorHistorial = new AdaptadorHistorial(pedidos,getActivity());
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        codigo = sharedPref.getString(Constantes.BD_CODIGO,null);


        return viewRoot;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView.setAdapter(adaptadorHistorial);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        auth = FirebaseAuth.getInstance();
        if (codigo != null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference pedidosEspecialBD = database.getReference()
                    .child(Constantes.BD_GERENTE).child(Constantes.BD_ADMIN).child(Constantes.BD_MENSAJERO)
                    .child(codigo).child(Constantes.PEDIDO);
            Log.i(TAG,codigo+ "aqui deberia estar el codigo del nmensajero");

            pedidosEspecialBD.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    pedidos.clear();
                    if (dataSnapshot.hasChildren()) {
                        for (DataSnapshot snapshot :
                                dataSnapshot.getChildren()) {
                            Pedidos pedido = snapshot.getValue(Pedidos.class);

                            pedidos.add(0, pedido);
                        }

                    }
                    if (pedidos.size() > 0) {
                        progressBar.setVisibility(View.INVISIBLE);
                        texto_vacio.setVisibility(View.INVISIBLE);
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        texto_vacio.setVisibility(View.VISIBLE);
                    }

                    adaptadorHistorial.notifyDataSetChanged();


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    texto_vacio.setText("Algo salió mal :( , Intente más tarde");
                    Log.e(TAG,"error en firebase");
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
        }else{
            texto_vacio.setText("Algo salió mal :( , Intente más tarde");
            Log.e(TAG,"error en codigo");
            progressBar.setVisibility(View.INVISIBLE);

        }
    }
}



























