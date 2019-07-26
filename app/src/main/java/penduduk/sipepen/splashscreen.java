package penduduk.sipepen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by NADA on 5/2/2019.
 */

public class splashscreen extends AppCompatActivity {

    public static int splash = 2000;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splashscreen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SessionManager sm = new SessionManager(getApplicationContext());
                sm.checkLogin();
                //Intent i = new Intent(getApplicationContext(),login.class);
                //startActivity(i);
                //finish();
            }
        }, splash);
    }
}
