package Sockets;

import Json.*;
import Preload.*;
import java.net.Socket;
import java.util.*;
import java.sql.*;
import javax.swing.DefaultListModel;

/**
 * Handles all rooms, connections, users, probably the server lobby.
 * @version 1.0b r32
 */
public class ServerLobby {
    protected List<ConnectionHandler> _users;
    protected List<Room> _rooms;
    protected List<Party> _parties;
    protected Map<Integer,Item> _items;
    protected Map<Integer, Skill> _skills;
    protected Map<Integer, Passive> _passives;
    protected SqlConnection sql;
    //Collections.synchronizedMap();
    public String messageOfTheDay, strNews, serverName, sToken;

    protected boolean _run = true;
    private Random _gen = new Random();

    public int[] arrRanks = new int[15];
    public int xpRate, goldRate, maxLevel;
    public int respawnTime = 8000;

    public ServerLobby() {
        sql = new SqlConnection();
        _rooms = new ArrayList<Room>();
        _parties = new ArrayList<Party>();
        _users = new ArrayList<ConnectionHandler>();
        _items = new HashMap<Integer,Item>();
        _skills = new HashMap<Integer, Skill>();
        _passives = new HashMap<Integer, Passive>();
        sql.connect();

        try {
            tracesql("Preloading Items...");
            ResultSet rs = sql.query("SELECT itemid FROM hs_items");

            SqlConnection xsql = new SqlConnection();
            xsql.connect();
            
            Long x = System.currentTimeMillis();
            int ii = 0;
            int rowCount = xsql.getRowCount("hs_items");
            Interface.jBar.setVisible(true);
            Interface.jBar.setMaximum(rowCount);
            while(rs.next()) {
                if(!_items.containsKey(rs.getInt("itemid"))) {
                    Item newItem = new Item(rs.getInt("itemid"), xsql);
                    _items.put(rs.getInt("itemid"), newItem);
                } else {
                    tracesql("Item ID: \"" + rs.getInt("itemid") + "\" already exists! Please check this item immediately!");
                }
                ii++;
                Interface.jBar.setValue(ii);
            }
            rs.close();

            tracesql("Preloading Skills...");
            ResultSet sk = sql.query("SELECT id FROM hs_skills");
            while(sk.next()) {
                if(!_skills.containsKey(sk.getInt("id"))) {
                    Skill newSkill = new Skill(sk.getInt("id"), xsql);
                    _skills.put(sk.getInt("id"), newSkill);
                }
            }
            sk.close();

            tracesql("Preloading Passive Skills...");
            ResultSet pk = sql.query("SELECT id FROM hs_passives");
            while(pk.next()) {
                if(!_passives.containsKey(pk.getInt("id"))) {
                    Passive newPassive = new Passive(pk.getInt("id"), xsql);
                    _passives.put(pk.getInt("id"), newPassive);
                }
            }
            pk.close();
            Long y = System.currentTimeMillis();
            tracesql("Loaded " + _items.size() + " items! Ignored Items:" + (ii - _items.size()));
            tracesql("Loaded " + _skills.size() + " skills!");
            tracesql("Loaded " + _passives.size() + " passive skills!");
            tracesql("Preloading took " + ((y - x) / 1000) + " seconds");
            Interface.jBar.setVisible(false);
            
            xsql.close();
            ResultSet ms = sql.query("SELECT * FROM hs_settings LIMIT 1");
            if (ms.next()) {
                strNews = "sNews=" + ms.getString("newsFile") + ",sMap=" + ms.getString("mapFile") + ",sBook=" + ms.getString("bookFile");
                xpRate = ms.getInt("xprate");
                goldRate = ms.getInt("goldrate");
                maxLevel = ms.getInt("maxlevel");
                sToken = ms.getString("loginkey");
                messageOfTheDay = ms.getString("message");
                ms.close();
            }
            trace("Message of the Day: " + messageOfTheDay);
            trace("Client Token: " + sToken);
            Interface.tokenButton.setEnabled(true);
            Interface.txtToken.setText(sToken);
            Interface.txtToken.setEnabled(true);
            ResultSet ss = sql.query("SELECT name FROM hs_servers WHERE ip='"+Interface.serverIp+"' LIMIT 1");
            if (ss.next()) {
                serverName = ss.getString("name");
                ss.close();
            }
            trace("Gold Rate: " + goldRate);
            trace("Exp Rate: " + xpRate);
            trace("Maximum Level: " + maxLevel);
            initArrRep();
            ((DefaultListModel)Interface.listLoggedIn.getModel()).clear();
            ((DefaultListModel)Interface.listExp.getModel()).clear();
            for(int i = 1; i < 100; i++) {
            ((DefaultListModel)Interface.listExp.getModel()).
                    addElement("Exp Required To Level \"" + (i + 1) + "\" :" + getXpToLevel(i));
            }
            sql.update("UPDATE hs_servers SET online=1,count=0 WHERE ip='" + Interface.serverIp + "'");
        } catch (Exception e) {
            trace(e.getMessage());
        }
    }

    protected void reloadItems() {
        try {
            tracesql("Preloaded Items Cleared!");
            tracesql("Reloading Items...");
            HashMap<Integer,Item> _newitems = new HashMap<Integer,Item>();
            ResultSet rs = sql.query("SELECT itemid FROM hs_items");

            SqlConnection xsql = new SqlConnection();
            xsql.connect();

            Long time = new Long(0);
            Long x = System.currentTimeMillis();
            int ii = 0;
            int rowCount = xsql.getRowCount("hs_items");
            Interface.jBar.setVisible(true);
            Interface.jBar.setMaximum(rowCount);
            while(rs.next()) {
                Long start = System.currentTimeMillis();
                if(!_newitems.containsKey(rs.getInt("itemid"))) {
                    Item newItem = new Item(rs.getInt("itemid"), xsql);
                    _newitems.put(rs.getInt("itemid"), newItem);
                } else {
                    tracesql("Item ID: \"" + rs.getInt("itemid") + "\" already exists! Please check this item immediately!");
                }
                Long end = System.currentTimeMillis();
                time += (end - start);
                ii++;
                Interface.jBar.setValue(ii);
            }
            Long y = System.currentTimeMillis();
            tracesql("Loaded " + _items.size() + " items! Ignored Items:" + (ii - _items.size()) + " Average Load: " + (time/ii) + "ms");
            tracesql("Reloading took " + ((y - x) / 1000) + " seconds");
            Interface.jBar.setVisible(false);
            _items.clear();
            _items = _newitems;
        } catch (Exception e) {
            tracesql("Error in reloading items: " + e.getMessage());
        }
    }

    public boolean isMapStaffOnly(String mapname) {
        try {
            ResultSet rs = sql.query("SELECT sExtra FROM hs_maps WHERE name='"+mapname+"'");
            if(rs.next()) {
                if(rs.getString("sExtra").equals("bStaff")) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    public boolean isMapUpgradeOnly(String mapname) {
        try {
            ResultSet rs = sql.query("SELECT sExtra FROM hs_maps WHERE name='"+mapname+"'");
            if(rs.next()) {
                if(rs.getString("sExtra").equals("bUpg")) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    public boolean isStaff(String username) {
        try {
            ResultSet rs = getHandler(username)._sql.query("SELECT admin,moderator,access FROM hs_users_characters WHERE username='"+username+"'");
            if(rs.next()) {
                if((rs.getInt("admin") > 0 || rs.getInt("moderator") > 0) && rs.getInt("access") >= 40) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }
    
    private void trace(String msg) {
        Interface._server.writeLog("[Lobby] " + msg, 2);
    }
    private void tracesql(String msg) {
        Interface._server.writeLog("[Lobby] (mySQL) " + msg, 2);
    }

    /**
     * Cleans and parses text same function as the client.
     * @param The text to be parsed.
     * @return Returns the cleaned version of the text as coded in the client.
     */
    public String cleanStr(String text)
    {
        text = text.replace("&#", "");
        text = text.replace("#038:#", "");
        if (text.indexOf("%") > -1)
            text = text.replace("%","#037:");
        else if (text.indexOf("#037:") > -1)
            text = text.replace("#037","%");
        if (text.indexOf("&") > -1)
            text = text.replace("&","#038:");
        else if (text.indexOf("#038:") > -1)
            text = text.replace("#038:","&");
        if (text.indexOf("<") > -1)
            text = text.replace("<","#060:");
        else if (text.indexOf("#060:") > -1)
            text = text.replace("#060:","&lt;");
        if (text.indexOf(">") > -1)
            text = text.replace(">","#062:");
        else if (text.indexOf("#062:") > -1)
            text = text.replace("#062:","&gt;");
        return text;
    }
    
    private String replaceCharAt(String s, int pos, String c) {
        return s.substring(0,pos) + c + s.substring((pos+1), s.length());
    }

    public String updateValue(String guildID, int index, int value)
    {
        String returnStr = "";
        if (value >= 0 && value < 10)
            returnStr = Integer.toString(value);
        else if (value >= 10 && value < 36)
            returnStr = String.valueOf(Character.toChars(value + 55));
        else
            returnStr = "0";

        return replaceCharAt(guildID, index, returnStr);
    }

    public int lookAtValue(String q, int slot)
    {
        try {
            return Character.getNumericValue(q.charAt(slot));
        } catch (Exception e) {
            return -1;
        }
    }

    protected void addRewards(int id, int cp, int gold, int exp, String type, int pID, int factionid, int iRep) {
        try {
            ConnectionHandler uho = getHandler(pID);
            cp = uho.getClassPoints(uho.uID) + cp;
            String factionName = getFactionName(factionid);
            int rank = getRankFromCP(cp);
            if (rank == -1) {
                rank = 10;
                cp = 302500;
            }

            if(uho.level >= maxLevel){
                exp = 0;
            }

            uho._sql.update("UPDATE hs_users_items SET classXP=" + cp + " WHERE userid=" + uho.uID + " AND equipped=1 AND sES='ar'");
            uho._sql.update("UPDATE hs_users_characters SET intGold=intGold+" + gold + ", intExp=intExp+" + exp + " WHERE id=" + uho.uID);
            ResultSet rs = uho._sql.query("SELECT intGold,intExp FROM hs_users_characters WHERE id=" + uho.uID);
            if (rs.next()) {
                JSONObject _rew = new JSONObject();
                _rew.put("id", id);
                _rew.put("iCP", cp);
                _rew.put("cmd", "addGoldExp");
                _rew.put("intGold", rs.getInt("intGold"));
                _rew.put("intExp", rs.getInt("intExp"));
                _rew.put("typ", type);
                
                if (uho.classRank != rank) {
                    uho.loadSkills(uho.classID);
                    uho.classRank = rank;
                }
                rs.close();
                if(factionid > 1) {
                    if((uho._sql.getRowCount("hs_users_factions WHERE factionid=" + factionid + " AND userid=" + uho.uID)) > 0) {
                        uho._sql.update("UPDATE hs_users_factions SET iRep=" + iRep + " WHERE userid=" + uho.uID + " AND factionid=" + factionid);
                        _rew.put("FactionID", factionid);
                        _rew.put("iRep", iRep);
                    } else {
                        uho._sql.update("INSERT INTO `hs_users_factions`(`userid`,`factionid`,`iRep`,`sName`) VALUES "
                                + "(" + uho.uID + ", " + factionid + ", " + iRep + ", '" + factionName + "');");
                        ResultSet fs = uho._sql.query("SELECT id FROM hs_users_factions WHERE userid=" + uho.uID + " AND factionid=" + factionid);
                        if(fs.next()) {
                            JSONObject _faction = new JSONObject();
                            _faction.put("cmd", "addFaction");
                            _faction.put("faction", new JSONObject().put("FactionID", factionid).put("CharFactionID", fs.getInt("id")).put("sName", factionName).put("iRep", 0));
                            uho.sendData(_faction);
                            _rew.put("FactionID", factionid);
                            _rew.put("iRep", iRep);
                        }
                    }
                }

                uho.sendData(_rew);
                uho.levelUp();
            }
            

            
        } catch (Exception e) {
            trace("Error in giving out rewards:" + e.getMessage());
        }
    }

    public String getFactionName(int factionid) {
        try {
            ResultSet rs = sql.query("SELECT sName FROM hs_factions WHERE id=" + factionid);
            if(rs.next()) {
                String name = rs.getString("sName");
                rs.close();
                return name;
            }
        } catch (SQLException e) {
            trace(e.getMessage());
        }
        return "";
    }

    private void initArrRep()
    {
        int i = 1;
        while (i < 10)
        {
            int rankExp = (int)(Math.pow((i + 1), 3) * 100);
            if (i > 1){
                arrRanks[i]=(rankExp + arrRanks[(i - 1)]);
            } else {
                arrRanks[i]=(rankExp + 100);
            }
            i++;
        }
    }

    protected int getRankFromCP(int cp){
        int i = 1;
        while (i < 10)
        {
            if (arrRanks[i] >= cp){
               return i;
            }
            i++;
        }
        return -1;
    }

    /**
     * Checks if the socket connection exists in the lobby.
     * @param _socket The socket to be checked.
     * @return True if the connection exists.
     */
    protected boolean checkConnection(Socket _socket) {
        for(int i = 0; i < _users.size(); i++) {
            if(_users.get(i) != null) {
                if(_users.get(i).socket == _socket) {
                    return true;
                }
            }
        }
        return false;
    }

    @SuppressWarnings("static-access")
    protected boolean addClient(ConnectionHandler _client) {
        if(getPID(_client.username) > 0)
            return false;
         else {
            _users.add(_client);
           ((DefaultListModel)Interface.listLoggedIn.getModel()).addElement(_client.username);
            Interface.sTotal.setText("Total Online: " + _users.size());
            sql.update("UPDATE hs_servers SET count=count+1 WHERE ip='" + Interface._server.serverIP + "'");
        }
        return true;
    }

    @SuppressWarnings("static-access")
    protected void removeClient(ConnectionHandler _client) {
        if(_users.contains(_client)) {
            _users.remove(_users.indexOf(_client));
            ((DefaultListModel)Interface.listLoggedIn.getModel()).removeElement(_client.username);
            Interface.sTotal.setText("Total Online: " + _users.size());
            sql.update("UPDATE hs_servers SET count=count-1 WHERE ip='" + Interface._server.serverIP + "'");
        }
    }

    public void setTrace(String _username, boolean value) {
        for(int i = 0; i < _users.size(); i++) {
            if(_users.get(i) != null) {
                if(_users.get(i).username.equals(_username)) {
                    _users.get(i)._trace = value;
                }
            }
        }
    }

    public void kickPlayer(String _username) {
        for(int i = 0; i < _users.size(); i++) {
            if(_users.get(i) != null) {
                if(_users.get(i).username.equals(_username)) {
                    _users.get(i).sendData("%xt%logoutWarning%-1%%60%");
                    _users.get(i).closeSocket();
                }
            }
        }
        sendData("%xt%warning%-1%" + _username + " is kicked out from the server.%");
    }

    public void kickPlayer(String _username, String reason) {
        for(int i = 0; i < _users.size(); i++) {
            if(_users.get(i) != null) {
                if(_users.get(i).username.equals(_username)) {
                    _users.get(i).sendData("%xt%logoutWarning%-1%%60%");
                    _users.get(i).closeSocket();
                }
            }
        }
        sendData("%xt%warning%-1%" + _username + " is kicked out from the server due to "+reason+".%");
    }

    public void banKickPlayer(String _username) {
        for(int i = 0; i < _users.size(); i++) {
            if(_users.get(i) != null) {
                if(_users.get(i).username.equals(_username)) {
                    _users.get(i).sendData("%xt%logoutWarning%-1%%60%");
                    _users.get(i).closeSocket();
                }
            }
        }
    }

    public void updatePlayer(String _username, String table, int value) {
        try {
            if(table.equals("mod")) {
                if (value > 0) {
                    sql.update("UPDATE hs_users_characters SET access=" + 40 + " WHERE username='" + _username + "'");
                    sendData("%xt%server%-1%" + _username + " has been promoted to Moderator.%");
                    trace("Player " + _username + " has been promoted to Moderator.");
                } else {
                    sql.update("UPDATE hs_users_characters SET access=" + 5 + " WHERE username='" + _username + "'");
                    sendData("%xt%warning%-1%" + _username + " has been demoted.%");
                    trace("Player " + _username + " has been demoted.");
                }

            }
            if(table.equals("admin")) {
                if (value > 0) {
                    sql.update("UPDATE hs_users_characters SET access=" + 60 + " WHERE username='" + _username + "'");
                    sendData("%xt%server%-1%" + _username + " has been promoted to Admin.%");
                    trace("Player " + _username + " has been promoted to Admin.");
                } else {
                    sql.update("UPDATE hs_users_characters SET access=" + 5 + " WHERE username='" + _username + "'");
                    sendData("%xt%warning%-1%" + _username + " has been demoted.%");
                    trace("Player " + _username + " has been demoted.");
                }

            }
            if(table.equals("banned")) {
                if (value > 0) {
                    sql.update("UPDATE hs_users_characters SET banned=" + value + " WHERE username='" + _username + "'");
                    sendData("%xt%warning%-1%" + _username + " is now banned.%");
                    banKickPlayer(_username);
                    trace("Player " + _username + " is now banned from the server.");
                } else {
                    sql.update("UPDATE hs_users_characters SET banned=" + value + " WHERE username='" + _username + "'");
                    sendData("%xt%server%-1%" + _username + " is now unbanned.%");
                    trace("Player " + _username + " is now unbanned.");
                }

            }
             if(table.equals("vip")) {
                if (value > 0) {
                    sql.update("UPDATE hs_users_characters SET upgrade=" + value + ",upgDays=" + 1337 + ",access=" + 6 + " WHERE username='" + _username + "'");
                    sendData("%xt%server%-1%" + _username + " is now a VIP.%");
                    trace("Player " + _username + " is now a VIP.");
                } else {
                    sql.update("UPDATE hs_users_characters SET upgrade=" + value + ",upgDays=" + "-1" + ",access=" + 5 + " WHERE username='" + _username + "'");
                    sendData("%xt%warning%-1%" + _username + " is not a VIP anymore.%");
                    trace("Player " + _username + " is not a VIP anymore.");
                }

            }
            if(table.equals("founder")) {
                if (value > 0) {
                    sql.update("UPDATE hs_users_characters SET isFounder=" + value + " WHERE username='" + _username + "'");
                    sendData("%xt%server%-1%" + _username + " is now a Founder.%");
                    trace("Player " + _username + " is now a Founder.");
                } else {
                    sql.update("UPDATE hs_users_characters SET isFounder=" + value + " WHERE username='" + _username + "'");
                    sendData("%xt%warning%-1%" + _username + " is not a Founder anymore.%");
                    trace("Player " + _username + " is not a Founder anymore.");
                }
            } else if (table.equals("delete")) {
                ResultSet rs = sql.query("SELECT id FROM hs_users_characters WHERE username='" + _username + "'");
                if(rs.next()) {
                    sql.update("DELETE FROM hs_users_characters WHERE id="+ rs.getInt("id"));
                    sql.update("DELETE FROM hs_items WHERE userid="+ rs.getInt("id"));
                    sql.update("DELETE FROM hs_friends WHERE userid="+ rs.getInt("id"));
                    sendData("%xt%warning%-1%" + _username + " is now deleted from database.%");
                    trace("Player " + _username + " is now delete from database.");
                }
                rs.close();
            }
        } catch (Exception e) {
            
        }
    }

    protected void removeClient(String _username) {
        for(int i = 0; i < _users.size(); i++) {
            if(_users.get(i) != null) {
                if(_users.get(i).username.equals(_username)) {
                    _users.get(i).closeSocket();
                }
            }
        }
    }

    public ConnectionHandler getHandler(String _username) {
        for(int i = 0; i < _users.size(); i++) {
            if(_users.get(i).username.equals(_username)) {
                return _users.get(i);
            }
        }
        return null;
    }

    public ConnectionHandler getHandler(int pID) {
        for(int i = 0; i < _users.size(); i++) {
            if(_users.get(i).pID == pID) {
                return _users.get(i);
            }
        }
        return null;
    }

    public void sendDataToPlayer(String _username, String _data) {
        for(int i = 0; i < _users.size(); i++) {
            if(_users.get(i).username.equals(_username)) {
                _users.get(i).sendData(_data);
            }
        }
    }

    public void sendDataToPlayer(String _username, JSONObject _data) {
        for(int i = 0; i < _users.size(); i++) {
            if(_users.get(i).username.equals(_username)) {
                _users.get(i).sendData(_data);
            }
        }
    }

    public void sendData(JSONObject _data) {
        for(int i = 0; i < _users.size(); i++) {
            _users.get(i).sendData(_data);
        }
    }

    public void sendData(String _data) {
        for(int i = 0; i < _users.size(); i++) {
            _users.get(i).sendData(_data);
        }
    }

    public void sendDataToPlayerParty(String _username, JSONObject _data, boolean notme) {
        int pID = getPID(_username);
        Party mParty = getHandler(_username).cParty;
        for(int i = 0; i < _users.size(); i++) {
            if(_users.get(i).cParty != null) {
                if(!(notme == true && _users.get(i).pID == pID) && (_users.get(i).cParty.ptID == mParty.ptID)) {
                    _users.get(i).sendData(_data);
                }
            }
        }
    }

    public void sendDataToPlayerParty(String _username, String _data, boolean notme) {
        int pID = getPID(_username);
        Party mParty = getHandler(_username).cParty;
        for(int i = 0; i < _users.size(); i++) {
            if(_users.get(i).cParty != null) {
                if(!(notme == true && _users.get(i).pID == pID) && (_users.get(i).cParty.ptID == mParty.ptID)) {
                    _users.get(i).sendData(_data);
                }
            }
        }
    }

    public void sendDataToPlayerMapByFrame(String _username, JSONObject _data, boolean notme) {
        int pID = getPID(_username);
        Room mRoom = getHandler(_username).cRoom;
        String frame = getHandler(_username).frame;
        for(int i = 0; i < _users.size(); i++) {
            if(!(notme == true && _users.get(i).pID == pID) && (_users.get(i).cRoom.rID() == mRoom.rID()) && (_users.get(i).frame.equals(frame))) {
                _users.get(i).sendData(_data);
            }
        }
    }

    public void sendDataToPlayerMap(String _username, JSONObject _data, boolean notme) {
        int pID = getPID(_username);
        Room mRoom = getHandler(_username).cRoom;
        for(int i = 0; i < _users.size(); i++) {
            if(!(notme == true && _users.get(i).pID == pID) && (_users.get(i).cRoom.rID() == mRoom.rID())) {
                _users.get(i).sendData(_data);
            }
        }
    }

    public void sendDataToPlayerMap(String _username, String _data, boolean notme) {
        int pID = getPID(_username);
        Room mRoom = getHandler(_username).cRoom;
        for(int i = 0; i < _users.size(); i++) {
            if(!(notme == true && _users.get(i).pID == pID) && (_users.get(i).cRoom.rID() == mRoom.rID())) {
                _users.get(i).sendData(_data);
            }
        }
    }

    private int availableRoomNumber(String roomName) {
        int lastRoomNum = 0;
        for(int i = 0; i < _rooms.size(); i++) {
            if(i >= 0) {
                if(_rooms.get(i).roomName.equals(roomName)) {
                    if(_rooms.get(i).roomNick.length() > 0)
                        lastRoomNum = _rooms.get(i).roomNum;
                }
            }
        }
        return (lastRoomNum + 1);
    }
    
    protected Room joinRoom(ConnectionHandler _client, String RoomName, int RoomNum) {
        RoomName = RoomName.toLowerCase();
        int id = getMapID(RoomName);
        if(id > 0) {
            for(int i = 0; i < (_rooms.size() + 1); i++) {
                if(_rooms.size() == i) {
                    /** Room is null create a new one **/
                    if(RoomNum < 0) {
                        RoomNum = availableRoomNumber(RoomName);
                    }
                    Room newRoom = new Room(id, RoomNum);
                    newRoom.addClient(_client);
                    _rooms.add(newRoom);
                    return newRoom;
                } else {
                    if(i >= 0) {
                        /** If room is not null **/
                        if(_rooms.get(i).roomName.equals(RoomName)) {
                            /** If room already exists **/
                            if(RoomNum > 0) {
                                /** If Room Number is given **/
                                /** If Room number exists **/
                                if(_rooms.get(i).roomNum == RoomNum) {
                                    /** If room is not full **/
                                    if(_rooms.get(i).users < 10) {
                                        /** If room is not full **/
                                        if(_client.cRoom != null) {
                                            if(_rooms.get(i).roomName.equals(_client.cRoom.roomName) && _rooms.get(i).roomNum == _client.cRoom.roomNum) {
                                                sendDataToPlayer(_client.username, "%xt%warning%-1%Cannot join a room you are already in.%");
                                                return null;
                                            }
                                        }
                                        _rooms.get(i).addClient(_client);
                                        return _rooms.get(i);
                                    } else {
                                        sendDataToPlayer(_client.username, "%xt%warning%-1%Room join failed, destination room is full.%");
                                        return null;
                                    }
                                }
                            } else if(_rooms.get(i).users < 10) {
                                /** If room is not full **/
                                if(_client.cRoom != null) {
                                    if(_rooms.get(i).roomName.equals(_client.cRoom.roomName) && _rooms.get(i).roomNum == _client.cRoom.roomNum) {
                                        sendDataToPlayer(_client.username, "%xt%warning%-1%Cannot join a room you are already in.%");
                                        return null;
                                    } 
                                }
                                _rooms.get(i).addClient(_client);
                                return _rooms.get(i);
                            } else {
                                Room newRoom = new Room(id, (_rooms.get(i).roomNum + 1));
                                newRoom.addClient(_client);
                                _rooms.add(newRoom);
                                return newRoom;
                            }
                        }
                    }
                }
            }
        } else {
            sendDataToPlayer(_client.username, "%xt%warning%-1%\""+RoomName+"\" is not a recognized map name.%");
        }
        return null;
    }

    protected Room joinRoom(ConnectionHandler _client, String RoomName, String RoomNick) {
        RoomName = RoomName.toLowerCase();
        int id = getMapID(RoomName);
        if(id > 0) {
            for(int i = 0; i < (_rooms.size() + 1); i++) {
                if(_rooms.size() == i) {
                    /** Room is null create a new one **/
                    Room newRoom = new Room(id, RoomNick);
                    newRoom.addClient(_client);
                    _rooms.add(newRoom);
                    return newRoom;
                } else {
                    if(i >= 0) {
                        /** If room is not null **/
                        if(_rooms.get(i).roomName.equals(RoomName)) {
                            /** If room already exists **/
                            if(RoomNick != null) {
                                /** If Room Number is given **/
                                /** If Room number exists **/
                                if(_rooms.get(i).roomNick.equals(RoomNick)) {
                                    /** If room is not full **/
                                    if(_rooms.get(i).users < 10) {
                                        /** If room is not full **/
                                        if(_client.cRoom != null) {
                                            if(_rooms.get(i).roomName.equals(_client.cRoom.roomName) && _rooms.get(i).roomNick.equals(_client.cRoom.roomNick)) {
                                                sendDataToPlayer(_client.username, "%xt%warning%-1%Cannot join a room you are currently in!%");
                                                return null;
                                            }
                                        }
                                        _rooms.get(i).addClient(_client);
                                        return _rooms.get(i);
                                    } else {
                                        sendDataToPlayer(_client.username, "%xt%warning%-1%Room join failed, destination room is full.%");
                                        return null;
                                    }
                                }
                            } else if(_rooms.get(i).users < 10) {
                                /** If room is not full **/
                                if(_client.cRoom != null) {
                                    if(_rooms.get(i).roomName.equals(_client.cRoom.roomName) && _rooms.get(i).roomNick.equals(_client.cRoom.roomNick)) {
                                        sendDataToPlayer(_client.username, "%xt%warning%-1%Cannot join a room you are currently in!%");
                                        return null;
                                    }
                                }
                                _rooms.get(i).addClient(_client);
                                return _rooms.get(i);
                            }
                        }
                    }
                }
            }
        } else {
            sendDataToPlayer(_client.username, "%xt%warning%-1%\""+RoomName+"\" is not a recognized map name.%");
        }
        return null;
    }

    public Party joinParty(String username, int ptID) {
        for(int i = 0; i < (_parties.size() + 1); i++) {
            if(_parties.size() == i) {
                Party _pt = new Party((_parties.size() + 1), username);
                _parties.add(_pt);
                return _pt;
            } else {
                if(ptID > 0) {
                    if(_parties.get(i).ptID == ptID) {
                        _parties.get(i).addMember(username);
                        return _parties.get(i);
                    }
                }
            }
        }
        return null;
    }

    public String getRandomNotice() {
        String[] rand = {
        "You can easily join PvP maps, just type anymapname-pvp or just type a map with you want and name it pvp.",
        "You can join a room with a custom name, just type anymapname-whatyoulike.",
        "You will be automatically kicked by the server if you start packet spamming.",
        "Moderators or Administrators will never ask for your password! Never get scammed!",
        "You will never ever gain moderator status if you keep asking for modship!",
        "If you have any questions to ask, please consult any staff members that are online.",
        "If you found any bugs, submit it to the administrators immediately!",
        "All exp and gold rate changes are made by the administrators or the server."
        };
        return rand[new Random().nextInt(rand.length)];
    }


    public int getPIDFD(String _username) {
        try {
            ResultSet rs = sql.query("SELECT username FROM hs_users_characters WHERE username='" + _username + "'");
            if(rs.next()) {
                //String uname = rs.getString("username");
                rs.close();
                return 1;
            }
        } catch (SQLException e) {
            trace(e.getMessage());
        }
        return -1;
    }
                
    public int getPID(String _username) {
        for(int i = 0; i < _users.size(); i++) {
            if(_users.get(i) != null) {
                if(_users.get(i).username.equals(_username)) {
                    return _users.get(i).pID;
                }
            }
        }
        return -1;
    }

    public int getPID(ConnectionHandler _u) {
        return _gen.nextInt((_users.lastIndexOf(_u) + 1) + 1120);
    }

    public String getUsername(int _pID) {
        for(int i = 0; i < _users.size(); i++) {
            if(_users.get(i) != null) {
                if(_users.get(i).pID == _pID) {
                    return _users.get(i).username;
                }
            }
        }
        return "";
    }

    protected int getUID(String username) {
        try {
            ResultSet rs = sql.query("SELECT id FROM hs_users_characters WHERE sName='" + username + "'");
            if(rs.next()) {
                int id = rs.getInt("id");
                rs.close();
                return id;
            }
        } catch (SQLException e) {
            trace(e.getMessage());
        }
        return -1;
    }

    protected int getMapID(String mapName) {
        try {
            ResultSet rs = sql.query("SELECT id FROM hs_maps WHERE name='" + mapName + "'");
            if(rs.next()) {
                int id = rs.getInt("id");
                rs.close();
                return id;
            }
        } catch (SQLException e) {
            trace(e.getMessage());
        }
        return -1;
    }

    protected final int getXpToLevel(int playerlevel) {
        if (playerlevel < maxLevel) {
            int points = 0;
            for (int lvl = 1; lvl <= playerlevel; lvl++) {
                points += Math.floor(lvl + 300 * Math.pow(2, lvl / 7.));
                //points += Math.floor(lvl + 380 * ((lvl+6 / 1.9782)));
            }
            //return (int) Math.floor(points / 4) + 1;
            return (250*((playerlevel)/2)*((playerlevel/2)*(playerlevel+1)/playerlevel)*(playerlevel/2)+100);
        }
        return 2000000000;
    }

    protected int formulateHP(int level) {
        return (int) Math.floor(level + 850 * Math.pow(1.2, level / 9.));
    }

    protected int formulateMP(int level) {
        return 500;
        //return (int) Math.floor(level + 26 * Math.pow(1.2, level / 9.));
    }

    @Override
    public void finalize() {
        try {
            for(int i = 0; i < _users.size(); i++) {
                _users.get(i).closeSocket();
            }
            sql.update("UPDATE hs_users_characters SET curServer='Offline' WHERE curServer='"+serverName+"'");
            sql.update("UPDATE hs_servers SET online=0 WHERE name='"+serverName+"'");
            _users.clear();
            _rooms.clear();
            _items.clear();
            sql.close();
            super.finalize();
        } catch (Exception e) {
        } catch (Throwable ex) {
            trace("Error in Finalize: " + ex.getMessage());
        }
    }
}
