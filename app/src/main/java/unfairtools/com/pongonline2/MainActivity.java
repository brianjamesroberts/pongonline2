package unfairtools.com.pongonline2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
    }

    public void startSecondActivity(){
        Intent intent = new Intent(this,GameActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        app = ((App)getApplication());

        app.main = this;

        app.curView = findViewById(R.id.activity_main);

        findViewById(R.id.button_login).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    public void run() {
                        InfoObject infoObj = new InfoObject();
                        infoObj.action = "LOGIN";
                        String userName = ((EditText)findViewById(R.id.edittext_user)).getText().toString();
                        String passWord = ((EditText)findViewById(R.id.edittext_password)).getText().toString();
                        infoObj.vals = new String[]{userName, passWord};
                        infoObj.appName = "pongonline";
                        app.mBoundService.sendTSL(infoObj.toJSon());
                    }
                }).start();
            }
        });
    }
}

