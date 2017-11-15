package com.example.test.ecuaruta.Activities;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.test.ecuaruta.Fragments.FragmentFive;
import com.example.test.ecuaruta.Fragments.FragmentFour;
import com.example.test.ecuaruta.Fragments.FragmentOne;
import com.example.test.ecuaruta.Fragments.FragmentTwo;
import com.example.test.ecuaruta.Fragments.NavigationDrawerFragment;
import com.example.test.ecuaruta.Fragments.QRFragment;
import com.example.test.ecuaruta.Logic.Denuncia;
import com.example.test.ecuaruta.QRLibrary.IntentIntegrator;
import com.example.test.ecuaruta.QRLibrary.IntentResult;
import com.example.test.ecuaruta.R;
import com.example.test.ecuaruta.SQLite.SQLiteHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);

        setSupportActionBar(mToolbar);


        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.fragment_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                Intent myIntent = new Intent();
                myIntent.setClass(view.getContext(), SearchActivity.class);

                startActivityForResult(myIntent, 1);

            }
        });





        ImageButton ib = (ImageButton) findViewById(R.id.qrbutton);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator.initiateScan(MainActivity.this);
            }
        });

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }

    }
    Fragment fragment = null;
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        //En este metodo se instancian las otras funcionalidades
        //Deben crear fragmentos como las clases que puse ahi, esas que dicen fragmentOne, fragmentTwo
        //en res/layout estan los xml que definen como se debe ver la actividad, eso hay que editar de acuerdo al prototipo

        //Toast.makeText(this, "Menu item selected -> " + position, Toast.LENGTH_SHORT).show();

        switch (position) {
            case 0:
                fragment = new FragmentOne();
                break;
            case 1:

                fragment = new FragmentTwo();
                break;
            case 2:
                fragment = new FragmentFour();
                break;
            case 3:
                fragment = new FragmentFive();
                break;
            case 4:
                IntentIntegrator.initiateScan(MainActivity.this);
                break;

            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();


        } else {
            System.out.println("MainActivity Error in creating fragment");
        }
    }


    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen())
            mNavigationDrawerFragment.closeDrawer();
        else
            super.onBackPressed();
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    public void Click(View v) {
        switch(v.getId()) {
            case R.id.TextView1:
                Intent myIntent = new Intent();
                myIntent.setClass(this, SearchActivity.class);

                startActivityForResult(myIntent, 1);
                break;


        }
    }

    public void agregareliminar(View v) {
        SQLiteHelper db = new SQLiteHelper(this);//Para el historial y favoritos
        int resId=0;
        Object[] lugar;
        switch(v.getId()) {
            case R.id.imageButton2:
                //borra el historial
                db.eliminar_historial();
                fragment = new FragmentTwo();

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();

                break;

        }
    }


    public void denunciar(View v){
        switch(v.getId()) {
            case R.id.btnEnviar:

                 new Denuncias().execute();
                break;
        }

    }

    public void onActivityResultParadas(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                //String stredittext=data.getStringExtra("paradas");
                FragmentOne f = new FragmentOne();
                Bundle args = new Bundle();
                args.get("paradas");
                f.setArguments(args);

            }
        }
    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case IntentIntegrator.REQUEST_CODE:
            {
                if (resultCode == RESULT_CANCELED){
                }
                else
                {
                    //Recogemos los datos   que nos envio el código Qr/codigo de barras
                    IntentResult scanResult = IntentIntegrator.parseActivityResult(
                            requestCode, resultCode, data);
                    String UPCScanned = scanResult.getContents();
                    //cOMO ES SOLO UN EJEMPLO LO SACAREMOS POR PANTALLA.
                    /*Toast.makeText(getApplicationContext(), UPCScanned, Toast.LENGTH_LONG
                    ).show();*/

                    Bundle bundle = new Bundle();
                    bundle.putString("calle",UPCScanned);
                    // set Fragmentclass Arguments
                    fragment = new QRFragment();
                    fragment.setArguments(bundle);

                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();

                }

                break;
            }
            case 1:{
                if(resultCode == RESULT_OK){
                    Bundle bundle = data.getExtras();
                    Log.i("Extra",bundle.get("paradas").toString());
                    fragment = new FragmentOne();
                    fragment.setArguments(bundle);

                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();


                }

            }

        }


    }



    public class Denuncias extends AsyncTask<Void, Void, Void> {
        Denuncia denuncia;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String placa = ((TextView)findViewById(R.id.txtPlaca1)).getText().toString() + ((TextView)findViewById(R.id.txtPlaca2)).getText().toString() ;
            String compania= ((TextView)findViewById(R.id.txtCompania)).getText().toString();
            String problema = ((TextView)findViewById(R.id.txtProblema)).getText().toString();
            String email = ((TextView)findViewById(R.id.txtEmail)).getText().toString();
            denuncia = new Denuncia(placa,compania,problema,email);

        }

        @Override
        protected Void doInBackground(Void... params) {

            JSONObject jsonObject = new JSONObject();

            try {

                Log.d("Denuncia doInBackgroud",denuncia.getPlaca());

                jsonObject.put("placaDenuncia", denuncia.getPlaca());
                jsonObject.put("companiaDenuncia", denuncia.getCompania());
                jsonObject.put("problemaDenuncia", denuncia.getProblema());
                jsonObject.put("emailDenuncia", denuncia.getEmail());

                URL url=new URL (getResources().getString(R.string.servicio_denuncias));//getResources().getString(R.string.servicio_paradas);
                String message = jsonObject.toString();

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /*milliseconds*/);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setFixedLengthStreamingMode(message.getBytes().length);

                //make some HTTP header nicety
                conn.setRequestProperty("Content-Type", "application/json");
                //conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");

                //open
                conn.connect();

                //setup send
                OutputStream os = new BufferedOutputStream(conn.getOutputStream());
                os.write(message.getBytes());
                //clean up
                os.flush();
                os.close();

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            TextView placa = (TextView)findViewById(R.id.txtPlaca1);
            placa.setText("");
            TextView placa2 = (TextView)findViewById(R.id.txtPlaca2);
            placa2.setText("");
            TextView compania= (TextView)findViewById(R.id.txtCompania);
            compania.setText("");
            TextView problema= (TextView)findViewById(R.id.txtProblema);
            problema.setText("");
            TextView email= (TextView)findViewById(R.id.txtEmail);
            email.setText("");


        }
    }

}
