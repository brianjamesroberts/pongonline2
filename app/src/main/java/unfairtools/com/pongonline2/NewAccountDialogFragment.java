package unfairtools.com.pongonline2;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by brianroberts on 10/4/16.
 */

public class NewAccountDialogFragment extends DialogFragment {

    App app;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup parent, Bundle savedInstanceState) {


      return inflater.inflate(R.layout.fragment_new_account_dialog_fragment,parent);

      }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        final View v5 = v;


        app = (App)getActivity().getApplication();

        app.curView = v;


        v.findViewById(R.id.button_new_account_new_account).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                try {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                }catch(Exception e){
                    e.printStackTrace();
                    Log.e("NewAccountDia",e.toString());
                }
                new Thread(new Runnable(){
                    public void run(){
                        //InfoObject infoObj = new InfoObject();
                        //infoObj.action = "NEW_ACCOUNT";
                        String userName = ((EditText)v5.findViewById(R.id.edittext_user_new_account)).getText().toString();
                        String passWord = ((EditText)v5.findViewById(R.id.edittext_password_new_account)).getText().toString();
                        //infoObj.vals = new String[]{userName,passWord};
                        //infoObj.appName = "pongonline";

                        ApiService svc = app.mBoundService.getRetrofit().create(ApiService.class);
                        Call<InfoObject> call = svc.newAccount(userName,passWord);
                        call.enqueue(new Callback<InfoObject>(){
                            @Override
                            public void onResponse(Call<InfoObject> call, retrofit2.Response<InfoObject> response) {
                                try {
                                    Log.e("Recvd", new JSONObject(response.body().toJSon()).toString());
                                    app.readTSLInfo(new JSONObject(response.body().toJSon()));
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                            @Override
                            public void onFailure(Call<InfoObject> call, Throwable t) {
                                Log.e("resp","failed " + t.toString());
                            }
                        });

                        //app.mBoundService.sendTSL(infoObj.toJSon());
                    }
                }).start();

            }
        });
    }



    @Override
    public void onStop(){
        super.onStop();


            PreferenceManager.getDefaultSharedPreferences(app).edit().putString("password",
                    ((EditText)getView().findViewById(R.id.edittext_password_new_account)).getText().toString()).commit();

            PreferenceManager.getDefaultSharedPreferences(app).edit().putString("user",
                    ((EditText)getView().findViewById(R.id.edittext_user_new_account)).getText().toString()).commit();

        app.curView = getActivity().findViewById(R.id.activity_main);


        ((MainActivity)getActivity()).resetText();


    }



}
