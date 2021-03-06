package unfairtools.com.pongonline2;

import java.util.ArrayList;

/**
 * Created by brianroberts on 9/29/16.
 */

public class AppInfo {

    String user;
    String password;
    String firstOrSecondPlayer;
    volatile boolean gameOver = false;
    volatile boolean youWin = false;
    ArrayList<Invite> invites;
    volatile CheckInvitesRunnable checkInvitesRunnable;
    volatile GameFragment.SendGameInfoUDPRunnable udpRunnable;
    CanvasView gameCanvas;

    public static boolean shownWinLoseSnackbar = false;


    public enum SCREENSTATUS {
        LOGIN,INVITES,GAME
    }

    SCREENSTATUS mScreenStatus = SCREENSTATUS.LOGIN;

    public void setScreenStatus(SCREENSTATUS status){
        mScreenStatus = status;
    }

}
