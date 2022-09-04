package com.dafunes.ulp.moscaapp.modelo;

/**
 * Created by Diego on 16/03/2018.
 */

public class FormularioDBDef {
    public static final String DATABASE_NAME = "MOSCA";
    public static final int DATABASE_VERSION = 1;

    public static class FORMULARIOS {
        //Nombre de el formulario
        public static final String TABLE_NAME = "formularios";
        //Nombre de las columnas que contiene el formulario
        public static final String ID_COL = "id";
        public static final String CODIGOQR_COL = "codigoqr";
        public static final String CORREO_COL = "correo";
        public static final String LAT_COL = "latitud";
        public static final String LONG_COL = "longitud";
        public static final String FECHA_COL = "fecha";
        public static final String HORA_COL = "hora";
        public static final String OBS_COL = "observaciones";
    }

    //Sentencia SQL que permite crear la tabla Formularios
        public static final String FORMULARIOS_TABLE_CREATE =
                "CREATE TABLE " +FORMULARIOS.TABLE_NAME+"("+
                        FORMULARIOS.ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                        FORMULARIOS.CODIGOQR_COL + " TEXT, "+
                        FORMULARIOS.CORREO_COL + " TEXT, "+
                        FORMULARIOS.LAT_COL + " TEXT, "+
                        FORMULARIOS.LONG_COL + " TEXT, "+
                        FORMULARIOS.FECHA_COL + " TEXT, "+
                        FORMULARIOS.HORA_COL + " TEXT, "+
                        FORMULARIOS.OBS_COL + " TEXT);";

        //Sentencia SQL que permite eliminar la tabla Formularios
        public static final String FORMULARIOS_TABLE_DROP = "DROP TABLE IF EXISTS " + FORMULARIOS.TABLE_NAME;

    }

