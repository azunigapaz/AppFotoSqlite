package com.josev.ejercicio23;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.josev.ejercicio23.configuracion.SQLiteConexion;
import com.josev.ejercicio23.configuracion.Transacciones;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    /* Declaracion de Variables */
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int PETICION_ACCESO_CAM = 101;
    private static final int IMAGE_PICK_GALLERY_CODE = 102;

    Uri photoUri;
    EditText txtdescripcionphoto;
    ImageView imageView;
    Bitmap bitmap;
    Bitmap imgBitmap;

    ImageButton imgbtnCamara, imgbtnGuardar, imgbtnGaleria, imgbtnmostraritems;
    String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtdescripcionphoto = (EditText) findViewById(R.id.txtdescripcionphoto);
        imageView = (ImageView) findViewById(R.id.imageView);

        imgbtnCamara = (ImageButton) findViewById(R.id.imgbtncamara);
        imgbtnGuardar = (ImageButton) findViewById(R.id.imgbtnguardarphoto);
        imgbtnGaleria = (ImageButton) findViewById(R.id.imgbtngaleria);
        imgbtnmostraritems = (ImageButton) findViewById(R.id.imgbtnmostraritems);
        imgbtnCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permisos();
            }
        });

        imgbtnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //guardarImagen();
                saveImagen(imgBitmap,txtdescripcionphoto.getText().toString());
            }
        });

        imgbtnGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityListaImagenes.class);
                startActivity(intent);
            }
        });

        imgbtnmostraritems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityConsultaImagenes.class);
                startActivity(intent);
            }
        });

    }



    private void permisos() {

        // Valido si el permiso esta otorgado
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            // Otorgo el permiso si no lo tengo
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.CAMERA }, PETICION_ACCESO_CAM);
        } else {
            tomarPhoto();
        }

    }

    @Override
    public void onRequestPermissionsResult(int RequestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(RequestCode, permissions, grantResults);

        if (RequestCode == PETICION_ACCESO_CAM) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                tomarPhoto();
            } else {
                Toast.makeText(getApplicationContext(), "Se necesita el permiso de camara", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void tomarPhoto() {

        Intent TomarPhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (TomarPhoto.resolveActivity(getPackageManager()) != null) {
            File photo = null;
            try {
                photo = createImageFile();
            } catch(IOException ex) {
                Log.e("Error", ex.toString());
            }

            if(photo != null) {
                photoUri = FileProvider.getUriForFile(this, "com.josev.ejercicio23.fileprovider", photo);
                TomarPhoto.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(TomarPhoto, REQUEST_IMAGE_CAPTURE);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Bundle extras = data.getExtras();
            imgBitmap = BitmapFactory.decodeFile(currentPhotoPath);
            imageView.setImageBitmap(imgBitmap);
        }
    }

    private File createImageFile() throws IOException {

        String imageFileName = "JPEG_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;

    }

    private void guardarImagen() {

        SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.NameDatabase, null, 1);
        SQLiteDatabase db = conexion.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put(Transacciones.imagen, imageView.getImageAlpha());
        valores.put(Transacciones.descripcion, txtdescripcionphoto.getText().toString());

        Long resultado = db.insert(Transacciones.tablaImagenes, Transacciones.id, valores);
        Toast.makeText(getApplicationContext(), "Imagen Agregada a la BD: ID: " + resultado.toString(), Toast.LENGTH_LONG).show();
        db.close();
        cleenscreen();

    }

    public void saveImagen(Bitmap bitmap, String descripcion){
        // tamaño del baos depende del tamaño de tus imagenes en promedio
        ByteArrayOutputStream baos = new ByteArrayOutputStream(20480);
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 , baos);
        byte[] blob = baos.toByteArray();
        // aqui tenemos el byte[] con el imagen comprimido, ahora lo guardemos en SQLite
        SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.NameDatabase, null, 1);
        SQLiteDatabase db = conexion.getWritableDatabase();

        String sql = "INSERT INTO imagenes (imagen, descripcion) VALUES(?,?)";
        SQLiteStatement insert = db.compileStatement(sql);
        insert.clearBindings();
        insert.bindBlob(1, blob);
        insert.bindString(2, descripcion);
        insert.executeInsert();
        db.close();

        Toast.makeText(getApplicationContext(), "Imagen Agregada a la BD: ID: ", Toast.LENGTH_LONG).show();

        cleenscreen();
    }

    private void cleenscreen() {

        txtdescripcionphoto.setText("");

    }

}