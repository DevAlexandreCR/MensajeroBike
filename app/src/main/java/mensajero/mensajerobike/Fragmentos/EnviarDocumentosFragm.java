package mensajero.mensajerobike.Fragmentos;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


import mensajero.mensajerobike.Constantes.Constantes;
import mensajero.mensajerobike.MainActivity;
import mensajero.mensajerobike.R;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class EnviarDocumentosFragm extends Fragment {

    public static final String TAG = "Enviar Documentos";
    private static final int CODE_PERFIL_CAMARA = 1;
    private static final int CODE_PERFIL_GALERIA = 11;
    private static final int CODE_LICENCIA_CAMARA = 2;
    private static final int CODE_LICENCIA_GALERIA = 22;
    private static final int CODE_SEGURO_CAMARA = 3;
    private static final int CODE_SEGURO_GALERIA = 33;
    private static final int CODE_TARJETA_CAMARA = 4;
    private static final int CODE_TARJETA_GALERIA = 44;
    private Button botonFotoPerfil;
    private Button botonFotoLicencia;
    private Button botonFotoSeguro;
    private Button botonFotoTarjeta;
    private Button botonGaleriaPerfil;
    private Button botonGaleriaLicencia;
    private Button botonGaleriaSeguro;
    private Button getBotonGaleriaTarjeta;
    private View.OnClickListener listenerCamara;
    private View.OnClickListener listenerGaleria;
    private ImageView imageviewperfil;
    private Bitmap imagenPerfil;
    private Bitmap imagenLicencia1;
    private Bitmap imagenLicencia2;
    private Bitmap imagenSeguro1;
    private Bitmap imagenseguro2;
    private Bitmap imagenTarjeta1;
    private Bitmap imagenTarjeta2;
    private StorageReference storage;
    private CheckBox checkBoxperfil,checkBox_lic1,checkBox_lic2,checkBox_seg1,checkBox_seg2,checkBox_tar1,checkBox_tar2;
    private String codigo;
    private ProgressBar subiendoImagenes;
    private Boolean TodosEnviados = false;
    private byte[] data0, data1, data2, data3, data4, data5, data6;
    private File photoFile;
    private Uri uri;
    String mCurrentPhotoPath;
    private AlertDialog alert;
    private CountDownTimer cuentaAtrasEnviarFoto;

    public EnviarDocumentosFragm() {

    }

    public EnviarDocumentosFragm(String cod) {
        // Required empty public constructor
        codigo = cod;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_enviar_documentos, container, false);

        botonFotoPerfil = v.findViewById(R.id.cam1);
        botonFotoLicencia = v.findViewById(R.id.cam2);
        botonFotoSeguro = v.findViewById(R.id.cam3);
        botonFotoTarjeta = v.findViewById(R.id.cam4);
        botonGaleriaPerfil = v.findViewById(R.id.galeria1);
        botonGaleriaLicencia = v.findViewById(R.id.galeria2);
        botonGaleriaSeguro = v.findViewById(R.id.galeria3);
        getBotonGaleriaTarjeta = v.findViewById(R.id.galerai4);
        imageviewperfil = v.findViewById(R.id.imagenperfil);
        subiendoImagenes = v.findViewById(R.id.progressenviar);
        subiendoImagenes.setVisibility(View.INVISIBLE);
        checkBoxperfil=v.findViewById(R.id.checkBox_perfil);
        checkBoxperfil.setClickable(false);
        checkBox_lic1 = v.findViewById(R.id.checkBox_lic_);
        checkBox_lic1.setClickable(false);
        checkBox_lic2 = v.findViewById(R.id.checkBox_lic2);
        checkBox_lic2.setClickable(false);
        checkBox_seg1 = v.findViewById(R.id.checkBox_seg1);
        checkBox_seg1.setClickable(false);
        checkBox_seg2 = v.findViewById(R.id.checkBox_seg2);
        checkBox_seg2.setClickable(false);
        checkBox_tar1 = v.findViewById(R.id.checkBox_tar1);
        checkBox_tar1.setClickable(false);
        checkBox_tar2 = v.findViewById(R.id.checkBox_tar2);
        checkBox_tar2.setClickable(false);

        listenerCamara = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();

                switch (id) {
                    case R.id.cam1:
                        abrirCamara(CODE_PERFIL_CAMARA);
                        break;
                    case R.id.cam2:
                        abrirCamara(CODE_LICENCIA_CAMARA);
                        break;
                    case R.id.cam3:
                        abrirCamara(CODE_SEGURO_CAMARA);
                        break;
                    case R.id.cam4:
                        abrirCamara(CODE_TARJETA_CAMARA);
                        break;
                }
            }
        };
        listenerGaleria = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("click", "galeria");
                int id = v.getId();
                switch (id) {
                    case R.id.galeria1:
                        abrirGaleria(CODE_PERFIL_GALERIA);
                        break;
                    case R.id.galeria2:
                        abrirGaleria(CODE_LICENCIA_GALERIA);
                        break;
                    case R.id.galeria3:
                        abrirGaleria(CODE_SEGURO_GALERIA);
                        break;
                    case R.id.galerai4:
                        abrirGaleria(CODE_TARJETA_GALERIA);
                        break;
                }
            }
        };


        // Inflate the layout for this fragment
        return v;
    }

    private void EnviarDocumentos(byte[] bytes, String nombre_archivo, final CheckBox checkBox) {

        subiendoImagenes.setVisibility(View.VISIBLE);
        alert.show();

        final UploadTask uploadTask= storage.child(nombre_archivo).putBytes(bytes);
        cuentaAtrasEnviarFoto = new CountDownTimer(10000,1000) {
            @Override
            public void onTick(long l) {
                alert.setMessage("Enviando foto... "+ l/1000);
            }

            @Override
            public void onFinish() {
                alert.dismiss();
                subiendoImagenes.setVisibility(View.INVISIBLE);
                uploadTask.cancel();
                Toast.makeText(getActivity(),"verifique su conexión a internet e intente nuevamente",Toast.LENGTH_LONG).show();
            }
        };
        cuentaAtrasEnviarFoto.start();

        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.i("imagen subida", task.toString());
                    checkBox.setChecked(true);
                    subiendoImagenes.setVisibility(View.INVISIBLE);
                    alert.dismiss();
                    cuentaAtrasEnviarFoto.cancel();
                    if (TodosLosAcrchivosEnviados()) {
                        DatabaseReference database = FirebaseDatabase.getInstance().getReference()
                                .child(Constantes.BD_GERENTE).child(Constantes.BD_ADMIN).child(Constantes.BD_MENSAJERO)
                                .child(codigo);
                        database.child(Constantes.BD_ESTADO_MENSAJERO).setValue(Constantes.ESTADO_VERIFICAR);
                        toast("Archivos enviados exitosamente");
                        getFragmentManager().beginTransaction().remove(EnviarDocumentosFragm.this).commit();
                        Intent lanzarmain = new Intent(getActivity(), MainActivity.class);
                        lanzarmain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        lanzarmain.setAction(Constantes.ACTION_INICIO_SESION);
                        startActivity(lanzarmain);

                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                subiendoImagenes.setVisibility(View.INVISIBLE);
                alert.dismiss();
                cuentaAtrasEnviarFoto.cancel();
                toast("La imagen no fue enviada, intente mas tarde");
            }
        });




    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        botonFotoPerfil.setOnClickListener(listenerCamara);
        botonFotoLicencia.setOnClickListener(listenerCamara);
        botonFotoSeguro.setOnClickListener(listenerCamara);
        botonFotoTarjeta.setOnClickListener(listenerCamara);
        botonGaleriaPerfil.setOnClickListener(listenerGaleria);
        botonGaleriaLicencia.setOnClickListener(listenerGaleria);
        botonGaleriaSeguro.setOnClickListener(listenerGaleria);
        getBotonGaleriaTarjeta.setOnClickListener(listenerGaleria);

        String path = "movil_" + codigo;
        storage = FirebaseStorage.getInstance().getReference().child("mensajeros")
                .child("mensajero_moto").child(path);

        alert = new AlertDialog.Builder(getActivity()).
                setMessage("Enviando foto...")
                .setCancelable(false).create();
    }

    private void abrirCamara(int Requestcode) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
            Log.i("version", "6 o superior");
            // versiones con android 6.0 o superior
            int permi = getActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if(permi != PackageManager.PERMISSION_GRANTED){
                Log.i("PERMISION", "DENIED");
                requestPermissions( new String[] {android.Manifest.permission.WRITE_EXTERNAL_STORAGE},101);
            }else{
                Intent CamaraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (CamaraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    Log.i("PERMISION", "GRANTED" );
                    // Create the File where the photo should go
                    photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        ex.printStackTrace();

                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        uri = FileProvider.getUriForFile(getActivity(),
                                "mensajero.mensajerobike.fileprovider",
                                photoFile);
                        CamaraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        //galleryAddPic();
                        startActivityForResult(CamaraIntent, Requestcode);
                    }
                }
            }
        } else{
            // para versiones anteriores a android 6.0
            Intent CamaraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (CamaraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                // Create the File where the photo should go
                photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    ex.printStackTrace();

                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    uri =  FileProvider.getUriForFile(getActivity(),
                            "mensajero.mensajerobike.fileprovider",
                            photoFile);
                    CamaraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(CamaraIntent, Requestcode);
                }
            }
        }

    }

    private void abrirGaleria(int Requestcode) {

        Intent GaleriaIntent = new Intent(Intent.ACTION_PICK);
        GaleriaIntent.putExtra("galeria","GALERIA");
        GaleriaIntent.setType("image/*");
        if (GaleriaIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(GaleriaIntent, Requestcode);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Matrix matrix = new Matrix();

        if (resultCode == RESULT_OK) {
                switch (requestCode) {
                    case CODE_PERFIL_CAMARA:
                            try {
                                Log.i("CAMARA", uri.toString());
                                imagenPerfil = MediaStore.Images.Media.getBitmap(getActivity()
                                        .getContentResolver(), uri);
                                matrix.postRotate(MainActivity.getImageRotation(uri,getActivity()));
                                imagenPerfil = Bitmap.createBitmap(imagenPerfil, 0, 0, imagenPerfil.getWidth(), imagenPerfil.getHeight(), matrix, true);
                                imagenPerfil.compress(Bitmap.CompressFormat.JPEG, 30, outputStream);
                                data0 = outputStream.toByteArray();
                                final String IMAGEN_PERFIL = "foto_perfil";
                                EnviarDocumentos(data0,IMAGEN_PERFIL,checkBoxperfil);
                                data0 = null;
                                imageviewperfil.setImageBitmap(imagenPerfil);
                            } catch (NullPointerException | IOException  i) {
                                i.printStackTrace();
                            }

                            break;
                    case CODE_PERFIL_GALERIA:
                            try {
                                Log.i("GALERIA", "true");
                                Uri imageuri = data.getData();
                                imagenPerfil = MediaStore.Images.Media.getBitmap(getActivity()
                                        .getContentResolver(), imageuri);
                                matrix.postRotate(MainActivity.getImageRotation(imageuri, getActivity()));
                                imagenPerfil = Bitmap.createBitmap(imagenPerfil, 0, 0, imagenPerfil.getWidth(), imagenPerfil.getHeight(), matrix, true);
                                imagenPerfil.compress(Bitmap.CompressFormat.JPEG, 30, outputStream);
                                //imagenPerfil = Bitmap.createScaledBitmap(imagenPerfil, 50, 50, false);
                                data0 = outputStream.toByteArray();
                                final String IMAGEN_PERFIL = "foto_perfil";
                                EnviarDocumentos(data0,IMAGEN_PERFIL,checkBoxperfil);
                                data0 = null;
                                imageviewperfil.setImageBitmap(imagenPerfil);
                            } catch (IOException e) {
                                toast("Ocurrio un error intente nuevamente");
                            }

                        break;
                    case CODE_LICENCIA_CAMARA:
                            try {
                                if (imagenLicencia1 == null) {
                                    imagenLicencia1 = MediaStore.Images.Media.getBitmap(getActivity()
                                            .getContentResolver(), uri);
                                    matrix.postRotate(MainActivity.getImageRotation(uri, getActivity()));
                                    imagenLicencia1 = Bitmap.createBitmap(imagenLicencia1, 0, 0, imagenLicencia1.getWidth(), imagenLicencia1.getHeight(), matrix, true);
                                    imagenLicencia1.compress(Bitmap.CompressFormat.JPEG, 30, outputStream);
                                    //imagenLicencia1 = Bitmap.createScaledBitmap(imagenLicencia1, 50, 50, false);
                                    data1 = outputStream.toByteArray();
                                    final String IMAGEN_LIC_1 = "foto_licencia1";
                                    EnviarDocumentos(data1,IMAGEN_LIC_1,checkBox_lic1);
                                    data1 = null;
                                    if (imagenLicencia1!=null) {
                                        imagenLicencia1.recycle();
                                    }
                                } else {
                                    imagenLicencia2 = MediaStore.Images.Media.getBitmap(getActivity()
                                            .getContentResolver(), uri);
                                    matrix.postRotate(MainActivity.getImageRotation(uri, getActivity()));
                                    imagenLicencia2 = Bitmap.createBitmap(imagenLicencia2, 0, 0, imagenLicencia2.getWidth(), imagenLicencia2.getHeight(), matrix, true);
                                    imagenLicencia2.compress(Bitmap.CompressFormat.JPEG, 30, outputStream);
                                    //imagenLicencia2 = Bitmap.createScaledBitmap(imagenLicencia2, 50, 50, false);
                                    data2 = outputStream.toByteArray();
                                    final String IMAGEN_LIC_2 = "foto_licencia2";
                                    EnviarDocumentos(data2,IMAGEN_LIC_2,checkBox_lic2);
                                    data2 = null;
                                    if (imagenLicencia2!=null) {
                                        imagenLicencia2.recycle();
                                    }
                                }

                            } catch (NullPointerException | IOException i) {
                                i.printStackTrace();
                            }
                            break;
                    case CODE_LICENCIA_GALERIA:
                            try {
                                Uri imageuri = data.getData();
                                if (imagenLicencia1 == null) {
                                    imagenLicencia1 = MediaStore.Images.Media.getBitmap(getActivity()
                                            .getContentResolver(), imageuri);
                                    matrix.postRotate(MainActivity.getImageRotation(imageuri, getActivity()));
                                    imagenLicencia1 = Bitmap.createBitmap(imagenLicencia1, 0, 0, imagenLicencia1.getWidth(), imagenLicencia1.getHeight(), matrix, true);
                                    imagenLicencia1.compress(Bitmap.CompressFormat.JPEG, 30, outputStream);
                                    //imagenLicencia1 = Bitmap.createScaledBitmap(imagenLicencia1, 50, 50, false);
                                    data1 = outputStream.toByteArray();
                                    final String IMAGEN_LIC_1 = "foto_licencia1";
                                    EnviarDocumentos(data1,IMAGEN_LIC_1,checkBox_lic1);
                                    data1 = null;
                                    if (imagenLicencia1!=null) {
                                        imagenLicencia1.recycle();
                                    }

                                } else {
                                    imagenLicencia2 = MediaStore.Images.Media.getBitmap(getActivity()
                                            .getContentResolver(), imageuri);
                                    matrix.postRotate(MainActivity.getImageRotation(imageuri, getActivity()));
                                    imagenLicencia2 = Bitmap.createBitmap(imagenLicencia2, 0, 0, imagenLicencia2.getWidth(), imagenLicencia2.getHeight(), matrix, true);
                                    imagenLicencia2.compress(Bitmap.CompressFormat.JPEG, 30, outputStream);
                                    //imagenLicencia2 = Bitmap.createScaledBitmap(imagenLicencia2, 50, 50, false);
                                    data2 = outputStream.toByteArray();
                                    final String IMAGEN_LIC_2 = "foto_licencia2";
                                    EnviarDocumentos(data2,IMAGEN_LIC_2,checkBox_lic2);
                                    data2 = null;
                                    if (imagenLicencia2!=null) {
                                        imagenLicencia2.recycle();
                                    }
                                }

                            } catch (IOException e) {
                                toast("Ocurrio un error intente nuevamente");
                            }

                        break;
                    case CODE_SEGURO_CAMARA:

                            try {

                                if (imagenSeguro1 == null) {
                                    imagenSeguro1 = MediaStore.Images.Media.getBitmap(getActivity()
                                            .getContentResolver(), uri);
                                    matrix.postRotate(MainActivity.getImageRotation(uri, getActivity()));
                                    imagenSeguro1 = Bitmap.createBitmap(imagenSeguro1, 0, 0, imagenSeguro1.getWidth(), imagenSeguro1.getHeight(), matrix, true);
                                    imagenSeguro1.compress(Bitmap.CompressFormat.JPEG, 30, outputStream);
                                    //imagenSeguro1 = Bitmap.createScaledBitmap(imagenSeguro1, 50, 50, false);
                                    data3 = outputStream.toByteArray();
                                    final String IMAGEN_SEG_1 = "foto_seguro1";
                                    EnviarDocumentos(data3,IMAGEN_SEG_1,checkBox_seg1);
                                    data3 = null;
                                    if (imagenSeguro1!=null) {
                                        imagenSeguro1.recycle();
                                    }

                                } else {
                                    imagenseguro2 = MediaStore.Images.Media.getBitmap(getActivity()
                                            .getContentResolver(), uri);
                                    matrix.postRotate(MainActivity.getImageRotation(uri, getActivity()));
                                    imagenseguro2 = Bitmap.createBitmap(imagenseguro2, 0, 0, imagenseguro2.getWidth(), imagenseguro2.getHeight(), matrix, true);
                                    imagenseguro2.compress(Bitmap.CompressFormat.JPEG, 30, outputStream);
                                    //imagenseguro2 = Bitmap.createScaledBitmap(imagenseguro2, 50, 50, false);
                                    data4 = outputStream.toByteArray();
                                    final String IMAGEN_SEG_2 = "foto_seguro2";
                                    EnviarDocumentos(data4,IMAGEN_SEG_2,checkBox_seg2);
                                    data4 = null;
                                    if (imagenseguro2!=null) {
                                        imagenseguro2.recycle();
                                    }
                                }
                            } catch (NullPointerException | IOException i) {
                                i.printStackTrace();
                            }
                            break;
                    case CODE_SEGURO_GALERIA:
                            try {
                                Uri imageuri = data.getData();
                                if (imagenSeguro1 == null) {
                                    imagenSeguro1 = MediaStore.Images.Media.getBitmap(getActivity()
                                            .getContentResolver(), imageuri);
                                    matrix.postRotate(MainActivity.getImageRotation(imageuri, getActivity()));
                                    imagenSeguro1 = Bitmap.createBitmap(imagenSeguro1, 0, 0, imagenSeguro1.getWidth(), imagenSeguro1.getHeight(), matrix, true);
                                    imagenSeguro1.compress(Bitmap.CompressFormat.JPEG, 30, outputStream);
                                    //imagenSeguro1 = Bitmap.createScaledBitmap(imagenSeguro1, 50, 50, false);
                                    data3 = outputStream.toByteArray();
                                    final String IMAGEN_SEG_1 = "foto_seguro1";
                                    EnviarDocumentos(data3,IMAGEN_SEG_1,checkBox_seg1);
                                    data3 = null;
                                    if (imagenSeguro1!=null) {
                                        imagenSeguro1.recycle();
                                    }
                                } else {
                                    imagenseguro2 = MediaStore.Images.Media.getBitmap(getActivity()
                                            .getContentResolver(), imageuri);
                                    matrix.postRotate(MainActivity.getImageRotation(imageuri, getActivity()));
                                    imagenseguro2 = Bitmap.createBitmap(imagenseguro2, 0, 0, imagenseguro2.getWidth(), imagenseguro2.getHeight(), matrix, true);
                                    imagenseguro2.compress(Bitmap.CompressFormat.JPEG, 30, outputStream);
                                    //imagenseguro2 = Bitmap.createScaledBitmap(imagenseguro2, 50, 50, false);
                                    data4 = outputStream.toByteArray();
                                    final String IMAGEN_SEG_2 = "foto_seguro2";
                                    EnviarDocumentos(data4,IMAGEN_SEG_2,checkBox_seg2);
                                    data4 = null;
                                    if (imagenseguro2!=null) {
                                        imagenseguro2.recycle();
                                    }
                                }

                            } catch (IOException e) {
                                toast("Ocurrio un error intente nuevamente");
                            }

                        break;
                    case CODE_TARJETA_CAMARA:

                            try {
                                if (imagenTarjeta1 == null) {
                                    imagenTarjeta1 = MediaStore.Images.Media.getBitmap(getActivity()
                                            .getContentResolver(), uri);
                                    matrix.postRotate(MainActivity.getImageRotation(uri, getActivity()));
                                    imagenTarjeta1 = Bitmap.createBitmap(imagenTarjeta1, 0, 0, imagenTarjeta1.getWidth(), imagenTarjeta1.getHeight(), matrix, true);
                                    imagenTarjeta1.compress(Bitmap.CompressFormat.JPEG, 30, outputStream);
                                    //imagenTarjeta1 = Bitmap.createScaledBitmap(imagenTarjeta1, 50, 50, false);
                                    data5 = outputStream.toByteArray();
                                    final String IMAGEN_TAR_1 = "foto_tarjeta1";
                                    EnviarDocumentos(data5,IMAGEN_TAR_1,checkBox_tar1);
                                    data5 = null;
                                    if (imagenTarjeta1!=null) {
                                        imagenTarjeta1.recycle();
                                    }
                                } else {
                                    imagenTarjeta2 = MediaStore.Images.Media.getBitmap(getActivity()
                                            .getContentResolver(), uri);
                                    matrix.postRotate(MainActivity.getImageRotation(uri, getActivity()));
                                    imagenTarjeta2 = Bitmap.createBitmap(imagenTarjeta2, 0, 0, imagenTarjeta2.getWidth(), imagenTarjeta2.getHeight(), matrix, true);
                                    imagenTarjeta2.compress(Bitmap.CompressFormat.JPEG, 30, outputStream);
                                    //imagenTarjeta2 = Bitmap.createScaledBitmap(imagenTarjeta2, 50, 50, false);
                                    data6 = outputStream.toByteArray();
                                    final String IMAGEN_TAR_2 = "foto_tarjeta2";
                                    EnviarDocumentos(data6,IMAGEN_TAR_2,checkBox_tar2);
                                    data6 = null;
                                    if (imagenTarjeta2!=null) {
                                        imagenTarjeta2.recycle();
                                    }
                                }
                            } catch (NullPointerException | IOException i) {
                                i.printStackTrace();
                            }
                            break;
                    case CODE_TARJETA_GALERIA:
                            try {
                                Uri imageuri = data.getData();
                                if (imagenTarjeta1 == null) {
                                    imagenTarjeta1 = MediaStore.Images.Media.getBitmap(getActivity()
                                            .getContentResolver(), imageuri);
                                    matrix.postRotate(MainActivity.getImageRotation(imageuri, getActivity()));
                                    imagenTarjeta1 = Bitmap.createBitmap(imagenTarjeta1, 0, 0, imagenTarjeta1.getWidth(), imagenTarjeta1.getHeight(), matrix, true);
                                    imagenTarjeta1.compress(Bitmap.CompressFormat.JPEG, 30, outputStream);
                                    //imagenTarjeta1 = Bitmap.createScaledBitmap(imagenTarjeta1, 50, 50, false);
                                    data5 = outputStream.toByteArray();
                                    final String IMAGEN_TAR_1 = "foto_tarjeta1";
                                    EnviarDocumentos(data5,IMAGEN_TAR_1,checkBox_tar1);
                                    data5 = null;
                                    if (imagenTarjeta1!=null) {
                                        imagenTarjeta1.recycle();
                                    }
                                } else {
                                    imagenTarjeta2 = MediaStore.Images.Media.getBitmap(getActivity()
                                            .getContentResolver(), imageuri);
                                    matrix.postRotate(MainActivity.getImageRotation(imageuri, getActivity()));
                                    imagenTarjeta2 = Bitmap.createBitmap(imagenTarjeta2, 0, 0, imagenTarjeta2.getWidth(), imagenTarjeta2.getHeight(), matrix, true);
                                    imagenTarjeta2.compress(Bitmap.CompressFormat.JPEG, 30, outputStream);
                                    //imagenTarjeta2 = Bitmap.createScaledBitmap(imagenTarjeta2, 50, 50, false);
                                    data6 = outputStream.toByteArray();
                                    final String IMAGEN_TAR_2 = "foto_tarjeta2";
                                    EnviarDocumentos(data6,IMAGEN_TAR_2,checkBox_tar2);
                                    data6 = null;
                                    if (imagenTarjeta2!=null) {
                                        imagenTarjeta2.recycle();
                                    }
                                }

                            } catch (IOException e) {
                                toast("Ocurrio un error intente nuevamente");
                            }
                        break;
                }

        } else {
            Log.i("resultactiuvity", "error en la foto");
            toast("No se seleccionó ninguna imagen");
        }
    }

    public Boolean TodosLosAcrchivosEnviados(){
        if(getImagenPerfil()==null){
            return  false;
        }else if(getImagenLicencia1()==null){
            return  false;
        }else if(getImagenLicencia2()==null){
            return  false;
        }else if(getImagenSeguro1()==null){
            return  false;
        }else if(getImagenseguro2()==null){
            return  false;
        }else if(getImagenTarjeta1()==null){
            return  false;
        }else if(getImagenTarjeta2()==null){
            return  false;
        }else{
            return  true;
        }
    }

    public Bitmap getImagenPerfil() {
        return imagenPerfil;
    }

    public Bitmap getImagenLicencia1() {
        return imagenLicencia1;
    }

    public Bitmap getImagenLicencia2() {
        return imagenLicencia2;
    }

    public Bitmap getImagenSeguro1() {
        return imagenSeguro1;
    }

    public Bitmap getImagenseguro2() {
        return imagenseguro2;
    }

    public Bitmap getImagenTarjeta1() {
        return imagenTarjeta1;
    }

    public Bitmap getImagenTarjeta2() {
        return imagenTarjeta2;
    }

    public void toast(String Mensaje) {
        Toast.makeText(getActivity(), Mensaje, Toast.LENGTH_LONG).show();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        uri = Uri.fromFile(f);
        mediaScanIntent.setData(uri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

}
