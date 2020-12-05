package mensajero.mensajerobike.Constantes;

/**
 * Created by equipo on 30/01/2018.
 */

public class Constantes {

    public static final String BD_NOMBRE_USUARIO = "nombre";
    public static final String BD_TELEFONO_USUARIO = "telefono";
    public static final String BD_ID_USUARIO = "id_usuario";
    public static final String BD_ID_PEDIDO = "id_pedido";
    public static final String BD_USUARIO="usuario";
    public static final String BD_GERENTE="gerente";
    public static final String BD_ADMIN="admin";
    public static final String BD_DIR_INICIAL="dir_inicial";
    public static final String BD_DIR_FINAL="dir_final";
    public static final String BD_ESTADO_PEDIDO="estado_pedido";
    public static final String BD_FECHA_PEDIDO="fecha_pedido";
    public static final String BD_VALOR_PEDIDO="valor_pedido";
    public static final String BD_COMENTARIO ="comentario";
    public static final String BD_CODIGO_MENSAJERO ="codigo_mensajero";
    public static final String BD_ASIGNAR_MOVIL ="asignar movil";
    public static final String URL_DESCARGA_APP = "https://play.google.com/store/apps/details?id=com.mensajero.equipo.mensajero";
    public static final String INICIAR_DE_NOTIFICACION = "notificacion";
    public static final String BD_MENSAJERO_CONECTADO = "mensajero_conectado";
    public static final String PEDIDO ="pedido";

    //estados del mensajero
    public static final String BD_MENSAJERO = "mensajero";
    public static final String BD_ESTADO_MENSAJERO = "estado";
    public static final String ESTADO_ACTIVO= "activo";
    public static final String ESTADO_BLOQUEADO= "bloqueado";
    public static final String ESTADO_VERIFICAR = "verificar";
    public static final String ESTADO_VERIFICADO = "verificado";
    public static final String ESTADO_SUBIR_IMAGENES = "subir_imagenes";
    public static final String ESTADO_CONECTADO = "Conectado";
    public static final String ESTADO_DESCONECTADO = "Desconectado";

    //datos del mensajero para la BD
    public static final String BD_PLACA = "placa";
    public static final String BD_RUTA_FOTOS_MENSAJERO = "mensajeros/mensajero_moto/movil_";
    public static final String BD_FOTO_DE_PERFIL = "foto_perfil.jpg";
    public static final String BD_CODIGO ="codigo";
    public static final String BD_TOKEN ="token";
    public static final String ID_LISTA ="id_lista";
    public static final String URL_FOTO_PERFIL_CONDUCTOR = "mensajeros/mensajero_moto/movil_";
    public static final String LAT_DIR_INICIAL = "lat_dir_ini";
    public static final String LGN_DIR_INICIAL = "lgn_dir_ini";
    public static final String LAST_LOCATION_HORA = "ultimo_cambio_location";
    public static final String CHAT = "chat";
    public static final String BD_SIN_PLACA = "sinplaca";

    //tipos de servicio
    public static final String BD_TIPO_PEDIDO="tipo_pedido";
    public static final String COMPRAS_PEDIR_DOMICILIOS="compras_domicilios";
    public static final String FACTURAS_TRAMITES="facturas_tramites";
    public static final String ENCOMIENDAS="encomiendas";
    public static final String SOLICITUD_RAPIDA="solicitud_rapida";

    //Constantes para los Job
    public static final int PRIORIDAD_ALTA = 1;

    //CONSTANTES PARA LOS SERVICIOS
    public static final String STARTFOREGROUND_ACTION = "mensajero.mensajerobike.foregroundservice.action.startforeground";
    public static final String STOPFOREGROUND_ACTION = "mensajero.mensajerobike.foregroundservice.action.stopforeground";
    public static final String ACTION_DESCONECTAR = "mensajero.mensajerobike.foregroundservice.action.desconectar";
    public static final String ACTION_CONECTAR = "mensajero.mensajerobike.foregroundservice.action.conectar";
    public static final String MAIN_ACTION = "mensajero.mensajerobike.foregroundservice.action.main";
    public static final String ACTION_INICIO_SESION = "mensajero.mensajerobike.inicio_sesion";
    public static final String ACTION_PEDIDO_ACEPTADO = "mensajero.mensajerobike.pedido_aceptado";
    public static final String ACTION_RECOGER = "mensajero.mensajerobike.action_recoger_pasajero";
    public static final String ACTION_INICIAR_CARRERA = "mensajero.mensajerobike.action_iniciar_carrera";
    public static final String ACTION_TERMINAR_CARRERA = "mensajero.mensajerobike.action_terminar_carrera";
    public static final String ACTION_CONECTAR_DESDE_NOTIFICACION = "mensajero.mensajerobike.foregroundservice.action.conectar_desde_notificacion";
    public static final String BD_MENSAJEROS_CERCA_ENV_MENSAJE ="mensajeros_env_mensaje";
    public static final String BD_POSICION_EN_LA_LISTA ="posicion_en_la_lista";
    public static final String SERVICIO_ACTIVO = "servicio_activo";
    public static final String SERVICIO_NUEVO = "servicio_nuevo";
    public static final String SERVICIO_NUEVO_OFICINA = "servicio_nuevo_oficina";
    public static final String LAT_INI = "lat_dir_ini";
    public static final String LNG_INI = "lgn_dir_ini";
    public static final String TIEMPO = "tiempo";
    public static final String DISTANCIA = "distancia";


    //estados del serviio

    public static final String ESTADO_EN_CURSO = "en_curso" ;
    public static final String ESTADO_CANCELADO = "cancelado";
    public static final String ESTADO_TERMINADO = "terminado";
    public static final String ESTADO_SERVICIO_INICIADO ="servicio_iniciado";
    public static final String TERMINAR_SERVICIO ="TERMINAR SERVICIO";
    public static final String INICIAR_SERVICIO ="INICIAR SERVICIO";
    //para encomiendas
    public static final String ESTADO_PAQUETE_RECOGIDO="paquete_recogido"; //cuando el mensajero recoge el paquete
    public static final String ESTADO_PAQUETE_ENTREGADO="paquete_entregado"; //cuando el mensajero entrega la encimienda, luego tendrá la opción de terminarlo si ya recibio el dinero
    //para compras
    public static final String ESTADO_MENSAJERO_EN_TIENDA="mensajero_en_tienda";//mensajero en el lugar de la compra, inicia a facturar
    public static final String ESTADO_COMPRA_REALIZADA="compra_realizada";// ya el mensajero se dirige a la entrega
    //para domicilios
    public static final String ESTADO_DOMICILIO_RECIBIDO="domicilio_recibido";

    //canales de notificacion id´s:
    public static final String ID_CANAL_CHAT = "com.mensajero.canal_05";
    public static final String NOMBRE_CANAL_CHAT= "com.mensajero.chat";
    public static final String ID_CANAL_DEFAULT = "com.mensajero.canal_06";
    public static final String NOMBRE_CANAL_DEFAULT= "com.mensajero.default";
    public static final String ID_CANAL_CONECTADO = "com.mensajerogo.canal_01";
    public static final String NOMBRE_CANAL_CONECTADO = "com.mensajerobike.canal_conectado";
    public static final String ID_CANAL_SERVICIO_CARRERA = "com.mensajerobike.canal_02";
    public static final String NOMBRE_CANAL_SERVICIO_CARRERA = "com.mensajero.canal_servicio_carrera";
    public static final String NOMBRE_CANAL_DESCONECTAR = "com.mensajerobike.canal_desconectar";
    public static final String ID_CANAL_DESCONECTAR = "com.mensajerobike.canal_03";
    public static final String CONFIRMAR_LLEGADA = "com.mensajerobike.confirmar_llegada";
    public static final String MENSAJE_CHAT = "mensaje_chat";
    public static final String CONFIRMAR_VALOR_VIAJE = "confirmar_valor_viaje";


    //acciones del receptor de mensajes
    public static final String ACTION_MENSAJE_CHAT = "action.mensaje_chat";
    public static final String ACTION_CONFIRMAR_VALOR_VIAJE = "action.confirma.valor.viaje";
    public static final String BD_ID_PEDIDO_ACEPTADO = "id_pedido_aceptado";
    public static final String ACTION_ACTUALIZAR = "actualizacion_disponible";
    public static final String TOKEN_CONDUCTOR = "token_conductor" ;

    //botn chat
    public static final String AVISA_QUE_LLEGASTE = "avisa que llegaste";
    public static final String MENSAJERO_EN_TIENDA = "llegaste a la tienda";
    public static final String COMPRA_REALIZADA = "He comprado el domicilio";
    public static final String PAQUETE_RECOGIDO = "Encomienda recogida";
    public static final String PAQUETE_ENTREGADO = "Encomienda entregada";
    public static final String MENSAJERO_EN_LUGAR_DE_RECOGIDA = "avisa que llegaste";

    //texto tipo servicio

    public static final String TIPO_SERVICIO_COMPRAS_DOMICILIOS = "Compras - Domicilios";
    public static final String TIPO_SERVICIO_TRAMITES_FACTURAS = "Pagos - Trámites";
    public static final String TIPO_SERVICIO_SOLICITUD_RAPIDA = "Domicilios";
    public static final String TIPO_SERVICIO_ENCOMIENDAS = "Encomiendas";
    public static final String AGREGAR_VALOR_SERVICIO = "AGREGAR VALOR SERVICIO";
    public static final String CODIGO_CENTRAL_MENSAJERO = "7gJSMJsZNjRUDoOzKnO0GefE0ND3";


    public interface NOTIFICATION_ID {
        int FOREGROUND_SERVICE = 101;
        int FOREGROUND_SERVICE_ESTADO = 102;
        int FOREGROUND_SERVICE_DESCEONECTAR = 103;
    }
}
