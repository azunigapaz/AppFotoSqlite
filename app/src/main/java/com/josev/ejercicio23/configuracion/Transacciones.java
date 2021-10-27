package com.josev.ejercicio23.configuracion;

public class Transacciones {

    public static final String NameDatabase = "PM01DB";

    public static final String tablaImagenes = "imagenes";

    public static final String id = "id";
    public static final String imagen = "imagen";
    public static final String descripcion = "descripcion";

    public static final String CreateTablaImagenes = "CREATE TABLE imagenes (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                                                                "imagen BLOB," +
                                                                                "descripcion TEXT)";

    public static final String DROPTableImagenes = "DROP TABLE IF EXISTS imagenes";

}
