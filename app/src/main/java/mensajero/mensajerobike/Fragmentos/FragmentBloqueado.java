package mensajero.mensajerobike.Fragmentos;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import mensajero.mensajerobike.Constantes.Constantes;
import mensajero.mensajerobike.R;


/**
 * A fragment with a Google +1 button.
 */
public class FragmentBloqueado extends Fragment {

    public static final String  TAG = "FragmentBlloqueado";
    private String estado_mensajero;
    TextView textView;


    public FragmentBloqueado() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_bloqueado, container, false);

        textView = view.findViewById(R.id.textBloqueado);


        return view;
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        estado_mensajero = bundle.getString(Constantes.BD_ESTADO_MENSAJERO,Constantes.ESTADO_BLOQUEADO);
        String mensaje = "Si enviaste los documentos, debes esperar hasta 24 horas hábiles para ser verificado;" +
                " de lo contrario puedes comunicarte al 3202973621 o enviarnos tus dudas a gerencia@mensajeroapp.co";

        switch (estado_mensajero){
            case Constantes.ESTADO_BLOQUEADO:
                mensaje = "Tu cuenta necesita verificación, escríbenos al WhatsApp 3202973621 o a gerencia@mensajeroapp.co para ayudarte a resolver el problema";
                break;
            case Constantes.ESTADO_VERIFICAR:
                mensaje = "Estamos verificando tus documentos, ésto puede demorar hasta 24 horas. Si pasado éste tiempo no tienes respuesta," +
                        " escríbenos al WhatsApp 3202973621 o a gerencia@mensajeroapp.co";
                break;
            case Constantes.ESTADO_VERIFICADO:
                mensaje = "Tus documentos ya están verificados, sólo debes enviar un hoja de vida con "+
                        "dos referencias personales, tu dirección y datos personales a gerencia@mensajeroapp.co "+
                        " Ésto con el fin de poder asignarte servicios de encomiendas de valor considerable. Con ésto ya podrás ser parte" +
                        " de la red de Mensajeros confiables.";
                break;
        }

        textView.setText(mensaje);


    }

    @Override
    public void onResume() {
        super.onResume();

    }


}
