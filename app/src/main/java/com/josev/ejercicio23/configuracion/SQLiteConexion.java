package com.josev.ejercicio23.configuracion;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.josev.ejercicio23.tablas.Person;

import java.util.ArrayList;

public class SQLiteConexion extends SQLiteOpenHelper {

    SQLiteConexion conexion;

    public SQLiteConexion(Context context, String dbname, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, dbname, factory, version);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Transacciones.CreateTablaImagenes);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Transacciones.DROPTableImagenes);
        onCreate(db);
    }

}
