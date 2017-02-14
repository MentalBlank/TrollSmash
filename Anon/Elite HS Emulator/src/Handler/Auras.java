package Handler;

import Sockets.ConnectionHandler;
import java.util.*;

/**
 * Handles all user auras.
 */
public class Auras {

    public int aurasActive[];
    
    public Auras() {
        aurasActive = new int[15];
    }

    public boolean isAuraActive(int auraID)
    {
        for(int i=1; i< 10; i++){
            if(aurasActive[i] == auraID){
                return true;
            }
        }
        return false;
    }

    public boolean addAura(ConnectionHandler user ,int auraID, int seconds, int[] ids, String[] type, String tgt, int max)
    {
        for(int i=1; i< 10; i++){
            if(aurasActive[i] == auraID){
                return false;
            } else if(aurasActive[i] == 0){
                aurasActive[i] = auraID;
                removeAura(user, auraID, seconds, ids, type, tgt, max);
                return true;
            }
        }
        return false;
    }

    public void removeAura(final ConnectionHandler user, final int auraID, final int seconds, final int[] ids, final String[] type, final String tgt, final int max)
    {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run() {
                for(int i=1; i< 10; i++){
                    if(aurasActive[i] == auraID){
                        aurasActive[i] = 0;
                        user.removeAura(auraID, type, ids, tgt, max);
                    }
                }
            }
        }, (seconds * 1000));
    }

    public void clearAuras(){
        for(int i = 1; i < 10; i++){
            aurasActive[i] = 0;
        }
    }
}
