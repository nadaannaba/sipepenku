package penduduk.sipepen;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import static android.R.attr.fragment;
import static penduduk.sipepen.daftar.CONNECTION_TIMEOUT;

/**
 * Created by NADA on 5/5/2019.

 */

public class FragmentData extends Fragment {
    private ListView listView;
    private String TAG = FragmentData.class.getSimpleName();
    private ProgressDialog pDialog;
    private ActionBar supportActionBar;
    private static String url = "https://pendataanpendudukrt5315.000webhostapp.com/sensus1/index.php/jsonparsing/anggotauser/";
    CustomListView adapter;

    ArrayList<HashMap<String, String>> data_map = new ArrayList<HashMap<String, String>>();

    android.app.Fragment fragment;
    FragmentTransaction fragmentTransaction;
    View rootview;
    FloatingActionButton ActionBtnTambah;

    public FragmentData(){

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        rootview = inflater.inflate(R.layout.fragment_data, container, false);

        new Tampildata().execute();

        ActionBtnTambah = (FloatingActionButton) rootview.findViewById(R.id.ActionBtnTambah);
        ActionBtnTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = new TambahData();
                fragmentTransaction = getFragmentManager().beginTransaction().replace(R.id.kontenlayout, fragment);
                fragmentTransaction.commit();
            }
        });
        return rootview;
    }

    private class Tampildata extends AsyncTask<Void, Void, Void> {
        SessionManager session = new SessionManager(getActivity());
        HashMap<String, String> user = session.getUserDetails();

        String iduser = user.get(SessionManager.KEY_IDUSER);

        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Memuat Data...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected Void doInBackground(Void...params) {
            HTTPhandler sh = new HTTPhandler();

            String jsonStr = sh.makeServiceCall(url+iduser);

            Log.e(TAG, "Respon dari url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonStr);

                    JSONArray dataku = jsonObject.getJSONArray("data");

                    for (int i = 0; i < dataku.length(); i++) {
                        JSONObject c = dataku.getJSONObject(i);
                        String names = c.getString("nama_lengkap").trim();
                        String desc = c.getString("status_keluarga").trim();
                        String idanggota = c.getString("id_anggota").trim();
                        Integer imageid = R.drawable.default_user_image;

                        HashMap map = new HashMap();
                        map.put("names", names);
                        map.put("desc", desc);
                        map.put("id_anggota", idanggota);

                        data_map.add(map);
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listView = (ListView) rootview.findViewById(R.id.listview);
                            listView.setDivider(null);
                            adapter = new CustomListView(getActivity(), data_map);
                            listView.setAdapter(adapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    String idnih = ((TextView) view.findViewById(R.id.idhidden)).getText().toString();

                                    Bundle arguments = new Bundle();
                                    arguments.putString("kirimid", idnih);

                                    fragment = new DetailData();
                                    fragment.setArguments(arguments);
                                    fragmentTransaction = getFragmentManager().beginTransaction().replace(R.id.kontenlayout, fragment);
                                    fragmentTransaction.commit();
                                }
                            });
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
}