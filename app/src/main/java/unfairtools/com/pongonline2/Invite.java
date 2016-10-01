package unfairtools.com.pongonline2;

/**
 * Created by brianroberts on 9/30/16.
 */

class Invite{
    boolean pending = true;
    String name = "";
    boolean fromMe = false;
    String gameNumber;
    String IAmPlayerNumber;
    String toUser;
    boolean validated = false;

    public Invite setName(String name1){
        this.name = name1;
        return this;
    }
}