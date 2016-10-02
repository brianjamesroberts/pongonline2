package unfairtools.com.pongonline2;


import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


public class GameActivity extends AppCompatActivity implements
        GameFragment.OnFragmentInteractionListener,
        InvitesFragment.OnFragmentInteractionListener {

public void onFragmentInteraction(Uri uri){

}

    private View mContentView;

    private App app;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        app = (App)getApplication();


        mContentView = findViewById(R.id.frag_container_invites);

        app.curView = mContentView;

        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        FragmentManager fm = getSupportFragmentManager();

        Fragment frag = fm.findFragmentByTag("invites_fragment");

        if(frag==null){
            frag = new InvitesFragment();

            fm.beginTransaction().add(R.id.frag_container_invites,frag,"invites_fragment").commit();
        }


    }

    public void swapForGame(String gameNum, String playerNum){
        FragmentManager fm = getSupportFragmentManager();

        Fragment frag = fm.findFragmentByTag("game_fragment");
        if(frag==null)
            frag = new GameFragment();
        Bundle bundle = new Bundle();
        bundle.putString("playerNumber",playerNum);
        bundle.putString("gameNumber", gameNum);
        frag.setArguments(bundle);

        fm.beginTransaction()
                .replace(R.id.frag_container_invites,frag,"invites_fragment")
                .addToBackStack("game_fragment").commit();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.

    }



}
