package com.josev.ejercicio23;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;

import com.josev.ejercicio23.configuracion.SQLiteConexion;
import com.josev.ejercicio23.configuracion.Transacciones;
import com.josev.ejercicio23.tablas.Person;

import java.util.ArrayList;

public class ActivityListaImagenes extends AppCompatActivity {
    SQLiteConexion conexion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewcontentlayout);

        conexion = new SQLiteConexion(this, Transacciones.NameDatabase,null,1);
        ListView listView = (ListView) findViewById(R.id.listview1);
        ArrayList<Person> dolist = getli();
        listadapter adp = new listadapter(ActivityListaImagenes.this,R.layout.view_content_layout,dolist);
        listView.setAdapter(adp);
    }

    private ArrayList<Person> getli(){
        Person person = null;
        ArrayList<Person> imagenesList = new ArrayList<>();

        SQLiteDatabase db = conexion.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM imagenes", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            person = new Person(cursor.getBlob(2));
            imagenesList.add(person);
            cursor.moveToNext();
        }
        return imagenesList;
    }

}