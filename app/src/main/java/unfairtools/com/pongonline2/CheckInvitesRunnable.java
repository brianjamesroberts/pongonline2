package unfairtools.com.pongonline2;

import android.util.Log;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by brianroberts on 9/30/16.
 */

class CheckInvitesRunnable implements Runnable{
    public App app;
    public InvitesFragment invitesFragment;

    public CheckInvitesRunnable(App app1){
        this.app = app1;
    }
    public volatile boolean halt = false;
    public void run(){
        app.info.invites = null;
        while(!halt) {
            try {

                Thread.sleep(1000);


                ApiService service = app.mBoundService.getRetrofit().create(ApiService.class);
                Call<InfoObject> call = service.postInvites(app.info.user);

                call.enqueue(new Callback<InfoObject>(){

                    @Override
                    public void onResponse(Call<InfoObject> call, retrofit2.Response<InfoObject> response) {
                        try {
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



            } catch (Exception e) {

            }
        }
    }

}
