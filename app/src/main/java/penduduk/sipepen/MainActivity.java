package penduduk.sipepen;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentDash fragmentDash = new FragmentDash();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(
                R.id.kontenlayout,
                fragmentDash,
                fragmentDash.getTag()
        ).commit();

        getSupportActionBar().setTitle("Beranda");
    }


    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        //}else if (getFragmentManager().getBackStackEntryCount()>0){
          //  getFragmentManager().popBackStackImmediate();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.profil) {
            FragmentProfil fragmentProfil = new FragmentProfil();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(
                    R.id.kontenlayout,
                    fragmentProfil,
                    fragmentProfil.getTag()
            ).commit();

            getSupportActionBar().setTitle("Profil");

        } else if (id == R.id.penduduk) {
            FragmentData fragmentData = new FragmentData();
            android.app.FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(
                    R.id.kontenlayout,
                    fragmentData,
                    fragmentData.getTag()
            ).commit();

            getSupportActionBar().setTitle("Data Penduduk");

        } else if (id == R.id.statistik) {
            StatistikPenduduk statistikPenduduk = new StatistikPenduduk();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(
                    R.id.kontenlayout,
                    statistikPenduduk,
                    statistikPenduduk.getTag()
            ).commit();

            getSupportActionBar().setTitle("Statistik Penduduk");

        } else if (id == R.id.keluar) {
            SessionManager sm = new SessionManager(this);
            sm.logoutUser();
            finish();
            startActivity(new Intent(getApplicationContext(),login.class));

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
