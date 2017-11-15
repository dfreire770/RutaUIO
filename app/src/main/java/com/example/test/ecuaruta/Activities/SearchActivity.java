package com.example.test.ecuaruta.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.test.ecuaruta.Logic.CustomAdapterSugerencias;
import com.example.test.ecuaruta.R;
import com.example.test.ecuaruta.SQLite.SQLiteHelper;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    ListView lv;
    Context context;
    //public static String [] prgmNameList={"1","2","3","4","5","6","7","8","9"};
    String busqueda="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        context=this;
        new BuscarRutas().execute();


        final EditText editText = (EditText) findViewById(R.id.searchtv);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    busqueda = editText.getText().toString();
                    agregarHistorial();
                    new BuscarLugar().execute();
                    return true;
                }
                return false;
            }
        });
        final ListView listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = ((TextView) view.findViewById(R.id.tv1)).getText().toString();
                //Toast.makeText(context, item, Toast.LENGTH_LONG).show();
                busqueda = item;
                agregarHistorial();
                new BuscarParadas().execute();
            }
        });

    }

    public void agregarHistorial(){

        SQLiteHelper db = new SQLiteHelper(this);//Para el historial y favoritos
        int resId=0;
        Object[] lugar;

        File database=getApplicationContext().getDatabasePath("ecuarutaDB");// creo un objeto tipo file para verificar si existe la base de datos
        //si la base de datos no existe entonces se crea
        if (!database.exists()) {

            Log.d("Info Main:", "La base de datos aun no existe, sera creada");
            db.addHistorial(busqueda);

        }
        else{
            Log.d("Info Main", "La DB ya existe");
            db.addHistorial(busqueda);

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
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
    }

    public class BuscarLugar extends AsyncTask<Boolean, Void, Void> {
        @Override
        protected Void doInBackground(Boolean... params) {
            StringBuilder content = new StringBuilder();

            // many of these calls can throw exceptions, so i've just
            // wrapped them all in one try/catch statement.
            try
            {
                URL url = new URL(getResources().getString(R.string.servicio_paradas_sector)+"/"+ URLEncoder.encode(busqueda,"UTF-8").replace("+", "%20"));

                // create a urlconnection object
                URLConnection urlConnection = url.openConnection();
                // wrap the urlconnection in a bufferedreader
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                String line;

                // read from the urlconnection via the bufferedreader
                while ((line = bufferedReader.readLine()) != null)
                {
                    content.append(line + "\n");
                    Log.d("resultado:", line);
                }
                bufferedReader.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            Log.i("Resultado:", content.toString());

            JSONObject jsonObject;
            ArrayList<String> paradas = new ArrayList<String>();
            try {
                JSONArray jArray = new JSONArray(content.toString());
                for (int i = 0; i < jArray.length(); i++) {

                    JSONObject jObject = jArray.getJSONObject(i);
                    String name = jObject.getString("NOMBREPARADA");
                    paradas.add(name);
                    Log.i("El producto es ", name);
                }

            }  catch (JSONException e) {
                e.printStackTrace();
            }

            String[] resultado = new String[paradas.size()];

            resultado = paradas.toArray(resultado);


            Intent intent = new Intent();
            intent.putExtra("paradas",paradas);
            setResult(RESULT_OK, intent);

            finish();
            return null;
        }

    }

    public class BuscarRutas extends AsyncTask<String,Void, String[]> {
        String compania = "";
        protected String[] doInBackground(String... params) {

            String paradas[]= readFromDB();

            //ArrayList<LatLng> posiciones = getLatLong(compania,paradas);

            return paradas;

        }
        protected void onPostExecute(String[] paradas) {
            if(paradas!=null){

                lv=(ListView) findViewById(R.id.listView);
                lv.setAdapter(new CustomAdapterSugerencias(SearchActivity.this, paradas, null));
            }


        }
        public String[] readFromDB() {
            String url = getResources().getString(R.string.servicio_ruta);
            //String url = "http://172.200.31.16:8181/ecuaruta/display.php";

            HttpGet httpGet = new HttpGet(url);
            HttpClient client = new DefaultHttpClient();
            HttpResponse response;

            StringBuilder stringBuilder = new StringBuilder();

            String result="";

            try {
                response = client.execute(httpGet);
                HttpEntity entity = response.getEntity();
                //String ruta = EntityUtils.toString(entity);
                InputStream stream = entity.getContent();

                int b;
                while ((b = stream.read()) != -1) {
                    stringBuilder.append((char) b);
                }

                stream.close();
                result = stringBuilder.toString();

            }
            catch(Exception e)
            {
                Log.e("log_tag", "Error in http connection " + e.toString());
            }

            JSONObject jsonObject;
            ArrayList<String> paradas = new ArrayList<String>();
            try {
                JSONArray jArray = new JSONArray(result);
                for(int i=0; i < jArray.length(); i++) {

                    JSONObject jObject = jArray.getJSONObject(i);

                    String name = jObject.getString("NOMBRERUTA");
                    paradas.add(name);
                    Log.i("La ruta es ", name);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            String[] resultado = new String[paradas.size()];
            resultado = paradas.toArray(resultado);

            return resultado;
        }
    }

    public class BuscarParadas extends AsyncTask<Boolean, Void, Void> {
        @Override
        protected Void doInBackground(Boolean... params) {
            StringBuilder content = new StringBuilder();

            // many of these calls can throw exceptions, so i've just
            // wrapped them all in one try/catch statement.
            try
            {

                URL url = new URL(getResources().getString(R.string.servicio_paradas_ruta)+"/"+URLEncoder.encode(busqueda,"UTF-8").replace("+", "%20"));

                // create a urlconnection object
                URLConnection urlConnection = url.openConnection();
                // wrap the urlconnection in a bufferedreader
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                String line;

                // read from the urlconnection via the bufferedreader
                while ((line = bufferedReader.readLine()) != null)
                {
                    content.append(line + "\n");
                    Log.d("resultado:", line);
                }
                bufferedReader.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            Log.i("Resultado:", content.toString());

            JSONObject jsonObject;
            ArrayList<String> paradas = new ArrayList<String>();
            try {
                JSONArray jArray = new JSONArray(content.toString());
                for (int i = 0; i < jArray.length(); i++) {

                    JSONObject jObject = jArray.getJSONObject(i);
                    String name = jObject.getString("NOMBREPARADA");
                    paradas.add(name);
                    Log.i("El producto es ", name);
                }

            }  catch (JSONException e) {
                e.printStackTrace();
            }

            String[] resultado = new String[paradas.size()];

            resultado = paradas.toArray(resultado);


            Intent intent = new Intent();
            intent.putExtra("paradas",paradas);
            setResult(RESULT_OK, intent);

            finish();
            return null;
        }

    }
}
