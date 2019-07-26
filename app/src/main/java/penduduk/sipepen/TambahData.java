package penduduk.sipepen;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatCallback;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import java.util.HashMap;

import static android.R.attr.fragment;
import static android.R.attr.targetActivity;

/**
 * Created by NADA on 5/3/2019.
 */

public class TambahData extends android.app.Fragment {
    public static final int CONNECTION_TIMEOUT=15000;
    public static final int READ_TIMEOUT=20000;
    private ProgressDialog pDialog;
    View rootview;
    private AppCompatCallback mCallBack;

    EditText anamalengkap,anoktp,atglLahir,aAlamat,akerjaan;
    Spinner  skelamin,sstatushidup,spendidikan,sagama,sstatusnikah,sstatuskeluarga;
    Button btnSimpan;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.tambah_data, container, false);

        anamalengkap = (EditText) rootview.findViewById(R.id.anama);
        anoktp = (EditText) rootview.findViewById(R.id.anoktp);
        atglLahir = (EditText) rootview.findViewById(R.id.atgllahir);
        aAlamat = (EditText) rootview.findViewById(R.id.aalamat);
        akerjaan = (EditText) rootview.findViewById(R.id.apekerjaan);
        skelamin = (Spinner) rootview.findViewById(R.id.spinkelamin);
        sstatushidup = (Spinner) rootview.findViewById(R.id.spinstatushidup);
        spendidikan = (Spinner) rootview.findViewById(R.id.spinpendidikan);
        sagama = (Spinner) rootview.findViewById(R.id.spinagama);
        sstatusnikah = (Spinner) rootview.findViewById(R.id.spinstatusnikah);
        sstatuskeluarga = (Spinner) rootview.findViewById(R.id.spinstatuskeluarga);

        btnSimpan = (Button) rootview.findViewById(R.id.btnsimpan);
        btnSimpan.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String namalengkap = anamalengkap.getText().toString();
                String noktp = anoktp.getText().toString();
                String jeniskelamin = skelamin.getSelectedItem().toString();
                String tgllahir = atglLahir.getText().toString();
                String alamat = aAlamat.getText().toString();
                String statushidup = sstatushidup.getSelectedItem().toString();
                String pendidikan = spendidikan.getSelectedItem().toString();
                String agama = sagama.getSelectedItem().toString();
                String pekerjaan = akerjaan.getText().toString();
                String statusnikah = sstatusnikah.getSelectedItem().toString();
                String statuskeluarga = sstatuskeluarga.getSelectedItem().toString();

                new SimpanData().execute(namalengkap, noktp, jeniskelamin, tgllahir, alamat,
                        statushidup, pendidikan, agama, pekerjaan, statusnikah, statuskeluarga);
                    }
                });
                return rootview;
            }

    public TambahData(){
    }

    private class SimpanData extends AsyncTask<String, String, String> {
        HttpURLConnection conn;
        URL url = null;
        SessionManager session = new SessionManager(getActivity());
        HashMap<String, String> user = session.getUserDetails();

        String iduser = user.get(SessionManager.KEY_IDUSER);

        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Menambahkan Data...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... params) {
            try {
                url = new URL("http://192.168.43.169/sensus/index.php/jsonparsing/tambahanggota/");

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "exception";
            }

            try {
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                conn.setDoInput(true);
                conn.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("iduser", iduser)
                        .appendQueryParameter("namalengkap", params[0])
                        .appendQueryParameter("noktp", params[1])
                        .appendQueryParameter("jeniskelamin", params[2])
                        .appendQueryParameter("tgllahir", params[3])
                        .appendQueryParameter("alamat", params[4])
                        .appendQueryParameter("statushidup", params[5])
                        .appendQueryParameter("pendidikan", params[6])
                        .appendQueryParameter("agama", params[7])
                        .appendQueryParameter("pekerjaan", params[8])
                        .appendQueryParameter("statusnikah", params[9])
                        .appendQueryParameter("statuskeluarga", params[10]);

                String query = builder.build().getEncodedQuery();

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException el) {
                el.printStackTrace();
                return "exception";
            }

            try {
                int response_code = conn.getResponseCode();

                if (response_code == HttpURLConnection.HTTP_OK) {
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    return (result.toString());

                } else {
                    return ("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }
        }

        protected void onPostExecute(String result) {
            pDialog.dismiss();

            if (result.equalsIgnoreCase("true")) {
                Toast.makeText(getActivity(), "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show();

            } else if (result.equalsIgnoreCase("false")) {
                Toast.makeText(getActivity(), "Data tidak boleh kosong", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {
                Toast.makeText(getActivity(), "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();
            }

            anamalengkap.setText("");
            anoktp.setText("");
            skelamin.setSelection(0);
            atglLahir.setText("");
            aAlamat.setText("");
            sstatushidup.setSelection(0);
            spendidikan.setSelection(0);
            sagama.setSelection(0);
            akerjaan.setText("");
            sstatusnikah.setSelection(0);
            sstatuskeluarga.setSelection(0);

            android.app.Fragment fragment = new FragmentData();
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction().replace(R.id.kontenlayout, fragment);
            fragmentTransaction.commit();
        }
    }
}
