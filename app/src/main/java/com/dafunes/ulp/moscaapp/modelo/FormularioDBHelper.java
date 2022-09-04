package com.dafunes.ulp.moscaapp.modelo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Diego on 16/03/2018.
 */

public class FormularioDBHelper extends SQLiteOpenHelper {

    public FormularioDBHelper(Context context) {
        super(context, FormularioDBDef.DATABASE_NAME, null, FormularioDBDef.DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(FormularioDBDef.FORMULARIOS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL(FormularioDBDef.FORMULARIOS_TABLE_DROP);
        this.onCreate( db );
    }
    public void insertForm(Formulario form){

        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FormularioDBDef.FORMULARIOS.CODIGOQR_COL,form.getCodigoqr());
        values.put(FormularioDBDef.FORMULARIOS.CORREO_COL,form.getCorreo());
        values.put(FormularioDBDef.FORMULARIOS.LAT_COL,form.getLatitud());
        values.put(FormularioDBDef.FORMULARIOS.LONG_COL,form.getLongitud());
        values.put(FormularioDBDef.FORMULARIOS.FECHA_COL,form.getFecha());
        values.put(FormularioDBDef.FORMULARIOS.HORA_COL,form.getHora());
        values.put(FormularioDBDef.FORMULARIOS.OBS_COL,form.getObs());

        db.insert(FormularioDBDef.FORMULARIOS.TABLE_NAME,null,values);

        db.close();
    }



    //Obtener uan Formulario dado un ID
    public Formulario getFormById(int id){
        // Declaramos un objeto Note para instanciarlo con el resultado del query
        Formulario aForm = null;

        // 1. Obtenemos una reference de la BD con permisos de lectura
        SQLiteDatabase db = this.getReadableDatabase();

        //Definimos un array con los nombres de las columnas que deseamos sacar
        String[] COLUMNS = {FormularioDBDef.FORMULARIOS.ID_COL, FormularioDBDef.FORMULARIOS.CODIGOQR_COL, FormularioDBDef.FORMULARIOS.CORREO_COL, FormularioDBDef.FORMULARIOS.LAT_COL,
                FormularioDBDef.FORMULARIOS.LONG_COL, FormularioDBDef.FORMULARIOS.FECHA_COL,FormularioDBDef.FORMULARIOS.HORA_COL,FormularioDBDef.FORMULARIOS.OBS_COL};


        // 2. Contruimos el query
        Cursor cursor =
                db.query(FormularioDBDef.FORMULARIOS.TABLE_NAME,  //Nombre de la tabla
                        COLUMNS, // b. Nombre de las Columnas
                        " id = ?", // c. Columnas de la clausula WHERE
                        new String[] { String.valueOf(id) }, // d. valores de las columnas de la clausula WHERE
                        null, // e. Clausula Group by
                        null, // f. Clausula having
                        null, // g. Clausula order by
                        null); // h. Limite de registros

        // 3. Si hemos obtenido algun resultado entonces sacamos el primero de ellos ya que se supone
        //que ha de existir un solo registro para un id
        if (cursor != null) {
            cursor.moveToFirst();
            // 4. Contruimos el objeto Formulario
            aForm = new Formulario();
            aForm.setId(Integer.parseInt(cursor.getString(0)));
            aForm.setCodigoqr(cursor.getString(1));
            aForm.setCorreo(cursor.getString(2));
            aForm.setLatitud(cursor.getString(3));
            aForm.setLongitud(cursor.getString(4));
            aForm.setFecha(cursor.getString(5));
            aForm.setHora(cursor.getString(6));
            aForm.setObs(cursor.getString(7));

        }

        // 5. Devolvemos el objeto Formulario
        return aForm;
    }

    public List<Formulario> getAllForms() {
        //Instanciamos un Array para llenarlo con todos los objetos Notes que saquemos de la BD
        List<Formulario> forms = new ArrayList<Formulario>();

        // 1. Aramos un String con el query a ejecutar
        String query = "SELECT  * FROM " + FormularioDBDef.FORMULARIOS.TABLE_NAME;

        // 2. Obtenemos una reference de la BD con permisos de escritura y ejecutamos el query
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. Iteramos entre cada uno de olos registros y agregarlos al array de Notas
        Formulario aForm = null;
        if (cursor.moveToFirst()) {
            do {
                aForm = new Formulario();
                aForm.setId(Integer.parseInt(cursor.getString(0)));
                aForm.setCodigoqr(cursor.getString(1));
                aForm.setCorreo(cursor.getString(2));
                aForm.setLatitud(cursor.getString(3));
                aForm.setLongitud(cursor.getString(4));
                aForm.setFecha(cursor.getString(5));
                aForm.setHora(cursor.getString(6));
                aForm.setObs(cursor.getString(7));

                // Add book to books
                forms.add(aForm);
            } while (cursor.moveToNext());
        }

        //Cerramos el cursor
        cursor.close();

        // Devolvemos los formularios encontradas o un array vacio en caso de que no se encuentre nada
        return forms;
    }


    public int updateForm(Formulario form){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FormularioDBDef.FORMULARIOS.CODIGOQR_COL,form.getCodigoqr());
        values.put(FormularioDBDef.FORMULARIOS.CORREO_COL,form.getCorreo());
        values.put(FormularioDBDef.FORMULARIOS.LAT_COL,form.getLatitud());
        values.put(FormularioDBDef.FORMULARIOS.LONG_COL,form.getLongitud());
        values.put(FormularioDBDef.FORMULARIOS.FECHA_COL,form.getFecha());
        values.put(FormularioDBDef.FORMULARIOS.HORA_COL,form.getHora());
        values.put(FormularioDBDef.FORMULARIOS.OBS_COL,form.getObs());

        int i=db.update(FormularioDBDef.FORMULARIOS.TABLE_NAME,
                values,
                FormularioDBDef.FORMULARIOS.ID_COL+"=?",
                new String[]{String.valueOf(form.getId() )});

        db.close();

        return i;
    }
    public void deleteForm(Formulario form){

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(FormularioDBDef.FORMULARIOS.TABLE_NAME,
                FormularioDBDef.FORMULARIOS.ID_COL+"=?",
                new String []{String.valueOf( form.getId() )});

        db.close();
    }
}
