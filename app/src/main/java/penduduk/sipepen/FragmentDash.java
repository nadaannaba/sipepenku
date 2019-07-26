package penduduk.sipepen;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;

/**
 * Created by NADA on 5/5/2019.
 */

public class FragmentDash extends Fragment {
    View rootview;
    TextView lblselamat;

    public FragmentDash(){

    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_dash, container, false);

        SessionManager session = new SessionManager(getActivity());
        HashMap<String, String> user = session.getUserDetails();

        String username = user.get(SessionManager.KEY_EMAIL);

        lblselamat = (TextView)rootview.findViewById(R.id.lblselamatdatang);
        lblselamat.setText("Selamat Datang, "+username+" !");

        return rootview;
    }
}
