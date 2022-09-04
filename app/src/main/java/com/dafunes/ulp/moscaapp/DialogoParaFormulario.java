package com.dafunes.ulp.moscaapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.dafunes.ulp.moscaapp.modelo.Formulario;

/**
 * Created by Diego on 16/03/2018.
 */

public class DialogoParaFormulario extends DialogFragment {
    private static final String TAG = DialogoParaFormulario.class.getName();

    //Declaramos los editext

    private EditText codigoqr;
    private EditText correo;
    private EditText latitud;
    private EditText longitud;
    private EditText fecha;
    private EditText hora;
    private EditText obs;

    //Declaramos una instancia de Formulario para tener datos de un formulario que se desee actualizar
    private Formulario miFormUpdate;

    public interface DialogoAgregarFormListener {

        //Método que se dispara cuando se intenta agregar un formulario
        void onDialogAddForm(Formulario nuevoForm);

        //Método que se dispara cuando se cancela el dialogo de agregar un formulario
        void onDialogCancelar();

        //Método que se dispara cuando se desea actualizar un formulario
        void onDialogActualizarForm(Formulario nuevoForm);
    }

    //Declaramos una instancia de la interfaz anteriormente descrita para usarla para enviar
    //la info al Activity principal
    DialogoAgregarFormListener miListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach( activity );
        try {
            miListener = (DialogoAgregarFormListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException( activity.toString()
                    + "debe implementar NoticeDialogListener" );
        }

    }

 /*   @Override
   public void onAttach(Context context) {
        super.onAttach( context );
        try {
            miListener = (DialogoAgregarFormListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException( context.toString()
                    +"debe implementar NoticeDialogListener");
        }
    }*/

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //return super.onCreateDialog( savedInstanceState );
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Obtenemos una referencia al layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        //Inflamos la vista del Dialogo
        View rootView = inflater.inflate(R.layout.dialog_agregarform, null);
        codigoqr = (EditText) rootView.findViewById(R.id.etcodigoqr);
        correo = (EditText) rootView.findViewById(R.id.etcorreo);
        latitud = (EditText) rootView.findViewById(R.id.etlat);
        longitud = (EditText) rootView.findViewById(R.id.etlong);
        fecha = (EditText) rootView.findViewById(R.id.etfecha);
        hora = (EditText) rootView.findViewById(R.id.ethora);
        obs = (EditText) rootView.findViewById(R.id.etobs);
        //Seteamos los valores de la nota que se desea actualizar (si aplica)
        setDataToUpdate();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(rootView)
                // Add action buttons
                .setPositiveButton(R.string.agregar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (miListener != null){
                            if (miFormUpdate != null) {
                                //Actualizamos las propiedades de el formulario
                                miFormUpdate.setCodigoqr(codigoqr.getText().toString());
                                miFormUpdate.setCorreo(correo.getText().toString());
                                miFormUpdate.setLatitud(latitud.getText().toString());
                                miFormUpdate.setLongitud(longitud.getText().toString());
                                miFormUpdate.setFecha(fecha.getText().toString());
                                miFormUpdate.setHora(hora.getText().toString());
                                miFormUpdate.setObs(obs.getText().toString());
                                //Ejecutamos el método onDialogUpdNote que esta implementado en el MainActivity
                                miListener.onDialogActualizarForm(miFormUpdate);
                            } else {
                                //Instanciamos el nuevo formulario
                                Formulario aForm = new Formulario(codigoqr.getText().toString(), correo.getText().toString(),
                                        latitud.getText().toString(), longitud.getText().toString(), fecha.getText().toString(),hora.getText().toString(),obs.getText().toString());
                                //Ejecutamos el método onDialogAddForm que esta implementado en el MainActivity
                                miListener.onDialogAddForm(aForm);
                            }
                        }
                        //Cancelamos el Dialogo
                        DialogoParaFormulario.this.getDialog().cancel();
                    }
                })
                .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (miListener != null){
                            //Ejecutamos el método onDialogCancel que esta implementado en el MainActivity
                            miListener.onDialogCancelar();
                        }
                        //Cancelamos el Dialogo
                        DialogoParaFormulario.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
    public Formulario getFormUpdate(){
        return miFormUpdate;
    }

    public void setFormUpdate(Formulario miFormUpdate){
        this.miFormUpdate=miFormUpdate;
    }

    private void setDataToUpdate(){
        if(miFormUpdate!=null){
            codigoqr.setText( miFormUpdate.getCodigoqr() );
            correo.setText( miFormUpdate.getCorreo() );
            latitud.setText( miFormUpdate.getLatitud() );
            longitud.setText( miFormUpdate.getLongitud() );
            fecha.setText( miFormUpdate.getFecha() );
            hora.setText( miFormUpdate.getHora() );
            obs.setText( miFormUpdate.getObs() );
        }
    }

}
