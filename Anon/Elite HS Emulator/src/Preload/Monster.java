package Preload;

import java.util.ArrayList;
import java.util.Random;
/**
 * Handles the monsters hp and etc...
 */
public class Monster {

    public int hp, mp, state, hpmax, mpmax, level, exp, gold, rep, pvpScore;
    public ArrayList<Integer> pIDs, drops, dropsPercentage;
    public String frame;

    public Monster(int _hp, int _mp, int _level, int _exp, int _gold, int _rep, int _pvpScore, String _frame, String _drops) {
        hp = _hp;
        mp = _mp;
        hpmax = _hp;
        mpmax = _mp;
        state = 1;
        level = _level;
        frame = _frame;
        gold = _gold;
        exp = _exp;
        rep = _rep;
        pvpScore = _pvpScore;
        pIDs = new ArrayList<Integer>();

        /** Initialize Drops **/

        drops = new ArrayList<Integer>();
        dropsPercentage = new ArrayList<Integer>();
        if(_drops.contains(",")) {
            String[] temp = _drops.split(",");
            for(int i = 0; i < temp.length; i++) {
                String[] _t = temp[i].split(":");
                drops.add(Integer.parseInt(_t[0]));
                dropsPercentage.add((int) (Double.parseDouble(_t[1]) * 100));
            }
        } else {
            String[] _t = _drops.split(":");
            drops.add(Integer.parseInt(_t[0]));
            dropsPercentage.add((int) (Double.parseDouble(_t[1]) * 100));
        }
    }

    public int[] getRandomDrop() {
        int[] _rand = new int[2];
        int randI = new Random().nextInt(drops.size());
        _rand[0] = drops.get(randI);
        _rand[1] = dropsPercentage.get(randI);
        return _rand;
    }

    public void die() {
        pIDs.clear();
    }

    public void killPlayer(int pID) {
        pIDs.remove(pIDs.indexOf(pID));
    }

    public void attack(int pID) {
        if(!pIDs.contains(pID)) {
            pIDs.add(pID);
        }
    }

    public boolean checkUser(int pID) {
        if(pIDs.contains(pID)) {
            return false;
        } else {
            return true;
        }
    }
}
