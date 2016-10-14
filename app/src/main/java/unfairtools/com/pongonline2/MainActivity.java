package unfairtools.com.pongonline2;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    public App app;



    protected void onStop(){
        super.onStop();
        app.main = null;
    }

    protected void onResume(){
        super.onResume();
        app.main = this;
        app.curView = this.findViewById(R.id.activity_main);

        Log.e("MainActivity","onResume");
    }

    public void startSecondActivity(){
        Intent intent = new Intent(this,GameActivity.class);
        startActivity(intent);
    }

    public void resetText(){
        Log.e("MainActivity", "Reset text called");
        ((EditText)findViewById(R.id.edittext_password)).setText(
                PreferenceManager.getDefaultSharedPreferences(app).getString("password",""));

        ((EditText)findViewById(R.id.edittext_user)).setText(
                PreferenceManager.getDefaultSharedPreferences(app).getString("user",""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        app = ((App)getApplication());

        app.main = this;

        //app.curView = findViewById(R.id.activity_main);

        //resets text fields for username password to desired state
        resetText();



        findViewById(R.id.button_new_account).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                NewAccountDialogFragment newFragment = new NewAccountDialogFragment();
                newFragment.show(getSupportFragmentManager(), "dialog");
            }

        });

        findViewById(R.id.button_login).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    InputMethodManager inputManager = (InputMethodManager)
                            app.getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);

                }catch(Exception e){
                    //e.printStackTrace();
                }

                PreferenceManager.getDefaultSharedPreferences(app).edit().putString("password",
                        ((EditText)findViewById(R.id.edittext_password)).getText().toString()).commit();

                PreferenceManager.getDefaultSharedPreferences(app).edit().putString("user",
                        ((EditText)findViewById(R.id.edittext_user)).getText().toString()).commit();


                app.login(((EditText)findViewById(R.id.edittext_user)).getText().toString(),
                        ((EditText)findViewById(R.id.edittext_password)).getText().toString());

//                new Thread(new Runnable() {
//                    public void run() {
//                        InfoObject infoObj = new InfoObject();
//                        infoObj.action = "LOGIN";
//                        String userName = ((EditText)findViewById(R.id.edittext_user)).getText().toString();
//                        String passWord = ((EditText)findViewById(R.id.edittext_password)).getText().toString();
//                        infoObj.vals = new String[]{userName, passWord};
//                        infoObj.appName = "pongonline";
//
//
//
//                        app.mBoundService.sendTSL(infoObj.toJSon());
//                    }
//                }).start();
            }
        });
    }
}

