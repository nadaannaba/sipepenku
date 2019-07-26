package penduduk.sipepen;

import android.app.Dialog;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

/**
 * Created by NADA on 5/9/2019.
 */

public class StatistikPenduduk extends Fragment {
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    private android.app.ActionBar supportActionBar;
    private ProgressDialog pDialog;
    private static String url;
    private String TAG = StatistikPenduduk.class.getSimpleName();
    View rootview;
    TableLayout tabelstatistik;
    TableRow rowlayout;
    TextView b1,b2,b3,b4,b5;

    ArrayList<HashMap<String, String>> data_map = new ArrayList<HashMap<String, String>>();

    public StatistikPenduduk(){
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.statistik_penduduk, container, false);

        tabelstatistik = (TableLayout) rootview.findViewById(R.id.tablelayout);

        TableRow row = new TableRow(getActivity());
        rowlayout = (TableRow) rootview.inflate(getActivity(), R.layout.statistik_penduduk, row);
        row.setLayoutParams(new LinearLayoutCompat.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));

        b1 = (TextView)rootview.findViewById(R.id.b1);
        b2 = (TextView)rootview.findViewById(R.id.b2);
        b3 = (TextView)rootview.findViewById(R.id.b3);
        b4 = (TextView)rootview.findViewById(R.id.b4);
        b5 = (TextView)rootview.findViewById(R.id.b5);

        new jmlpenduduk().execute();

        return rootview;
    }

    private class jmlpenduduk extends AsyncTask<Void, Void, Void> {
        protected void onPreExecute(){
            super.onPreExecute();

            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Memuat Data...");
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected Void doInBackground(Void... params) {
            SessionManager session = new SessionManager(getActivity());
            HashMap<String, String> user = session.getUserDetails();

            final String iduser = user.get(SessionManager.KEY_IDUSER);
            url = "https://pendataanpendudukrt5315.000webhostapp.com/sensus1/index.php/jsonparsing/jumlahpenduduk/";
            HTTPhandler sh = new HTTPhandler();

            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Respon dari url: " + jsonStr);

            if(jsonStr != null) try {
                JSONObject jsonObject = new JSONObject(jsonStr);

                JSONArray dataku = jsonObject.getJSONArray("data");

                JSONObject c = dataku.getJSONObject(0);
                final String tb1 = String.valueOf(c.getInt("jmlpenduduk"));
                final String tb2 = String.valueOf(c.getInt("jmlkeluarga"));
                final String tb3 = String.valueOf(c.getInt("jmllaki"));
                final String tb4 = String.valueOf(c.getInt("jmlperempuan"));
                final String tb5 = String.valueOf(c.getInt("jmlmeninggal"));

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        b1.setText(tb1);
                        b2.setText(tb2);
                        b3.setText(tb3);
                        b4.setText(tb4);
                        b5.setText(tb5);

                    }
                });

            } catch (final JSONException e) {
                Log.e(TAG, "Data parsing bermasalah : " + e.getMessage());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Data parsing bermasalah : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else{
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

        @Override
        protected void onPostExecute(Void aVoid) {
            pDialog.dismiss();
        }
    }

}
