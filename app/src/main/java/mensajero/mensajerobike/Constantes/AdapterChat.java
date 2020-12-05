package mensajero.mensajerobike.Constantes;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import mensajero.mensajerobike.R;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.ViewHolder> {

    private static final String TAG = "AdapterChat";
    private static final int MENSAJE_ENVIADO = 1;
    private static final int MENSAJE_RECIBIDO = 2;
    private Context mContext;
    private List<MensajeChat> mMessageList;

    public AdapterChat(Context mContext, List<MensajeChat> mMessageList) {
        this.mContext = mContext;
        this.mMessageList = mMessageList;
    }

    @Override
    public int getItemViewType(int position) {

        MensajeChat mensaje =  mMessageList.get(position);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            if(mensaje.getMensajeSender().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                return MENSAJE_ENVIADO;
            }else{
                return MENSAJE_RECIBIDO;
            }
        }else{
            return 0;
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;

        if(viewType == MENSAJE_ENVIADO){
            view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.mensajes_enviados,parent,false);

            return new EnviarMensajeHolder(view);
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.mensajes_recibidos,parent,false);

            return new RecibirMensajeHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MensajeChat mensaje = mMessageList.get(position);

        Log.i(TAG,holder.getItemViewType()+"tipo view");

        switch (holder.getItemViewType()) {
            case MENSAJE_ENVIADO:
                 holder.bindType(mensaje);
                break;
            case MENSAJE_RECIBIDO:
                holder.bindType(mensaje);
        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }


    private class EnviarMensajeHolder extends ViewHolder{

        TextView textViewMensajeEnviado, tiempoText;

        public EnviarMensajeHolder(View itemView) {
            super(itemView);

            textViewMensajeEnviado = itemView.findViewById(R.id.texto_mensaje_enviado);
            tiempoText = itemView.findViewById(R.id.tiempo_mensaje_enviado);
        }

        @Override
        public void bindType(MensajeChat mensajeChat) {
            textViewMensajeEnviado.setText(mensajeChat.getMensaje());

            // Format the stored timestamp into a readable String using method.
            tiempoText.setText(DateUtils.formatDateTime(mContext,mensajeChat.getMensajeTiempo(),
                    (
                    DateUtils.FORMAT_SHOW_TIME |
                    DateUtils.FORMAT_NUMERIC_DATE)));
        }
    }

    private class RecibirMensajeHolder extends ViewHolder{

        TextView TextoMensajeRecibido,Textotiempo,textoNombre;

        public RecibirMensajeHolder(View itemView) {
            super(itemView);

            TextoMensajeRecibido = itemView.findViewById(R.id.texto_mensaje_recibido);
            Textotiempo = itemView.findViewById(R.id.tiempo_mensaje_recibido);
            textoNombre = itemView.findViewById(R.id.texto_nombre_remitente);
        }

        @Override
        public void bindType(MensajeChat mensajeChat) {
            Log.i(TAG,"cargando datos en views");
            TextoMensajeRecibido.setText(mensajeChat.getMensaje());

            // Format the stored timestamp into a readable String using method.
            Textotiempo.setText(DateUtils.formatDateTime(mContext,mensajeChat.getMensajeTiempo(),
                    (
                            DateUtils.FORMAT_SHOW_TIME |
                            DateUtils.FORMAT_NUMERIC_DATE)));
            textoNombre.setText(mensajeChat.getNombreSender());

            }
    }

    public abstract class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void bindType(MensajeChat mensajeChat);
    }
}
