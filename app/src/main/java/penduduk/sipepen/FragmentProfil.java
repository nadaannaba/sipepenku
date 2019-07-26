package penduduk.sipepen;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;



/**
 * Created by NADA on 5/11/2019.
 */

public class FragmentProfil extends Fragment {
    private String TAG = FragmentProfil.class.getSimpleName();
    private ProgressDialog pDialog;
    private static String url;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    EditText anamapengguna, aemail, akatasandi, atelepon,anokk;
    TextView txtusername;
    Button btnubah;
    View rootview;
    private ActionBar supportActionBar;

    public FragmentProfil() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_profil, container, false);

        anamapengguna = (EditText) rootview.findViewById(R.id.anamapengguna);
        aemail = (EditText) rootview.findViewById(R.id.aemail);
        akatasandi = (EditText) rootview.findViewById(R.id.akatasandi);
        atelepon = (EditText) rootview.findViewById(R.id.atelepon);
        anokk = (EditText) rootview.findViewById(R.id.anokk);
        txtusername = (TextView) rootview.findViewById(R.id.txtusername);


        new TampilData().execute();

        btnubah = (Button) rootview.findViewById(R.id.btnubah);
        btnubah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String namapengguna = anamapengguna.getText().toString();
                String email = aemail.getText().toString();
                String katasandi = akatasandi.getText().toString();
                String telepon = atelepon.getText().toString();
                String nokk = anokk.getText().toString();

                new UbahData().execute(namapengguna, email, katasandi, telepon, nokk);
            }
        });
        return rootview;
    }

    private class TampilData extends AsyncTask<Void, Void, Void> {
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Memuat Data...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected Void doInBackground(Void... params) {
            SessionManager session = new SessionManager(getActivity());
            HashMap<String, String> user = session.getUserDetails();

            String iduser = user.get(SessionManager.KEY_IDUSER);
            url = "https://pendataanpendudukrt5315.000webhostapp.com/sensus1/index.php/jsonparsing/viewprofile/"+iduser;
            HTTPhandler sh = new HTTPhandler();

            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Respon dari url: " + jsonStr);

            if(jsonStr != null) {
                try{
                    JSONObject jsonObject = new JSONObject(jsonStr);

                    JSONArray dataku = jsonObject.getJSONArray("data");

                    JSONObject c = dataku.getJSONObject(0);
                    final String namapengguna = c.getString("username").trim();
                    final String email = c.getString("email").trim();
                    final String katasandi = c.getString("password").trim();
                    final String telepon = c.getString("telepon").trim();
                    final String nokk = c.getString("no_kk").trim();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            anamapengguna.setText(namapengguna);
                            aemail.setText(email);
                            akatasandi.setText(katasandi);
                            atelepon.setText(telepon);
                            anokk.setText(nokk);
                            txtusername.setText(namapengguna);
                        }
                    });

                }catch (final JSONException e){
                    Log.e(TAG, "Data parsing bermasalah : "+ e.getMessage());
                    getActivity().runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Data parsing bermasalah : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }else{
                Log.e(TAG, "Tidak bisa ambil data dari server");
                getActivity().runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Tidak bisa ambil data dari server. Silahkan check koneksi!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;
        }

        protected void onPostExecute(Void aVoid) {

            pDialog.dismiss();
        }
    }

    private class UbahData extends AsyncTask<String, String, String> {
        HttpURLConnection conn;
        URL urlubah = null;
        SessionManager session = new SessionManager(getActivity());
        HashMap<String, String> user = session.getUserDetails();

        String iduser = user.get(SessionManager.KEY_IDUSER);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Mengubah Data...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try{
                urlubah = new URL("https://pendataanpendudukrt5315.000webhostapp.com/sensus1/index.php/jsonparsing/ubahprofile/");

            }catch (MalformedURLException e){
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }

            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection)urlubah.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("iduser", iduser)
                        .appendQueryParameter("username", params[0])
                        .appendQueryParameter("email", params[1])
                        .appendQueryParameter("password", params[2])
                        .appendQueryParameter("telepon", params[3])
                        .appendQueryParameter("no_kk", params[4]);

                String query = builder.build().getEncodedQuery();

                Log.e(TAG, "Respon dari url: " + query);

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            }catch (IOException e1){
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try{
                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return(result.toString());

                }else {
                    return ("unsuccessful");
                }

            }catch (IOException e){
                e.printStackTrace();
                return "exception";
            }finally {
                conn.disconnect();
            }

        }

        @Override
        protected void onPostExecute(String result) {
            pDialog.dismiss();
            Log.e(TAG, "Respon dari url: " + result);
            if(result.equalsIgnoreCase("true"))
            {
                Toast.makeText(getActivity(), "Data berhasil diubah", Toast.LENGTH_SHORT).show();
                new TampilData().execute();

            }else if (result.equalsIgnoreCase("false")){

                // If username and password does not match display a error message
                Toast.makeText(getActivity(), "Gagal mengubah data", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(getActivity(), "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();

            }
        }
    }
}
