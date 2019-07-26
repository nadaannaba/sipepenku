package penduduk.sipepen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

/**
 * Created by NADA on 5/3/2019.
 */

public class daftar extends AppCompatActivity {
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    Button btnbuat;
    EditText anamapengguna, aemail, akatasandi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daftar);

        anamapengguna = (EditText)findViewById(R.id.namapengguna);
        aemail = (EditText)findViewById(R.id.email);
        akatasandi = (EditText)findViewById(R.id.katasandi);

        //hapus actionbar shadow
        getSupportActionBar().setElevation(0);
        //aktifin back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnbuat = (Button)findViewById(R.id.btnbuat);
        btnbuat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String namapengguna = anamapengguna.getText().toString();
                String email = aemail.getText().toString();
                String katasandi = akatasandi.getText().toString();

                new Simpan().execute(namapengguna,email,katasandi,"","");
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    //Triggers when LOGIN Button clicked
    public void checkSimpan(View arg0) {

        String namapengguna = anamapengguna.getText().toString();
        String email = aemail.getText().toString();
        String katasandi = akatasandi.getText().toString();

        new Simpan().execute(namapengguna,email,katasandi);
    }

    private class Simpan extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(daftar.this);
        HttpURLConnection conn;
        URL url = null;
        OutputStream os = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try{
                // Enter URL address where your php file resides
                url = new URL("https://pendataanpendudukrt5315.000webhost.com/sensus1/index.php/jsonparsing/register/");

            }catch (MalformedURLException e){
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }

            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("User-Agent","");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("namapengguna", params[0])
                        .appendQueryParameter("email", params[1])
                        .appendQueryParameter("password", params[2])
                        .appendQueryParameter("telepon", params[3])
                        .appendQueryParameter("no_kk", params[4]);

                String query = builder.build().getEncodedQuery();

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
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(input));
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
            pdLoading.dismiss();

            if(!result.equalsIgnoreCase("false"))
            {
                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */
                //SessionManager sm = new SessionManager(getApplicationContext());
                //sm.createRegSession(aemail.getText().toString(), akatasandi.getText().toString(), result);
                Intent intent = new Intent(daftar.this,login.class);
                startActivity(intent);
                finish();

            }else if (result.equalsIgnoreCase("false")){

                // If username and password does not match display a error message
                Toast.makeText(getApplicationContext(), "Data tidak boleh kosong", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(getApplicationContext(), "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();

            }
        }
    }
}
