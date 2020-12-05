package mensajero.mensajerobike.Constantes;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import mensajero.mensajerobike.R;
import mensajero.mensajerobike.Constantes.Mensajeros;

public class AdaptadorHistorial extends RecyclerView.Adapter<AdaptadorHistorial.PedidosViewHolder>{

    List<Pedidos> pedidos;
    Context context;
    private Mensajeros mensajero;


    public AdaptadorHistorial(List<Pedidos> pedidos, Context context) {
        this.context = context;
        this.pedidos = pedidos;
    }

    @Override
    public PedidosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recicler_servicios,parent,false);
        PedidosViewHolder holder = new PedidosViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(PedidosViewHolder holder, int position) {

        final Pedidos pedido = pedidos.get(position);

        holder.texto_valor.setText(String.valueOf(pedido.getValor_pedido()));
        if (pedido.getCalificacion()!= 0) {
            holder.ratingBar.setRating((float)pedido.getCalificacion());
        }
        holder.texto_fecha.setText(pedido.getFecha_pedido());
        holder.texto_estado.setText(pedido.getEstado_pedido());
        holder.texto_direccion.setText(pedido.getDir_inicial());

    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }

    public static class PedidosViewHolder extends RecyclerView.ViewHolder {

        TextView texto_fecha,texto_valor,texto_direccion, texto_estado;
        RatingBar ratingBar;

        public PedidosViewHolder(View itemView) {
            super(itemView);

            texto_direccion = itemView.findViewById(R.id.Rec_dir_inicial);
            texto_estado = itemView.findViewById(R.id.Rec_estado_viaje);
            texto_fecha = itemView.findViewById(R.id.Rec_fecha);
            texto_valor = itemView.findViewById(R.id.Rec_valor);
            ratingBar = itemView.findViewById(R.id.Rec_ratingbar);

        }
    }
}
