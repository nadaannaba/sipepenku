package penduduk.sipepen;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by NADA on 5/9/2019.
 */

public class CustomListView extends BaseAdapter {
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater = null;

    public CustomListView(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data = d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {return data.size();}

    public Object getItem(int position) {return position; }

    public long getItemId(int position) {return position; }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_layout, null);

        TextView textViewName = (TextView) vi.findViewById(R.id.textViewName);
        TextView textViewDesc = (TextView) vi.findViewById(R.id.textViewDesc);
        TextView textViewId = (TextView) vi.findViewById(R.id.idhidden);

        HashMap<String, String> kumpuldata = new HashMap<String, String>();
        kumpuldata = data.get(position);

        textViewName.setText(kumpuldata.get("names"));
        textViewDesc.setText(kumpuldata.get("desc"));
        textViewId.setText(kumpuldata.get("id_anggota"));

        return vi;
    }
}
