package mensajero.mensajerobike;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import mensajero.mensajerobike.Constantes.Constantes;
import mensajero.mensajerobike.Constantes.Mensajeros;

// librerias de facebook


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    // UI referencias.
    private android.support.design.widget.TextInputEditText mPasswordView, mNombreView, mNumeroView
            , mEmailView ;
    private Button mRegsitrarButton,BotonEnviarCodigo;
    private Button mEmailSignInButton;
    private TextInputLayout tnombrelayout, temaillyout;
    private String email,codigo;
    private String nombre,placa;
    private String numero;
    private String password;
    public List<Mensajeros> listaMensajeros;
    private final String NUEVA_CUENTA = "¿No tienes cuenta? Regístrate";
    private final String TAG = "LoginActivity";
    public Animation vibrar;
    boolean mensajero_existe = false;
    //aqui se registran los datos del ususario en la base de datos
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference()
            .child(Constantes.BD_GERENTE);
    private Query query;
    public String id_mensajero;
    public String id_auth;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String token,IdVerficacion;
    private View.OnFocusChangeListener focusChangeListener;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //para ocultar el teclado
        focusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        };

        progressBar = findViewById(R.id.progresbar);
        progressBar.getIndeterminateDrawable()
                .setColorFilter(Color.rgb(43,150,211), PorterDuff.Mode.SRC_IN);


        //definimos la animacion
        vibrar = AnimationUtils.loadAnimation(this, R.anim.vibrar);
        vibrar.setRepeatCount(3);
        vibrar.reset();


        //inicializamos el arreglo de ususarios
        listaMensajeros = new ArrayList<>();

        mEmailView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);
        mNombreView = findViewById(R.id.nombre);
        mNumeroView = findViewById(R.id.numero_tel);
        tnombrelayout = findViewById(R.id.tnombre);
        temaillyout = findViewById(R.id.temail);
        mEmailView.setOnFocusChangeListener(focusChangeListener);
        mPasswordView.setOnFocusChangeListener(focusChangeListener);
        mNombreView.setOnFocusChangeListener(focusChangeListener);
        mNumeroView.setOnFocusChangeListener(focusChangeListener);
        mEmailView.setVisibility(View.INVISIBLE);
        mNombreView.setVisibility(View.INVISIBLE);
        tnombrelayout.setVisibility(View.INVISIBLE);
        temaillyout.setVisibility(View.INVISIBLE);

        //probar si esta la sesion iniciada
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() != null) {
                    int contador=0;
                    do{
                        try{
                            id_mensajero =mAuth.getCurrentUser().getUid();

                        }catch (NullPointerException e){
                            e.printStackTrace();
                            contador++;
                        }
                        contador++;
                    }while (id_mensajero ==null||contador==5);

                    if(contador==5){
                        toast("por favor vuelva a abrir mensajero");
                        //finish();
                    }

                    Intent sesionIniciada = new Intent(LoginActivity.this, MainActivity.class);
                    sesionIniciada.putExtra(Constantes.BD_ID_USUARIO, id_mensajero);
                    sesionIniciada.setAction(Constantes.ACTION_INICIO_SESION);
                    sesionIniciada.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(sesionIniciada);
                    progressBar.setVisibility(View.INVISIBLE);
                    finishAffinity();

                } else {
                   // toast("por favor vuelva a abrir mensajero");
                   // finish();
                }
            }
        };

        mEmailSignInButton = findViewById(R.id.iniciarSesion);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEmailSignInButton.getText().equals("Registrarse")) {

                    registrar();
                } else {
                    mPasswordView.setError(null);
                    password = mPasswordView.getText().toString();

                    if(TextUtils.isEmpty(password)){
                        mPasswordView.setError(getResources().getString(R.string.error_incorrect_password));
                        return;
                    }
                    try{
                        signInWithPhoneAuthCredential(PhoneAuthProvider.getCredential(IdVerficacion,password));
                    }catch (IllegalArgumentException e){
                        toast("A ocurrido un error intente nuevamente");
                        Log.e("Error de codigo",e.getLocalizedMessage());
                        return;
                    }


                }

            }
        });

        //boton para enviar el código de verificacion
        BotonEnviarCodigo = findViewById(R.id.botoncodigo);
        BotonEnviarCodigo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                numero = mNumeroView.getText().toString();
                InputMethodManager inputMethodManager  = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(mNumeroView.getWindowToken(), 0);
                if(!isNumeroValid(numero)) {
                    mNumeroView.setError(getString(R.string.error_invalid_numero));
                    mNumeroView.requestFocus();
                    return;
                }else {
                    //vamos a ver si el mensajero ya tiene código
                    toast("Solicitud enviada por favor espere...");
                    progressBar.setVisibility(View.VISIBLE);

                    query = database.child(Constantes.BD_ADMIN).child(Constantes.BD_MENSAJERO)
                            .orderByKey();
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (listaMensajeros != null) listaMensajeros.clear();
                            for (DataSnapshot snapshot :
                                    dataSnapshot.getChildren()) {
                                Mensajeros mensajero = snapshot.getValue(Mensajeros.class);
                                listaMensajeros.add(mensajero);
                                try {
                                    if (mensajero.getTelefono() != null) {
                                        if (mensajero.getTelefono().equals(numero)) {
                                            mensajero_existe = true;
                                            Log.i("numero", String.valueOf(mensajero_existe));
                                        }
                                    }
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                    toast("Ha ocurrido un error intente nuevamente");
                                    progressBar.setVisibility(View.INVISIBLE);
                                    return;
                                }

                            }
                            Log.i("mensajeroesisis", String.valueOf(mensajero_existe));

                            numero = "+57"+numero;
                            if (mensajero_existe) {
                                PhoneAuthProvider.getInstance().verifyPhoneNumber(numero, 20, TimeUnit.SECONDS, LoginActivity.this
                                        , new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                            @Override
                                            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                                                signInWithPhoneAuthCredential(phoneAuthCredential);
                                                progressBar.setVisibility(View.INVISIBLE);


                                            }

                                            @Override
                                            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                                super.onCodeSent(s, forceResendingToken);

                                                IdVerficacion = s;
                                                toast("esperando código de verificación...");
                                                progressBar.setVisibility(View.VISIBLE);
                                            }

                                            @Override
                                            public void onCodeAutoRetrievalTimeOut(String s) {
                                                toast("No pudimos saber si le llegó el código, Inicie Sesión o intentelo nuevamente");
                                                progressBar.setVisibility(View.INVISIBLE);
                                                super.onCodeAutoRetrievalTimeOut(s);
                                            }

                                            @Override
                                            public void onVerificationFailed(FirebaseException e) {

                                                Log.i("failed", e.getMessage());
                                                toast("Ha ocurrido un error intente nuevamente");
                                                progressBar.setVisibility(View.INVISIBLE);
                                            }
                                        });
                            } else {

                                toast("El número de teléfono aún no está registrado");
                                mEmailSignInButton.startAnimation(vibrar);
                                mEmailSignInButton.setText(R.string.action_registrarse);
                                PhoneAuthProvider.getInstance().verifyPhoneNumber(numero, 10, TimeUnit.SECONDS, LoginActivity.this
                                        , new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                            @Override
                                            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                                               // signInWithPhoneAuthCredential(phoneAuthCredential);
                                                progressBar.setVisibility(View.INVISIBLE);


                                            }

                                            @Override
                                            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                                super.onCodeSent(s, forceResendingToken);

                                                IdVerficacion = s;
                                                //toast("esperando código de verificación...");
                                                progressBar.setVisibility(View.VISIBLE);
                                            }

                                            @Override
                                            public void onCodeAutoRetrievalTimeOut(String s) {
                                                toast("Registre los datos solicitados y agregue el código enviado al "+numero);
                                                progressBar.setVisibility(View.INVISIBLE);
                                                super.onCodeAutoRetrievalTimeOut(s);
                                            }

                                            @Override
                                            public void onVerificationFailed(FirebaseException e) {

                                                Log.i("failed", e.getMessage());
                                                toast("Ha ocurrido un error intente nuevamente");
                                                progressBar.setVisibility(View.INVISIBLE);
                                            }
                                        });

                                mRegsitrarButton.setText(R.string.si_cuenta);
                                mEmailView.setVisibility(View.VISIBLE);
                                mNombreView.setVisibility(View.VISIBLE);
                                tnombrelayout.setVisibility(View.VISIBLE);
                                temaillyout.setVisibility(View.VISIBLE);

                                //asignamos
                                LinearLayout layout = findViewById(R.id.layoutregistrar);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                                        , LinearLayout.LayoutParams.WRAP_CONTENT);
                                layout.setLayoutParams(params);
                            }



                            query.removeEventListener(this);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }


            }
        });

        mRegsitrarButton = findViewById(R.id.registrar);
        mRegsitrarButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mRegsitrarButton.getText().toString().equals(NUEVA_CUENTA)) {
                    mEmailSignInButton.startAnimation(vibrar);
                    mEmailSignInButton.setText(R.string.action_registrarse);
                    mRegsitrarButton.setText(R.string.si_cuenta);
                    mEmailView.setVisibility(View.VISIBLE);
                    mNombreView.setVisibility(View.VISIBLE);
                    tnombrelayout.setVisibility(View.VISIBLE);
                    temaillyout.setVisibility(View.VISIBLE);
                    LinearLayout layout = findViewById(R.id.layoutregistrar);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                            , LinearLayout.LayoutParams.WRAP_CONTENT);
                    layout.setLayoutParams(params);
                } else {
                    mEmailSignInButton.startAnimation(vibrar);
                    mEmailSignInButton.setText(R.string.action_sign_in);
                    mRegsitrarButton.setText(R.string.nueva_cuenta);
                    mEmailView.setVisibility(View.INVISIBLE);
                    mNombreView.setVisibility(View.INVISIBLE);
                    tnombrelayout.setVisibility(View.INVISIBLE);
                    temaillyout.setVisibility(View.INVISIBLE);
                }

            }

        });

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            id_mensajero = user.getUid();
                            if(mEmailSignInButton.getText().equals("Registrarse")){
                                if(id_mensajero!=null){
                                    token = FirebaseInstanceId.getInstance().getToken();
                                    float cal = 5;
                                    String estado = Constantes.ESTADO_SUBIR_IMAGENES;
                                    placa = Constantes.BD_SIN_PLACA;
                                    DatabaseReference currentdatabase = database.child(Constantes.BD_ADMIN)
                                            .child(Constantes.BD_MENSAJERO).child(id_mensajero);
                                    codigo = currentdatabase.getKey();
                                    String urlfoto = Constantes.BD_RUTA_FOTOS_MENSAJERO + codigo +
                                            Constantes.BD_FOTO_DE_PERFIL;
                                    Mensajeros nuevomensajero;
                                    nuevomensajero = new Mensajeros(codigo,id_mensajero,nombre,urlfoto,cal,placa,email,numero
                                            ,password,token,estado,0.0,0.0);
                                    currentdatabase.setValue(nuevomensajero);

                                }
                            }
                            progressBar.setVisibility(View.INVISIBLE);
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            progressBar.setVisibility(View.INVISIBLE);
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                String error = ((FirebaseAuthInvalidCredentialsException) task.getException()).getErrorCode();
                                Log.e(TAG,((FirebaseAuthInvalidCredentialsException) task.getException()).getErrorCode());
                                if(error.equalsIgnoreCase("ERROR_SESSION_EXPIRED")){
                                    toast("El código ha expirado, solicite uno nuevamente");
                                }else{
                                    toast("Error código errado");
                                }
                            }
                        }
                    }
                });
    }
    private void registrar() {

        email = mEmailView.getText().toString().trim();
        password = mPasswordView.getText().toString();
        nombre = mNombreView.getText().toString();
        numero = mNumeroView.getText().toString();
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mNumeroView.setError(null);
        mNombreView.setError(null);

        View foco = null;
        Boolean cancel=false;

        if(!isEmailValid(email)){
            Toast.makeText(LoginActivity.this, "ingrese el email correctamente",
                    Toast.LENGTH_SHORT).show();
            mEmailView.setError(getString(R.string.error_invalid_email));
            foco = mEmailView;
            cancel = true;
        }else if(!isPasswordValid(password)){
            Toast.makeText(LoginActivity.this, "la contraseña debe tener almenos 4 digitos",
                    Toast.LENGTH_SHORT).show();
            mPasswordView.setError(getString(R.string.error_invalid_password));
            foco = mPasswordView;
            cancel = true;
        }else if (!isNombreValid(nombre)){
            Toast.makeText(LoginActivity.this, "el nombre es demasiado corto",
                    Toast.LENGTH_SHORT).show();
            mNombreView.setError(getString(R.string.error_invalid_nombre));
            foco = mNombreView;
            cancel = true;
        }else if (!isNumeroValid(numero)){
            Toast.makeText(LoginActivity.this, "el numero debe tener 10 digitos",
                    Toast.LENGTH_SHORT).show();
            mNumeroView.setError(getString(R.string.error_invalid_numero));
            foco = mNumeroView;
            cancel = true;
        }

        if(cancel) {

            foco.requestFocus();

        }else{
            try {
                signInWithPhoneAuthCredential(PhoneAuthProvider.getCredential(IdVerficacion,password));
            } catch (Exception e) {
                e.printStackTrace();
                toast("Intente nuevamente");
            }
        }

    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }
    private boolean isNombreValid(String nombre) {
        //TODO: Replace this with your own logic
        return nombre.length() > 5;
    }

    private boolean isNumeroValid(String numero) {
        //TODO: Replace this with your own logic
        return numero.length() == 10;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);


    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }


    }

    private void toast(String mensaje){
        Toast.makeText(this,mensaje,Toast.LENGTH_LONG).show();

    }
}
