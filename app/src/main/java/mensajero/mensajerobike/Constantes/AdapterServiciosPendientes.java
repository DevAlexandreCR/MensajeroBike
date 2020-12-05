package mensajero.mensajerobike.Constantes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.List;

import mensajero.mensajerobike.MainActivity;
import mensajero.mensajerobike.R;


public class AdapterServiciosPendientes extends RecyclerView.Adapter<AdapterServiciosPendientes.PedidosViewHolder>{

    List<Pedidos> pedidos;
    Context context;
    SharedPreferences preferences;

    public AdapterServiciosPendientes() {
    }

    public AdapterServiciosPendientes(List<Pedidos> pedidos, Context context) {
        this.pedidos = pedidos;
        this.context = context;
    }

    @NonNull
    @Override
    public PedidosViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recicler_servicios_pendientes,viewGroup,false);
        PedidosViewHolder holder = new PedidosViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PedidosViewHolder pedidosViewHolder, int i) {

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        final Pedidos pedido = pedidos.get(i);

        pedidosViewHolder.texto_fecha.setText(pedido.getFecha_pedido());
        if (!pedido.getTipo_pedido().equals(Constantes.SOLICITUD_RAPIDA)) {
            pedidosViewHolder.texto_tipo.setText(pedido.getTipo_pedido().replace("_"," "));
        }else{
            pedidosViewHolder.texto_tipo.setText(Constantes.TIPO_SERVICIO_SOLICITUD_RAPIDA);
        }
        pedidosViewHolder.texto_direccion.setText(pedido.getDir_inicial());
        pedidosViewHolder.texto_nombre.setText(pedido.getNombre());

        if(pedido.getCondicion() != null){
            pedidosViewHolder.texto_condicion.setText(pedido.getCondicion());
        }else {
            pedidosViewHolder.texto_condicion.setText(" Ninguna");
        }
        pedidosViewHolder.aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setCancelable(true)
                           .setTitle("Tomar Servicio")
                           .setMessage("¿ Desea tomar éste servicio ?")
                           .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Mensajeros mensajero;
                                    Gson gson = new Gson(); //Instancia Gson.
                                    String json = preferences.getString(Constantes.BD_MENSAJERO, null);
                                    mensajero = gson.fromJson(json, Mensajeros.class);
                                    String codigo = mensajero.getCodigo();
                                    final String token_conductor = mensajero.getToken();


                                    final DatabaseReference databaseasignarmensajero = FirebaseDatabase.getInstance().getReference()
                                            .child(Constantes.BD_GERENTE).child(Constantes.BD_ADMIN).child(Constantes.PEDIDO)
                                            .child(pedido.getId_pedido());
                                    databaseasignarmensajero.child(Constantes.BD_CODIGO_MENSAJERO).setValue(codigo)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    databaseasignarmensajero.child(Constantes.BD_ESTADO_PEDIDO).setValue(Constantes.ESTADO_EN_CURSO);
                                                    databaseasignarmensajero.child(Constantes.TOKEN_CONDUCTOR).setValue(token_conductor);
                                                    Intent pedidoAceptado = new Intent(context,MainActivity.class);
                                                    pedidoAceptado.setAction(Constantes.ACTION_PEDIDO_ACEPTADO);
                                                    pedidoAceptado.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    pedidoAceptado.putExtra(Constantes.BD_ID_PEDIDO,pedido.getId_pedido());
                                                    context.startActivity(pedidoAceptado);
                                                }
                                            });

                                }
                           }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                           }).show();
                }catch (Exception e){

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }

    public static class PedidosViewHolder extends RecyclerView.ViewHolder {

        TextView texto_fecha,texto_direccion, texto_tipo, texto_nombre, texto_condicion;
        Button aceptar;

        public PedidosViewHolder(View itemView) {
            super(itemView);

            texto_direccion = itemView.findViewById(R.id.text_direccion_pendiente);
            texto_tipo = itemView.findViewById(R.id.text_tipo_pendiente);
            texto_fecha = itemView.findViewById(R.id.text_fecha_pendiente);
            aceptar = itemView.findViewById(R.id.button_aceptar_pendiente);
            texto_nombre = itemView.findViewById(R.id.text_nombre_pendiente);
            texto_condicion = itemView.findViewById(R.id.text_condicion_pendiente);

        }
    }
}
