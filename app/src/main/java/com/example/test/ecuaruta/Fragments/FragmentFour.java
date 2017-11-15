package com.example.test.ecuaruta.Fragments;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.test.ecuaruta.Logic.RutaAdapter;
import com.example.test.ecuaruta.Logic.ruta;
import com.example.test.ecuaruta.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Diego on 10/8/2015.
 */
public class FragmentFour extends Fragment {
    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;
    //List<ruta> items = new ArrayList<>();

    public FragmentFour() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_kawaii_cards, container, false);

        // Inicializar Animes


        /*items.add(new ruta(R.drawable.bus, "Ecovia", "230","Ruta de las Universidades"));
        items.add(new ruta(R.drawable.bus, "Trole", "456","Expresso Escolar"));
        items.add(new ruta(R.drawable.bus, "MetroBus", "342","Expresso La Y - El Recreo"));*/
        // Obtener el Recycler
        recycler = (RecyclerView) rootView.findViewById(R.id.reciclador);
        recycler.setHasFixedSize(true);

        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(this.getActivity().getBaseContext());
        recycler.setLayoutManager(lManager);

        new BuscarRutas().execute();

        // Crear un nuevo adaptador
        /*adapter = new RutaAdapter(items);
        recycler.setAdapter(adapter);*/

        return rootView;
    }


    public class BuscarRutas extends AsyncTask<String,Void, List<ruta> > {
        String compania = "";
        protected List<ruta>  doInBackground(String... params) {

            //String paradas[]= readFromDB();
            List<ruta> items = readFromDB();
            //ArrayList<LatLng> posiciones = getLatLong(compania,paradas);

            return items;

        }
        protected void onPostExecute(List<ruta> items) {
            if(items!=null){

                adapter = new RutaAdapter(items);
                recycler.setAdapter(adapter);
/*
                lv=(ListView) findViewById(R.id.listView);
                lv.setAdapter(new CustomAdapterSugerencias(SearchActivity.this, paradas, null));*/
                Log.d("Display: ","Esta agregando rutas");
            }


        }

        public List<ruta> readFromDB() {
            String url = getResources().getString(R.string.servicio_ruta);

            //String url = "http://192.168.0.102:81/ecuaruta/display.php";

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
            //ArrayList<String> paradas = new ArrayList<String>();
            List<ruta> items = new ArrayList<>();
            try {
                JSONArray jArray = new JSONArray(result);
                for(int i=0; i < jArray.length(); i++) {

                    JSONObject jObject = jArray.getJSONObject(i);

                    String name = jObject.getString("NOMBRERUTA").trim();
                    String inicio = jObject.getString("INICIORUTA");
                    String fin = jObject.getString("FINRUTA");
                    items.add(new ruta(R.drawable.bus, name, inicio,fin));
                    //paradas.add(name);
                    Log.i("La ruta es ", name);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            //String[] resultado = new String[paradas.size()];
            //resultado = paradas.toArray(resultado);

            return items;
        }
    }


}