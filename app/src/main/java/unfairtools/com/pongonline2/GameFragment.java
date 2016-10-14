package unfairtools.com.pongonline2;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GameFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public App app;

    // TODO: Rename and change types of parameters

    public String mGameNumber;
    public String mPlayerNumber;

    private OnFragmentInteractionListener mListener;

    public GameFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GameFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GameFragment newInstance(String param1, String param2) {
        GameFragment fragment = new GameFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    public SendGameInfoUDPRunnable mUDPRunnable;

    class SendGameInfoUDPRunnable implements Runnable{
        public volatile boolean halt = false;

        private App app;
        private GameFragment frag;
        public SendGameInfoUDPRunnable(App app1, GameFragment frag1){
            app = app1;
            frag = frag1;
        }

        public void run(){

            final CanvasView canvas = (CanvasView) getView().findViewById(R.id.signature_canvas);
            while(!halt){
                //Log.e("GAMEFRAGMENT","Halt is " + halt);

                try{
                    Thread.sleep(30);
                    //Log.e("GameFragment","Sending game info udp");
                    InfoObject inf = new InfoObject();
                    inf.action = "SEND_GAME_INFO";
                    inf.vals = new String[]{app.info.user, mGameNumber, mPlayerNumber, canvas.getPaddleY() + ""};
                    //inf.vals = new String[]{mGameNumber,mPlayerNumber};
                    app.mBoundService.sendUDP(inf.toJSon());
                }catch(Exception e){

                }
            }

        }
    }


    public void onResume(){
        super.onResume();

        AppInfo.shownWinLoseSnackbar = false;

        mGameNumber = getArguments().getString("gameNumber");
        mPlayerNumber = getArguments().getString("playerNumber");
        app = ((App)getActivity().getApplication());
        app.info.gameCanvas = (CanvasView)getView().findViewById(R.id.signature_canvas);
        app.info.gameCanvas.player = Integer.parseInt(mPlayerNumber);

        if(mUDPRunnable!=null){
            mUDPRunnable.halt = true;
            mUDPRunnable = null;
        }

        mUDPRunnable = new SendGameInfoUDPRunnable(app,this);
        app.info.udpRunnable = mUDPRunnable;
        new Thread(mUDPRunnable).start();

    }

    public void onPause(){
        super.onPause();
        app.info.gameCanvas = null;
        app.info.udpRunnable = null;
        if(mUDPRunnable!=null){
            mUDPRunnable.halt = true;
            mUDPRunnable = null;
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGameNumber = getArguments().getString("gameNumber");
            mPlayerNumber = getArguments().getString("playerNumber");
            Log.e("GameFragment","game number: " + mGameNumber);
            Log.e("GameFragment","player number: " + mPlayerNumber);
        }
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState){

        v.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_game, container, false);


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
