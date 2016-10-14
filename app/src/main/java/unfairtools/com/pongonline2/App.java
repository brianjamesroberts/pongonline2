package unfairtools.com.pongonline2;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by brianroberts on 9/29/16.
 */

public class App extends Application {


    public AppInfo info;

    public View curView;

    public InvitesFragment invitesFragment;

    public InvitesFragment invFrag;


    public MainActivity main;

    public static String TAG = "PongOnline2";

    public boolean mIsBound;
    public ConnectionService mBoundService;


    public static String loginURL = "https://158.69.207.153/pongonline/api/";


    public void login(String usernm, String passwd){
        final String usernm1 = usernm;
        final String passwd1 = passwd;
        new Thread(new Runnable(){
            public void run() {

                Log.e(TAG,"Logging in");
                ApiService service = mBoundService.getRetrofit().create(ApiService.class);
                Call<InfoObject> call = service.postLogin(usernm1,passwd1);

                Log.e("call","URL of call is " + call.request().url());

                call.enqueue(new Callback<InfoObject>(){

                    @Override
                    public void onResponse(Call<InfoObject> call, retrofit2.Response<InfoObject> response) {
                        try {
                            Log.e("Recvd", new JSONObject(response.body().toJSon()).toString());
                            readTSLInfo(new JSONObject(response.body().toJSon()));
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(Call<InfoObject> call, Throwable t) {
                        Log.e("resp","failed " + t.toString());
                    }
                });



            }
        }).start();
    }

    public volatile boolean connectionReady = false;


    public void cancelInvitesRunnable(){
        if (info.checkInvitesRunnable!=null){
            info.checkInvitesRunnable.halt = true;
            info.checkInvitesRunnable = null;
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            Log.e(TAG, "Attempting to bind service");
            mBoundService = ((ConnectionService.LocalBinder) service).getService();


            mBoundService.initVals(getResources());
            mBoundService.initConnectionService();

            //blocking call
            mBoundService.getConnectionTSL();
            //blocking call
            mBoundService.getConnectionUDP();


            connectionReady = true;
            beginReadTSL();
            beginReadUDP();

        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mBoundService = null;
            Toast.makeText(getApplicationContext(), "Service disconnected",
                    Toast.LENGTH_SHORT).show();
        }
    };

        class UDPReadRunnable implements Runnable {
            public volatile boolean halt = false;
            App app;

            public UDPReadRunnable(App app1){
                this.app = app1;
            }
            public void run() {

                while(!halt) {
                    String line = mBoundService.readUDP();
                    try {
                        final JSONObject obj = new JSONObject(line);
                        switch ((String) obj.get("action")) {
                            case "halt":
                                //Log.e("APP","HALT REC'VD");
                                    if(app.info.udpRunnable!=null) {
                                        //halts the sending runnable
                                        app.info.udpRunnable.halt = true;
                                        app.info.udpRunnable = null;
                                    }
                                    JSONArray valArray = obj.getJSONArray("vals");
                                    if(valArray!=null){
                                        //Log.e("APP","Val array of 0 is " + valArray.getString(0));
                                        if(valArray.getString(0).equals("true")){
                                            if(!AppInfo.shownWinLoseSnackbar) {
                                                Log.e("APP","Showing you win");
                                                Snackbar.make(app.info.gameCanvas, "YOU WIN", 7000).show();
                                                AppInfo.shownWinLoseSnackbar = true;
                                            }
                                        }else{
                                            if(!AppInfo.shownWinLoseSnackbar) {
                                                Log.e("APP","Showing you lose");
                                                Snackbar.make(app.info.gameCanvas, "YOU LOSE", 7000).show();
                                                AppInfo.shownWinLoseSnackbar = true;
                                            }
                                        }
                                    }
                                break;
                            case "GAME_INFO":
                                //Log.e("App","Game info received player " + info.firstOrSecondPlayer);

                                final JSONArray arr = obj.getJSONArray("vals");
                                final String ballPosX = arr.getString(1);
                                final String ballPosY = arr.getString(2);
                                final String oppPaddle = arr.getString(3);

                                if(app.info.gameCanvas!=null) {
                                    app.info.gameCanvas.post(new Runnable() {
                                        public void run() {
                                            try {
                                                app.info.gameCanvas.player = Integer.parseInt(arr.getString(0));
                                                app.info.gameCanvas.clearCanvas();
                                                app.info.gameCanvas.xball = Float.parseFloat(ballPosX);
                                                app.info.gameCanvas.yball = Float.parseFloat(ballPosY);
                                                app.info.gameCanvas.oppPaddle = Float.parseFloat(oppPaddle);
                                                app.info.gameCanvas.invalidate();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                                break;
                            default:
                                break;

                        }
                    }catch (Exception e){
                        Log.e("App","" + line);
                        e.printStackTrace();
                    }
                }
            }
        }

        public void beginReadUDP(){
            new Thread(new UDPReadRunnable(App.this)).start();
        }

        public void beginReadTSL(){
            new Thread(new TSLReadRunnable()).start();
        }



        class TSLReadRunnable implements Runnable {
            public volatile boolean halt = false;

            public void run() {
                while (!halt) {
                    if (!connectionReady) {
                        try {
                            Thread.sleep(100);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        continue;
                    }
                    //Log.e(App.TAG, "waiting for read...");
                    String line = mBoundService.readTSLLine();
                    //Log.e(App.TAG, "Read tsl: " + line);
                    try {
                        final JSONObject obj = new JSONObject(line);

                        readTSLInfo(obj);

                    }catch(Exception e){
                        e.printStackTrace();
                        System.exit(0);
                    }
                }
            }
        }

        public void readTSLInfo(JSONObject obj){
            Log.e("resp","readTSLInfo called");
            try {
                switch ((String) obj.get("action")) {

                    case "INVITE_RECEIVE":
                        //2d String array
                        JSONArray arr = obj.getJSONArray("maps");
                        int size = arr.length();
                        ArrayList<Invite> invites = new ArrayList<Invite>();
                        for (int i = 0; i < size; i++) {
                            Invite inv = new Invite();
                            JSONArray jarr = arr.getJSONArray(i);
                            inv.gameNumber = jarr.getString(0);
                            inv.name = jarr.getString(1);
                            if (jarr.length() > 2 && jarr.getString(2).equals("pending")) {
                                inv.fromMe = true;
                            } else {
                                inv.fromMe = false;
                            }
                            inv.toUser = jarr.getString(3);
                            inv.validated = Boolean.parseBoolean(jarr.getString(4));

                            invites.add(inv);
                        }
                        info.invites = invites;

                        try {
                            if(invitesFragment!=null) {
                                invitesFragment.getView().post(new Runnable() {
                                    public void run() {
                                        invitesFragment.checkInvitesView();
                                    }
                                });
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }


                        break;
                    case "login_accepted":
                        info.user = obj.getJSONArray("vals").getString(0);
                        info.password = obj.getJSONArray("vals").getString(1);
                        info.mScreenStatus = AppInfo.SCREENSTATUS.GAME;
                        Log.e("TAG", "Logging in, starting invites view activity");
                        main.startSecondActivity();
                        main = null;
                        break;

                    case "login_denied":
                        try {
                            Snackbar snackbar = Snackbar.make(curView, "Incorrect login information...", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;

                    case "SNACKBAR":
                        try {
                            Snackbar snackbar = Snackbar.make(curView, obj.getJSONArray("vals").getString(0), Snackbar.LENGTH_LONG);
                            snackbar.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }





    void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        bindService(new Intent(this,
                ConnectionService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        mBoundService.killWriter();
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }


    @Override
    public void onCreate(){
        super.onCreate();
        Log.e(TAG,"oncreate called");
        info = new AppInfo();
        doBindService();
    }
}


