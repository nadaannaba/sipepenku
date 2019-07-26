package penduduk.sipepen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by NADA on 5/9/2019.
 */

@SuppressLint("CommitPrefEdits")
public class SessionManager {

    SharedPreferences pref;

    SharedPreferences.Editor editor;

    Context _context;

    int PRIVATE_MODE = 0;

        private static final String PREF_NAME = "Sesi";

        private static final String IS_LOGIN = "IsLoggedIn";
        public static final String KEY_EMAIL = "email";
        public static final String KEY_SANDI = "sandi";
        public static final String KEY_IDUSER = "id_user";


        public SessionManager(Context context){
            this._context = context;
            pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
            editor = pref.edit();
        }

        public void createLoginSession(String email, String sandi, String iduser){
            editor.putBoolean(IS_LOGIN, true);

            editor.putString(KEY_EMAIL, email);
            editor.putString(KEY_SANDI, sandi);
            editor.putString(KEY_IDUSER, iduser);
            editor.commit();
        }

        public void createRegSession(String email, String sandi, String iduser){
            editor.putBoolean(IS_LOGIN, true);

            editor.putString(KEY_EMAIL, email);
            editor.putString(KEY_SANDI, sandi);
            editor.putString(KEY_IDUSER, iduser);
            editor.commit();
        }

        public void checkLogin(){
            if(!this.isLoggedIn()){
                Intent i = new Intent(_context, login.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                _context.startActivity(i);
            }else{
                Intent i = new Intent(_context, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                _context.startActivity(i);
            }

        }

        public HashMap<String, String> getUserDetails(){
            HashMap<String, String> user = new HashMap<String, String>();

            user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
            user.put(KEY_SANDI, pref.getString(KEY_SANDI, null));
            user.put(KEY_IDUSER, pref.getString(KEY_IDUSER, null));

            return user;
        }

        public void logoutUser(){

            editor.clear();
            editor.commit();

            Intent i = new Intent(_context, login.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        }

        public void hapussesi(){
            editor.clear();
            editor.commit();
        }

        public boolean isLoggedIn(){

            return pref.getBoolean(IS_LOGIN, false);
        }
}
