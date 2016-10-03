package unfairtools.com.pongonline2;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;



/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InvitesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InvitesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InvitesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    public App app;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public InvitesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InvitesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InvitesFragment newInstance(String param1, String param2) {
        InvitesFragment fragment = new InvitesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        app = ((App)getActivity().getApplication());
        app.info.setScreenStatus(AppInfo.SCREENSTATUS.INVITES);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_invites, container, false);

                v.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        RecyclerView recyclerView = ((RecyclerView)v.findViewById(R.id.recyclerview_invites));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.setAdapter(new InviteAdapter(mInvites));

        return v;
    }



    public ArrayList<Invite> mInvites = new ArrayList<Invite>();

    class InviteHolder extends RecyclerView.ViewHolder{

        TextView otherUser;
        TextView gameNumber;
        Button acceptButton;
        Button playButton;

        public InviteHolder(View v){
            super(v);
            otherUser = (TextView)v.findViewById(R.id.invite_holder_user_text);
            gameNumber = (TextView)v.findViewById(R.id.invite_holder_game_number_text);
            acceptButton = (Button)v.findViewById(R.id.invite_holder_accept_button);
            playButton = (Button)v.findViewById(R.id.invite_holder_play_button);
        }


    }

    class InviteAdapter extends RecyclerView.Adapter<InviteHolder> {
        private volatile ArrayList<Invite> mInvites;

        public InviteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater
                    .inflate(R.layout.invite_layout, parent, false);
            return new InviteHolder(view);
        }

        @Override
        public void onBindViewHolder(InviteHolder holder, int position) {
            Invite invite = mInvites.get(position);

            if(invite.fromMe){
                holder.acceptButton.setVisibility(View.GONE);
                if(invite.validated){
                    holder.otherUser.setText("Play with " + invite.toUser + " now ->");
                    holder.playButton.setVisibility(View.VISIBLE);
                }else{
                    holder.otherUser.setText("Pending invite to " + invite.toUser);
                    holder.playButton.setVisibility(View.GONE);
                }
                //to me
            }else{
                if(invite.validated){
                    holder.acceptButton.setVisibility(View.GONE);
                    holder.otherUser.setText("You've accepted, go play ->");
                    holder.playButton.setVisibility(View.VISIBLE);
                }else{
                    holder.acceptButton.setVisibility(View.VISIBLE);
                    holder.otherUser.setText(invite.name + " wants to play");
                    holder.playButton.setVisibility(View.GONE);
                }

            }



                final String gameNum = invite.gameNumber;
                holder.acceptButton.setOnClickListener(new Button.OnClickListener(){
                    public void onClick(View v){
                        new Thread(new Runnable(){
                            public void run(){
                                InfoObject info = new InfoObject();
                                info.action = "VALIDATE_GAME_INVITE";
                                info.vals = new String[]{gameNum};
                                app.mBoundService.sendTSL(info.toJSon());
                            }
                        }).start();

                    }
                });

            final boolean fromMe = invite.fromMe;


            holder.playButton.setOnClickListener(new Button.OnClickListener(){
                public void onClick(View v){
                    new Thread(new Runnable(){
                        public void run(){
                            String playerNum;
                            if(fromMe)
                                playerNum = "1";
                            else
                                playerNum = "2";

                            //String gameNum = invite.gameNumber;
                            InfoObject info  = new InfoObject();
                            info.action = "JOIN_GAME";
                            info.vals = new String[]{app.info.user,playerNum,gameNum};
                            app.mBoundService.sendTSL(info.toJSon());

                        }
                    }).start();
                    String playerNum;
                    if(fromMe)
                        playerNum = "1";
                    else
                        playerNum = "2";
                    ((GameActivity)InvitesFragment.this.getActivity()).swapForGame(gameNum, playerNum);

                }
            });

            holder.otherUser.invalidate();
            holder.gameNumber.invalidate();
        }


        public InviteAdapter(ArrayList<Invite> invites){
            mInvites = invites;
        }
        public void setInvites(ArrayList<Invite> inv){
            mInvites.clear();
            mInvites.addAll(inv);
            for(int i = 0; i < mInvites.size();i++)
                notifyItemChanged(i);

        }
        public int getItemCount(){
            return mInvites.size();
        }

    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState){
        final View v5 = v;
        v.findViewById(R.id.button_invite).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View vs){


                try {
                    InputMethodManager inputManager = (InputMethodManager)
                            app.getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);

                }catch(Exception e){
                    e.printStackTrace();
                }
                final String inviteName = ((EditText)v5.findViewById(R.id.edittext_invite_user)).getText().toString();
                new Thread(new Runnable(){
                    public void run(){
                        try {
                            app.info.firstOrSecondPlayer = "1";
                            InfoObject inf = new InfoObject();
                            Log.e("REQUESTING", "NEW GAMEEEE WHYYYY");
                            inf.action = "NEW_GAME";
                            inf.appName = "pongonline";
                            inf.vals = new String[]{inviteName,app.info.user};
                            String json = inf.toJSon();
                            app.mBoundService.sendTSL(json);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();



            }
        });



    }

    public void onStart(){
        super.onStart();
        Log.e("InvitesFragment","Onstart called");
        app.cancelInvitesRunnable();
        app.info.checkInvitesRunnable = new CheckInvitesRunnable(app,this);
        new Thread(app.info.checkInvitesRunnable).start();

    }

    public void onStop(){
        super.onStop();
        app.cancelInvitesRunnable();
    }


public void checkInvitesView(){
    try {
        InviteAdapter invAdap = (InviteAdapter) ((RecyclerView) getView().findViewById(R.id.recyclerview_invites)).getAdapter();
        if (app.info.invites == null)
            invAdap.setInvites(new ArrayList<Invite>());
        else
            invAdap.setInvites(app.info.invites);
        invAdap.notifyDataSetChanged();
        ((RecyclerView) getView().findViewById(R.id.recyclerview_invites)).invalidate();
    }catch(Exception e){
        e.printStackTrace();
    }
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
