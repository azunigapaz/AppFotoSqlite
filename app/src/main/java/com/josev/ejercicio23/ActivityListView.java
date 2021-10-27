package com.josev.ejercicio23;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;

import com.josev.ejercicio23.configuracion.SQLiteConexion;
import com.josev.ejercicio23.configuracion.Transacciones;
import com.josev.ejercicio23.tablas.Imagenes;

import java.text.ParseException;
import java.util.ArrayList;

public class ActivityListView extends AppCompatActivity {

    SQLiteConexion conexion;
    ListView listaimagenes;
    ArrayList<Imagenes> lista;
    ArrayList<String> ArregloImagenes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        
        conexion = new SQLiteConexion(this, Transacciones.NameDatabase, null, 1);
        
        ObtenerImagenes();
    }

    private void ObtenerImagenes() {

        SQLiteDatabase db = conexion.getReadableDatabase();
        Imagenes list_imagenes = null;
        lista = new ArrayList<Imagenes>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Transacciones.tablaImagenes, null);

        while (cursor.moveToNext()) {
            list_imagenes = new Imagenes();
            list_imagenes.setId(cursor.getInt(0));
            //list_imagenes.setImagen(cursor.getBlob(1));
            list_imagenes.setDescripcion(cursor.getString(2));

            lista.add(list_imagenes);
        }

        cursor.close();
        filllist();
    }

    private void filllist() {

        ArregloImagenes = new ArrayList<String>();

        for(int i=0; i < lista.size(); i++) {
            ArregloImagenes.add(lista.get(i).getId() +" | "+ lista.get(i).getDescripcion());
        }
    }
}