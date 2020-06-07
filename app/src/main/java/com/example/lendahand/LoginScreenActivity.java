package com.example.lendahand;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LoginScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        //TODO: Shared preferences, stay logged in
        //ensuring that we stay logged in
        /*
        if(StayLoggedIn.getUserName(LoginScreenActivity.this).length() == 0)
        {
            // call Login Activity

        }
        else
        {
            // Stay at the current activity.
        }

*/
        setContentView(R.layout.activity_login_screen);

    }


    public void LogIn(View view) {
        //TODO: Validations and decryption



        //Toast to display that they are logging in
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) findViewById(R.id.custom_toast_container));

        TextView txtToast = (TextView) layout.findViewById(R.id.txtToast);
        txtToast.setText(R.string.txt_toast_login);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();

        //TODO: Change this to certain screen depending on if logged in user is donor/ donee

        final Intent intent = new Intent(this, MainActivity.class);

        //thread is used to make sure toast is just shown on login
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();

    }

    @Override
    public void onBackPressed() {
        //TODO: get rid of toast on back button pressed

    }

    public void SignIn(View view) {
        Intent intent = new Intent(this, SelectUserTypeActivity.class);
        startActivity(intent);
        finish();
    }
}
