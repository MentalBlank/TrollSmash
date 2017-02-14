/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Sockets;

import java.sql.*;
import Json.*;
import Preload.Monster;
import java.util.*;

/**
 * Handles the server rooms
 * @version 0.9b r4
 */
public class Room {
    
    public int roomID;
    public int roomNum;
    public int users;
    
    public int bScore = 0;
    public int rScore = 0;
    public int pvpActive = 1;

    public String sFile, sExtra, roomName, roomNick;

    public String[] mons, monnumbs, monframe;

    public List<Monster> _monsters = new ArrayList<Monster>();
    public HashMap<Integer, Integer> _mapItems = new HashMap<Integer, Integer>();
    public String[] _users = new String[10];

    private SqlConnection sql = new SqlConnection();
    private Random _gen = new Random();
    private boolean mArea = false;
    public boolean pvpArea = false;

    public int rID() {
        return 10201 + (roomID * roomNum * 2);
    }

    public Room(int MapID, int RoomNum) {
        try {
            if(sql.connect()) {
                ResultSet rs = sql.query("SELECT * FROM hs_maps WHERE id=" + MapID);
                if(rs.next()) {
                    roomName = rs.getString("name");
                    sExtra = rs.getString("sExtra");
                    sFile = rs.getString("fileName");
                    mons = rs.getString("monsterid").split(",");
                    monnumbs = rs.getString("monsternumb").split(",");
                    monframe = rs.getString("monsterframe").split(",");
                    if(rs.getString("mapitems").length() > 0) {
                        if(rs.getString("mapitems").contains(",")) {
                            String[] temp = rs.getString("mapitems").split(",");
                            for(int i = 0; i < temp.length; i++) {
                                String[] _t = temp[i].split(":");
                                _mapItems.put(Integer.parseInt(_t[0]), Integer.parseInt(_t[1]));
                            }
                        } else {
                            String[] _t = rs.getString("mapitems").split(":");
                            _mapItems.put(Integer.parseInt(_t[0]), Integer.parseInt(_t[1]));
                        }
                    }
                    roomID = MapID;
                    roomNum = RoomNum;
                    roomNick = "";
                    if(roomName.equals("bludrutbrawl")) {
                        pvpArea = true;
                    }
                    if(rs.getString("monsternumb").length() > 0) {
                        mArea = true;
                        initMonsters();
                    }
                }
            }
            for(int i = 1; i<10; i++) {
                _users[i] = "";
            }
        } catch (SQLException e) {
            Interface.writeLog(e.getMessage(), 2);
        }
    }

    public Room(int MapID, String _roomNick) {
        try {
            if(sql.connect()) {
                ResultSet rs = sql.query("SELECT * FROM hs_maps WHERE id=" + MapID);
                if(rs.next()) {
                    roomName = rs.getString("name");
                    sExtra = rs.getString("sExtra");
                    sFile = rs.getString("fileName");
                    mons = rs.getString("monsterid").split(",");
                    monnumbs = rs.getString("monsternumb").split(",");
                    monframe = rs.getString("monsterframe").split(",");
                    roomID = MapID;
                    roomNick = _roomNick;
                    if(rs.getString("mapitems").length() > 0) {
                        if(rs.getString("mapitems").contains(",")) {
                            String[] temp = rs.getString("mapitems").split(",");
                            for(int i = 0; i < temp.length; i++) {
                                String[] _t = temp[i].split(":");
                                _mapItems.put(Integer.parseInt(_t[0]), Integer.parseInt(_t[1]));
                            }
                        } else {
                            String[] _t = rs.getString("mapitems").split(":");
                            _mapItems.put(Integer.parseInt(_t[0]), Integer.parseInt(_t[1]));
                        }
                    }
                    if(_roomNick.equals("pvp") || roomName.equals("bludrutbrawl")) {
                        pvpArea = true;
                    }
                    roomNum =  _roomNick.hashCode();
                    if(rs.getString("monsternumb").length() > 0) {
                        mArea = true;
                        initMonsters();
                    }
                }
            }
            for(int i = 1; i<10; i++) {
                _users[i] = "";
            }
        } catch (SQLException e) {
            Interface.writeLog(e.getMessage(), 2);
        }
    }

    public void monsterActive(final int mID) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                     attackPlayer(mID);
                } catch (Exception e) {
                }
            }
        }, (1800 + _gen.nextInt(500)));
    }

    public void attackPlayer(int mID) {
        try {
            int mID2 = mID - 1;
            int _pID = _monsters.get(mID2).pIDs.get(_gen.nextInt(_monsters.get(mID2).pIDs.size()));
            int damage = (int) (1 + (_monsters.get(mID2).level * _monsters.get(mID2).level / 16) + (_monsters.get(mID2).level) * 1.25);
            int damage2 = (int) (40 + (_monsters.get(mID2).level * _monsters.get(mID2).level / 4) + (_monsters.get(mID2).level) * 1.25);
            int damage3 = damage + _gen.nextInt(damage2 - damage);

            int crit = _gen.nextInt(Interface._server.lobby.maxLevel);
            int dodge = _gen.nextInt(Interface._server.lobby.maxLevel);

            ConnectionHandler uho = Interface._server.lobby.getHandler(_pID);
            double areduce = 0;
            double preduce = uho._passives.get(0).reduction + uho._passives.get(1).reduction;

            for(int xd = 0; xd < uho._skills.size(); xd++) {
                if(uho._skills.get(xd).auraID > 0) {
                    if(uho._auras.isAuraActive(uho._skills.get(xd).auraID)) {
                        areduce = uho._skills.get(xd).aura.reduction;
                    }
                }
            }

            damage3 = (int) (damage3 - (damage3 * (areduce + preduce)));

            if (damage3 <= 0){
                damage3 = 0;
            }

            if(_monsters.get(mID2).frame.equals(uho.frame)) {
                String hit = "hit";
                if (_gen.nextInt(_monsters.get(mID2).level) > crit) {
                    hit = "crit";
                    damage3 = damage3 * 2;
                } else if (_gen.nextInt(uho.level) > dodge) {
                    hit = "dodge";
                    damage3 = 0;
                }
                uho.hp -= damage3;
                if (uho.hp <= 0) {
                    uho.hp = 0;
                    uho.state = 0;
                    _monsters.get(mID2).state = 1;
                    _monsters.get(mID2).killPlayer(_pID);
                } else if (uho.hp > 0) {
                    uho.state = 2;
                    _monsters.get(mID2).state = 2;
                }

                JSONObject ct = new JSONObject();
                JSONObject _anim = new JSONObject();

                ct.put("cmd", "ct");

                _anim.put("strFrame", _monsters.get(mID2).frame);
                _anim.put("cInf", "m:" + mID);
                _anim.put("fx", "m");
                _anim.put("animStr", "Attack1,Attack2");
                _anim.put("tInf", "p:" + _pID);

                ct.put("anims", new JSONArray().put(_anim));
                if(uho.state == 0) {
                    JSONObject m = new JSONObject();
                    JSONObject _m = new JSONObject();
                    _m.put("intState", 1);
                    m.put("" + mID, _m);
                    ct.put("m", m);
                }
                JSONObject p = new JSONObject();
                JSONObject _p = new JSONObject();
                if(uho.state != 0) {
                    _p.put("intHP", uho.hp);
                } else {
                    _p.put("intState", 0);
                    _p.put("intHP", 0);
                    _p.put("intMP", 0);
                }
                p.put(uho.username, _p);
                ct.put("p", p);
                JSONObject actionResult = new JSONObject();
                JSONObject _ar = new JSONObject();
                _ar.put("hp", damage3);
                _ar.put("cInf", "m:" + mID);
                _ar.put("tInf", "p:"+_pID);
                _ar.put("type", hit);
                actionResult.put("actionResult", _ar);
                actionResult.put("iRes", 1);
                ct.put("sara", new JSONArray().put(actionResult));
                Interface._server.lobby.sendDataToPlayerMap(uho.username, ct, false);
                if(_monsters.get(mID2).pIDs.size() > 0) {
                    monsterActive(mID);
                }
            }
        } catch (Exception e) {
            Interface.writeLog("[Room] AI: " + e.getMessage(), 2);
        }
    }

    public void givePvPScore(String username, String type, int id) {
        try {
            if(pvpArea && users > 1 && pvpActive == 1){
                int userGain = 0;
                JSONObject _pvps = new JSONObject();
                _pvps.put("cmd", "PVPS");
                JSONObject _rd = new JSONObject().put("v", 0).put("r", 0).put("m", 0).put("k", 0);
                JSONObject _bd = new JSONObject().put("v", 0).put("r", 0).put("m", 0).put("k", 0);
                int pvpTeam = Interface._server.lobby.getHandler(username).pvpTeam;
                if(type.equals("m")) {
                    if(pvpTeam == 0){
                        bScore = bScore + _monsters.get(id).pvpScore;
                        if(bScore >= 1000){
                            bScore = 1000;
                            pvpActive = 0;
                            pvpWin(username, pvpTeam);
                        }
                    } else if(pvpTeam == 1){
                        rScore = rScore + _monsters.get(id).pvpScore;
                        if(rScore >= 1000){
                            rScore = 1000;
                            pvpWin(username, pvpTeam);
                        }
                    }
                    userGain = _monsters.get(id).pvpScore;
                } else if (type.equals("p")) {
                    ConnectionHandler uho = Interface._server.lobby.getHandler(id);
                    if(pvpTeam == 0){
                        bScore = bScore + 80 +(uho.level*5);
                        if(bScore >= 1000){
                            bScore = 1000;
                            pvpWin(username, pvpTeam);
                        }
                    } else if(pvpTeam == 1){
                        rScore = rScore + 80 +(uho.level*5);
                        if(rScore >= 1000){
                            rScore = 1000;
                            pvpWin(username, pvpTeam);
                        }
                    }
                    userGain = 80 +(uho.level*5);
                }
                _rd.put("v", rScore);
                _bd.put("v", bScore);
                _pvps.put("pvpScore", new JSONArray().put(_bd).put(_rd));
                Interface._server.lobby.sendDataToPlayerMap(username, "%xt%server%-1%" + username + " won " + userGain + " points for his team!%", false);
                Interface._server.lobby.sendDataToPlayerMap(username, _pvps, false);
            }
        } catch (Exception e) {
            Interface.writeLog(e.getMessage(), 2);
        }
    }

    public void pvpWin(final String username, int pvpTeam) {
        try {
            pvpActive = 0;
            final JSONObject _pvpc = new JSONObject();
            _pvpc.put("cmd", "PVPC");
            final JSONObject _rd = new JSONObject().put("v", rScore).put("r", 0).put("m", 0).put("k", 0);
            final JSONObject _bd = new JSONObject().put("v", bScore).put("r", 0).put("m", 0).put("k", 0);
            _pvpc.put("team", pvpTeam);
            _pvpc.put("pvpScore", new JSONArray().put(_bd).put(_rd));
            Interface._server.lobby.sendDataToPlayerMap(username, _pvpc, false);
            Interface._server.lobby.sendDataToPlayerMap(username, "%xt%warning%-1%Score will be reseted in 10 seconds!%", false);
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                   pvpActive = 1;
                   bScore = 0;
                   rScore = 0;
                   try {
                        _rd.put("v", rScore);
                        _bd.put("v", bScore);
                       _pvpc.put("pvpScore", new JSONArray().put(_bd).put(_rd));
                   } catch (JSONException j){
                   }
                   Interface._server.lobby.sendDataToPlayerMap(username, _pvpc, false);
                   Interface._server.lobby.sendDataToPlayerMap(username, "%xt%server%-1%Score was reseted!%", false);
                }
            },10000);
        } catch (Exception e) {
        }
    }

    public int getCID(String username) {
        for(int i = 1; i<10; i++) {
            if(_users[i].equals(username)) {
                return i;
            }
        }
        return -1;
    }
    public void addClient(ConnectionHandler _client) {
        users++;
        
        if(_client._trace) {
            if(roomNick.length() > 0) {
                Interface.writeLog(_client.username + " has joined " + roomName + "-" + roomNick, 2);
            } else {
                Interface.writeLog(_client.username + " has joined " + roomName + "-" + roomNum, 2);
            }
        }

        int pID = Interface._server.lobby.getPID(_client.username);
        for(int i = 1; i < 10; i++) {
            if(_users[i].length() <= 0) {
                _users[i] = _client.username;
                if(pvpArea) {
                    if (i % 2 == 0) {
                        _client.pvpTeam = 0;
                    } else if (i % 2 != 0) {
                        _client.pvpTeam = 1;
                    }
                }
                break;
            }
        }
        String data = "<msg t='sys'><body action='joinOK' r='"+rID()+"'><pid id='"+pID+"' /><vars /><uLs r='"+rID()+"'>";
        for(int c = 1; c < 10; c++) {
            if(_users[c].length() > 0) {
                int cID = Interface._server.lobby.getPID(_users[c]);
                data += "<u i='"+cID+"' m='0' s='0' p='"+c+"'><n><![CDATA["+_users[c]+"]]></n><vars></vars></u>";
            }
        }
        data += "</uLs></body></msg>";
        Interface._server.lobby.sendDataToPlayer(_client.username, data);
    }

    public void giveRewards(int monID) {
        int usersAttacking = _monsters.get(monID).pIDs.size();
        int goldreward = (_monsters.get(monID).gold * Interface._server.lobby.goldRate) / usersAttacking;
        int xpreward = (_monsters.get(monID).exp * Interface._server.lobby.xpRate * _monsters.get(monID).level) / usersAttacking;
        int cpreward = ((_monsters.get(monID).rep * Interface._server.lobby.xpRate * _monsters.get(monID).level) / usersAttacking) + _gen.nextInt(50);
        for(int i=0; i < _monsters.get(monID).pIDs.size(); i++) {
            Interface._server.lobby.addRewards((monID + 1), cpreward, goldreward, xpreward, "m", _monsters.get(monID).pIDs.get(i), -1, 0);
        }
    }
    
    public void dropItems(int monID) {
        for(int i=0; i < _monsters.get(monID).pIDs.size(); i++) {
            for(int r = 0; r < _monsters.get(monID).drops.size(); r++) {
                int isDropped = _gen.nextInt(95);
                if (_monsters.get(monID).dropsPercentage.get(r) > isDropped) {
                    Interface._server.lobby.getHandler(_monsters.get(monID).pIDs.get(i)).dropItem(_monsters.get(monID).drops.get(r));
                }
            }
        }
    }

    public void joinUser(String username) {
        try {
            if(users > 1) {
                ConnectionHandler uho = Interface._server.lobby.getHandler(username);
                String data = "<msg t='sys'><body action='uER' r='"+rID()+"'><u i='"+uho.pID+"' m='0' s='0' p='"+getCID(username)+"'><n><![CDATA["+username+"]]></n><vars></vars></u></body></msg>";
                Interface._server.lobby.sendDataToPlayerMap(username, data, true);
                JSONObject inform = new JSONObject();
                inform.put("cmd", "uotls");
                JSONObject u = new JSONObject();
                u.put("uoName", uho.username);
                u.put("strUsername", uho.username.toUpperCase());
                u.put("strFrame",uho.frame);
                u.put("strPad", uho.pad);
                u.put("intState", uho.state);
                u.put("intLevel", uho.level);
                u.put("entID", uho.pID);
                u.put("entType", "p");
                u.put("showHelm", uho.showHelm);
                u.put("showCloak", uho.showCloak);
                u.put("intHP", uho.hp);
                u.put("intMP", uho.mp);
                if(pvpArea)
                    u.put("pvpTeam", uho.pvpTeam);
                
                u.put("intHPMax", uho.hpmax);
                u.put("intMPMax", uho.mpmax);
                u.put("tx", uho.tx);
                u.put("ty", uho.ty);
                //u.put("fly", uho.fly);
                u.put("afk", uho.afk);
        
                inform.put("o", u);
                inform.put("unm",  username);
                Interface._server.lobby.sendDataToPlayerMap(username, inform, true);
            }
        } catch (JSONException e) {
            //trace(e.getMessage());
        }
    }

    private void initMonsters() {
        try {
            for (int o = 0; o < monnumbs.length; o++) {
                ResultSet rs = sql.query("SELECT * FROM hs_monsters WHERE MonID="+Integer.parseInt(monnumbs[o]));
                if(rs.next()) {
                    Monster _mon = new Monster((rs.getInt("intHPMax") + _gen.nextInt(50)), rs.getInt("intMPMax"), rs.getInt("intLevel"), rs.getInt("intExp"), rs.getInt("intGold"), rs.getInt("intRep"), rs.getInt("pvpScore"), monframe[o], rs.getString("strDrops"));
                    _monsters.add(_mon);
                }
                rs.close();
            }
        } catch (Exception e) {
            Interface.writeLog(e.getMessage(), 2);
        }
    }

    public JSONArray getMonBranch() {
        try {
            if(mArea) {
                JSONArray monbranch = new JSONArray();
                for (int o = 0; o < monnumbs.length; o++) {
                    ResultSet os = sql.query("SELECT * FROM hs_monsters WHERE MonID="+Integer.parseInt(monnumbs[o]));
                    JSONObject xx = new JSONObject();
                    if (os.next()) {
                        xx.put("intHPMax", _monsters.get(o).hpmax);
                        xx.put("iLvl", os.getInt("intLevel"));
                        xx.put("MonMapID", (o+1));
                        xx.put("MonID", Integer.parseInt(monnumbs[o]));
                        xx.put("intMP", _monsters.get(o).mp);
                        xx.put("wDPS", os.getInt("iDPS"));
                        xx.put("intState", _monsters.get(o).state);
                        xx.put("intMPMax", _monsters.get(o).mpmax);
                        xx.put("intHP", _monsters.get(o).hp);
                        xx.put("bRed", "0");
                        if(pvpArea) {
                            if(os.getString("react").contains("0")) {
                                String[] x = os.getString("react").split(",");
                                xx.put("react", new JSONArray().put(Integer.parseInt(x[0])).put(Integer.parseInt(x[1])));
                            } else {
                                JSONArray x2 = new JSONArray();
                                if (o % 2 == 0) {
                                    x2.put(1).put(0);
                                } else if (o % 2 != 0) {
                                    x2.put(0).put(1);
                                }
                                xx.put("react", x2);
                            }
                        }
                        monbranch.put(xx);
                    }
                }
                return monbranch;
            }
        } catch (Exception e) {
            Interface.writeLog(e.getMessage(), 2);
        }
        return new JSONArray();
    }

    public JSONArray getMonMap() {
        try {
            if(mArea) {
                JSONArray monmap = new JSONArray();
                for (int o = 0; o < monnumbs.length; o++) {
                    JSONObject xx = new JSONObject();
                    xx.put("MonMapID", (o + 1));
                    xx.put("strFrame", monframe[o]);
                    xx.put("intRSS", -1);
                    xx.put("MonID", monnumbs[o]);
                    xx.put("bRed", 0);
                    monmap.put(xx);
                }
                return monmap;
            }
        } catch (Exception e) {
            Interface.writeLog(e.getMessage(), 2);
        }
        return new JSONArray();
    }

    public JSONArray getMonDef() {
        try {
            if(mArea) {
                JSONArray mondef = new JSONArray();
                for (int e = 0; e < mons.length; e++) {
                    JSONObject mon = new JSONObject();
                    ResultSet rs = sql.query("SELECT * FROM hs_monsters WHERE MonID="+Integer.parseInt(mons[e]));
                    if (rs.next()) {
                        mon.put("sRace", rs.getString("sRace"));
                        mon.put("MonID", rs.getInt("MonID"));
                        mon.put("intMP", _monsters.get(e).mpmax);
                        mon.put("intLevel", rs.getInt("intLevel"));
                        mon.put("intMPMax", _monsters.get(e).mpmax);
                        mon.put("intHP", _monsters.get(e).hpmax);
                        mon.put("strBehave", "walk");
                        mon.put("intHPMax", _monsters.get(e).hpmax);
                        mon.put("strElement", rs.getString("strElement"));
                        mon.put("strLinkage", rs.getString("strLinkage"));
                        mon.put("strMonFileName", rs.getString("strMonFileName"));
                        mon.put("strMonName", rs.getString("strMonName"));
                        mondef.put(mon);
                    }
                    rs.close();
                }
                return mondef;
            }      
        } catch (Exception e) {

        }
        return new JSONArray();
    }

    public JSONArray getUsers() {
        try {
            JSONArray a = new JSONArray();
            for(int i = 1; i < 10; i++) {
                if(_users[i].length() > 0) {
                    JSONObject u = new JSONObject();
                    ConnectionHandler uho = Interface._server.lobby.getHandler(_users[i]);
                    int eID = Interface._server.lobby.getPID(uho.username);
                    u.put("uoName", uho.username);
                    u.put("strUsername", uho.username.toUpperCase());
                    u.put("strFrame",uho.frame);
                    u.put("strPad", uho.pad);
                    u.put("intState", uho.state);
                    u.put("intLevel", uho.level);
                    u.put("entID", eID);
                    u.put("entType", "p");
                    u.put("showHelm", uho.showHelm);
                    u.put("showCloak", uho.showCloak);
                    u.put("intHP", uho.hp);
                    u.put("intMP", uho.mp);
                    
                    if(pvpArea)
                        u.put("pvpTeam", uho.pvpTeam);
                    u.put("intHPMax", uho.hpmax);
                    u.put("intMPMax", uho.mpmax);
                    u.put("tx", uho.tx);
                    u.put("ty", uho.ty);
                    u.put("fly", uho.fly);
                    u.put("afk", uho.afk);
                    
                    a.put(u);
                }
            }
            return a;
        } catch (JSONException e) {

        }
        return null;
    }

    public void respawnMonster(final int monID) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    _monsters.get(monID).mp = _monsters.get(monID).mpmax;
                    _monsters.get(monID).hp = _monsters.get(monID).hpmax;
                    _monsters.get(monID).state = 1;
                    JSONObject xx = new JSONObject();
                    xx.put("cmd", "mtls");
                    xx.put("id", (monID+1));
                    JSONObject x2 = new JSONObject();
                    x2.put("intMP", _monsters.get(monID).mp);
                    x2.put("intState", 1);
                    x2.put("intHP", _monsters.get(monID).hp);
                    xx.put("o", x2);
                    String data = "%xt%respawnMon%-1%"+(monID+1)+"%";
                    for(int c = 1; c < 10; c++) {
                        if(_users[c].length() > 0) {
                            Interface._server.lobby.sendDataToPlayerMap(_users[c], xx, false);
                            Interface._server.lobby.sendDataToPlayerMap(_users[c], data, false);
                        }
                    }
                } catch (Exception e) {
                }
            }
        }, Interface._server.lobby.respawnTime);
    }

    

    public void removeClient(ConnectionHandler _client) {
        for(int i = 1; i < 10; i++) {
            if(_users[i].equals(_client.username)) {
                String data = "<msg t='sys'><body action='userGone' r='" + rID() + "'><user id='" + _client.pID + "' /></body></msg>";
                Interface._server.lobby.sendDataToPlayerMap(_client.username, data, true);
                data = "%xt%exitArea%-1%" + _client.pID + "%" + _client.username + "%";
                Interface._server.lobby.sendDataToPlayerMap(_client.username, data, true);
                _users[i] = "";
                users--;
                break;
            }
        }
    }

    @Override
    public void finalize() {
        try {
            super.finalize();
        } catch (Throwable ex) {
        }
        sql.close();
    }
}
