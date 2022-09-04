package com.dafunes.ulp.moscaapp;


import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.dafunes.ulp.moscaapp.modelo.Formulario;
import com.dafunes.ulp.moscaapp.modelo.FormularioDBHelper;


import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.sql.*;



public class ComprobarDatos extends AppCompatActivity implements DialogoParaFormulario.DialogoAgregarFormListener, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = ComprobarDatos.class.getName();


    private FormularioDBHelper miDbHelper;
    private List<Formulario> miAllForms;
    private ArrayList<String> miFormsCod;
    private ArrayAdapter miAdapter;
    private String scanresult;
    static final int REQUEST_LOCATION = 1;
    Location location;
    LocationManager locationManager;
    double latitude,longitude;
    private List<Formulario> todos;
    private String resultado;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_comprobar_datos );
        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );


        FloatingActionButton fab = (FloatingActionButton) findViewById( R.id.fab );
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Conectar conectar = new Conectar();
                //conectar.execute();
                tareaWS tws=new tareaWS();
                tws.execute();


            }
        } );


        miDbHelper = new FormularioDBHelper( this );

        prepararListView();

        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
        drawer.addDrawerListener( toggle );
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener( this );

        miUbicacion();



        //Establezco citerios para buscar un proveedor
        /*Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String bestProvider = locman.getBestProvider(criteria, true);*/



/////////////////////////////////////////////////////////////// Soy un separador!

/////////////////////////////////////////////////////////////// Creación del formulario con el código QR escaneado!
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            if (checkLocation()) {

                scanresult = extras.getString( "CONTENIDO" );
                SimpleDateFormat sdffecha = new SimpleDateFormat( "dd-MM-yyyy" );
                SimpleDateFormat sdfhora = new SimpleDateFormat( "h:mm a" );
                String fechaactual = sdffecha.format( Calendar.getInstance().getTime() );
                String horaactual = sdfhora.format( Calendar.getInstance().getTime() );
                String email = getEmail( this );
                String lat = String.valueOf( latitude );
                String lng = String.valueOf( longitude );
                String obss = "";


                Formulario aForm = new Formulario( scanresult, email,
                        lat, lng, fechaactual, horaactual, obss );
                onDialogAddForm( aForm );

            }

        }

    }//Fin onCreate

//clase conectar

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        if (drawer.isDrawerOpen( GravityCompat.START )) {
            drawer.closeDrawer( GravityCompat.START );
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.home, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //Mostramos el dialogo del formulario
        if (id == R.id.action_agregar) {
            showAddFormDialog();
            return true;
        }

        return super.onOptionsItemSelected( item );
    }

    private void prepararListView() {
        //Instanciamos el listview
        ListView miListView = (ListView) findViewById( R.id.lvFormulario );

        //Definimos un array con los valores de los títulos(código qr)
        //que presentaremos en el listview
        refreshForms();

        //Definimos el Adaptar el cual se encargará de mapear con el UI los diferentes
        // títulos de los formularios
        miAdapter = new ArrayAdapter<String>( this,
                android.R.layout.simple_list_item_1, android.R.id.text1, miFormsCod );

        // Asignamos el adapter al ListView, para presentar los datos en pantalla
        if (miListView != null) {
            miListView.setAdapter( miAdapter );

            //Definimos un OnItemClickListener para saber cuando el usuario ha seleccionado
            // un formulario y poder navegar a la vista de detalle de el formulario
            miListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Formulario selForm = miAllForms.get( position );
                    Log.d( TAG, "Formulario Seleccionado:" + selForm.getCodigoqr() );
                    //Instanciamos el DialogoParaFormulario
                    DialogoParaFormulario newAddForm = new DialogoParaFormulario();
                    //Seteamos el form que deseamos actualizar
                    newAddForm.setFormUpdate( selForm );
                    //Mostramos el DialogoParaFormulario asignandole el tag "addform"
                    newAddForm.show( getSupportFragmentManager(), "addform" );

                }

            } );
            //Definimos un  OnItemLongClickListener con el cual conseguiremos borrar el formulario
            miListView.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d( TAG, "onItemLongClick" + miAllForms.get( position ).getCodigoqr() );
                    //Obtenemos la referencia a la nota sobre la cual el usuario
                    // esta haciendo LongClick
                    Formulario delForm = miAllForms.get( position );
                    if (miDbHelper != null) {
                        //Borramos el formulario de la base de datos
                        miDbHelper.deleteForm( delForm );
                        //Refrescamos la lista de formularios
                        refreshForms();
                        //Devolvemos true para evitar que se ejecute el OnItemClickListener
                        return true;
                    }

                    //Devolvemos false para que se ejecute el OnItemClickListener ya que no
                    // se ha logrado el borrado de el formulario
                    return false;
                }
            } );
        }
    }

    private void refreshForms() {
        //Cargamos todos los formularios
        miAllForms = miDbHelper.getAllForms();

        //Iteramos sobre todos los formularios para pasar los titulos(codigo qr) a un Array String
        int idx = 0;
        miFormsCod = new ArrayList<String>();
        for (Formulario aForm : miAllForms) {
            miFormsCod.add( aForm.getCodigoqr() );
            idx++;
        }
        //Si el Adapter esta instanciado notificamos los cambios
        if (miAdapter != null) {
            //Limpiamos todos los datos
            miAdapter.clear();
            //Agreamos los nuevos datos
            miAdapter.addAll( miFormsCod );
            //Notificamos los cambios
            miAdapter.notifyDataSetChanged();
        }
    }

    private void showAddFormDialog() {
        Log.d( TAG, "showAddFormDialog" );
        //Instanciamos el DialogFormulario
        DialogoParaFormulario newAddForm = new DialogoParaFormulario();
        //Mostramos el DialogForNote asignadole el tag "addform"
        newAddForm.show( getSupportFragmentManager(), "addform" );
    }

    @Override
    public void onDialogAddForm(Formulario nuevoForm) {
        Log.d( TAG, "onDialogAddForm" );
        if (miDbHelper != null) {
            //Insertamos el nuevo registro
            miDbHelper.insertForm( nuevoForm );
            //Refrescamos el ListView
            refreshForms();
        }
    }

    @Override
    public void onDialogCancelar() {
        Log.d( TAG, "onDialogCancel" );
    }

    @Override
    public void onDialogActualizarForm(Formulario updForm) {
        Log.d( TAG, "onDialogUpdNote" );
        if (miDbHelper != null) {
            //Insertamos el nuevo registro
            miDbHelper.updateForm( updForm );
            //Refrescamos el ListView
            refreshForms();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        switch (id) {
            case R.id.nav_home:
                Intent h = new Intent( ComprobarDatos.this, Home.class );
                startActivity( h );
                break;
            case R.id.nav_scan:
                Intent i = new Intent( ComprobarDatos.this, Escaner.class );
                startActivity( i );
                break;

        }

        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        drawer.closeDrawer( GravityCompat.START );
        return true;
    }
    /////////////////////////////////////////////////////////////// Obtener correo principal(com.google)

    static String getEmail(Context context) {
        AccountManager accManager = AccountManager.get( context );
        Account account = getAccount( accManager );

        if (account == null) {
            return null;
        } else {
            return account.name;
        }
    }

    private static Account getAccount(AccountManager accManager) {
        Account[] accounts = accManager.getAccountsByType( "com.google" );
        Account account;
        if (accounts.length > 0) {
            account = accounts[0];
        } else {
            account = null;
        }

        return account;
    }

    ///////////////////////////////////////////////////////////////Ubicación
    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder( this );
        dialog.setTitle( "Permitir ubicación" )
                .setMessage( "Su ubicación esta desactivada.\npor favor active su ubicación " )
                .setPositiveButton( "Configuración de ubicación", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS );
                        startActivity( myIntent );
                    }
                } )
                .setNegativeButton( "Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                } );
        dialog.show();
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void actualizarUbicacion(Location location) {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();

        }
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            actualizarUbicacion( location );
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void miUbicacion(){

        if (ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager=(LocationManager)getSystemService( Context.LOCATION_SERVICE );
        location = locationManager.getLastKnownLocation( LocationManager.GPS_PROVIDER );
        actualizarUbicacion( location );
        locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER,15000,0,locationListener );
    }

    private class tareaWS extends AsyncTask<Void,Void,Boolean>{

        @Override
        protected Boolean doInBackground(Void... params) {

            final String NAMESPACE = "namespace";
            final String URL= "url";
            final String METHOD_NAME = "Insertar";
            final String SOAP_ACTION = "namespace/Insertar";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            request.addProperty("qr", "a999999");


            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope( SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject( request );
            HttpTransportSE ht = new HttpTransportSE(URL);
            try {
                ht.call(SOAP_ACTION, envelope);
                SoapPrimitive response = (SoapPrimitive)envelope.getResponse();
                resultado=response.toString();
                Log.i("Resultado: ",resultado);

            }
            catch (Exception e)
            {
                Log.i("Error: ",e.getMessage());
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if(success==false){
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(getApplicationContext(), "Datos insertados "+resultado, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
        }
    }


///////////////////////////////////////////////////////////////

}


