package com.example.test.ecuaruta.Fragments;

import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.test.ecuaruta.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Diego on 10/8/2015.
 */
public class FragmentOne extends Fragment implements GoogleMap.OnMyLocationChangeListener{

    MapView mMapView;
    private LocationManager locationManager;
    private GoogleMap mMap;
    private ArrayList paradas = null;

    public FragmentOne() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.searchbar_layout, container, false);

        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        mMap= mMapView.getMap();
        if (ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Snackbar.make(this.getView(), "GPS Desactivado", Snackbar.LENGTH_LONG)
               //     .setAction("Action", null).show();
        }

        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(this);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            paradas = (ArrayList) bundle.get("paradas");
            Log.i("Extra en Fragment",bundle.get("paradas").toString());
            new Ruta().execute();
        }
        //mMap.setOnInfoWindowClickListener();
        //new Ruta().execute();


        return view;
    }

    double latitude;
    double longitude;

    @Override
    public void onMyLocationChange(Location location) {

        latitude = location.getLatitude();

        // Getting longitude of the current location
        longitude = location.getLongitude();

        // Creating a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);

        // Showing the current location in Google Map
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        // Zoom in the Google Map
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

    }

    public class Ruta extends AsyncTask<String,Void, ArrayList<LatLng>> {
        String compania = "";
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.busicon);

        protected ArrayList<LatLng> doInBackground(String... params) {

            //String paradas[]= readFromDB();
            ArrayList<LatLng> posiciones = getLatLong(paradas);

            return posiciones;

        }

        protected void onPostExecute(ArrayList<LatLng> posiciones) {
            if(posiciones!=null){
                for(int i=0;i<posiciones.size();i++){
                    MarkerOptions markerOptions = new MarkerOptions().position(posiciones.get(i))
                            .title(paradas.get(i).toString())
                            .snippet("Toque para mayor informacion")
                            .icon(icon);
                    mMap.addMarker(markerOptions);


                    Log.d("Marcador ","Agregado");

                }
            }
            else{
                Log.d("Error:","no se encontro lo solicitado");
            }
        }

        public ArrayList<LatLng> getLatLong(ArrayList paradas) {
            ArrayList<LatLng> posiciones = new ArrayList<>();
            for(int i=0;i<paradas.size();i++)
            {
                String uri = "http://maps.google.com/maps/api/geocode/json?address=" +
                        URLEncoder.encode(compania+","+paradas.get(i).toString()+",Quito,Ecuador") + "&sensor=false";

                HttpGet httpGet = new HttpGet(uri);
                HttpClient client = new DefaultHttpClient();
                HttpResponse response;
                StringBuilder stringBuilder = new StringBuilder();

                double lng=0;
                double lat=0;
                try {
                    response = client.execute(httpGet);
                    HttpEntity entity = response.getEntity();
                    InputStream stream = entity.getContent();
                    int b;
                    while ((b = stream.read()) != -1) {
                        stringBuilder.append((char) b);
                    }
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(stringBuilder.toString());

                    lng = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                            .getJSONObject("geometry").getJSONObject("location")
                            .getDouble("lng");

                    lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                            .getJSONObject("geometry").getJSONObject("location")
                            .getDouble("lat");

                    Log.d("latitude", "" + lat);
                    Log.d("longitude", "" + lng);

                    posiciones.add(new LatLng(lat,lng));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return posiciones;
        }

        /*public ArrayList getAllParadas() {
            //String url = "http://172.200.31.16:8181/ecuaruta/display.php";
            String url = getResources().getString(R.string.servicio_paradas);
            //String url = "http://192.168.0.104:81/ecuaruta/sector.php";
            String result="";

            StringBuilder stringBuilder = new StringBuilder();
            ArrayList paradas = new ArrayList();
            try{
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet httpget = new HttpGet(url);
                HttpResponse response = httpclient.execute(httpget);
                HttpEntity entity = response.getEntity();
                //result = EntityUtils.toString(entity);
                //Log.e("Resultado: ", result);
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
                Log.e("log_tag", "Error in http connection "+e.toString());
            }

            JSONObject jsonObject;

            try {
                JSONArray jArray = new JSONArray(result);
                for(int i=0; i < jArray.length(); i++) {

                    JSONObject jObject = jArray.getJSONObject(i);

                    String name = jObject.getString("NOMBRECALLE");
                    paradas.add(name);
                    Log.e("La parada es ", name);
                }

                //}

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return paradas;
        }*/


    }




}