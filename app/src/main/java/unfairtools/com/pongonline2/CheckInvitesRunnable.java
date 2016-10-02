package unfairtools.com.pongonline2;

/**
 * Created by brianroberts on 9/30/16.
 */

class CheckInvitesRunnable implements Runnable{
    public App app;
    public InvitesFragment invitesFragment;
    public CheckInvitesRunnable(App app1, InvitesFragment frag){
        this.app = app1;
        this.invitesFragment = frag;
    }
    public volatile boolean halt = false;
    public void run(){
        while(!halt) {
            try {
                InfoObject inf = new InfoObject();
                inf.action = "INVITES?";
                inf.appName = "pongonline";
                inf.vals = new String[]{app.info.user};
                final String json = inf.toJSon(inf);
                app.mBoundService.sendTSL(json);
                invitesFragment.getView().post(new Runnable(){
                    public void run(){
                        invitesFragment.checkInvitesView();
                    }
                });
                Thread.sleep(1000);

            } catch (Exception e) {

            }
        }
    }

}
