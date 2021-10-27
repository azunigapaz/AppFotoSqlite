package com.josev.ejercicio23;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.josev.ejercicio23.configuracion.SQLiteConexion;
import com.josev.ejercicio23.configuracion.Transacciones;
import com.josev.ejercicio23.tablas.Imagenes;
import com.josev.ejercicio23.tablas.Person;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

public class ActivityConsultaImagenes extends AppCompatActivity {

    // Variables globales
    SQLiteConexion conexion;
    ListView lvconsultaimagenes;
    final ArrayList<Imagenes> lista = new ArrayList<Imagenes>();
    ArrayList<String> ArregloImagenes;
    Imagenes list_Imagenes = null;
    EditText txtbuscarimageneslista;
    ImageView imgreturnlc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta_imagenes);

        txtbuscarimageneslista = (EditText) findViewById(R.id.txtbuscarimagenlista);

        conexion = new SQLiteConexion(this, Transacciones.NameDatabase,null,1);
        lvconsultaimagenes = (ListView) findViewById(R.id.lvconsultaimagenes);

        imgreturnlc = (ImageView) findViewById(R.id.imgreturnlc);

        ObtenerListaImagenes();

        // Creamos el adapter
        ArrayAdapter adp =new ArrayAdapter(this, android.R.layout.simple_list_item_1,ArregloImagenes);

        lvconsultaimagenes.setAdapter(adp);

        txtbuscarimageneslista.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adp.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        imgreturnlc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });


    }

    private void ObtenerListaImagenes() {

        SQLiteDatabase db = conexion.getReadableDatabase();

        // creamos el cursor con el query
        Cursor cursor = db.rawQuery("SELECT * FROM " + Transacciones.tablaImagenes, null);

        // recorremos el cursor
        while(cursor.moveToNext()){

            list_Imagenes = new Imagenes();
            list_Imagenes.setId(cursor.getInt(0));
            list_Imagenes.setImagen(cursor.getBlob(1));
            list_Imagenes.setDescripcion(cursor.getString(2));

            lista.add(list_Imagenes);


        }

        //cerramos el cursor
        cursor.close();

        filllist();

    }

    private void filllist() {

        ArregloImagenes = new ArrayList<String>();

        Bitmap bitmap = null;
        for(int i = 0; i < lista.size(); i++){

            byte[] blob = lista.get(i).getImagen();
            ByteArrayInputStream bais = new ByteArrayInputStream(blob);
            bitmap = BitmapFactory.decodeStream(bais);

            ArregloImagenes.add(lista.get(i).getId() + " | " +
                    lista.get(i).getImagen() + " | " +
                    lista.get(i).getDescripcion() + " | ");
        }

    }

    private ArrayList<Person> getli(){
        Person person = null;
        ArrayList<Person> imagenesList = new ArrayList<>();

        SQLiteDatabase db = conexion.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM imagenes", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            person = new Person(cursor.getBlob(1));
            imagenesList.add(person);
            cursor.moveToNext();
        }
        return imagenesList;
    }


    public Bitmap buscarImagen(long id){
        SQLiteDatabase db = conexion.getReadableDatabase();

        String sql = String.format("SELECT * FROM imagenes WHERE id = %d", id);
        Cursor cursor = db.rawQuery(sql, new String[] {});
        Bitmap bitmap = null;
        if(cursor.moveToFirst()){
            byte[] blob = cursor.getBlob(1);
            ByteArrayInputStream bais = new ByteArrayInputStream(blob);
            bitmap = BitmapFactory.decodeStream(bais);
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        db.close();
        return bitmap;
    }


}