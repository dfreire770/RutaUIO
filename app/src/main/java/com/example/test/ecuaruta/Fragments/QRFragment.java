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
import android.widget.TextView;

import com.example.test.ecuaruta.Logic.RutaAdapter;
import com.example.test.ecuaruta.Logic.ruta;
import com.example.test.ecuaruta.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Diego on 10/8/2015.
 */
public class QRFragment extends Fragment {
    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;

    String calle;
    //List<ruta> items = new ArrayList<>();

    public QRFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_qrresult, container, false);

        calle = getArguments().getString("calle");

        TextView nombreCalle = (TextView) rootView.findViewById(R.id.textViewCalle);
        nombreCalle.setText(calle);
        //return inflater.inflate(R.layout.fragment, container, false);


        // Inicializar Animes


        /*items.add(new ruta(R.drawable.bus, "Ecovia", "230","Ruta de las Universidades"));
        items.add(new ruta(R.drawable.bus, "Trole", "456","Expresso Escolar"));
        items.add(new ruta(R.drawable.bus, "MetroBus", "342","Expresso La Y - El Recreo"));*/
        // Obtener el Recycler
        recycler = (RecyclerView) rootView.findViewById(R.id.reciclador1);
        recycler.setHasFixedSize(true);

        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(this.getActivity().getBaseContext());
        recycler.setLayoutManager(lManager);

        new BuscarCalle().execute();

        // Crear un nuevo adaptador
        /*adapter = new RutaAdapter(items);
        recycler.setAdapter(adapter);*/

        return rootView;
    }


    public class BuscarCalle extends AsyncTask<String,Void, List<ruta> > {
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
                Log.d("Display: ","Esta agregando rutas");
            }


        }

        public List<ruta> readFromDB() {
            StringBuilder content = new StringBuilder();

            // many of these calls can throw exceptions, so i've just
            // wrapped them all in one try/catch statement.
            try
            {
                URL url = new URL(getResources().getString(R.string.servicio_ruta)+"/"+ URLEncoder.encode(calle, "UTF-8").replace("+", "%20"));

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
            List<ruta> items = new ArrayList<>();
            try {
                JSONArray jArray = new JSONArray(content.toString());
                for(int i=0; i < jArray.length(); i++) {

                    JSONObject jObject = jArray.getJSONObject(i);
                    //String calle = jObject.getString("NOMBRECALLE");
                    String name = jObject.getString("NOMBRERUTA").trim();
                    String inicio = jObject.getString("INICIORUTA");
                    String fin = jObject.getString("FINRUTA");
                    items.add(new ruta(R.drawable.bus, name, inicio,fin));
                    Log.e("La parada es ", name);
                }

                //}

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                    //String[] resultado = new String[paradas.size()];
                //resultado = paradas.toArray(resultado);

                return items;

        }
    }


}