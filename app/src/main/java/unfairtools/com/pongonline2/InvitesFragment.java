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

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;


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
                        //| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        //| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                );

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

        public void clearList(){
            mInvites.clear();
        }

        @Override
        public void onBindViewHolder(InviteHolder holder, int position) {
            Invite invite = mInvites.get(position);

            if(invite.fromMe){
                holder.acceptButton.setVisibility(View.GONE);
                if(invite.validated){
                    holder.otherUser.setText(invite.toUser + " accepted your invite");
                    holder.playButton.setVisibility(View.VISIBLE);
                }else{
                    holder.otherUser.setText("Pending invite to " + invite.toUser);
                    holder.playButton.setVisibility(View.GONE);
                }
                //to me
            }else{
                if(invite.validated){
                    holder.acceptButton.setVisibility(View.GONE);
                    holder.otherUser.setText("Accepted " + invite.name + "'s invite");
                    holder.playButton.setVisibility(View.VISIBLE);
                }else{
                    holder.acceptButton.setVisibility(View.VISIBLE);
                    holder.otherUser.setText(invite.name + " wants to play");
                    holder.playButton.setVisibility(View.GONE);
                }

            }
            holder.otherUser.invalidate();
            holder.gameNumber.invalidate();


                final String gameNum = invite.gameNumber;
                holder.acceptButton.setOnClickListener(new Button.OnClickListener(){
                    public void onClick(View v){
                        new Thread(new Runnable(){
                            public void run(){
                                //InfoObject info = new InfoObject();
                                //info.action = "VALIDATE_GAME_INVITE";
                                //info.vals = new String[]{gameNum};
                               // app.mBoundService.sendTSL(info.toJSon());

                                ApiService svc = app.mBoundService.getRetrofit().create(ApiService.class);
                                Call<InfoObject> call = svc.validateInvite(gameNum);
                                try {
                                    call.execute();
//                                      (new Callback<InfoObject>(){
//                                        @Override
//                                        public void onResponse(Call<InfoObject> call, retrofit2.Response<InfoObject> response) {
//                                            try {
//                                                Log.e("Recvd", new JSONObject(response.body().toJSon()).toString());
//                                                if())
//                                                app.readTSLInfo(new JSONObject(response.body().toJSon()));
//                                            }catch (Exception e){
//                                                e.printStackTrace();
//                                            }
//                                        }
//                                        @Override
//                                        public void onFailure(Call<InfoObject> call, Throwable t) {
//                                            Log.e("resp","failed " + t.toString());
//                                        }
//                                    });
                                }catch(Exception e){
                                    e.printStackTrace();
                                }

                            }
                        }).start();

                    }
                });

            final boolean fromMe = invite.fromMe;
            holder.playButton.setOnClickListener(new Button.OnClickListener(){
                public void onClick(View v) {
                    String playerNum;
                    if (fromMe) playerNum = "1";
                    else playerNum = "2";
                    ((GameActivity)InvitesFragment.this.getActivity()).swapForGame(gameNum, playerNum);
                }
            });

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
        ((TextView)v.findViewById(R.id.invite_fragment_username_display)).setText(app.info.user);
        v.findViewById(R.id.button_invite).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View vs){

                try {
                    InputMethodManager inputManager = (InputMethodManager)
                            app.getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);

                }catch(Exception e){
                    //e.printStackTrace();
                }
                final String inviteName = ((EditText)v5.findViewById(R.id.edittext_invite_user)).getText().toString();
                new Thread(new Runnable(){
                    public void run(){
                        try {
                            //app.info.firstOrSecondPlayer = "1";
//

                            ApiService a = app.mBoundService.getRetrofit().create(ApiService.class);
                            Call<InfoObject> call = a.inviteUser(inviteName,app.info.user);

                            Log.e("call","invite user" + call.request().url());

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



                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();



            }
        });



    }

    public void onResume(){
        super.onResume();
        ((InviteAdapter)((RecyclerView) getView().findViewById(R.id.recyclerview_invites)).getAdapter()).clearList();
        ((RecyclerView) getView().findViewById(R.id.recyclerview_invites)).getAdapter().notifyDataSetChanged();
        app.invitesFragment = this;
    }

    @Override
    public void onPause(){
        super.onPause();
        app.invitesFragment = null;
    }

    public void onStart(){
        super.onStart();
        Log.e("InvitesFragment","Onstart called");
        app.cancelInvitesRunnable();
        ((InviteAdapter)((RecyclerView) getView().findViewById(R.id.recyclerview_invites)).getAdapter()).clearList();
        app.info.checkInvitesRunnable = new CheckInvitesRunnable(app);
        new Thread(app.info.checkInvitesRunnable).start();

    }

    public void onStop(){
        super.onStop();
        app.cancelInvitesRunnable();
        ((InviteAdapter)((RecyclerView) getView().findViewById(R.id.recyclerview_invites)).getAdapter()).clearList();

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
