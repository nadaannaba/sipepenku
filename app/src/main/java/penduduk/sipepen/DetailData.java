package penduduk.sipepen;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

/**
 * Created by NADA on 5/12/2019.
 */

public class DetailData extends Fragment {
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    private ProgressDialog pDialog;
    private static String url;
    private String TAG = DetailData.class.getSimpleName();

    EditText anamalengkap,anoktp,atglLahir,aAlamat,akerjaan;
    Spinner skelamin,sstatushidup,spendidikan,sagama,sstatusnikah,sstatuskeluarga;
    Button btnubah, btnhapus;
    Fragment fragment;
    FragmentTransaction fragmentTransaction;
    View rootview;

    public DetailData() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.detail_data, container, false);

        anamalengkap = (EditText)rootview.findViewById(R.id.anama);
        anoktp = (EditText)rootview.findViewById(R.id.anomor);
        atglLahir = (EditText)rootview.findViewById(R.id.atgllahir);
        aAlamat = (EditText)rootview.findViewById(R.id.aalamat);
        akerjaan = (EditText)rootview.findViewById(R.id.apekerjaan);
        skelamin = (Spinner) rootview.findViewById(R.id.spinkelamin);
        sstatushidup = (Spinner) rootview.findViewById(R.id.spinstatushidup);
        spendidikan = (Spinner)rootview.findViewById(R.id.spinpendidikan);
        sagama = (Spinner)rootview.findViewById(R.id.spinagama);
        sstatusnikah = (Spinner)rootview.findViewById(R.id.spinstatusnikah);
        sstatuskeluarga = (Spinner)rootview.findViewById(R.id.spinstatuskeluarga);

        new TampilData().execute();

        btnhapus = (Button)rootview.findViewById(R.id.btnhapus);
        btnhapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HapusData().execute();
            }
        });

        btnubah = (Button)rootview.findViewById(R.id.btnubah);
        btnubah.setOnClickListener(new View.OnClickListener() {
            @Override
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

                new UbahData().execute(namalengkap,noktp,jeniskelamin,tgllahir,alamat,
                        statushidup,pendidikan,agama,pekerjaan,statusnikah,statuskeluarga);
            }
        });

        return rootview;
    }

    private class TampilData extends AsyncTask<Void, Void, Void> {
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Memuat Data...");
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected Void doInBackground(Void... params) {
            String id = getArguments().getString("kirimid");
            url = "https://pendataanpendudukrt5315.000webhostapp.com/sensus1/index.php/jsonparsing/dataanggota/"+id;
            HTTPhandler sh = new HTTPhandler();

            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Respon dari url: " +jsonStr);

            if (jsonStr != null) {
                try{
                    JSONObject jsonObject = new JSONObject(jsonStr);

                    JSONArray dataku = jsonObject.getJSONArray("data");

                    JSONObject c = dataku.getJSONObject(0);
                    final String namalengkap = c.getString("nama_lengkap").trim();
                    final String noktp = c.getString("no_ktp").trim();
                    final String jeniskelamin = c.getString("jenis_kelamin").trim();
                    final String tgllahir = c.getString("tgl_lahir").trim();
                    final String alamat = c.getString("alamat").trim();
                    final String statushidup = c.getString("status_hidup").trim();
                    final String pendidikan = c.getString("pendidikan").trim();
                    final String agama = c.getString("agama").trim();
                    final String pekerjaan = c.getString("pekerjaan").trim();
                    final String status = c.getString("status_nikah").trim();
                    final String statuskeluarga = c.getString("status_keluarga").trim();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            anamalengkap.setText(namalengkap);
                            anoktp.setText(noktp);
                            atglLahir.setText(tgllahir);
                            aAlamat.setText(alamat);
                            akerjaan.setText(pekerjaan);

                            skelamin.setSelection(getIndex(skelamin, jeniskelamin));
                            sstatushidup.setSelection(getIndex(sstatushidup, statushidup));
                            spendidikan.setSelection(getIndex(spendidikan, pendidikan));
                            sagama.setSelection(getIndex(sagama, agama));
                            sstatusnikah.setSelection(getIndex(sstatusnikah, status));
                            sstatuskeluarga.setSelection(getIndex(sstatuskeluarga, statuskeluarga));
                        }
                    });

                }catch (final JSONException e) {
                    Log.e (TAG, "Data parsing bermasalah: " +e.getMessage());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Data parsing bermasalah: " +e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }else {
                Log.e(TAG, "Tidak bisa ambil data dari server");
                getActivity().runOnUiThread(new Runnable() {
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

    private int getIndex(Spinner spinner, String myString) {

        int index = 0;

        for (int i=0;i<spinner.getCount();i++) {
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }

    private class HapusData extends AsyncTask<String, String, String> {
        HttpURLConnection conn;
        URL urlhapus = null;

        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Menghapus Data...");
            pDialog.setCancelable(false);
            pDialog.show();
    }

    protected String doInBackground(String... params) {
        String id = getArguments().getString("kirimid");
        try{
            urlhapus = new URL("https://pendataanpendudukrt5315.000webhostapp.com/sensus1/index.php/jsonparsing/hapusanggota/");

        }catch (MalformedURLException e){
            e.printStackTrace();
            return "exception";
        }

        try{
            conn = (HttpURLConnection)urlhapus.openConnection();
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setConnectTimeout(CONNECTION_TIMEOUT);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("idanggota", id);
            String query = builder.build().getEncodedQuery();

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();
            conn.connect();

        }catch (IOException e1){
            e1.printStackTrace();
            return "exception";
        }

        try{
            int response_code = conn.getResponseCode();

            if (response_code == HttpURLConnection.HTTP_OK) {
                InputStream input = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                return(result.toString());

            }else {
                return ("Unsuccessfull");

            }

        }catch (IOException e){
            e.printStackTrace();
            return "exception";

        }finally {
            conn.disconnect();
        }
    }

    protected void onPostExecute(String result) {
        pDialog.dismiss();
        Log.e(TAG, "Respon dari url: " + result);
        if(result.equalsIgnoreCase("true"))
        {
            fragment = new FragmentData();
            fragmentTransaction = getFragmentManager().beginTransaction().replace(R.id.kontenlayout, fragment);
            fragmentTransaction.commit();

            Toast.makeText(getActivity(), "Data berhasil dihapus", Toast.LENGTH_SHORT).show();

        }else if (result.equalsIgnoreCase("false")){

            Toast.makeText(getActivity(), "Gagal menghapus data", Toast.LENGTH_LONG).show();

        } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

            Toast.makeText(getActivity(), "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class UbahData extends AsyncTask<String, String, String> {
        HttpURLConnection conn;
        URL urlubah = null;

        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Mengubah Data...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... params) {
            String id = getArguments().getString("kirimid");

            try {
                // Enter URL address where your php file resides
                urlubah = new URL("https://pendataanpendudukrt5315.000webhostapp.com/sensus1/index.php/jsonparsing/ubahanggota/");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }

            try {
                conn = (HttpURLConnection) urlubah.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("idanggota", id)
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

                Log.e(TAG, "Respon dari url: " + query);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                e1.printStackTrace();
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
            Log.e(TAG, "Respon dari url: " + result);
            if (result.equalsIgnoreCase("true")) {
                new TampilData().execute();

                Toast.makeText(getActivity(), "Data berhasil diubah", Toast.LENGTH_SHORT).show();

            } else if (result.equalsIgnoreCase("false")) {

                Toast.makeText(getActivity(), "Gagal mengubah data", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(getActivity(), "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();

            }
        }
    }
}
