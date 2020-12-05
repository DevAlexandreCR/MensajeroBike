package mensajero.mensajerobike.Fragmentos;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mensajero.mensajerobike.Constantes.AdapterChat;
import mensajero.mensajerobike.Constantes.Constantes;
import mensajero.mensajerobike.Constantes.MensajeChat;
import mensajero.mensajerobike.Constantes.Pedidos;
import mensajero.mensajerobike.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentChat extends Fragment implements View.OnClickListener {

    public static final String TAG = "Chat";

    private RecyclerView recyclerView;
    private AdapterChat adapterChat;
    private List<MensajeChat> mensajes;
    private String id_pedido;
    private ValueEventListener listenerChat;
    private TextView texto_chat_vacio;
    private Button enviar;
    private String nombre;
    private EditText textoMensaje;
    private ProgressBar progressBar;
    private Pedidos pedido;
    //para activar la funcion de chat
    private FirebaseFunctions funtions;


    public FragmentChat() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_fragment_chat, container, false);

        funtions = FirebaseFunctions.getInstance();
        mensajes = new ArrayList<>();
        recyclerView = v.findViewById(R.id.reyclerview_message_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapterChat = new AdapterChat(getActivity(),mensajes);
        textoMensaje = v.findViewById(R.id.edittext_chatbox);
        texto_chat_vacio = v.findViewById(R.id.texto_chat_vacio);
        enviar = v.findViewById(R.id.button_chatbox_send);
        progressBar = v.findViewById(R.id.progressBar_enviar_mensaje);
        if (getArguments() != null) {
            id_pedido = getArguments().getString(Constantes.BD_ID_PEDIDO);
            nombre = getArguments().getString(Constantes.BD_NOMBRE_USUARIO);
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference pedidosEspecialBD = database.getReference()
                    .child(Constantes.BD_GERENTE).child(Constantes.BD_ADMIN).child(Constantes.PEDIDO)
                    .child(id_pedido);
            Query query = pedidosEspecialBD;
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    pedido = dataSnapshot.getValue(Pedidos.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        listenerChat = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mensajes.clear();
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot snapshot :
                            dataSnapshot.getChildren()) {
                        MensajeChat mensaje = snapshot.getValue(MensajeChat.class);
                        mensajes.add(mensaje);
                    }
                    if (!mensajes.isEmpty()) {
                        texto_chat_vacio.setVisibility(View.INVISIBLE);
                    }

                    adapterChat.notifyDataSetChanged();
                }
                recyclerView.scrollToPosition(mensajes.size()-1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView.setAdapter(adapterChat);
        enviar.setOnClickListener(this);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        if (id_pedido != null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference pedidosEspecialBD = database.getReference()
                    .child(Constantes.BD_GERENTE).child(Constantes.BD_ADMIN).child(Constantes.PEDIDO)
                    .child(id_pedido).child(Constantes.CHAT);

            Query query = pedidosEspecialBD.orderByKey();
            query.addValueEventListener(listenerChat);


        }else{
            Toast.makeText(getActivity(),"Ha ocurrido un error, intenta nuevamente",Toast.LENGTH_LONG).show();

        }


    }

    private Task<String> ChatServicio(String mensaje, String token_usuario) {
        Map<String, Object> data = new HashMap<>();
        data.put("clave",Constantes.MENSAJE_CHAT);
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

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id){
            case R.id.button_chatbox_send:

                if (textoMensaje.getText() != null && textoMensaje.getText().toString().length() >= 2) {
                    progressBar.setVisibility(View.VISIBLE);
                    final String mensaje = textoMensaje.getText().toString();
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference pedidosEspecialBD = database.getReference()
                            .child(Constantes.BD_GERENTE).child(Constantes.BD_ADMIN).child(Constantes.PEDIDO)
                            .child(id_pedido).child(Constantes.CHAT);

                    pedidosEspecialBD = pedidosEspecialBD.push();
                    String id_usuario = FirebaseAuth.getInstance().getUid();

                    pedidosEspecialBD.setValue(new MensajeChat(mensaje,id_usuario,nombre))
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    if (pedido!=null) {
                                        ChatServicio(mensaje,pedido.getToken());
                                    }else {
                                        Toast.makeText(getActivity(),"intente más tarde",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getActivity(),"intente más tarde",Toast.LENGTH_SHORT).show();
                        }
                    });
                    try {
                        textoMensaje.setText("");
                        InputMethodManager inputManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow( textoMensaje.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        recyclerView.scrollToPosition(mensajes.size()-1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }else{
                    Toast.makeText(getActivity(),"Mensaje muy corto",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
