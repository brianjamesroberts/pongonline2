package unfairtools.com.pongonline2;

import java.util.ArrayList;

/**
 * Created by brianroberts on 9/29/16.
 */

public class AppInfo {

    String user;
    String password;
    String firstOrSecondPlayer;
    ArrayList<Invite> invites;
    CheckInvitesRunnable checkInvitesRunnable;



    public enum SCREENSTATUS {
        LOGIN,INVITES,GAME
    }

    SCREENSTATUS mScreenStatus = SCREENSTATUS.LOGIN;

    public void setScreenStatus(SCREENSTATUS status){
        mScreenStatus = status;
    }

}
