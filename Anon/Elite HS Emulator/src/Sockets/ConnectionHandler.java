package Sockets;

import java.io.*;
import java.net.Socket;
import java.sql.*;
import Json.*;
import Preload.*;
import Handler.*;
import java.util.*;

/**
 * Handles the client's connection requests.
 * @version 0.3b
 */
public class ConnectionHandler implements Runnable {
    protected Socket socket;
    protected PrintWriter sockOut = null;
    protected BufferedReader sockIn = null;
    protected ServerBase server;
    protected ServerLobby lobby;
    protected boolean _running = true;
    protected SqlConnection _sql;
    protected Room cRoom;
    protected Party cParty;
    protected Auras _auras = new Auras();
    protected StringBuffer _sb = new StringBuffer();
    protected List<Skill> _skills = new ArrayList<Skill>();
    protected List<Passive> _passives = new ArrayList<Passive>();
    protected List<String> _friends = new ArrayList<String>();
    protected List<Integer> _loadedItems = new ArrayList<Integer>();
    protected HashMap<Integer, Integer> _tempItems = new HashMap<Integer, Integer>();

    protected int[] qAccepted = new int[20];

    Random _gen = new Random();

    /** AQW Properties **/

    public boolean afk;
    public boolean fly;
    public boolean _trace = true;

    public boolean ptInvite ;
    public boolean frndInvite;
    public boolean acceptPMs;
    public boolean acceptGoto;
    public boolean bTT;
    public boolean showPet;
    public boolean showCloak;
    public boolean showHelm;
    public boolean soundOn;

    public int ia1;
    public int pkSend;
    public int iDrops;
    public int curTurn;

    public int level;
    public int hp;
    public int hpmax;
    public int mp;
    public int mpmax;
    public int uID;
    public int pID;
    public int tx;
    public int ty;
    public int state = 1;
    public int statsEND;
    public int classID;
    public int classRank;
    public int access;
    public int isFounder;
    public int upgrade;
    public int upgDays;
    public int pvpTeam;

    public int wepDPS;
    public int wepRng;
    public int wepLvl;

    public int expToLevel;

    public String username = "User";
    public String gender;
    public String frame = "Enter";
    public String pad = "Spawn";
    public String strQuests;

    public ConnectionHandler(Socket _socket, ServerBase _server, ServerLobby _lobby) {
        socket = _socket;
        server = _server;
        lobby = _lobby;
        try {
            sockIn = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
            sockOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            //new PrintWriter(_socket.getOutputStream(), true);
        } catch (IOException e) {
            trace("in or out failed");
            System.exit(-1);
        }
    }

    public boolean parseBoolean(int i) {
        if(i > 0) {
            return true;
        } else {
            return false;
        }
    }

    public String buildString(String ... _s) {
        _sb.setLength(0);
        for(int i = 0; i < _s.length; i++) {
            _sb.append(_s[i]);
        }
        return _sb.toString();
    }

    public void readA1Pref() {
        if(showCloak)
            updateAchievement(0, 0);
        else
            updateAchievement(0, 1);
        if(showHelm)
            updateAchievement(1, 0);
        else
            updateAchievement(1, 1);
        if(showPet)
            updateAchievement(2, 0);
        else
            updateAchievement(2, 1);
        updateAchievement(3, 0);
        updateAchievement(4, 0);
        updateAchievement(5, 0);
        updateAchievement(6, 0);
        updateAchievement(7, 0);
        updateAchievement(8, 0);
        updateAchievement(9, 0);

        if(ptInvite) {
            sendData("%xt%server%-1%Accepting party invites.%");
            updateAchievement(8, 0);
        } else {
            sendData("%xt%warning%-1%Ignoring party invites.%");
            updateAchievement(8, 1);
        }

        if(acceptGoto) {
            sendData("%xt%server%-1%Accepting goto requests.%");
            updateAchievement(4, 0);
        } else {
            sendData("%xt%warning%-1%Blocking goto requests.%");
            updateAchievement(4, 1);
        }

        if(frndInvite) {
            sendData("%xt%server%-1%Accepting Friend requests.%");
            updateAchievement(7, 0);
        } else {
            updateAchievement(7, 1);
            sendData("%xt%warning%-1%Ignoring Friend requests.%");
        }

        if(soundOn)
            updateAchievement(5, 0);
        else
            updateAchievement(5, 1);
        
        if(acceptPMs) {
            sendData("%xt%server%-1%Accepting PMs.%");
            updateAchievement(10, 0);
        } else {
            updateAchievement(10, 1);
            sendData("%xt%warning%-1%Ignoring PMs.%");
        }
        
        if(bTT) {
            updateAchievement(11, 0);
            sendData("%xt%server%-1%Ability ToolTips will always show on mouseover.%");
        } else {
            updateAchievement(11, 1);
            sendData("%xt%warning%-1%Ability ToolTips will not show on mouseover during combat.%");
        }

        trace("User Preferences Loaded!");
    }

    public void updateAchievement(int index, int value)
    {
        if (value == 0)
            ia1 = ia1 & ~(int)Math.pow(2, index);
        else if (value == 1)
            ia1 = ia1 | (int)Math.pow(2, index);
        
    }

    public int getAchievement(int isOpen)
    {
        if (isOpen < 0 || isOpen > 31)
        {
            return -1;
        }
        return (ia1 & (int)Math.pow(2, isOpen)) == 0 ? (0) : (1);
    }

    private void trace(String msg) {
        if(_trace)
            server.writeLog(buildString("[", username, "] ", msg), 2);
        
    }
    
    private void traceError(String msg) {
        server.writeLog(buildString("[", username, "] ", msg), 2);
    }

    protected void traceLogin(String msg) {
        //server.writeLog("[User] " + msg, 1);
    }
    
    protected void traceChat(String msg) {
        if(cRoom.roomNick.length() > 0)
            server.writeLog(buildString("[", cRoom.roomName, "-", cRoom.roomNick, "] ", username, ": ", lobby.cleanStr(msg)), 3);
        else
            server.writeLog(buildString("[", cRoom.roomName, "-", Integer.toString(cRoom.roomNum), "] ", username, ": ", lobby.cleanStr(msg)).toString(), 3);
    }

    protected void tracePacket(String msg) {
        if(_trace)
            server.writeLog(msg, 4);
    }

    public void closeSocket()
    {
        try {
            _running = false;
            if (cParty != null) {
                partyLeave();
            }
            if (cRoom != null) {
                cRoom.removeClient(this);
            }
            server.writeLog(buildString("[Notice] ", username, " has logged out."), 3);
            _sql.update("UPDATE hs_users_characters SET curServer='Offline' WHERE id=" + uID);
            sendOfflineStatus();
            lobby.removeClient(this);
            _friends.clear();
            _auras.clearAuras();
            _skills.clear();
            _loadedItems.clear();
            _sql.close();
            sockOut.close();
            sockIn.close();
            socket.close();
            trace("Socket Closed.");
        } catch (IOException e) {
            traceError("Could not close socket :" + e.getMessage());
        }
    }

    @Override
    public void run(){
        try {

            int dRead;
            boolean zeroByteRead = false;
            char cbuf[] = new char[socket.getReceiveBufferSize()];

            while(_running){
                if((dRead = sockIn.read(cbuf, 0, cbuf.length)) != -1) {
                    if(zeroByteRead || !_running)
                        break;
                    if (dRead == 0) {
                        zeroByteRead = true;
                        break;
                    } else  {
                        parsePacket(String.copyValueOf(cbuf, 0, dRead));
                    }
                } else {
                    break;
                }
            }
            closeSocket();
        } catch (IOException e) {
            System.out.println("Read failed: " + e.getMessage());
        }
    }

    private String getCmd(String data){
        if (data.startsWith("<")) {
            int endArrow = data.indexOf(">");
            int endSlash = data.indexOf("/>");
            if (endSlash < 0)
                endSlash = endArrow + 1;
            if (endSlash < endArrow) 
                return data.substring(1, endSlash);
             else 
                return data.substring(1, endArrow);
        } else if (data.startsWith("%")) {
            String data_handled[] = data.split("%");
            return data_handled[3];
        }
        return "Error";
    }

    public void sendData(String data) {
        String pData = (buildString(data, "\u0000"));
        tracePacket(pData);
        char[] cbuf = pData.toCharArray();
        sockOut.write(cbuf, 0, cbuf.length);
        //sockOut.write(pData, 0, pData.length());
        sockOut.flush();
    }

    public void sendData(JSONObject data) {
        try {
            JSONObject json = new JSONObject();
            JSONObject b = new JSONObject();
            b.put("r", -1);
            b.put("o", data);
            json.put("t","xt");
            json.put("b", b);
            String pData = (buildString(json.toString(), "\u0000"));
            tracePacket(pData);
            char[] cbuf = pData.toCharArray();
            sockOut.write(cbuf, 0, cbuf.length);
            //sockOut.write(pData, 0, pData.length());
            sockOut.flush();
        } catch (JSONException e) {
            traceError(e.getMessage());
        }
    }

    private void parsePacket(String data) {
        try {
            Long start = null;
            if(_trace)
                start = System.currentTimeMillis();

            data = data.substring(0, (data.length() - 1));
            String cmd = getCmd(data);
            Packet recvPack = new Packet();
            recvPack.setPacket(data);
            recvPack.removeHeader();
            tracePacket(data);

            pkSend += 1;

            if(pkSend > 300)
                lobby.kickPlayer(username, "packet spamming");

            String dataNod[] = recvPack.getPacket().split("%");
            if(cmd.equals("policy-file-request")) {
                sendData("<cross-domain-policy><allow-access-from domain='*' to-ports='5588' /></cross-domain-policy>");
            } else if (cmd.equals("msg t='sys'")) {
                String sys = recvPack.getXMLSingle("body action");
                if(sys.equals("verChk")) {
                    sendData("<msg t='sys'><body action='apiOK'></body></msg>");
                } else if (sys.equals("login")) {
                    String[] info = recvPack.getCDATA(recvPack.getXML("nick")).split("~");
                    String user = info[0];
                    int charid = Integer.parseInt(info[2]);
                    String pass = recvPack.getCDATA(recvPack.getXML("pword")).split("~")[1];
                    handleLogin(user, pass, charid);
                }
            } else if(cmd.equals("firstJoin")) {
                loadInventoryBig();
                joinRoom("battleon", 1, "Enter", "Spawn");
            } else if(cmd.equals("moveToCell")) {
                moveToCell(dataNod[2], dataNod[3]);
            } else if(cmd.equals("retrieveUserDatas")) {
                initUserDatas(recvPack.getPacket());
            } else if(cmd.equals("retrieveUserData")) {
                initUserData(Integer.parseInt(dataNod[2]));
            } else if(cmd.equals("retrieveInventory")) {
                loadInventoryBig();
            } else if (cmd.equals("mv")) {
                if(dataNod.length > 5) {
                    if(dataNod[5].equals("1"))
                        fly = true;
                     else
                        fly = false;

                    userMove(Integer.parseInt(dataNod[2]), Integer.parseInt(dataNod[3]), Integer.parseInt(dataNod[4]), true, true);
                } else {
                    userMove(Integer.parseInt(dataNod[2]), Integer.parseInt(dataNod[3]), Integer.parseInt(dataNod[4]), true, false);
                }
            } else if (cmd.equals("message")) {
                userChat(Integer.parseInt(dataNod[1]), dataNod[2], dataNod[3]);
            } else if (cmd.equals("cmd")) {
                String cmdSwitch = dataNod[2];
                if (cmdSwitch.equals("tfer")) {
                    try {
                        if (dataNod[4].indexOf("-") > 0) {
                            String room[] = dataNod[4].split("-");
                            if(!isInteger(room[1])) {
                                joinRoom(room[0], room[1], "Enter", "Spawn");
                            } else {
                                joinRoom(room[0], Integer.parseInt(room[1]), "Enter", "Spawn");
                            }
                        } else {
                            if (dataNod.length > 6) {
                                joinRoom(dataNod[4], 1, dataNod[5], dataNod[6]);
                            } else {
                                joinRoom(dataNod[4], 1, "Enter", "Spawn");
                            }
                        }
                    } catch (Exception e) {
                        traceError(e.getMessage());
                    }
                } else if (cmdSwitch.equals("uopref")) {
                    changeUserSettings(Boolean.parseBoolean(dataNod[4]), dataNod[3]);
                } else if (cmdSwitch.equals("goto")) {
                    if(lobby.getPID(dataNod[3]) > 0) {
                        ConnectionHandler _temp = lobby.getHandler(dataNod[3]);
                        if(_temp.acceptGoto) {
                            if(!_temp.cRoom.roomName.equals("bludrutbrawl")) {
                                if(_temp.cRoom.roomNick.length() > 0)
                                    joinRoom(_temp.cRoom.roomName, _temp.cRoom.roomNick, _temp.frame, _temp.pad);
                                else
                                    joinRoom(_temp.cRoom.roomName, _temp.cRoom.roomNum, _temp.frame, _temp.pad);
                            } else {
                                sendData(buildString("%xt%warning%-1%", dataNod[3], " is currently busy.%"));
                            }
                        } else {
                             sendData(buildString("%xt%warning%-1%", dataNod[3], " is ignoring goto requests.%"));
                             sendData("%xt%warning%-1%Invalid /goto request.%");
                        }
                    } else {
                        sendData(buildString("%xt%server%-1%Player \"", dataNod[3], "\" could not be found%"));
                    }
                } else if (cmdSwitch.equals("mute")) {
                    sendData("%xt%mute%-1%" + (1 * 1000) + "%");
                } else if (cmdSwitch.equals("unmute") && access > 39) {
                    lobby.sendDataToPlayer(dataNod[3], "%xt%unmute%-1%");
                } else if ((cmdSwitch.equals("iay") || cmdSwitch.equals("adminyell")) && access > 39) {
                    lobby.sendData(buildString("%xt%moderator%-1%(", username, "): ", dataNod[3], "%"));
                    server.writeLog(buildString("[Global] (", username, "): ", dataNod[3]), 3);
                } else if (cmdSwitch.equals("level") && access > 39) {
                    if(isInteger(dataNod[3]))
                        if(Integer.parseInt(dataNod[3]) <= lobby.maxLevel && Integer.parseInt(dataNod[3]) > 0)
                            levelUp(Integer.parseInt(dataNod[3]));
                        else if(Integer.parseInt(dataNod[3]) > lobby.maxLevel)
                            sendData("%xt%warning%-1%Maximum level is "+lobby.maxLevel+"!%");
                        else if(Integer.parseInt(dataNod[3]) < 1)
                            sendData("%xt%warning%-1%Minimum level is 1!%");
                } else if (cmdSwitch.equals("kick") && access > 39) {
                    if(lobby.getPID(dataNod[3]) > 0)
                        lobby.kickPlayer(dataNod[3]);
                    else
                        sendData(buildString("%xt%server%-1%Player \"", dataNod[3], "\" could not be found%"));
                } else if (cmdSwitch.equals("ban") && access > 39) {
                    if(lobby.getPID(dataNod[3]) > 0)
                        lobby.updatePlayer(dataNod[3], "banned", 1);
                    else
                    if(lobby.getPIDFD(dataNod[3]) > 0)
                        lobby.updatePlayer(dataNod[3], "banned", 1);
                    else
                        sendData(buildString("%xt%server%-1%Player \"", dataNod[3], "\" could not be found%"));
                } else if (cmdSwitch.equals("unban") && access > 39) {
                    if(lobby.getPID(dataNod[3]) > 0)
                        lobby.updatePlayer(dataNod[3], "banned", 0);
                    else
                    if(lobby.getPIDFD(dataNod[3]) > 0)
                        lobby.updatePlayer(dataNod[3], "banned", 0);
                    else
                        sendData(buildString("%xt%server%-1%Player \"", dataNod[3], "\" could not be found%"));
                } else if (cmdSwitch.equals("addrep") && access > 39) {
                    if(isInteger(dataNod[3]))
                        lobby.addRewards(pID, Integer.parseInt(dataNod[3]), 0, 0, "p", pID, -1, 0);
                } else if (cmdSwitch.equals("addexp") && access > 39) {
                    if(isInteger(dataNod[3]))
                        lobby.addRewards(pID, 0, 0, Integer.parseInt(dataNod[3]), "p", pID, -1, 0);
                } else if (cmdSwitch.equals("shutdown") && access > 59) {
                    Interface._beginShutdown(false);
                } else if (cmdSwitch.equals("shutdownnow") && access > 59) {
                    Interface._Shutdown();
                } else if (cmdSwitch.equals("restart") && access > 59) {
                    Interface._beginShutdown(true);
                } else if (cmdSwitch.equals("restartnow") && access > 59) {
                    Interface._Shutdown();
                    Interface._Boot();
                } else if (cmdSwitch.equals("kickall") && access > 59) {
                    //Do Nothing
                } else if (cmdSwitch.equals("pull") && access > 59) {
                    if(lobby.getPID(dataNod[3]) > 0) {
                        if(lobby.getHandler(dataNod[3]).cRoom.rID() == cRoom.rID()) {
                            lobby.getHandler(dataNod[3]).moveToUser(pID);
                        } else {
                            if(cRoom.roomNick.length() > 0)
                                lobby.getHandler(dataNod[3]).joinRoom(cRoom.roomName, cRoom.roomNick, frame, pad);
                            else {
                                lobby.getHandler(dataNod[3]).joinRoom(cRoom.roomName, cRoom.roomNum, frame, pad);
                            }
                        }
                    } else
                        sendData(buildString("%xt%server%-1%Player \"", dataNod[3], "\" could not be found%"));
                } else if (cmdSwitch.equals("clear") && access > 59) {
                    if(dataNod[3].equals("item")) {
                        lobby.reloadItems();
                    }
                } else if (cmdSwitch.equals("bank") && access > 39) {
                    loadBank();
                } else if (cmdSwitch.equals("who")) {
                    JSONObject who = new JSONObject();
                    who.put("cmd", "who");
                    JSONObject users = new JSONObject();
                    for(int i = 0; i < cRoom._users.length; i++) {
                        if(cRoom._users[i].length() > 0) {
                            ConnectionHandler uho = lobby.getHandler(cRoom._users[i]);
                            users.put(""+uho.pID, new JSONObject().put("iLvl", uho.level).put("sName", cRoom._users[i].toUpperCase()).put("sClass", "Not Supported"));
                        }
                    }
                    who.put("users", users);
                    sendData(who);
                } else {
                    //Do Nothing
                }
            } else if (cmd.equals("loadShop")) {
                loadShop(Integer.parseInt(dataNod[2]));
            } else if (cmd.equals("equipItem")) {
                equipItem(Integer.parseInt(dataNod[2]));
            } else if (cmd.equals("gar")) {
                /**
                if(dataNod.length > 4) {
                    if(dataNod[4].equals("xhs"))
                        playerAttack(dataNod[3], Integer.parseInt(dataNod[2]));
                    else
                        lobby.kickPlayer(username, "attack packet hack.");
                } else {
                    
                }
                **/
                playerAttack(dataNod[3], Integer.parseInt(dataNod[2]));
            } else if (cmd.equals("restRequest")) {
                restPlayer();
            } else if (cmd.equals("buyItem")) {
                buyItem(Integer.parseInt(dataNod[2]));
            } else if (cmd.equals("em")) {
                lobby.sendDataToPlayerMap(username, buildString("%xt%em%-1%", username, "%", dataNod[2], "%"), false);
            } else if (cmd.equals("removeItem")) {
                removeItem(Integer.parseInt(dataNod[2]), Integer.parseInt(dataNod[3]));
            } else if (cmd.equals("unequipItem")) {
                unequipItem(Integer.parseInt(dataNod[2]));
            } else if (cmd.equals("sellItem")) {
                sellItem(Integer.parseInt(dataNod[2]), Integer.parseInt(dataNod[4]));
            } else if (cmd.equals("resPlayerTimed")) {
                resPlayerTimed();
            } else if (cmd.equals("emotea")) {
                lobby.sendDataToPlayerMap(username , buildString("%xt%emotea%-1%", dataNod[2], "%", Integer.toString(pID), "%"), true);
            } else if (cmd.equals("afk")) {
                setAFK(Boolean.parseBoolean(dataNod[2]));
            } else if (cmd.equals("cc")) {
                sendData(buildString("%xt%cc%-1%", dataNod[2], "%", username, "%"));
            } else if (cmd.equals("loadHairShop")) {
                loadHairShop(Integer.parseInt(dataNod[2]));
            } else if (cmd.equals("changeArmorColor")) {
                changeArmorColor(Integer.parseInt(dataNod[2]), Integer.parseInt(dataNod[3]), Integer.parseInt(dataNod[4]), Integer.parseInt(dataNod[5]), Integer.parseInt(dataNod[6]), Integer.parseInt(dataNod[7]), Integer.parseInt(dataNod[8]), Integer.parseInt(dataNod[9]), Integer.parseInt(dataNod[10]));
                //changeArmorColor(Integer.parseInt(dataNod[2]), Integer.parseInt(dataNod[3]), Integer.parseInt(dataNod[4]));
            } else if (cmd.equals("changeColor")) {
                changeColor(Integer.parseInt(dataNod[2]), Integer.parseInt(dataNod[3]), Integer.parseInt(dataNod[4]), Integer.parseInt(dataNod[5]));
            } else if (cmd.equals("enhanceItemShop")) {
                enhanceItem(Integer.parseInt(dataNod[2]), Integer.parseInt(dataNod[3]), false);
            } else if (cmd.equals("enhanceItemLocal")) {
                enhanceItem(Integer.parseInt(dataNod[2]), Integer.parseInt(dataNod[3]), true);
            } else if (cmd.equals("ia")) {
                sendIAResponse(dataNod[3], dataNod[2]);
            } else if (cmd.equals("mtcid")) {
                /* Send move to cell by id */
                sendData(buildString("%xt%mtcid%-1%", dataNod[2], "%"));
                /* Switch Move to Cell by ID for Bludrut Brawl */
                switch (Integer.parseInt(dataNod[2])) {
                    case 30:
                        moveToCell("Morale1C", "Top");
                        break;
                    case 29:
                        moveToCell("Enter1", "Left");
                        break;
                    case 28:
                        moveToCell("Captain1", "Spawn");
                        break;
                    case 27:
                        moveToCell("Morale1B", "Right");
                        break;
                    case 26:
                        moveToCell("Morale1C", "Right");
                        break;
                    case 25:
                        moveToCell("Morale1C", "Left");
                        break;
                    case 24:
                        moveToCell("Morale1A", "Right");
                        break;
                    case 23:
                        moveToCell("Morale1B", "Left");
                        break;
                    case 22:
                        moveToCell("Crosslower", "Right");
                        break;
                    case 21:
                        moveToCell("Resource1A", "Right");
                        break;
                    case 20:
                        moveToCell("Resource1B", "Left");
                        break;
                    case 19:
                        moveToCell("Crossupper", "Right");
                        break;
                    case 18:
                        moveToCell("Resource1A", "Left");
                        break;
                    case 17:
                        moveToCell("Crosslower", "Middle");
                        break;
                    case 16:
                        moveToCell("Resource0A", "Right");
                        break;
                    case 15:
                        moveToCell("Morale1A", "Left");
                        break;
                    case 14:
                        moveToCell("Crossupper", "Bottom");
                        break;
                    case 13:
                        moveToCell("Morale0A", "Right");
                        break;
                    case 12:
                        moveToCell("Crossupper", "Right");
                        break;
                    case 11:
                        moveToCell("Resource0B", "Right");
                        break;
                    case 10:
                        moveToCell("Resource0A", "Left");
                        break;
                    case 9:
                        moveToCell("Crosslower", "Left");
                        break;
                    case 8:
                        moveToCell("Morale0B", "Right");
                        break;
                    case 7:
                        moveToCell("Morale0A", "Left");
                        break;
                    case 6:
                        moveToCell("Morale0C", "Right");
                        break;
                    case 5:
                        moveToCell("Morale0C", "Left");
                        break;
                    case 4:
                        moveToCell("Morale0B", "Left");
                        break;
                    case 3:
                        moveToCell("Captain0", "Spawn");
                        break;
                    case 2:
                        moveToCell("Enter0", "Right");
                        break;
                    case 1:
                        moveToCell("Morale0C", "Top");
                        break;
                }
            } else if (cmd.equals("PVPQr")) {
                pvpQuery(dataNod[2], -1);
            } else if (cmd.equals("PVPIr")) {
                if (!dataNod[2].equals("0")) {
                    pvpQuery("done", -1);
                    joinRoom("bludrutbrawl", -1, "Enter", "Spawn");
                } else {
                    pvpQuery("none", -1);
                }
            } else if (cmd.equals("whisper")) {
                if (lobby.getPID(dataNod[3].toLowerCase()) > 0) {
                    ConnectionHandler uho = lobby.getHandler(dataNod[3].toLowerCase());
                    if(uho.acceptPMs) {
                        uho.sendData(buildString("%xt%whisper%-1%", dataNod[2], "%" + username, "%", dataNod[3].toLowerCase(), "%0%"));
                        sendData(buildString("%xt%whisper%-1%", dataNod[2], "%" + username, "%", dataNod[3].toLowerCase(), "%0%"));
                    } else
                        sendData(buildString("%xt%server%-1%Player ", dataNod[3] ," is not accepting PMs at this time.%"));
                    traceChat(buildString("Message To \"", dataNod[3], "\": ", dataNod[2]));
                } else {
                    sendData(buildString("%xt%server%-1%Player \"", dataNod[3], "\" could not be found%"));
                }
            } else if (cmd.equals("addFriend")) {
                addFriend(username, dataNod[2].toLowerCase(), true);
            } else if (cmd.equals("requestFriend")) {
                requestFriend(dataNod[2]);
            } else if (cmd.equals("declineFriend")) {
                lobby.sendDataToPlayer(dataNod[2], "%xt%server%-1%" + username + " declined your friend request.%");
                try {
                    lobby.sendDataToPlayer(dataNod[2], new JSONObject().put("cmd", "declineFriend").put("unm", username));
                } catch (JSONException e) {
                }
            } else if (cmd.equals("gp")) {
                String gpSwitch = dataNod[2];
                if (gpSwitch.equals("pi")) {
                    partyInvite(dataNod[3].toLowerCase());
                } else if (gpSwitch.equals("pd")) {
                    partyDecline(Integer.parseInt(dataNod[3]));
                } else if (gpSwitch.equals("pa")) {
                    partyAccept(Integer.parseInt(dataNod[3]));
                } else if (gpSwitch.equals("pl")) {
                    partyLeave();
                } else if (gpSwitch.equals("pk")) {
                    partyKick(dataNod[3].toLowerCase());
                } else if (gpSwitch.equals("pp")) {
                    partyPromote(dataNod[3].toLowerCase());
                } else if (gpSwitch.equals("ps")) {
                    partySummon(dataNod[3].toLowerCase());
                } else if (gpSwitch.equals("psd")) {
                    partySummonDecline(dataNod[3].toLowerCase());
                }
            } else if (cmd.equals("getQuests")) {
                getQuests(recvPack.getPacket());
            } else if (cmd.equals("aggroMon")) {
                int mID = Integer.parseInt(dataNod[2]);
                if (cRoom._monsters.get((mID - 1)).state != 0) {
                    cRoom._monsters.get((mID - 1)).attack(pID);
                    cRoom.monsterActive(mID);
                }
            } else if (cmd.equals("acceptQuest")) {
                for (int i = 1; i < 20; i++) {
                    if (qAccepted[i] == 0) {
                        qAccepted[i] = Integer.parseInt(dataNod[2]);
                        trace("Quest ID: " + dataNod[2] + " accepted.");
                        return;
                    }
                }
            } else if (cmd.equals("tryQuestComplete")) {
                tryQuestComplete(Integer.parseInt(dataNod[2]), Integer.parseInt(dataNod[3]));
            } else if (cmd.equals("getDrop")) {
                getDrop(Integer.parseInt(dataNod[2]));
            } else if (cmd.equals("updateQuest")) {
                updateQuest(Integer.parseInt(dataNod[2]), Integer.parseInt(dataNod[3]));
            } else if (cmd.equals("buyBagSlots")) {
                buyBagSlots(Integer.parseInt(dataNod[2]));
            } else if (cmd.equals("buyBankSlots")) {
                buyBankSlots(Integer.parseInt(dataNod[2]));
            } else if (cmd.equals("buyHouseSlots")) {
                buyHouseSlots(Integer.parseInt(dataNod[2]));
            } else if (cmd.equals("bankFromInv")) {
                bankFromInv(Integer.parseInt(dataNod[2]), Integer.parseInt(dataNod[3]));
            } else if (cmd.equals("bankToInv")) {
                bankToInv(Integer.parseInt(dataNod[2]), Integer.parseInt(dataNod[3]));
            } else if (cmd.equals("loadBank")) {
                loadBank();
            } else if (cmd.equals("loadWarVars")) {
                loadWarVars();
            } else if (cmd.equals("getMapItem")) {
                if(cRoom._mapItems != null) {
                    dropItem(cRoom._mapItems.get(Integer.parseInt(dataNod[2])));
                }
            } else {
                trace("Unknown Client Command: " + cmd);
            }
            if(_trace) {
                Long end = System.currentTimeMillis();
                trace("Parsing MS: " + (end - start) + "ms CMD: \"" + cmd + "\"");
            }
        } catch (Exception e) {
            traceError("Error in getting packet: " + e.getMessage());
        }
    }

    private void loadWarVars() {
        try {
            ResultSet is = _sql.query("SELECT * FROM hs_events_wars");
            if (is.next()) {
                sendData(new JSONObject().put("cmd", "loadWarVars")
                        .put("intWar1", is.getInt("intWar1"))
                        .put("intWar2", is.getInt("intWar2"))
                        .put("intWar3", is.getInt("intWar3"))
                        .put("intWar4", is.getInt("intWar4"))
                        .put("intWar5", is.getInt("intWar5"))
                        .put("intWarTotal", is.getInt("intWarTotal")));
            }
            is.close();
        } catch (Exception e) {
            traceError("Error in loading war vars: " + e.getMessage());
        }
    }

    private void bankFromInv(int itemid, int adjustid) {
        try {
            int i = _sql.getRowCount("hs_users_items WHERE bBank=1 AND userid="+uID);
            ResultSet is = _sql.query("SELECT slotBank FROM hs_users_characters WHERE id="+uID);
            if (is.next()) {
                if (is.getInt("slotBank") <= i) {
                    sendData("%xt%warning%-1%You have the maximum items you can in your bank.%");
                } else {
                    _sql.update("UPDATE hs_users_items SET bBank=1 WHERE userid=" + uID + " AND bBank=0 AND id=" + adjustid);
                    sendData(new JSONObject().put("ItemID", itemid).put("cmd", "bankFromInv"));
                }
            }
            is.close();
        } catch (Exception e) {
            traceError("Error in putting item to bank: " + e.getMessage());
        }
    }

    protected void bankToInv(int itemid, int adjustid) {
        try {
            int i = _sql.getRowCount("hs_users_items WHERE bBank=0 AND userid="+uID);
            ResultSet is = _sql.query("SELECT slotBag FROM hs_users_characters WHERE id="+uID);
            if (is.next()) {
                if (is.getInt("slotBag") <= i) {
                    sendData("%xt%warning%-1%You have the maximum items you can in your inventory.%");
                } else {
                    _sql.update("UPDATE hs_users_items SET bBank=0 WHERE userid=" + uID + " AND bBank=1 AND id=" + adjustid);
                    sendData(new JSONObject().put("ItemID", itemid).put("cmd", "bankToInv"));
                }
            }
            is.close();
        } catch (Exception e) {
            traceError("Error in in putting item to inventory: " + e.getMessage());
        }
    }

    private void buyBagSlots(int amount) {
        try {
            JSONObject _bbs = new JSONObject().put("cmd", "buyBagSlots").put("bitSuccess", 1);

            int coins = 0;
            int curSlots = 0;
            ResultSet is = _sql.query("SELECT coins,slotBag FROM hs_users_characters WHERE id="+uID);
            if (is.next()) {
                coins = is.getInt("coins");
                curSlots = is.getInt("slotBag");
            }
            is.close();
            if (curSlots >= 75) {
                sendData(_bbs.put("bitSuccess", 0).put("strMessage", "You have the maximum bag slots avaliable.").put("iSlots", 0));
                return;
            }

            if (coins < (200 * amount)) {
                sendData(_bbs.put("bitSuccess", 0).put("strMessage", "You do not have enough coins to buy that many slots.").put("iSlots", 0));
            } else {
                _sql.update("UPDATE hs_users_characters SET coins=coins-" + (200 * amount) + ", slotBag=slotBag+" + amount + " WHERE id=" + uID);
                sendData(_bbs.put("iSlots", amount));
            }

            is.close();
        } catch (Exception e) {
            traceError("Error in buying bag slots: " + e.getMessage());
        }
    }

    private void buyBankSlots(int amount) {
        try {
            JSONObject _bbs = new JSONObject().put("cmd", "buyBankSlots").put("bitSuccess", 1);

            int coins = 0;
            int curSlots = 0;
            ResultSet is = _sql.query("SELECT coins,slotBank FROM hs_users_characters WHERE id="+uID);
            if (is.next()) {
                coins = is.getInt("coins");
                curSlots = is.getInt("slotBank");
            }
            is.close();
            if (curSlots >= 60) {
                sendData(_bbs.put("bitSuccess", 0).put("strMessage", "You have the maximum bank slots avaliable.").put("iSlots", 0));
                return;
            }

            if (coins < (200 * amount)) {
                sendData(_bbs.put("bitSuccess", 0).put("strMessage", "You do not have enough coins to buy that many slots.").put("iSlots", 0));
            } else {
                _sql.update("UPDATE hs_users_characters SET coins=coins-" + (200 * amount) + ", slotBank=slotBank+" + amount + " WHERE id=" + uID);
                sendData(_bbs.put("iSlots", amount));
            }

            is.close();
        } catch (Exception e) {
            traceError("Error in buying bank slots: " + e.getMessage());
        }
    }

    private void buyHouseSlots(int amount) {
        try {
            JSONObject _bbs = new JSONObject().put("cmd", "buyHouseSlots").put("bitSuccess", 1);

            int coins = 0;
            int curSlots = 0;
            ResultSet is = _sql.query("SELECT coins,slotHouse FROM hs_users_characters WHERE id="+uID);
            if (is.next()) {
                coins = is.getInt("coins");
                curSlots = is.getInt("slotHouse");
            }
            is.close();
            if (curSlots >= 30) {
                sendData(_bbs.put("bitSuccess", 0).put("strMessage", "You have the maximum house slots avaliable.").put("iSlots", 0));
                return;
            }

            if (coins < (200 * amount)) {
                sendData(_bbs.put("bitSuccess", 0).put("strMessage", "You do not have enough coins to buy that many slots.").put("iSlots", 0));
            } else {
                _sql.update("UPDATE hs_users_characters SET coins=coins-" + (200 * amount) + ", slotHouse=slotHouse+" + amount + " WHERE id=" + uID);
                sendData(_bbs.put("iSlots", amount));
            }

            is.close();
        } catch (Exception e) {
            traceError("Error in buying House slots: " + e.getMessage());
        }
    }

    private void loadBank() {
        try {
            JSONArray items = new JSONArray();
            int itemCount = _sql.getRowCount("hs_users_items WHERE bBank=1 AND userid="+uID);
            ResultSet rs2 = _sql.query("SELECT * FROM hs_users_items WHERE bBank=1 AND userid="+uID);
            int[] charitemid = new int[itemCount];
            int[] itemid = new int[itemCount];
            int[] equip = new int[itemCount];
            int[] itemlevel = new int[itemCount];
            int[] classxp = new int[itemCount];
            int[] qty = new int[itemCount];
            int[] enhid = new int[itemCount];
            int i = 0;
            while (rs2.next()) {
                charitemid[i] = rs2.getInt("id");
                itemid[i] = rs2.getInt("itemid");
                equip[i] = rs2.getInt("equipped");
                itemlevel[i] = rs2.getInt("iLvl");
                enhid[i] = rs2.getInt("EnhID");
                qty[i] = rs2.getInt("iQty");
                if (rs2.getString("sES").equals("ar")) {
                    classxp[i] = rs2.getInt("classXP");
                }
                i++;
            }
            rs2.close();
            for(int e = 0; e < i; e++) {
                int c = itemid[e];
                _loadedItems.add(c);
                JSONObject temp = new JSONObject();
                temp.put("ItemID", c);
                temp.put("sLink", lobby._items.get(c).sLink);
                temp.put("sElmt", lobby._items.get(c).sElmt);
                temp.put("bStaff", lobby._items.get(c).bStaff);
                temp.put("iRng", lobby._items.get(c).iRng);
                temp.put("iDPS", lobby._items.get(c).iDPS);
                temp.put("bCoins", lobby._items.get(c).bCoins);
                temp.put("sES", lobby._items.get(c).sES);
                temp.put("sType", lobby._items.get(c).sType);
                temp.put("iCost", lobby._items.get(c).iCost);
                temp.put("iRty", lobby._items.get(c).iRty);
                if (lobby._items.get(c).sES.equals("ar")) {
                    temp.put("iQty", classxp[e]);
                } else {
                    temp.put("iQty", qty[e]);
                }
                if (lobby._items.get(c).sES.equals("Weapon")) {
                    temp.put("EnhDPS", 100);
                }
                if (lobby._items.get(c).sType.equals("Enhancement") || lobby._items.get(c).sType.equals("Necklace") || lobby._items.get(c).sType.equals("Item") || lobby._items.get(c).sType.equals("Quest Item") || lobby._items.get(c).sType.equals("Pet") || lobby._items.get(c).sType.equals("Armor")) {
                    temp.put("EnhID", 0);
                    temp.put("PatternID", enhid[e]);
                }

                if (lobby._items.get(c).sType.equals("Enhancement") || enhid[e] == -1) {
                    temp.put("iLvl", lobby._items.get(c).iLvl);
                } else {
                    temp.put("EnhLvl", itemlevel[e]);
                    temp.put("EnhID", "1863");
                    temp.put("EnhRty", "1");
                    temp.put("EnhPatternID", enhid[e]);
                }
                temp.put("sIcon", lobby._items.get(c).sIcon);
                temp.put("bTemp", lobby._items.get(c).bTemp);
                temp.put("CharItemID", charitemid[e]);
                temp.put("iHrs", lobby._items.get(c).iHrs);
                temp.put("sFile", lobby._items.get(c).sFile);
                temp.put("iStk", lobby._items.get(c).iStk);
                temp.put("sDesc", lobby._items.get(c).sDesc);
                temp.put("bBank", "0");
                temp.put("bUpg", lobby._items.get(c).bUpg);
                temp.put("bEquip", equip[e]);
                temp.put("sName", lobby._items.get(c).sName);
                items.put(temp);
            }
            sendData(new JSONObject().put("cmd", "loadBank").put("items", items));
        } catch (Exception e) {
            traceError("Error in loading bank: " + e.getMessage());
        }
    }

    private void updateQuest(int index, int value)
    {
        try {
            strQuests = lobby.updateValue(strQuests, index, value);
            _sql.update("UPDATE hs_users_characters SET strQuests='"+strQuests+"' WHERE id="+uID);
            sendData(new JSONObject().put("cmd", "updateQuest").put("iIndex", index).put("iValue", value));
            trace("Quest String Updated! New Quest String: " + strQuests);
                trace("Look at Value Test: " + lobby.lookAtValue(strQuests, index));
        } catch (JSONException e) {
            traceError("Error in update quest: " + e.getMessage());
        }
    }

    private void tryQuestComplete(int questid, int citemid) {
        trace("Completing Quest ID: " + questid);
        
        try {
            ResultSet rs = _sql.query("SELECT * FROM hs_quests WHERE id="+questid);
            if (rs.next()) {
                String sName = rs.getString("sName");
                int iGold = rs.getInt("iGold");
                int iExp = rs.getInt("iExp");
                int iRep = rs.getInt("iRep");
                int iSlot = rs.getInt("iSlot");
                int iValue = rs.getInt("iValue");
                int iWar = rs.getInt("iWar");
                int factionID = rs.getInt("factionID");
                String rewType = rs.getString("rewType");
                String rewards[] = rs.getString("oRewards").split(":");
                String t[] = rs.getString("turnin").split(",");
                int qty[] = new int[t.length];
                int itemid[] = new int[t.length];
                rs.close();
                if(state == 2 && iWar == 0) {
                    sendData("%xt%warning%-1%You cannot do that while in combat.%");
                    return;
                }

                JSONObject _rew = new JSONObject().put("iCP", 0).put("intGold", iGold).put("intExp", iExp).put("typ", "q");
                if(factionID > 1) {
                    _rew.put("FactionID", factionID);
                    _rew.put("iRep", iRep);
                }
                for (int b = 0; b < t.length; b++) {
                    String xx[] = t[b].split(":");
                    qty[b] = Integer.parseInt(xx[1]);
                    itemid[b] = Integer.parseInt(xx[0]);
                }
                boolean sendReward = false;
                if (turnInItem(itemid, qty)) {
                    sendReward = true;
                } else {
                    JSONObject ccqr = new JSONObject().put("cmd", "ccqr").put("bSuccess", 0);
                    ccqr.put("QuestID", questid).put("sName", sName);
                    sendData(ccqr);
                }
                if (sendReward) {
                    if(iSlot > 0) {
                        updateQuest(iSlot, iValue);
                    }
                    if(iWar > 0) {
                        _sql.update("UPDATE hs_events_wars SET intWar" + iWar + "=intWar" + iWar + "+" + iRep);
                    }
                    lobby.addRewards(questid, 0, iGold, iExp, "q", pID, factionID, iRep);
                    
                    JSONObject ccqr = new JSONObject().put("cmd", "ccqr").put("bSuccess", 1).put("rewardObj", _rew);
                    ccqr.put("QuestID", questid).put("sName", sName);
                    sendData(ccqr);
                    if (rewType.equals("C")) {
                        ResultSet xs = _sql.query("SELECT id,sES FROM hs_users_items WHERE itemid="+citemid+" AND userid="+uID);
                        if (!xs.next()) {
                            xs.close();
                            dropItem(citemid);
                        } else {
                            String type = xs.getString("sES");
                            xs.close();
                            if (type.equals("None")) {
                                dropItem(citemid);
                            } else {
                                //sendData("%xt%warning%-1%The selected item was not dropped, because the item already exists in your bank nor inventory.%");
                            }
                        }
                    } else if (rewType.equals("R")) {
                        int i = _gen.nextInt(rewards.length);
                        ResultSet xs = _sql.query("SELECT id,sES FROM hs_users_items WHERE itemid="+Integer.parseInt(rewards[i])+" AND userid="+uID);
                        if (!xs.next()) {
                            xs.close();
                            dropItem(Integer.parseInt(rewards[i]));
                        } else {
                            String type = xs.getString("sES");
                            xs.close();
                            if (type.equals("None")) {
                                dropItem(Integer.parseInt(rewards[i]));
                            } else {
                                //sendData("%xt%warning%-1%Some of the items were not dropped, because that item already exists in your bank nor inventory.%");
                            }
                        }
                    } else if (rewType.equals("S")) {
                        for (int i = 0; i < rewards.length; i++) {
                            ResultSet xs = _sql.query("SELECT id,sES FROM hs_users_items WHERE itemid="+Integer.parseInt(rewards[i])+" AND userid="+uID);
                            if (!xs.next()) {
                                xs.close();
                                dropItem(Integer.parseInt(rewards[i]));
                            } else {
                                String type = xs.getString("sES");
                                xs.close();
                                if (type.equals("None")) {
                                    dropItem(Integer.parseInt(rewards[i]));
                                } else {
                                    //sendData("%xt%warning%-1%Some of the items were not dropped, because that item already exists in your bank nor inventory.%");
                                }
                            }
                        }
                    }
                    for (int e = 0; e < qAccepted.length; e++) {
                        if (qAccepted[e] == questid) {
                            qAccepted[e] = 0;
                            trace("Quest has been completed ID: " + questid);
                            return;
                        }
                    }
                }
            }
        } catch (Exception e) {
            traceError("Error in completing quest: " + e.getMessage());
        }
    }

    private boolean isItemLoaded(int itemID) {
        return _loadedItems.contains(itemID);
    }

    private boolean isItemAtMax(int itemID) {
        try {
            ResultSet rs = _sql.query("SELECT iQty FROM hs_users_items WHERE itemID=" + itemID);
            if(rs.next()) {
                if(lobby._items.get(itemID).sType.equals("Item")) {
                    if(rs.getInt("iQty") >= lobby._items.get(itemID).iStk)
                        return true;
                } else {
                    return true;
                }
            } else if (lobby._items.get(itemID).bTemp > 0) {
                if(_tempItems.containsKey(itemID)) {
                    if(_tempItems.get(itemID) >= lobby._items.get(itemID).iStk)
                        return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }
    
    private boolean isQuestActive(int questid) {
        for(int i = 0; i < qAccepted.length; i++) {
            if(questid == qAccepted[i])
                return true;
        }
        return false;
    }

    public void dropItem(int itemid) {
        try {
            if(itemid > 0 && !isItemAtMax(itemid)) {
                if(lobby._items.get(itemid).sReqQuests.length() > 0) {
                    if(lobby._items.get(itemid).sReqQuests.length() > 1) {
                        boolean drop = false;
                        String[] what = lobby._items.get(itemid).sReqQuests.split(",");
                        for(int i = 0; i < what.length; i++) {
                            if(isQuestActive(Integer.parseInt(what[i])))
                                drop = true;
                        }
                        if(drop) {
                            if(!isItemLoaded(itemid)) {
                                JSONObject d = new JSONObject().put("cmd", "addItems").put("items", new JSONObject().put("" + itemid, getItemInfo(itemid).put("iQty", 1)));
                                sendData(d);
                            } else {
                                JSONObject d = new JSONObject().put("cmd", "addItems").put("items", new JSONObject().put("" + itemid, new JSONObject().put("ItemID", itemid).put("iQty", 1)));
                                sendData(d);
                            }

                            if(_tempItems.containsKey(itemid)) {
                                _tempItems.put(itemid, (_tempItems.get(itemid) + lobby._items.get(itemid).iQty));
                            } else {
                                _tempItems.put(itemid, lobby._items.get(itemid).iQty);
                            }
                            trace("ItemID: " + itemid + " Quantity: " + _tempItems.get(itemid));
                        }
                    } else {
                        if(isQuestActive(Integer.parseInt(lobby._items.get(itemid).sReqQuests))) {
                            if(!isItemLoaded(itemid)) {
                                JSONObject d = new JSONObject().put("cmd", "addItems").put("items", new JSONObject().put("" + itemid, getItemInfo(itemid).put("iQty", 1)));
                                sendData(d);
                            } else {
                                JSONObject d = new JSONObject().put("cmd", "addItems").put("items", new JSONObject().put("" + itemid, new JSONObject().put("ItemID", itemid).put("iQty", 1)));
                                sendData(d);
                            }

                            if(_tempItems.containsKey(itemid)) {
                                _tempItems.put(itemid, (_tempItems.get(itemid) + lobby._items.get(itemid).iQty));
                            } else {
                                _tempItems.put(itemid, lobby._items.get(itemid).iQty);
                            }
                            trace("ItemID: " + itemid + " Quantity: " + _tempItems.get(itemid));
                        }
                    }
                } else if (lobby._items.get(itemid).bTemp > 0) {
                    if(!isItemLoaded(itemid)) {
                        JSONObject d = new JSONObject().put("cmd", "addItems").put("items", new JSONObject().put("" + itemid, getItemInfo(itemid).put("iQty", 1)));
                        sendData(d);
                    } else {
                        JSONObject d = new JSONObject().put("cmd", "addItems").put("items", new JSONObject().put("" + itemid, new JSONObject().put("ItemID", itemid).put("iQty", 1)));
                        sendData(d);
                    }

                    if(_tempItems.containsKey(itemid)) {
                        _tempItems.put(itemid, (_tempItems.get(itemid) + lobby._items.get(itemid).iQty));
                    } else {
                        _tempItems.put(itemid, lobby._items.get(itemid).iQty);
                    }
                    trace("ItemID: " + itemid + " Quantity: " + _tempItems.get(itemid));
                } else {
                    if(!isItemLoaded(itemid)) {
                        JSONObject d = new JSONObject().put("cmd", "dropItem").put("items", new JSONObject().put("" + itemid, getItemInfo(itemid).put("iQty", 1)));
                        sendData(d);
                    } else {
                        JSONObject d = new JSONObject().put("cmd", "dropItem").put("items", new JSONObject().put("" + itemid, new JSONObject().put("ItemID", itemid).put("iQty", 1)));
                        sendData(d);
                    }

                    iDrops += 1;
                }
            }
        } catch (Exception e) {
            traceError("Error in drop item: " + e.getMessage());
        }
    }

    private void dropItem(int itemid, int qty) {
        try {
            if(itemid > 0 && !isItemAtMax(itemid)) {
                JSONObject d = new JSONObject().put("cmd", "dropItem").put("items", new JSONObject().put("" + itemid, getItemInfo(itemid).put("iQty", qty)));
                iDrops += 1;
                sendData(d);
            }
        } catch (Exception e) {
            traceError("Error in drop item: " + e.getMessage());
        }
    }

    private void getDrop(int itemid) {
        try {
            boolean doContinue = true;
            int adjustid = 0;
            String sES = lobby._items.get(itemid).sES;
            String className = lobby._items.get(itemid).sName;
            int itemlevel = lobby._items.get(itemid).iLvl;
            int qty = lobby._items.get(itemid).iQty;
            String isitem = lobby._items.get(itemid).sType;
            if (iDrops > 0) {
                ResultSet rs = _sql.query("SELECT iQty FROM hs_users_items WHERE itemid="+itemid+" AND userid="+uID);
                if(rs.next()) {
                    if(rs.getInt("iQty") >= lobby._items.get(itemid).iStk) {
                        doContinue = false;
                    } else {
                        if(iDrops > 0) {
                            iDrops -= 1;
                        }
                    }
                } else
                    iDrops -= 1;

                rs.close();
            } else {
                doContinue = false;
            }
            if (doContinue) {
                if (sES.equals("ar")) {
                    _sql.update("INSERT INTO hs_users_items (itemid, userid, sES, className, classXP , iLvl, EnhID) VALUES (" + itemid + ", " + uID + ", '" + sES + "', '" + className + "', '0', '0','-1')");
                } else if (isitem.equals("Item") || isitem.equals("Quest Item")) {
                    ResultSet es = _sql.query("SELECT id FROM hs_users_items WHERE itemid="+itemid+" AND userid="+uID);
                    if (es.next()) {
                        _sql.update("UPDATE hs_users_items SET iQty=iQty+" + qty + " WHERE itemid=" + itemid + " AND userid=" + uID);
                    } else {
                        _sql.update("INSERT INTO hs_users_items (itemid, userid, sES, iLvl, EnhID) VALUES (" + itemid + ", " + uID + ", '" + sES + "', '" + itemlevel + "', '-1')");
                    }
                } else {
                    _sql.update("INSERT INTO hs_users_items (itemid, userid, sES, iLvl, EnhID) VALUES (" + itemid + ", " + uID + ", '" + sES + "', '0', '-1')");
                }
            }
            if (doContinue) {
                ResultSet es = _sql.query("SELECT id FROM hs_users_items WHERE itemid="+itemid+" AND userid="+uID);
                if (es.next()) {
                    adjustid = es.getInt("id");
                }
                es.close();
                JSONObject d = new JSONObject().put("cmd", "getDrop").put("CharItemID", adjustid).put("bBank", false).put("ItemID", itemid).put("iQty", qty);
                sendData(d);
            }
        } catch (Exception e) {
            traceError("Error in getting drop: " + e.getMessage());
        }
    }
    
    private JSONObject getItemInfo(int itemid) {
        JSONObject _p = new JSONObject();
        try {
            _p.put("sIcon", lobby._items.get(itemid).sIcon);
            _p.put("ItemID", itemid);
            _p.put("iLvl", lobby._items.get(itemid).iLvl);
            _p.put("sLink", lobby._items.get(itemid).sLink);
            _p.put("sElmt", lobby._items.get(itemid).sElmt);
            _p.put("bTemp", lobby._items.get(itemid).bTemp);
            _p.put("bStaff", lobby._items.get(itemid).bStaff);
            _p.put("iRng", lobby._items.get(itemid).iRng);
            _p.put("bCoins", lobby._items.get(itemid).bCoins);
            _p.put("iDPS", lobby._items.get(itemid).iDPS);
            _p.put("sES", lobby._items.get(itemid).sES);
            _p.put("bitSuccess", 1);
            _p.put("sType", lobby._items.get(itemid).sType);
            _p.put("sDesc", lobby._items.get(itemid).sDesc);
            _p.put("iStk", lobby._items.get(itemid).iStk);
            _p.put("iCost", lobby._items.get(itemid).iCost);
            _p.put("bUpg", lobby._items.get(itemid).bUpg);
            _p.put("bHouse", 0);
            _p.put("iRty", lobby._items.get(itemid).iRty);
            _p.put("iQty", lobby._items.get(itemid).iQty);
            _p.put("sName", lobby._items.get(itemid).sName);
            _p.put("sReqQuests", lobby._items.get(itemid).sReqQuests);
            _loadedItems.add(itemid);
        } catch (Exception e) {
            traceError("Error in get item info: " + e.getMessage());
        }
        return _p;
    }

    private void getQuests(String quests) {
        try {
            JSONObject _gq = new JSONObject();
            _gq.put("cmd", "getQuests");
            String questIDs[] = quests.split("%");
            JSONObject q = new JSONObject();
            /* Get each quest info... */

            for (int i = 2; i < questIDs.length; i++) {
                trace("Loading Quest ID: " + questIDs[i]);
                ResultSet rs = _sql.query("SELECT * FROM hs_quests WHERE id="+Integer.parseInt(questIDs[i]));
                if (rs.next()) {
                    boolean load = true;
                    if(rs.getInt("iSlot") > 0) {
                        if(lobby.lookAtValue(strQuests, rs.getInt("iSlot")) >= rs.getInt("iValue")) {

                        }
                    }

                    JSONObject qi = new JSONObject();
                    String[] oItems;
                    String[] oRewards;
                    String[] turnin;

                    /* Initialize the variables */

                    oItems = rs.getString("oItems").split(":");
                    if (rs.getString("oRewards").length() > 0) {
                        oRewards = rs.getString("oRewards").split(":");
                    } else {
                        oRewards = new String[0];
                    }
                    turnin = rs.getString("turnin").split(",");

                    qi.put("sFaction", rs.getString("sFaction"));
                    qi.put("iLvl", rs.getInt("iLvl"));
                    qi.put("FactionID", rs.getString("factionID"));
                    qi.put("iClass", rs.getString("iClass"));
                    qi.put("iReqRep", rs.getString("iReqRep"));
                    qi.put("iValue", rs.getString("iValue"));
                    qi.put("bOnce", rs.getInt("bOnce"));
                    qi.put("iGold", rs.getString("iGold"));
                    qi.put("iRep", rs.getString("iRep"));
                    qi.put("bitSuccess", 1);
                    qi.put("sEndText", rs.getString("sEndText"));
                    qi.put("sDesc", rs.getString("sDesc"));
                    qi.put("QuestID", ""+rs.getInt("id"));
                    qi.put("bUpg", rs.getString("bUpg"));
                    qi.put("iReqCP", rs.getString("iReqCP"));
                    qi.put("iSlot", rs.getInt("iSlot"));
                    qi.put("iExp", rs.getInt("iExp"));
                    qi.put("iWar", rs.getInt("iWar"));
                    qi.put("sName", rs.getString("sName"));

                    /* If there are quest item drops, add them */
                    JSONObject _ow = new JSONObject();
                    if (rs.getString("oRewards").length() > 0) {
                        JSONObject _i = new JSONObject();
                        for (int x = 0; x < oRewards.length; x++) {
                            _i.put(oRewards[x], getItemInfo(Integer.parseInt(oRewards[x])));
                        }
                        _ow.put("items" + rs.getString("rewType"), _i);

                    }
                    qi.put("oRewards", _ow);

                    /* Items to send to finish the quest */
                    JSONArray _ti = new JSONArray();
                    for (int a = 0; a < turnin.length; a++) {
                        String[] droppart = turnin[a].split(":");
                        _ti.put(new JSONObject().put("ItemID", droppart[0]).put("iQty", droppart[1]));
                    }
                    qi.put("turnin", _ti);

                    /* Required items for turning in quest */

                    JSONObject _oi = new JSONObject();
                    for (int x = 0; x < oItems.length; x++) {
                        _oi.put(oItems[x], getItemInfo(Integer.parseInt(oItems[x])));
                    }
                    qi.put("oItems", _oi);
                    q.put(""+rs.getInt("id"), qi);
                    trace("Quest ID Loaded: " + questIDs[i] + " to " + username);
                } else {
                    trace("Quest ID Not Found!: " + questIDs[i]);
                }
                rs.close();
            }
            _gq.put("quests", q);
            sendData(_gq);

        } catch (Exception e) {
            traceError("Error in getting quests: " + e.getMessage());
        }
        /* Get Quests by Zeroskull */
    }

    public void removeAura(int auraID, String[] type, int[] ids, String tgt, int max) {
        try {
            ResultSet rs = _sql.query("SELECT name FROM hs_skills_auras WHERE id="+auraID);
            if(rs.next()){
                JSONObject _ct = new JSONObject();
                _ct.put("cmd", "ct");
                String _x = "";
                for (int z = 0; z < max; z++) {
                    if(type[z].equals(tgt)){
                        if (z != 0) {
                            _x += ",";
                        }
                        _x += type[z] + ":" + ids[z];
                    }
                }
                _ct.put("a", new JSONArray().put(new JSONObject().put("cmd", "aura-").put("aura", new JSONObject().put("nam", rs.getString("name"))).put("tInf", _x)));
                lobby.sendDataToPlayerMap(username, _ct, false);
            }
            rs.close();
        } catch(Exception e) {
            traceError("Error in removing aura: " + e.getMessage());
        }
    }

    private void partyAccept(int ptID) {
        if (cParty == null) {
            trace("Accepting party invite, party id:" + ptID);
            cParty = lobby.joinParty(username, ptID);
            try {
                JSONObject _pa = new JSONObject();
                _pa.put("cmd", "pa");
                _pa.put("ul", cParty.getMembers());
                _pa.put("owner", cParty.ptLeader);
                _pa.put("pid", ptID);
                sendData(_pa);

                _pa = new JSONObject();
                _pa.put("cmd", "pa");
                _pa.put("ul", new JSONArray().put(username));
                _pa.put("owner", cParty.ptLeader);
                _pa.put("pid", ptID);
                lobby.sendDataToPlayerParty(username, _pa, true);
            } catch (JSONException e) {
            }
        } else {
            sendData("%xt%warning%-1%You are already in a party.%");
        }
    }

    private void partyDisband() {
        try {
            JSONObject _pc = new JSONObject();
            _pc.put("cmd", "pc");
            _pc.put("pid", cParty.ptID);
            lobby.sendDataToPlayerParty(username, _pc, false);
            lobby._parties.remove(cParty);
            for(int i = 0; i < 2; i++) {
                lobby.getHandler(cParty._members[i]).cParty = null;
            }
        } catch (JSONException e) {
        }
    }

    private void partyLeave() {
        try {
            if(cParty != null) {
                String oldpt = cParty.ptLeader;
                JSONObject _pr = new JSONObject();
                _pr.put("cmd", "pr");
                _pr.put("owner", cParty.ptLeader);
                _pr.put("pid", cParty.ptID);
                _pr.put("typ", "l");
                _pr.put("unm", username);
                lobby.sendDataToPlayerParty(username, _pr, false);
                cParty.removeMember(username);
                if(cParty.members < 2) {
                    partyDisband();
                } else if(oldpt.equals(username)) {
                    JSONObject _pp = new JSONObject();
                    _pp.put("cmd", "pp");
                    _pp.put("owner", cParty.ptLeader);
                    lobby.sendDataToPlayerParty(username, _pp, false);
                }
                cParty = null;
            } else {
                sendData("%xt%warning%-1%You are not in a party.%");
            }
        } catch (JSONException e) {
        }
    }

    private void partyInvite(String otherchar) {
        if(lobby.getHandler(otherchar).ptInvite) {
            if(cParty == null) {
                cParty = lobby.joinParty(username, -1);
            }
            sendData("%xt%server%-1%You have invited "+otherchar+" to join you party.%");
            try {
                JSONObject _pi = new JSONObject();
                _pi.put("cmd", "pi");
                _pi.put("owner", cParty.ptLeader);
                _pi.put("pid", cParty.ptID);
                lobby.sendDataToPlayer(otherchar, _pi);
            } catch (JSONException e) {
            }
        } else {
            sendData("%xt%warning%-1%"+otherchar.toUpperCase()+" cannot recieve party invitations.%");
        }
    }

    private void partyPromote(String otherchar) {
        try {
            if (lobby.getHandler(otherchar).cParty.ptID == cParty.ptID) {
                JSONObject _pp = new JSONObject();
                _pp.put("cmd", "pp");
                _pp.put("owner", otherchar);
                lobby.sendDataToPlayerParty(username, _pp, false);
                cParty.ptLeader = otherchar;
            } else {
                sendData("%xt%warning%-1%That player is not in your party.%");
            }
        } catch (JSONException e) {
        }
    }

    private void partySummonDecline(String otherchar) {
        sendData("%xt%server%-1%You declined the summon.%");
        lobby.sendDataToPlayer(otherchar, "%xt%server%-1%" + username + " declined your summon.%");
    }

    private void partySummon(String otherchar) {
        try {
            if (lobby.getHandler(otherchar).cParty.ptID == cParty.ptID) {
                sendData("%xt%server%-1%You attempt to summon " + otherchar + " to you.%");
                if (lobby.getPID(otherchar) > 0) {
                    JSONObject _ps = new JSONObject();
                    _ps.put("cmd", "ps");
                    _ps.put("unm", username);
                    ConnectionHandler uho = lobby.getHandler(otherchar);
                    if (uho.cRoom.rID() != cRoom.rID()) {
                        lobby.sendDataToPlayer(otherchar, _ps);
                    } else {
                        _ps.put("strF", frame);
                        _ps.put("strP", pad);
                        lobby.sendDataToPlayer(otherchar, _ps);
                    }
                }
            } else {
                sendData("%xt%warning%-1%That player is not in your party.%");
            }
        } catch (JSONException e) {
        }
    }

    private void partyKick(String otherchar) {
        try {
            if (lobby.getHandler(otherchar).cParty.ptID == cParty.ptID) {
                JSONObject _pr = new JSONObject();
                _pr.put("cmd", "pr");
                _pr.put("owner", cParty.ptLeader);
                _pr.put("pid", cParty.ptID);
                _pr.put("typ", "k");
                _pr.put("unm", otherchar);
                lobby.sendDataToPlayerParty(username, _pr, false);
                cParty.removeMember(otherchar);
                lobby.getHandler(otherchar).cParty = null;
                if(cParty.members < 2) {
                    partyDisband();
                }
            } else {
                sendData("%xt%warning%-1%That player is not in your party.%");
            }
        } catch (JSONException e) {
        }
    }

    private void partyDecline(int ptID) {
        sendData("%xt%server%-1%You have declined the invitation.%");
        try {
            JSONObject _pd = new JSONObject();
            _pd.put("cmd", "pd");
            _pd.put("unm", username);
           
            String ptLeader = lobby._parties.get((ptID - 1)).ptLeader;
            lobby.sendDataToPlayer(ptLeader, _pd);
            if(lobby._parties.get((ptID - 1)).members < 2) {
                lobby._parties.remove(lobby.getHandler(ptLeader).cParty);
                lobby.getHandler(ptLeader).cParty = null;
            }
        } catch (JSONException e) {
        }
    }

    private void requestFriend(String otherchar) {
        try {
            if(lobby.getHandler(otherchar).access >= 40) {
                return;
            }
            if(lobby.getHandler(otherchar).frndInvite) {
                String curfriends = "";
                ResultSet rs = _sql.query("SELECT friendid FROM hs_users_friends WHERE userid="+uID);
                if (rs.next()) {
                    curfriends = rs.getString("friendid");
                }
                rs.close();

                if (curfriends.indexOf(Integer.toString(lobby.getUID(otherchar))) != -1) {
                    sendData("%xt%warning%-1%" + otherchar + " was already added to your friends list.%");
                } else {
                    sendData("%xt%server%-1%You have requested " + otherchar + " to be friends.%");
                    lobby.sendDataToPlayer(otherchar, new JSONObject().put("cmd", "requestFriend").put("unm", username));
                }
            } else {
                sendData("%xt%warning%-1%"+otherchar.toUpperCase()+" is not accepting friend requests.%");
            }
        } catch (Exception e) {
            traceError("Error in request Friend: " + e.getMessage());
        }
    }

    private void addFriend(String thischar, String otherchar, boolean repeat) {
        try {
            String[] account2 = new String[1];
            account2[0] = "" + lobby.getUID(thischar);

            int oUID = lobby.getUID(otherchar);

            int numberOfColumns = _sql.getRowCount("hs_users_friends WHERE userid=" + account2[0]);

            if (!(numberOfColumns > 0)) {
                _sql.update("INSERT INTO hs_users_friends (userid) VALUES (" + uID + ")");
            }

            JSONObject _f = new JSONObject();
            _f.put("cmd", "addFriend");

            if (numberOfColumns < 10) {
                ResultSet rs = _sql.query("SELECT * FROM hs_users_friends WHERE userid="+Integer.parseInt(account2[0]));
                if (rs.next()) {
                    if (rs.getString("friendid").indexOf(Integer.toString(oUID)) != -1) {
                        lobby.sendDataToPlayer(thischar, "%xt%warning%-1%" + otherchar + " was already added to your friends list.%");
                    } else if (rs.getString("friendid").length() > 0) {
                        _sql.update("UPDATE hs_users_friends SET friendid=CONCAT(friendid, " + "',', " + oUID + ") WHERE userid=" + account2[0]);
                        rs.close();
                        ResultSet is = _sql.query("SELECT iLvl,id,sName,curServer FROM hs_users_characters WHERE id="+oUID);
                        if (is.next()) {
                            _f.put("friend", new JSONObject().put("iLvl", is.getInt("iLvl")).put("ID", is.getInt("id")).put("sName", is.getString("sName")).put("sServer", is.getString("curServer")));
                        }
                        lobby.sendDataToPlayer(thischar, _f);
                        lobby.sendDataToPlayer(thischar, "%xt%server%-1%" + otherchar + " has been added to your friends list.%");
                        is.close();
                    } else {
                        _sql.update("UPDATE hs_users_friends SET friendid=CONCAT(friendid, " + oUID + ") WHERE userid=" + account2[0]);

                        rs.close();
                        ResultSet is = _sql.query("SELECT iLvl,id,sName,curServer FROM hs_users_characters WHERE id="+oUID);
                        if (is.next()) {
                            _f.put("friend", new JSONObject().put("iLvl", is.getInt("iLvl")).put("ID", is.getInt("id")).put("sName", is.getString("sName")).put("sServer", is.getString("curServer")));
                        }
                        lobby.sendDataToPlayer(thischar, _f);
                        lobby.sendDataToPlayer(thischar, "%xt%server%-1%" + otherchar + " has been added to your friends list.%");
                        is.close();

                    }
                }
            }
            if (repeat) {
                addFriend(otherchar, thischar, false);
            }
        } catch (Exception e) {
            traceError("Error in adding friend: " + e.getMessage());
        }
    }

    private void pvpQuery(String warzone, int avgWait) {
        try {
            if (warzone.equals("bludrutbrawl")) {
                sendData("%xt%server%-1%You joined the Warzone queue for Bludrut Brawl!%");
                sendData(new JSONObject().put("cmd", "PVPQ").put("bitSuccess", 1).put("avgWait", avgWait).put("warzone", warzone));
                newPvpWarzone(warzone);
            } else if (warzone.equals("done")) {
                sendData(new JSONObject().put("cmd", "PVPQ").put("bitSuccess", 0));
            } else if (warzone.equals("none")) {
                sendData("%xt%server%-1%You have been removed from the Warzone's queue%");
                sendData(new JSONObject().put("cmd", "PVPQ").put("bitSuccess", 0));
            }
        } catch (JSONException e) {
        }
    }

    private void newPvpWarzone(final String warzone) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendData("%xt%server%-1%A new Warzone battle has started!%");
                try {
                    sendData(new JSONObject().put("cmd", "PVPI").put("warzone", warzone));
                } catch (JSONException e) {
                }
            }
        }, 3000);
    }

    private void resetState() {
        if(state == 2) {
            try {
                sendData(new JSONObject().put("cmd", "ct").put("p", new JSONObject().put(username, new JSONObject().put("intState", 1))));
            } catch (Exception e) {
            }
            state = 1;
            autoRestTimer();
        }
    }

    private void moveToCell(String _frame, String _pad) {
        frame = _frame;
        pad = _pad;
        tx = 0;
        ty = 0;
        lobby.sendDataToPlayerMap(username, "%xt%uotls%-1%" + username + "%strPad:" + pad + ",tx:0,strFrame:" + frame + ",ty:0,fly:" + fly + "%", true);
        resetState();
    }

    private void changeUserSettings(boolean set, String tobeset) {
        try {
            JSONObject uotls = new JSONObject();
            uotls.put("cmd", "uotls");
            uotls.put("unm", username);
            if (set) {
                _sql.update("UPDATE hs_users_characters SET " + tobeset + "=1 WHERE id=" + uID);
            } else {
                _sql.update("UPDATE hs_users_characters SET " + tobeset + "=0 WHERE id=" + uID);
            }

            if (tobeset.equals("bPet") && set) {
                showPet = true;
            } else if (tobeset.equals("bPet") && !set) {
                showPet = false;
            }
            if (tobeset.equals("bCloak") && set) {
                showPet = true;
                uotls.put("o", new JSONObject().put("showCloak", true));
                lobby.sendDataToPlayerMap(username, uotls, true);
            } else if (tobeset.equals("bCloak") && !set) {
                showCloak = false;
                uotls.put("o", new JSONObject().put("showCloak", false));
                lobby.sendDataToPlayerMap(username, uotls, true);
            }
            if (tobeset.equals("bHelm") && set) {
                showHelm = true;
                uotls.put("o", new JSONObject().put("showHelm", true));
                lobby.sendDataToPlayerMap(username, uotls, true);
            } else if (tobeset.equals("bHelm") && !set) {
                showHelm = false;
                uotls.put("o", new JSONObject().put("showHelm", false));
                lobby.sendDataToPlayerMap(username, uotls, true);
            }
            if (tobeset.equals("bGoto") && set) {
                sendData("%xt%server%-1%Accepting goto requests.%");
                acceptGoto = true;
            } else if (tobeset.equals("bGoto") && !set) {
                sendData("%xt%warning%-1%Blocking goto requests.%");
                acceptGoto = false;
            }
            if (tobeset.equals("bWhisper") && set) {
                sendData("%xt%server%-1%Accepting PMs.%");
                acceptPMs = true;
            } else if (tobeset.equals("bWhisper") && !set) {
                sendData("%xt%warning%-1%Ignoring PMs.%");
                acceptPMs = false;
            }
            if (tobeset.equals("bTT") && set) {
                sendData("%xt%server%-1%Ability ToolTips will always show on mouseover.%");
            } else if (tobeset.equals("bTT") && !set) {
                sendData("%xt%warning%-1%Ability ToolTips will not show on mouseover during combat.%");
            }
            if (tobeset.equals("bFriend") && set) {
                sendData("%xt%server%-1%Accepting Friend requests.%");
                frndInvite = true;
            } else if (tobeset.equals("bFriend") && !set) {
                sendData("%xt%warning%-1%Ignoring Friend requests.%");
                frndInvite = false;
            }
            if (tobeset.equals("bParty") && set) {
                sendData("%xt%server%-1%Accepting party invites.%");
                ptInvite = true;
            } else if (tobeset.equals("bParty") && !set) {
                sendData("%xt%warning%-1%Ignoring party invites.%");
                ptInvite = false;
            }
        } catch (JSONException e) {

        }
    }

    private void sendIAResponse(String oName, String type) {
        int rval = _gen.nextInt(10000);
        try {
            JSONObject _ia = new JSONObject();
            _ia.put("iAccessLevel", access);
            _ia.put("val", rval);
            _ia.put("cmd", "ia");
            _ia.put("oName", oName);
            _ia.put("typ", type);
            _ia.put("iUpgDays", 0);
            _ia.put("unm", username);
            lobby.sendDataToPlayerMap(username, _ia, false);
        } catch (JSONException e) {
        }
    }

    private void moveToUser(int pID) {
        ConnectionHandler uho = lobby.getHandler(pID);
        int newx = uho.tx;
        int newy = uho.ty;
        if (uho.tx > tx) {
            newx -= 96;
        } else {
            newx += 96;
        }
        try {
        if(uho.fly && !fly) {
            fly = true;
            userMove(newx, newy, 0, false, true);
            Thread.sleep(1400);
        } else if (!uho.fly && fly) {
            fly = false;
            userMove(newx, newy, 0, false, true);
            Thread.sleep(1400);
        }
        } catch (Exception e) {}
        userMove(newx, newy, 32, false, false);
        sendData("%xt%server%-1%You are being pulled by " + uho.username + "%");
    }

    private void resPlayerTimed() {
        if (cRoom.pvpArea && cRoom.roomName.equals("bludrutbrawl")) {
            sendData("%xt%resTimed%-1%Enter" + pvpTeam + "%Spawn%");
        } else
            sendData("%xt%resTimed%-1%Enter%Spawn%");
        hp = hpmax;
        mp = mpmax;
        state = 1;
        sendUotls(true, false, true, false, false, true);
        _sql.update("UPDATE hs_users_characters SET killed=killed+1 WHERE username='" + username + "'");
    }

    protected void setAFK(boolean _afk) {
        lobby.sendDataToPlayerMap(username, "%xt%uotls%-1%" + username + "%afk:" + _afk + "%" , false);
        if (_afk != afk) {
            afk = _afk;
            if (afk) {
                sendData("%xt%server%-1%You are now Away From Keyboard (AFK).%");
                traceChat("I am now away from keyboard (AFK).");
            } else {
                sendData("%xt%server%-1%You are no longer Away From Keyboard (AFK).%");
                traceChat("I am no longer away from keyboard (AFK).");
            }
        }
    }

    private void removeItem(int itemid, int deleteid) {
        try {
            JSONObject _del = new JSONObject();
            _del.put("cmd", "removeItem");
            ResultSet rs = _sql.query("SELECT sES,iQty FROM hs_users_items WHERE id="+deleteid);
            if (rs.next()) {
                String sES = rs.getString("sES");
                int iQty = rs.getInt("iQty");
                rs.close();
                if (sES.equals("None")) {
                    _sql.update("UPDATE hs_users_items SET iQty=iQty-1 WHERE id=" + deleteid);
                    if ((iQty - 1) == 1) {
                        _sql.update("DELETE FROM hs_users_items WHERE id=" + deleteid);
                    }
                } else {
                    _sql.update("DELETE FROM hs_users_items WHERE id=" + deleteid);
                }
                _del.put("bitSuccess", 1);
                _del.put("CharItemID", deleteid);
                sendData(_del);
            } else {
                _del.put("strMessage", "Item Does Not Exist");
                _del.put("bitSuccess", 1);
                _del.put("CharItemID", -1);
                sendData(_del);
            }
            rs.close();
        } catch (Exception e) {
            traceError("Error in deleting item: " + e.getMessage());
        }
    }

    protected void sellItem(int itemid, int adjustid) {
        try {
            int sellprice = lobby._items.get(itemid).iCost / 4;
            int iscoins = lobby._items.get(itemid).bCoins;
            int qty = lobby._items.get(itemid).iQty;
            String isitem = lobby._items.get(itemid).sType;
            JSONObject _sell = new JSONObject();
            _sell.put("cmd", "sellItem");
            ResultSet rs = _sql.query("SELECT id FROM hs_users_items WHERE id="+adjustid);
            if (rs.next()) {
                rs.close();
                if (iscoins != 1) {
                    _sql.update("UPDATE hs_users_characters SET gold=gold+" + sellprice + " WHERE id=" + uID);
                } else {
                    _sql.update("UPDATE hs_users_characters SET coins=coins+" + sellprice + " WHERE id=" + uID);
                }
                if (isitem.equals("Item") || isitem.equals("Quest Item")) {
                    _sql.update("UPDATE hs_users_items SET iQty=iQty-1 WHERE itemid=" + itemid + " AND userid=" + uID);
                    if (qty == 1) {
                        _sql.update("DELETE FROM hs_users_items WHERE id=" + adjustid);
                    }
                } else {
                    _sql.update("DELETE FROM hs_users_items WHERE id=" + adjustid);
                }
                _sell.put("intAmount", sellprice);
                _sell.put("CharItemID", adjustid);
                _sell.put("bCoins", iscoins);
                sendData(_sell);
            } else {
                _sell.put("strMessage", "Item Does Not Exist");
                _sell.put("bitSuccess", 1);
                _sell.put("CharItemID", -1);
                sendData(_sell);
            }

        } catch (Exception e) {
            traceError("Error in selling item: " + e.getMessage());
        }
    }

    private void unequipItem(int itemid) {
        try {
            JSONObject _un = new JSONObject();
            _un.put("uid", pID);
            _un.put("ItemID", itemid);
            _un.put("strES", lobby._items.get(itemid).sES);
            _un.put("cmd", "unequipItem");
            _sql.update("UPDATE hs_users_items SET equipped=0 WHERE userid=" + uID + " AND itemid=" + itemid + " AND equipped=1");
            lobby.sendDataToPlayerMap(username, _un, false);
        } catch (Exception e) {
            traceError("Error in unequipping item: " + e.getMessage());
        }
    }
    
    private void joinRoom(String roomName, int roomNum, String _frame, String _pad) {

        if(lobby.isMapStaffOnly(roomName) && !lobby.isStaff(username)) {
            sendData("%xt%warning%-1%\""+roomName+"\" is a staff only map.%");
            return;
        } else if (lobby.isMapUpgradeOnly(roomName) && !(upgrade > 0 && upgDays >= 0)) {
            sendData("%xt%warning%-1%\""+roomName+"\" is an upgrade only map.%");
            return;
        }

        frame = _frame;
        pad = _pad;
        tx = 0;
        ty = 0;
        if(roomNum > 10000) {
            roomNum = _gen.nextInt(99999);
        } else if (roomNum < 1 && roomNum != -1) {
            roomNum = _gen.nextInt(99999);
        }
        Room newRoom = lobby.joinRoom(this, roomName, roomNum);
        if((newRoom != null)) {
            if(cRoom != null) {
                cRoom.removeClient(this);
                //if(cRoom.users <=0) {
                    //lobby._rooms.remove(lobby._rooms.indexOf(cRoom));
                //}
            }
            cRoom = newRoom;
            if(cRoom.pvpArea && cRoom.roomName.equals("bludrutbrawl")) {
                frame = "Enter" + pvpTeam;
            }
            cRoom.joinUser(username);
            try {
                JSONObject o = new JSONObject();
                o.put("cmd", "moveToArea");
                o.put("areaName", cRoom.roomName + "-" + cRoom.roomNum);
                o.put("intKillCount", 0);
                o.put("uoBranch", cRoom.getUsers());
                o.put("strMapFileName", cRoom.sFile);
                o.put("mondef", cRoom.getMonDef());
                o.put("intType", 2);
                if(cRoom.pvpArea) {
                    o.put("pvpTeam", pvpTeam);
                    String b = "{\"id\":8,\"sName\":\"Legends\"}";
                    String r = "{\"id\":7,\"sName\":\"Overlords\"}";
                    o.put("PVPFactions", new JSONArray().put(new JSONObject(b)).put(new JSONObject(r)));
                    String rs = buildString("{\"v\":", Integer.toString(cRoom.bScore), ",\"r\":0,\"m\":0,\"k\":0}");
                    String bs = buildString("{\"v\":", Integer.toString(cRoom.rScore), ",\"r\":0,\"m\":0,\"k\":0}");
                    o.put("pvpScore", new JSONArray().put(new JSONObject(rs)).put(new JSONObject(bs)));
                }
                o.put("monBranch", cRoom.getMonBranch());
                o.put("wB", new JSONArray());
                o.put("sExtra", cRoom.sExtra);
                o.put("monmap", cRoom.getMonMap());
                o.put("areaId", cRoom.rID());
                o.put("strMapName", roomName);
                sendData(o);
                resetState();
            } catch (JSONException e) {
                traceError(e.getMessage());
            }
            sendData("%xt%server%-1%You joined \""+cRoom.roomName+"-"+cRoom.roomNum+"\"!%");
        } 
    }

    private void joinRoom(String roomName, String roomNick, String _frame, String _pad) {

        if(lobby.isMapStaffOnly(roomName) && !lobby.isStaff(username)) {
            sendData("%xt%warning%-1%\""+roomName+"\" is a staff only map.%");
            return;
        } else if (lobby.isMapUpgradeOnly(roomName) && !(upgrade > 0 && upgDays >= 0)) {
            sendData("%xt%warning%-1%\""+roomName+"\" is an upgrade only map.%");
            return;
        }
        
        frame = _frame;
        pad = _pad;
        tx = 0;
        ty = 0;
        
        Room newRoom = lobby.joinRoom(this, roomName, roomNick);
        if((newRoom != null)) {
            if(cRoom != null) {
                cRoom.removeClient(this);
                //if(cRoom.users <=0) {
                    //lobby._rooms.remove(lobby._rooms.indexOf(cRoom));
                //}
            }
            cRoom = newRoom;
            if(cRoom.pvpArea && cRoom.roomName.equals("bludrutbrawl")) {
                frame = "Enter" + pvpTeam;
            }
            cRoom.joinUser(username);
            try {
                JSONObject o = new JSONObject();
                o.put("cmd", "moveToArea");
                o.put("areaName", cRoom.roomName + "-" + cRoom.roomNick);
                o.put("intKillCount", 0);
                o.put("uoBranch", cRoom.getUsers());
                o.put("strMapFileName", cRoom.sFile);
                o.put("mondef", cRoom.getMonDef());
                o.put("intType", 2);
                if(cRoom.pvpArea) {
                    o.put("pvpTeam", pvpTeam);
                    String b = "{\"id\":8,\"sName\":\"Legends\"}";
                    String r = "{\"id\":7,\"sName\":\"Overlords\"}";
                    o.put("PVPFactions", new JSONArray().put(new JSONObject(b)).put(new JSONObject(r)));
                    String rs = buildString("{\"v\":", Integer.toString(cRoom.bScore), ",\"r\":0,\"m\":0,\"k\":0}");
                    String bs = buildString("{\"v\":", Integer.toString(cRoom.rScore), ",\"r\":0,\"m\":0,\"k\":0}");
                    o.put("pvpScore", new JSONArray().put(new JSONObject(rs)).put(new JSONObject(bs)));
                }
                o.put("monBranch", cRoom.getMonBranch());
                o.put("wB", new JSONArray());
                o.put("sExtra", cRoom.sExtra);
                o.put("monmap", cRoom.getMonMap());
                o.put("areaId", cRoom.rID());
                o.put("strMapName", roomName);
                sendData(o);
                resetState();
            } catch (JSONException e) {
                traceError(e.getMessage());
            }
            sendData("%xt%server%-1%You joined \""+cRoom.roomName+"-"+cRoom.roomNick+"\"!%");
        }
    }

    protected void sendOfflineStatus() {
        try {
            String _username = "";
            ResultSet rs = _sql.query("SELECT sName FROM hs_users_characters WHERE id="+uID);
            if(rs.next()) {
                _username = rs.getString("sName");
            }
            trace("Sending Offline Status");
            JSONObject _off = new JSONObject().put("cmd", "updateFriend");
            _off.put("friend", new JSONObject().put("iLvl", level).put("ID", uID).put("sName", _username).put("sServer", "Offline"));
            for (String u:_friends) {
                if (lobby.getPID(u) > 0) {
                    lobby.sendDataToPlayer(u, _off);
                    lobby.sendDataToPlayer(u, "%xt%server%-1%" + username + " has logged out.%");
                }
            }
        } catch (Exception e) {
        }
    }

    protected void sendOnlineStatus(boolean addMsg) {
        try {
            String _username = "";
            ResultSet rs = _sql.query("SELECT sName FROM hs_users_characters WHERE id="+uID);
            if(rs.next()) {
                _username = rs.getString("sName");
            }
            trace("Sending Online Status");
            JSONObject _on = new JSONObject().put("cmd", "updateFriend");
            _on.put("friend", new JSONObject().put("iLvl", level).put("ID", uID).put("sName", _username).put("sServer", lobby.serverName));
            for (String u:_friends) {
                if (lobby.getPID(u) > 0) {
                    lobby.sendDataToPlayer(u, _on);
                    if(addMsg)
                        lobby.sendDataToPlayer(u, "%xt%server%-1%" + username + " has logged in.%");
                }
            }
        } catch (Exception e) {
        }
    }

    private void handleLogin(String _user, String _pass, int charid) {
        try {
            _sql = new SqlConnection();
            if(_sql.connect()) {
                ResultSet rs = _sql.query("SELECT * FROM hs_users WHERE username='" + _user + "' AND password='" + _pass + "'");
                traceLogin("Logging in... " + _user);
                if(rs.next()) {
                    rs.close();
                    ResultSet ch = _sql.query("SELECT * FROM hs_users_characters WHERE id=" + charid);
                    if(ch.next()) {
                        username = ch.getString("sName").toLowerCase();
                        uID = ch.getInt("id");
                        //statsEND = (int) ((rs.getInt("END") * 1.39576)/0.2442+(rs.getInt("END")/3.5+12));
                        level = ch.getInt("iLvl");
                        expToLevel = lobby.getXpToLevel(level);
                        gender = ch.getString("strGender");
                        upgrade = ch.getInt("iUpg");
                        upgDays = ch.getInt("iUpgDays");
                        //classID = rs.getInt("currentClass");
                        access = ch.getInt("intAccessLevel");
                        strQuests = ch.getString("strQuests");
                        ptInvite = parseBoolean(ch.getInt("bParty"));
                        frndInvite = parseBoolean(ch.getInt("bFriend"));
                        acceptPMs = parseBoolean(ch.getInt("bWhisper"));
                        acceptGoto = parseBoolean(ch.getInt("bGoto"));
                        showPet = parseBoolean(ch.getInt("bPet"));
                        showCloak = parseBoolean(ch.getInt("bCloak"));
                        showHelm = parseBoolean(ch.getInt("bHelm"));
                        soundOn = parseBoolean(ch.getInt("bSoundOn"));
                        bTT = parseBoolean(ch.getInt("bTT"));
                    } else {
                        sendData("%xt%loginResponse%-1%0%-1%%Character not found!%");
                    }
                    readA1Pref();

                } else {
                    traceLogin("Unable to retrieve user data for " + _user);
                    sendData("%xt%loginResponse%-1%0%-1%%User Data for '" + _user + "' could not be retrieved. Please contact the Mystical HeroSmash staff to resolve the issue.%");
                }

                   
                rs.close();
                _sql.update("UPDATE hs_users_characters SET curServer='" + lobby.serverName + "' WHERE id=" + uID);
                hp = lobby.formulateHP(level)+statsEND;
                hpmax = lobby.formulateHP(level)+statsEND;
                mp = lobby.formulateMP(level);
                mpmax = lobby.formulateMP(level);
                if(lobby.addClient(this)) {
                    pID = lobby.getPID(this);
                    traceLogin(username + " is now logged in.");
                    server.writeLog("[Notice] " + username + " has logged in.", 3);
                    ping();
                    sendData(buildString("%xt%loginResponse%-1%1%", Integer.toString(lobby.getPID(username)), "%", username, "%", lobby.messageOfTheDay, "%", _pass, "%", lobby.strNews, "%"));
                    sendData("{\"t\":\"xt\",\"b\":{\"r\":-1,\"o\":{\"cmd\":\"cvu\",\"o\":{\"PCstRatio\":7.47,\"PChpDelta\":1640,\"PChpBase1\":360,\"baseHit\":0,\"intSPtoDPS\":10,\"resistRating\":17,\"curveExponent\":0.66,\"baseCritValue\":1.5,\"PChpGoal100\":4000,\"intLevelCap\":100,\"baseMiss\":0.1,\"baseParry\":0.03,\"GstBase\":12,\"modRating\":3,\"baseResistValue\":0.7,\"baseBlockValue\":0.7,\"intHPperEND\":5,\"baseHaste\":0,\"baseBlock\":0,\"statsExponent\":1,\"PChpBase100\":2000,\"intAPtoDPS\":10,\"PCstBase\":15,\"baseCrit\":0.05,\"baseEventValue\":0.05,\"GstGoal\":572,\"PChpGoal1\":400,\"GstRatio\":5.6,\"intLevelMax\":100,\"bigNumberBase\":8,\"PCstGoal\":762,\"baseDodge\":0.04,\"PCDPSMod\":0.85}}}}");
                } else {
                    traceLogin(username + " is still logged in, logging out existing login.");
                    sendData("%xt%loginResponse%-1%0%-1%%The user account is still logged in, please try again while the server logs it out.%");
                    lobby.removeClient(username);
                    _running = false;
                }
                
            } else {
                trace("Sql Connection Failed");
                 _running = false;
            }
        } catch (Exception e) {
            traceError("Error: " + e.getMessage() + " Cause: " + e.getCause());
        }
    }

    private boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public int getClassPoints(int userid)
    {
        try {
            ResultSet rs = _sql.query("SELECT classXP FROM hs_users_items WHERE sES='ar' AND equipped=1 AND userid="+userid);
            if (rs.next()) {
                int t = rs.getInt("classXP");
                rs.close();
                return t;
            }
        } catch (Exception e) {
            return getClassPoints(userid);

        }
        return -1;
    }

    private String getClassName(int userid) {
        try {
            ResultSet rs = _sql.query("SELECT className FROM hs_users_items WHERE sES='ar' AND equipped=1 AND userid="+userid);
            if (rs.next()) {
                String t = rs.getString("className");
                rs.close();
                return t;
            }
            rs.close();
        } catch (Exception e) {
            return getClassName(userid);
        }
        return "Error";
    }

    private JSONObject getEquipment(int userid) {
        try {
            JSONObject jData = new JSONObject();
            int i = 0;
            ResultSet rs = _sql.query("SELECT itemid,sES,iLvl FROM hs_users_items WHERE equipped=1 AND userid="+userid);
            int itemID[]= new int[10];
            while(rs.next()){
                if(lobby._items.containsKey(rs.getInt("itemid"))) {
                    itemID[i] = rs.getInt("itemid");
                    if(rs.getString("sES").equals("we") && userid == uID) {
                        wepLvl = rs.getInt("iLvl");
                        wepRng = lobby._items.get(rs.getInt("itemid")).iRng;
                        wepDPS = lobby._items.get(rs.getInt("itemid")).iDPS;
                    }
                    i++;
                }
            }
            rs.close();
            for (int x = 0; x < i; x++)
            {
                JSONObject iData = new JSONObject();
                iData.put("ItemID", itemID[x]);
                iData.put("sFile", lobby._items.get(itemID[x]).sFile);
                iData.put("sLink", lobby._items.get(itemID[x]).sLink);
                if(lobby._items.get(itemID[x]).sES.equals("we") || lobby._items.get(itemID[x]).sES.equals("he")) {
                    iData.put("sType", lobby._items.get(itemID[x]).sType);
                }
                jData.put(lobby._items.get(itemID[x]).sES, iData);
            }
            return jData;
        } catch (Exception e) {
            traceError("Error in Get Equipment: " + e.getMessage() + " Cause: " + e.getCause());
            //return getEquipment(userid);
        }
        return new JSONObject();
    }

    protected void levelUp() {
        try {
            ResultSet rs = _sql.query("SELECT intExp FROM hs_users_characters WHERE id="+this.uID);
            if (rs.next()) {
                int xp = rs.getInt("intExp");
                if (xp >= expToLevel) {
                    _sql.update("UPDATE hs_users_characters SET iLvl=iLvl+1, intExp=0 WHERE id=" + uID);
                    level += 1;
                    mp = lobby.formulateMP(level);
                    hp = lobby.formulateHP(level);
                    hpmax = lobby.formulateHP(level);
                    mpmax = lobby.formulateMP(level);
                    JSONObject _lvl = new JSONObject();
                    _lvl.put("cmd", "levelUp");
                    _lvl.put("intExpToLevel", (expToLevel = lobby.getXpToLevel(level)));
                    _lvl.put("intLevel", level);
                    sendData(_lvl);
                    sendUotls(true, true, true, true, true, false);
                    sendOnlineStatus(false);
                }
            }
            rs.close();
        } catch (Exception err) {
            traceError("Error in level up: " + err.getMessage());
        }
    }

    protected void levelUp(int _level) {
        try {
            _sql.update("UPDATE hs_users_characters SET iLvl="+_level+", intExp=0 WHERE id=" + uID);
            level = _level;
            mp = lobby.formulateMP(level);
            hp = lobby.formulateHP(level);
            hpmax = lobby.formulateHP(level);
            mpmax = lobby.formulateMP(level);
            JSONObject _lvl = new JSONObject();
            _lvl.put("cmd", "levelUp");
            _lvl.put("intExpToLevel", (expToLevel = lobby.getXpToLevel(level)));
            _lvl.put("intLevel", level);
            sendData(_lvl);
            sendUotls(true, true, true, true, true, false);
            sendOnlineStatus(false);
        } catch (Exception err) {
            traceError("Error in level up: " + err.getMessage());
        }
    }

    private void initUserDatas(String Packet) {
        try {
            JSONObject jPack = new JSONObject();
            jPack.put("cmd", "initUserDatas");
            JSONArray a = new JSONArray();
            String dataNod[] = Packet.split("%");
            for (int i = 2; i < dataNod.length; i++) {
                if (isInteger(dataNod[i])) {
                    int _pID = Integer.parseInt(dataNod[i]);
                    int id = lobby.getHandler(_pID).uID;
                    if (id > 0) {
                        int classPoints = getClassPoints(id);
                        JSONObject _equip = getEquipment(id);
                        String className = getClassName(id);
                        ResultSet rs = _sql.query("SELECT * FROM hs_users_characters WHERE id="+id);
                        if (rs.next()) {
                            JSONObject jData = new JSONObject();
                            String _username = rs.getString("sName").toLowerCase();
                            int playerLevel = rs.getInt("iLvl");
                            jData.put("uid", lobby.getPID(_username));
                            jData.put("strFrame", frame);
                            jData.put("strPad",pad);
                            JSONObject data = new JSONObject();
                            data.put("intColorAccessory", rs.getInt("intColorAccessory"));
                            data.put("iCP", classPoints);
                            data.put("intLevel", playerLevel);
                            data.put("intMP", mp);
                            data.put("eqp", _equip);
                            data.put("intColorSkin", rs.getInt("intColorSkin"));
                            data.put("intMPMax", mpmax);
                            data.put("intHPMax", hpmax);
                            data.put("intColorTrim", rs.getInt("intColorTrim"));
                            data.put("intColorBase", rs.getInt("intColorBase"));
                            data.put("iNose", rs.getInt("iNose"));
                            data.put("iEye", rs.getInt("iEye"));
                            data.put("iMouth", rs.getInt("iMouth"));
                            data.put("intHP", hp);
                            data.put("intColorEye", rs.getInt("intColorEye"));
                            data.put("strClassName", className);
                            data.put("intAccessLevel", rs.getInt("intAccessLevel"));
                            data.put("strHairName", rs.getString("strHairName"));
                            data.put("strHairFilename", rs.getString("strHairFilename"));
                            data.put("intColorHair", rs.getString("intColorHair"));
                            data.put("HairID",rs.getInt("HairID"));
                            data.put("strGender", rs.getString("strGender"));
                            data.put("strUsername", _username);
                            data.put("strMapName", cRoom.roomName);
                            if(id == this.uID){
                                data.put("iDEX", 0);
                                data.put("intCoins", rs.getInt("intCoins"));
                                data.put("iEND", 0);
                                data.put("intActivationFlag", rs.getInt("intActivationFlag"));
                                data.put("iLCK", 0);
                                data.put("iDBCP", classPoints);
                                data.put("iDBGold", rs.getInt("intGold"));
                                data.put("intGold", rs.getInt("intGold"));
                                data.put("dCreated", "0000-00-00T00:00:00");
                                data.put("strQuests", rs.getString("strQuests"));
                                data.put("lastArea", rs.getString("lastArea"));
                                data.put("iFounder", "1");
                                data.put("intDBExp", rs.getInt("intExp"));
                                data.put("intExp", rs.getInt("intExp"));
                                data.put("sHouseInfo", new JSONArray());
                                data.put("iBankSlots", rs.getInt("iBankSlots"));
                                data.put("iHouseSlots", rs.getInt("iHouseSlots"));
                                data.put("dUpgExp", "2012-01-20T17:53:00");
                                data.put("iUpg", rs.getInt("iUpg"));
                                data.put("CharID", rs.getInt("id"));
                                data.put("strEmail", "emailisprivate@thankyou.com");
                                data.put("iINT", 0);
                                data.put("UserID", rs.getInt("userid"));
                                data.put("iBagSlots", rs.getInt("iBagSlots"));
                                data.put("ig0", 0);
                                data.put("iUpgDays", rs.getInt("iUpgDays"));
                                data.put("sCountry", "US");
                                data.put("iSTR", 0);
                                data.put("ip0", 0);
                                data.put("iq0", 0);
                                data.put("iAge", 14);
                                data.put("iWIS", 0);
                                data.put("intExpToLevel", lobby.getXpToLevel(level));
                                data.put("ia0", rs.getString("ia0"));
                                data.put("ia1", ia1);
                                data.put("id0", 0);
                            }
                            data.put("bitSuccess", "1");
                            jData.put("data", data);
                            a.put(jData);
                        }
                        jPack.put("a", a);
                        rs.close();
                    }
                }
            }
            sendData(jPack);
        } catch (Exception e) {
            traceError("Error in retrieve user datas: " + e.getMessage() + ", uid: " + Packet);
            try {
                Thread.sleep(200);
            } catch (Exception e2) {
                traceError("retrieveUserDatas sleep failed: " + e2.getMessage());
            }
        }
    }

    private void initUserData(int pID) {
        try {
            JSONObject jPack = new JSONObject();
            jPack.put("cmd", "initUserData");
            int id = lobby.getHandler(pID).uID;
            if (id > 0) {
                int classPoints = getClassPoints(id);
                JSONObject _equip = getEquipment(id);
                String className = getClassName(id);
                ResultSet rs = _sql.query("SELECT * FROM hs_users_characters WHERE id="+id);
                if (rs.next()) {
                    String _username = rs.getString("sName").toLowerCase();
                    int playerLevel = rs.getInt("iLvl");
                    jPack.put("uid", lobby.getPID(_username));
                    jPack.put("strFrame", frame);
                    jPack.put("strPad",pad);
                    JSONObject data = new JSONObject();
                    data.put("intColorAccessory", rs.getInt("intColorAccessory"));
                    data.put("iCP", classPoints);
                    data.put("intLevel", playerLevel);
                    data.put("intMP", mp);
                    data.put("eqp", _equip);
                    data.put("intColorSkin", rs.getInt("intColorSkin"));
                    data.put("intMPMax", mpmax);
                    data.put("intHPMax", hpmax);
                    data.put("iNose", rs.getInt("iNose"));
                    data.put("iEye", rs.getInt("iEye"));
                    data.put("iMouth", rs.getInt("iMouth"));
                    data.put("intColorTrim", rs.getInt("intColorTrim"));
                    data.put("intColorBase", rs.getInt("intColorBase"));
                    data.put("intHP", hp);
                    data.put("intColorEye", rs.getInt("intColorEye"));
                    data.put("strClassName", className);
                    data.put("intAccessLevel", rs.getInt("intAccessLevel"));
                    data.put("strHairName", rs.getString("strHairName"));
                    data.put("strHairFilename", rs.getString("strHairFilename"));
                    data.put("intColorHair", rs.getString("intColorHair"));
                    data.put("HairID",rs.getInt("HairID"));
                    data.put("strGender", rs.getString("strGender"));
                    data.put("strUsername", _username);
                    data.put("strMapName", cRoom.roomName);
                    data.put("bitSuccess", "1");
                    jPack.put("data", data);
                }
                rs.close();
            }
            sendData(jPack);
        } catch (Exception e) {
            traceError("Error in retrieving user data:" + e.getMessage() + " cause:" + e.getCause());
        }
    }


    private void loadInventoryBig() {
        String enhp = "{\"t\":\"xt\",\"b\":{\"r\":-1,\"o\":{\"cmd\":\"enhp\",\"o\":{\"3\":{\"iWIS\":\"0\",\"sDesc\":\"M2\",\"ID\":\"3\",\"iSTR\":\"30\",\"iLCK\":\"0\",\"sName\":\"Thief\",\"iDEX\":\"45\",\"iEND\":\"25\",\"iINT\":\"0\"},\"2\":{\"iWIS\":\"0\",\"sDesc\":\"M1\",\"ID\":\"2\",\"iSTR\":\"44\",\"iLCK\":\"0\",\"sName\":\"Fighter\",\"iDEX\":\"13\",\"iEND\":\"43\",\"iINT\":\"0\"},\"1\":{\"iWIS\":\"16\",\"sDesc\":\"none\",\"ID\":\"1\",\"iSTR\":\"16\",\"iLCK\":\"0\",\"sName\":\"Adventurer\",\"iDEX\":\"16\",\"iEND\":\"18\",\"iINT\":\"16\"},\"7\":{\"iWIS\":\"15\",\"sDesc\":\"C2\",\"ID\":\"7\",\"iSTR\":\"0\",\"iLCK\":\"0\",\"sName\":\"Healer\",\"iDEX\":\"0\",\"iEND\":\"40\",\"iINT\":\"45\"},\"6\":{\"iWIS\":\"0\",\"sDesc\":\"C1\",\"ID\":\"6\",\"iSTR\":\"0\",\"iLCK\":\"20\",\"sName\":\"Wizard\",\"iDEX\":\"0\",\"iEND\":\"30\",\"iINT\":\"50\"},\"5\":{\"iWIS\":\"0\",\"sDesc\":\"M3\",\"ID\":\"5\",\"iSTR\":\"28\",\"iLCK\":\"0\",\"sName\":\"Hybrid\",\"iDEX\":\"20\",\"iEND\":\"25\",\"iINT\":\"27\"},\"9\":{\"iWIS\":\"0\",\"sDesc\":\"S1\",\"ID\":\"9\",\"iSTR\":\"5\",\"iLCK\":\"70\",\"sName\":\"Lucky\",\"iDEX\":\"10\",\"iEND\":\"15\",\"iINT\":\"0\"}}}}}";
        //sendData(enhp);
        sendData("{\"t\":\"xt\",\"b\":{\"r\":-1,\"o\":{\"tempSta\":{\"ba\":{},\"Weapon\":{},\"innate\":{\"STR\":12,\"INT\":2,\"DEX\":10,\"WIS\":5,\"LCK\":3,\"END\":14},\"ar\":{},\"he\":{}},\"cmd\":\"stu\",\"sta\":{\"$tdo\":0.04,\"$thi\":0,\"_cmi\":1,\"$smb\":0,\"_tdo\":0.04,\"_cmo\":1,\"_sem\":0.05,\"$WIS\":5,\"$tha\":0,\"$tpa\":0.03,\"_cdi\":1,\"_sp\":0,\"$cpo\":1,\"_chi\":1,\"$cpi\":1,\"_cdo\":1,\"_tbl\":0,\"_tpa\":0.03,\"_cho\":1,\"$LCK\":3,\"$shb\":0,\"$STR\":12,\"$sem\":0.05,\"_ap\":0,\"_sbm\":0.7,\"$cmi\":1,\"$cai\":1,\"$tbl\":0,\"_srm\":0.7,\"_cai\":1,\"$DEX\":10,\"_STR\":12,\"$ap\":0,\"$cao\":1,\"_DEX\":10,\"$sbm\":0.7,\"$cmc\":1,\"$INT\":2,\"_cpi\":1,\"$chi\":1,\"$cho\":1,\"_INT\":2,\"_scm\":1.5,\"_cao\":1,\"_END\":14,\"_WIS\":5,\"_shb\":0,\"_tre\":0.07,\"$cdo\":1,\"$tcr\":0.05,\"$END\":14,\"$cdi\":1,\"_cpo\":1,\"$scm\":1.5,\"_tcr\":0.05,\"_tha\":0,\"_thi\":0,\"$srm\":0.7,\"$cmo\":1,\"$sp\":0,\"_LCK\":3,\"_cmc\":1,\"$tre\":0.07,\"_smb\":0},\"wDPS\":15}}}");
        JSONObject jData = new JSONObject();
        try {
            jData.put("cmd", "loadInventoryBig");
            
            JSONArray friends = new JSONArray();
            try {
                ResultSet rs = _sql.query("SELECT * FROM hs_users_friends WHERE userid="+uID);
                if (rs.next()) {
                    if (rs.getString("friendid").length() > 0) {
                        String[] friendslist = new String[1];
                        if(rs.getString("friendid").contains(","))
                            friendslist = rs.getString("friendid").split(",");
                        else
                            friendslist[0] = rs.getString("friendid");
                        rs.close();
                        for(int e = 0; e < friendslist.length; e++) {
                            if(friendslist[e].length() > 0){
                                ResultSet is = _sql.query("SELECT * FROM hs_users_characters WHERE id="+Integer.parseInt(friendslist[e]));
                                if (is.next()) {
                                    JSONObject temp = new JSONObject();
                                    temp.put("iLvl", is.getInt("iLvl"));
                                    temp.put("ID", is.getInt("id"));
                                    temp.put("sName", is.getString("sName"));
                                    temp.put("sServer", is.getString("curServer"));
                                    friends.put(temp);
                                    _friends.add(is.getString("sName").toLowerCase());
                                }
                                is.close();
                            }
                        }
                    }
                }
            } catch (Exception fx) {
            }
            jData.put("friends", friends);
            
            JSONArray items = new JSONArray();
            int itemCount = _sql.getRowCount("hs_users_items WHERE bBank=0 AND userid="+uID);
            ResultSet rs2 = _sql.query("SELECT id,itemid,equipped,iLvl,EnhID,sES,iQty,classXP FROM hs_users_items WHERE bBank=0 AND userid="+uID);
            int[] charitemid = new int[itemCount];
            int[] itemid = new int[itemCount];
            int[] equip = new int[itemCount];
            int[] itemlevel = new int[itemCount];
            int[] classxp = new int[itemCount];
            int[] qty = new int[itemCount];
            int[] enhid = new int[itemCount];
            int i = 0;
            while (rs2.next()) {
                charitemid[i] = rs2.getInt("id");
                itemid[i] = rs2.getInt("itemid");
                equip[i] = rs2.getInt("equipped");
                itemlevel[i] = rs2.getInt("iLvl");
                enhid[i] = rs2.getInt("EnhID");
                qty[i] = rs2.getInt("iQty");
                if (rs2.getString("sES").equals("ar")) {
                    classxp[i] = rs2.getInt("classXP");
                }
                i++;
            }
            rs2.close();
            for(int e = 0; e < i; e++) {
                int c = itemid[e];
                _loadedItems.add(c);
                JSONObject temp = new JSONObject();
                if(lobby._items.containsKey(c)) {
                    temp.put("ItemID", c);
                    temp.put("sLink", lobby._items.get(c).sLink);
                    temp.put("sElmt", lobby._items.get(c).sElmt);
                    temp.put("bStaff", lobby._items.get(c).bStaff);
                    temp.put("iRng", lobby._items.get(c).iRng);
                    temp.put("iDPS", lobby._items.get(c).iDPS);
                    temp.put("bCoins", lobby._items.get(c).bCoins);
                    temp.put("sES", lobby._items.get(c).sES);
                    temp.put("sType", lobby._items.get(c).sType);
                    temp.put("iCost", lobby._items.get(c).iCost);
                    temp.put("iRty", lobby._items.get(c).iRty);
                    temp.put("iQty", qty[e]);
                    if (lobby._items.get(c).sES.equals("ar")) {
                        //temp.put("iQty", classxp[e]);
                    } else {

                    }
                    /**
                    if (lobby._items.get(c).sES.equals("Weapon")) {
                        temp.put("EnhDPS", 100);
                    }
                    if (lobby._items.get(c).sType.equals("Enhancement") || lobby._items.get(c).sType.equals("Necklace") || lobby._items.get(c).sType.equals("Item") || lobby._items.get(c).sType.equals("Quest Item") || lobby._items.get(c).sType.equals("Pet") || lobby._items.get(c).sType.equals("Armor")) {
                        temp.put("EnhID", 0);
                        temp.put("PatternID", enhid[e]);
                    }

                    if (lobby._items.get(c).sType.equals("Enhancement") || enhid[e] == -1) {
                        temp.put("iLvl", lobby._items.get(c).iLvl);
                    } else {
                        temp.put("EnhLvl", itemlevel[e]);
                        temp.put("EnhID", "1863");
                        temp.put("EnhRty", "1");
                        temp.put("EnhPatternID", enhid[e]);
                    }
                     * **/
                    temp.put("sIcon", lobby._items.get(c).sIcon);
                    temp.put("bTemp", lobby._items.get(c).bTemp);
                    temp.put("CharItemID", charitemid[e]);
                    temp.put("iHrs", lobby._items.get(c).iHrs);
                    temp.put("sFile", lobby._items.get(c).sFile);
                    temp.put("iStk", lobby._items.get(c).iStk);
                    temp.put("sDesc", lobby._items.get(c).sDesc);
                    temp.put("bBank", "0");
                    temp.put("bUpg", lobby._items.get(c).bUpg);
                    temp.put("bEquip", equip[e]);
                    temp.put("sName", lobby._items.get(c).sName);
                    items.put(temp);
                } else {
                    trace("Item not found! ItemID: " + c);
                }
            }
            jData.put("items", items);
            JSONArray _f = new JSONArray();
            try {
                ResultSet fs = _sql.query("SELECT * FROM hs_users_factions WHERE userid="+uID);
                while(fs.next()) {
                    _f.put(new JSONObject().put("FactionID", fs.getInt("factionid")).put("CharFactionID", fs.getInt("id")).put("sName", fs.getString("sName")).put("iRep", fs.getInt("iRep")));
                }
            } catch (Exception ex) {
            }
            jData.put("factions", _f);
            jData.put("hitems", new JSONArray());
            sendData(jData);
            //updateClass();
            loadSkills(classID);
            sendOnlineStatus(true);
            sendData("%xt%server%-1%Character load complete.%");
        } catch (Exception e) {
            traceError("Error in load big inventory: " + e.getMessage());
            if(e.getMessage().equals("null")){
                 _running = false;
            }
        }
    }

    private void updateClass() {
        try {
            int classXP = getClassPoints(uID);
            clearAuras();
            ResultSet rs2 = _sql.query("SELECT * FROM hs_classes WHERE classid="+classID);
            if (rs2.next()) {
                JSONObject ye = new JSONObject();
                ye.put("uid", pID);
                ye.put("sStats",rs2.getString("sStats"));
                ye.put("iCP", classXP);
                ye.put("cmd", "updateClass");
                ye.put("sDesc", rs2.getString("sDesc"));
                ye.put("sClassCat", rs2.getString("sClassCat"));
                ye.put("aMRM", new JSONArray().put(rs2.getString("aMRM")));
                ye.put("sClassName", rs2.getString("className"));
                sendData(ye);
                ye = new JSONObject();
                ye.put("uid", pID);
                ye.put("iCP", classXP);
                ye.put("cmd", "updateClass");
                ye.put("sClassCat", rs2.getString("sClassCat"));
                ye.put("sClassName", rs2.getString("className"));
                lobby.sendDataToPlayerMap(username, ye, true);
            }
        } catch (Exception e) {
            traceError("Error in update class: " + e.getMessage());
        }
    }

    private void clearAuras() {
        String cA = "{\"t\":\"xt\",\"b\":{\"r\":-1,\"o\":{\"cmd\":\"clearAuras\"}}}";
        sendData(cA);
        _auras.clearAuras();
    }
    
    public void loadSkills(int classid) {

        /**
        int cp = getClassPoints(uID);
        int rank = lobby.getRankFromCP(cp);

        if (rank == 9 || rank==-1) {
            rank = 10;
        }
        classRank = rank;
        try {
            JSONObject sAct = new JSONObject();
            sAct.put("cmd", "sAct");
            ResultSet rs = _sql.query("SELECT className,passives,skills FROM hs_classes WHERE classid="+classid);
            if (rs.next()) {
                trace("Loading Skills for \"" + rs.getString("className") + "\"");
                JSONObject actions = new JSONObject();
                JSONArray passive = new JSONArray();
                String[] passives = rs.getString("passives").split(",");
                String[] skills = rs.getString("skills").split(",");
                rs.close();
                _passives.clear();
                for (int x = 0; x < passives.length; x++) {
                    JSONObject _p = new JSONObject();
                    ResultSet es = _sql.query("SELECT * FROM hs_passives WHERE id="+Integer.parseInt(passives[x]));
                    if (es.next()) {
                        _p.put("icon", es.getString("icon"));
                        _p.put("ref", es.getString("ref"));
                        _p.put("nam", es.getString("name"));
                        _p.put("desc", es.getString("desc"));
                        if (rank <= 3) {
                            _p.put("isOK", false);
                        } else {
                            _p.put("isOK", true);
                        }
                        _p.put("auras", new JSONArray().put(new JSONObject()));
                        _p.put("typ", es.getString("type"));
                        passive.put(_p);
                    }
                    es.close();
                    _passives.add(lobby._passives.get(Integer.parseInt(passives[x])));
                }
                actions.put("passive", passive);
                JSONArray active = new JSONArray();
                _skills.clear();
                for (int e = 0; e < skills.length; e++) {
                    JSONObject _s = new JSONObject();
                    ResultSet is = _sql.query("SELECT * FROM hs_skills WHERE id="+skills[e]);
                    if (is.next()) {
                        _s.put("icon", is.getString("icon"));
                        _s.put("mp", is.getInt("mana"));
                        _s.put("nam", is.getString("name"));
                        _s.put("anim", is.getString("anim"));
                        _s.put("desc", is.getString("desc"));
                        if (rank <= 1 && e == 2) {
                            _s.put("isOK", false);
                        } else if (rank <= 2 && e == 3) {
                            _s.put("isOK", false);
                        } else if (rank <= 4 && e == 4) {
                            _s.put("isOK", false);
                        } else {
                            _s.put("isOK", true);
                        }
                        if (is.getInt("tgtMax") != 0) {
                            _s.put("tgtMax", is.getInt("tgtMax"));
                            _s.put("tgtMin", is.getInt("tgtMin"));
                        }
                        _s.put("range", is.getInt("range"));
                        _s.put("fx", is.getString("fx"));
                        _s.put("damage", is.getString("damage"));
                        _s.put("dsrc", is.getString("dsrc"));
                        _s.put("ref", is.getString("ref"));
                        if(is.getString("ref").equals("aa")){
                            _s.put("auto", true);
                        }
                        _s.put("tgt", is.getString("tgt"));
                        _s.put("typ", is.getString("typ"));
                        _s.put("str1", is.getString("str1"));
                        _s.put("cd", is.getInt("cd"));
                        active.put(_s);
                    }
                    is.close();
                    _skills.add(lobby._skills.get(Integer.parseInt(skills[e])));
                }
                String x = "{\"icon\":\"icu1\",\"nam\":\"Potions\",\"anim\":\"Salute\",\"mp\":0,\"desc\":\"Equip a potion or scroll from your inventory to use it here.\",\"isOK\":true,\"range\":808,\"fx\":\"\",\"ref\":\"i1\",\"tgt\":\"f\",\"typ\":\"m\",\"strl\":\"\",\"cd\":5000}";
                active.put(new JSONObject(x));
                actions.put("active", active);
                sAct.put("actions", actions);
                sendData(sAct);
                x = "{\"t\":\"xt\",\"b\":{\"r\":-1,\"o\":{\"tempSta\":{\"ba\":{\"STR\":9,\"INT\":9,\"DEX\":7,\"END\":8},\"Weapon\":{\"STR\":24,\"DEX\":7,\"END\":23},\"innate\":{\"STR\":61,\"INT\":11,\"DEX\":50,\"WIS\":23,\"LCK\":14,\"END\":68},\"ar\":{\"STR\":14,\"DEX\":4,\"END\":13},\"he\":{\"STR\":12,\"INT\":12,\"DEX\":9,\"END\":11}},\"cmd\":\"stu\",\"sta\":{\"$tdo\":0.115,\"$thi\":0.038500000000000006,\"_cmi\":1,\"$smb\":0,\"_tdo\":0.04,\"_cmo\":1,\"_sem\":0.05,\"$WIS\":23,\"$tha\":0.00000000000232,\"$tpa\":0.03,\"_cdi\":1,\"_sp\":0,\"$cpo\":1.1,\"_chi\":1,\"$cpi\":1,\"_cdo\":1,\"_tbl\":0,\"_tpa\":0.03,\"_cho\":1,\"$LCK\":14,\"$shb\":0,\"$STR\":120,\"$sem\":0.12000000000000001,\"_ap\":0,\"_sbm\":0.7,\"$cmi\":0.92,\"$cai\":0.9,\"$tbl\":0,\"_srm\":0.7,\"_cai\":1,\"$DEX\":77,\"_STR\":61,\"$ap\":240,\"$cao\":1,\"_DEX\":50,\"$sbm\":0.61,\"$cmc\":1,\"$INT\":32,\"_cpi\":1,\"$chi\":1,\"$cho\":1,\"_INT\":11,\"_scm\":1.5,\"_cao\":1,\"_END\":68,\"_WIS\":23,\"_shb\":0,\"_tre\":0.07,\"$cdo\":1,\"$tcr\":0.16999999999999998,\"$END\":123,\"$cdi\":1,\"_cpo\":1,\"$scm\":1.675,\"_tcr\":0.05,\"_tha\":0,\"_thi\":0,\"$srm\":0.7,\"$cmo\":1,\"$sp\":64,\"_LCK\":14,\"_cmc\":1,\"$tre\":0.07,\"_smb\":0},\"wDPS\":500}}}";
                sendData(x);
            }
            rs.close();
        } catch (Exception e) {
            traceError("Error in load skills: " + e.getMessage());
        }
         * **/
        _passives.add(lobby._passives.get(1));
        _passives.add(lobby._passives.get(1));
        _skills.add(lobby._skills.get(1));
        sendData("{\"t\":\"xt\",\"b\":{\"r\":-1,\"o\":{\"cmd\":\"sAct\",\"actions\":{\"passive\":[],\"active\":[{\"icon\":\"iwd1\",\"mp\":0,\"nam\":\"Auto attack\",\"anim\":\"Attack1,Attack2\",\"desc\":\"A basic attack, taught to all adventurers.\",\"range\":301,\"fx\":\"m\",\"damage\":1,\"dsrc\":\"AP2\",\"ref\":\"aa\",\"auto\":true,\"tgt\":\"h\",\"typ\":\"aa\",\"strl\":\"\",\"cd\":2000}]}}}}");
    }


     private void userMove(int _tx, int _ty, int speed, boolean cansee, boolean canfly) {
        if (_tx < 0)
            _tx = 0;
        if (_ty < 0)
            _ty = 0;
            
        tx = _tx;
        ty = _ty;
        String data ="%xt%uotls%-1%" + username + "%sp:" + speed + ",tx:" + tx + ",ty:" + ty + ",strFrame:" + frame + "%";
        if(canfly) {
            data ="%xt%uotls%-1%" + username + "%sp:" + speed + ",tx:" + tx + ",ty:" + ty + ",strFrame:" + frame + ",fly:" + fly + "%";
        }
        
        lobby.sendDataToPlayerMap(username, data, cansee);
    }

    private void userChat(int rID, String msg, String typ) {
        String data = "%xt%chatm%" + rID + "%" + typ + "~" + msg + "%" + username + "%" + uID + "%" + rID + "%";
        if(typ.equals("party"))
            lobby.sendDataToPlayerParty(username, data, false);
        else {
            if(lobby.isStaff(username)) {
                //data = "%xt%chatm%" + rID + "%moderator~" + msg + "%" + username + "%" + uID + "%" + rID + "%";
                lobby.sendDataToPlayerMap(username, data, false);
            } else {
                lobby.sendDataToPlayerMap(username, data, false);
            }
        }
        traceChat(msg);
    }

    private void loadShop(int shopID) {
        try {
            ResultSet rs = _sql.query("SELECT * FROM hs_shops WHERE shopid=" + shopID);
            if (rs.next()) {
                JSONObject _shop = new JSONObject();
                _shop.put("cmd", "loadShop");
                JSONObject _info = new JSONObject();
                _info.put("bitSuccess", "1");
                JSONArray _items = new JSONArray();
                String[] items = rs.getString("items").split(",");
                int house = rs.getInt("bHouse");
                int staff = rs.getInt("bStaff");
                String field = rs.getString("sField");
                rs.close();
                for (int e = 0; e < items.length; e++) {
                    int c = Integer.parseInt(items[e]);
                    JSONObject temp = new JSONObject();
                    temp.put("ItemID", c);
                    temp.put("sLink", lobby._items.get(c).sLink);
                    temp.put("sElmt", lobby._items.get(c).sElmt);
                    temp.put("bStaff", lobby._items.get(c).bStaff);
                    temp.put("iRng", lobby._items.get(c).iRng);
                    temp.put("iDPS", lobby._items.get(c).iDPS);
                    temp.put("bCoins", lobby._items.get(c).bCoins);
                    temp.put("sES", lobby._items.get(c).sES);
                    temp.put("sType", lobby._items.get(c).sType);
                    temp.put("iCost", lobby._items.get(c).iCost);
                    temp.put("iRty", lobby._items.get(c).iRty);
                    temp.put("ShopItemID", (shopID + e));
                    temp.put("iQty", lobby._items.get(c).iQty);
                    /**
                    if (lobby._items.get(c).sES.equals("ar")) {
                        temp.put("iQty", 0);
                    } else {
                        temp.put("iQty", lobby._items.get(c).iQty);
                    }
                    if (lobby._items.get(c).sES.equals("Weapon")) {
                        temp.put("EnhDPS", 100);
                    }
                    if (lobby._items.get(c).sType.equals("Enhancement") || lobby._items.get(c).sType.equals("Necklace") || lobby._items.get(c).sType.equals("Item") || lobby._items.get(c).sType.equals("Quest Item") || lobby._items.get(c).sType.equals("Pet") || lobby._items.get(c).sType.equals("Armor")) {
                        temp.put("EnhID", 0);
                        temp.put("PatternID", lobby._items.get(c).EnhID);
                    }

                    if (lobby._items.get(c).sType.equals("Enhancement") || lobby._items.get(c).EnhID == -1) {
                        temp.put("iLvl", lobby._items.get(c).iLvl);
                    } else {
                        temp.put("EnhLvl", lobby._items.get(c).iLvl);
                        temp.put("EnhID", "1863");
                        temp.put("EnhRty", "1");
                        temp.put("EnhPatternID", lobby._items.get(c).EnhID);
                    }
                     **/
                    temp.put("sIcon", lobby._items.get(c).sIcon);
                    temp.put("bTemp", lobby._items.get(c).bTemp);
                    temp.put("iHrs", lobby._items.get(c).iHrs);
                    temp.put("sFile", lobby._items.get(c).sFile);
                    temp.put("iLvl", lobby._items.get(c).iLvl);
                    temp.put("iStk", lobby._items.get(c).iStk);
                    temp.put("sDesc", lobby._items.get(c).sDesc);
                    temp.put("sFaction", lobby._items.get(c).sFaction);
                    temp.put("iReqRep", lobby._items.get(c).iReqRep);
                    temp.put("bHouse", 0);
                    temp.put("iReqCP", lobby._items.get(c).iReqCP);
                    temp.put("FactionID", lobby._items.get(c).FactionID);
                    temp.put("iClass", lobby._items.get(c).iClass);
                    temp.put("iQSValue", 0);
                    temp.put("iQSindex", -1);
                    temp.put("bUpg", lobby._items.get(c).bUpg);
                    temp.put("sName", lobby._items.get(c).sName);
                    _items.put(temp);
                }
                _info.put("items", _items);
                _info.put("ShopID", shopID);
                _info.put("sField", field);
                _info.put("bStaff", staff);
                _info.put("bHouse", house);
                _info.put("iIndex", -1);
                _shop.put("shopinfo", _info);
                sendData(_shop);
            } else {
                sendData("%xt%warning%Shop is currently unavailable or it does not exist yet!%");
            }
        } catch (Exception e) {
            traceError("Error in loading shop: " + e.getMessage());
        }
    }

    private void equipItem(int itemID) {
        try {
            JSONObject _equip = new JSONObject();
            _equip.put("uid", pID);
            _equip.put("cmd", "equipItem");
            _equip.put("ItemID", itemID);
            _equip.put("strES", lobby._items.get(itemID).sES);
            _equip.put("sFile", lobby._items.get(itemID).sFile);
            _equip.put("sLink", lobby._items.get(itemID).sLink);

            if (lobby._items.get(itemID).sES.equals("we") || lobby._items.get(itemID).sES.equals("he")) {
                _equip.put("sType", lobby._items.get(itemID).sType);
                ResultSet gItems = _sql.query("SELECT iLvl FROM hs_users_items WHERE itemid=" + itemID + " AND sES='we' AND userid=" + uID);
                if (gItems.next()) {
                    wepLvl = gItems.getInt("iLvl");
                    wepRng = lobby._items.get(itemID).iRng;
                    wepDPS = lobby._items.get(itemID).iDPS;
                }
                gItems.close();
            }
            
            _sql.update("UPDATE hs_users_items SET equipped=0 WHERE userid=" + uID + " AND equipped=1 AND sES='" + lobby._items.get(itemID).sES + "'");
            _sql.update("UPDATE hs_users_items SET equipped=1 WHERE userid=" + uID + " AND itemid=" + itemID + " AND equipped=0");
            lobby.sendDataToPlayerMap(username, _equip, false);

            if (lobby._items.get(itemID).sES.equals("ar")) {
                classID = lobby._items.get(itemID).classID;
                _sql.update("UPDATE hs_users_characters SET currentClass=" + classID + " WHERE id=" + uID);
                _sql.update("UPDATE hs_users_items SET className='" + lobby._items.get(itemID).sName + "' WHERE userid=" + uID + " AND sES='ar' AND equipped=1");
                updateClass();
                loadSkills(classID);
            }
        } catch (Exception e) {
            traceError("Error in equipping item: " + e.getMessage());
        }
    }

    protected void playerAttack(String attack, int turn) {
        try {
            int monsterid[] = new int[15];
            int monsterid2[] = new int[15];
            int weaponDmg = 1;

            weaponDmg = (15 * 2) + level;//2 = weapon speed
            int iRng = wepRng / 100;
            weaponDmg = weaponDmg * 1;//1 = autoattack damage AA.damage
            int weaponDmgMin = (int) Math.floor((weaponDmg + weaponDmg * iRng)/2);
            int weaponDmgMax = (int) Math.ceil(weaponDmg + weaponDmg * iRng);
            //trace("/Min"+ weaponDmgMin +"/Max"+ weaponDmgMax +"/iRng"+ iRng +"/iDps"+ wepDPS+"/iLvl"+wepLvl);
            int damage = weaponDmgMin + (int )(wepLvl * 3 - wepLvl / 1.5);
            int damage2 = weaponDmgMax + (int )(wepLvl * 3 - wepLvl / 1.5);

            int crit[] = new int[15];
            int dodge[] = new int[15];
            int miss[] = new int[15];
            int damage3[] = new int[15];
            String type[] = new String[15];
            String skill[] = new String[15];
            String mToAttack[];
            if (attack.indexOf(",") != -1) {
                mToAttack = attack.split(",");
            } else {
                mToAttack = new String[1];
                mToAttack[0] = attack;
            }
            String hit[] = new String[15];

            int skillpos = 0;
            boolean addState = false;

            for (int i = 0; i < mToAttack.length; i++) {
                String[] monsters = mToAttack[i].split(">");
                skill[i] = monsters[0];
                String[] tobehit = monsters[1].split(":");
                monsterid[i] = Integer.parseInt(tobehit[1]);
                monsterid2[i] = Integer.parseInt(tobehit[1]) - 1;
                type[i] = tobehit[0];

                crit[i] = _gen.nextInt(lobby.maxLevel);
                dodge[i] = _gen.nextInt(lobby.maxLevel);
                miss[i] = _gen.nextInt(lobby.maxLevel);
            }

            if (skill[0].equals("a1")) {
                skillpos = 1;
            } else if (skill[0].equals("a2")) {
                skillpos = 2;
            } else if (skill[0].equals("a3")) {
                skillpos = 3;
            } else if (skill[0].equals("a4")) {
                skillpos = 4;
            }

            mp -= _skills.get(skillpos).mp;

            for (int e = 0; e < mToAttack.length; e++) {
                boolean acrit = false;
                boolean pcrit = false;

                if(_passives.get(0).iscrit || _passives.get(1).iscrit)
                    pcrit = true;

                double adamage = 1;
                double sdamage = _skills.get(0).damage;
                double pdamage = _passives.get(0).damage + _passives.get(1).damage;
                /** Checks for Active Auras **/
                for(int xd = 0; xd < _skills.size(); xd++) {
                    if(_skills.get(xd).auraID > 0) {
                        if(_auras.isAuraActive(_skills.get(xd).auraID)) {
                            acrit = _skills.get(xd).aura.iscrit;
                            adamage += _skills.get(xd).aura.damage;
                        }
                    }
                }
                damage3[e] = (int)(damage + _gen.nextInt(damage2 - damage) * (adamage + sdamage + pdamage));
                hit[e] = "hit";
                if(damage3[e] < 0) {
                    damage3[e] = 0;
                }

                if (_gen.nextInt(level) > crit[e] || _skills.get(skillpos).iscrit || acrit || pcrit) {
                    hit[e] = "crit";
                    damage3[e] = (int)(damage3[e] * 1.5);
                }

                if (hp >= 1 && type[e].equals("m") && !skill[e].equals("i1")) {
                    if(cRoom._monsters.get(monsterid2[e]).checkUser(pID))
                    {
                        cRoom._monsters.get(monsterid2[e]).attack(pID);
                        cRoom.monsterActive(monsterid[e]);
                        state = 2;
                    }
                    if (_gen.nextInt(cRoom._monsters.get(monsterid2[e]).level) > dodge[e]) {
                        hit[e] = "dodge";
                        damage3[e] = 0;
                    } else if (_gen.nextInt(cRoom._monsters.get(monsterid2[e]).level) > miss[e]) {
                        hit[e] = "miss";
                        damage3[e] = 0;
                    }
                    cRoom._monsters.get(monsterid2[e]).hp -= damage3[e];
                    if (cRoom._monsters.get(monsterid2[e]).hp <= 0 && cRoom._monsters.get(monsterid2[e]).state >= 1 && type[e].equals("m")) {

                        /** Give Drops and Rewards **/
                        cRoom.giveRewards(monsterid2[e]);
                        cRoom.dropItems(monsterid2[e]);

                        /** Old Method
                        int[] it = cRoom._monsters.get(monsterid2[e]).getRandomDrop();
                        if (it[1] > isDropped) {
                            dropItem(it[0]);
                        }
                         **/

                        cRoom._monsters.get(monsterid2[e]).hp = 0;
                        cRoom._monsters.get(monsterid2[e]).mp = 0;
                        cRoom._monsters.get(monsterid2[e]).die();
                        cRoom.respawnMonster(monsterid2[e]);
                        cRoom.givePvPScore(username, "m", monsterid2[e]);
                        _sql.update("UPDATE hs_users_characters SET monkill=monkill+1 WHERE id=" + uID);
                        addState = true;
                        state = 1;
                    }
                } else if (type[e].equals("p") && hp >= 1 && !skill[e].equals("i1")) {
                    ConnectionHandler uho = lobby.getHandler(monsterid[e]);
                    if (uho.pID != pID) {
                        if (uho.hp >= uho.hpmax) {
                            addState = true;
                        }

                        double areduce = 0;
                        double preduce = uho._passives.get(0).reduction + uho._passives.get(1).reduction;

                        for(int xd = 0; xd < uho._skills.size(); xd++) {
                            if(uho._skills.get(xd).auraID > 0) {
                                if(uho._auras.isAuraActive(uho._skills.get(xd).auraID)) {
                                    areduce = uho._skills.get(xd).aura.reduction;
                                }
                            }
                        }

                        damage3[e] = (int) (damage3[e] - (damage3[e] * (areduce + preduce)));
                        if (_gen.nextInt(uho.level) > dodge[e]) {
                            hit[e] = "dodge";
                            damage3[e] = 0;
                        } else if (_gen.nextInt(uho.level) > miss[e]) {
                            hit[e] = "miss";
                            damage3[e] = 0;
                        }
                        state = 2;
                        uho.hp -= damage3[e];
                        trace("Player " + uho.username + " hp:" + uho.hp + " damage: " + damage3[e]);
                        if (uho.hp <= 0) {
                            uho.hp = 0;
                            uho.mp = 0;
                            uho.state = 0;
                            state = 1;
                            cRoom.givePvPScore(username, "p", uho.pID);
                             _sql.update("UPDATE hs_users_characters SET pvpkill=pvpkill+1 WHERE username='" + username + "'");
                            addState = true;
                        }
                    }
                }
            }

            lobby.sendDataToPlayerMap(username, skillPacket(skillpos, addState, mToAttack, type, hit, damage3, monsterid, turn), false);
            if(state == 1) {
                autoRestTimer();
            }
        } catch (Exception e) {
            traceError("Error in player attack: " + e.getMessage() + " cause: " + e.getCause());
        }
    }

    protected JSONObject skillPacket(int i, boolean addState, String[] monsters, String[] type, String[] hit, int[] damage3, int[] monsterid, int turn) {
        try {
            JSONObject ct = new JSONObject();

            /** Sometimes bugged, removed..
            if(turn != curTurn) {
                lobby.kickPlayer(username, "hacking (spamming attack packets).");
            }

            curTurn += 1;
            if(curTurn == 31) {
                curTurn = 0;
            }
             
            **/

            /* Initialize the variables */
            boolean addSarsa = true;
            int monsterid2[] = new int[15];
            boolean addMonsters = false;
            for (int x = 0; x < monsters.length; x++) {
                monsterid2[x] = monsterid[x] - 1;
            }

            ct.put("cmd", "ct");

            JSONArray anims = new JSONArray();
            JSONObject _anim = new JSONObject();
            _anim.put("strFrame", frame);
            _anim.put("cInf", "p:" + pID);
            _anim.put("fx", _skills.get(i).fx);
            _anim.put("animStr", _skills.get(i).anim);
            /* Adds the player or monster ids to be healed or attack */
            String _mon = "";
            for (int z = 0; z < monsters.length; z++) {
                if (z != 0) {
                    _mon += ",";
                }
                _mon += type[z] + ":" + monsterid[z];
            }
            _anim.put("tInf", _mon);
            _anim.put("str1", _skills.get(i).str1);
            anims.put(_anim);
            
            /** Check if skill has an aura or not **/

            if(_skills.get(i).auraID > 0) {
                addSarsa = false;
                JSONArray _a = new JSONArray();
                String _x = "";
                if(_skills.get(i).aura.type.equals("passive")) {
                    if(_auras.addAura(this, _skills.get(i).auraID, _skills.get(i).aura.seconds, monsterid, type, "p", monsters.length)){
                        for (int z = 0; z < monsters.length; z++) {
                            if(type[z].equals("p")){
                                if (z != 0) {
                                    _x += ",";
                                }
                                _x += type[z] + ":" + monsterid[z];
                            }
                        }

                        JSONObject _ar = new JSONObject();
                        _ar.put("cat", _skills.get(i).aura.cat);
                        _ar.put("nam", _skills.get(i).aura.name);
                        _ar.put("t", "s");
                        _ar.put("s", "s");
                        _ar.put("dur", _skills.get(i).aura.seconds);
                        _ar.put("isNew", true);
                        _a.put(new JSONObject().put("cInf", "p:" + pID).put("cmd", "aura+").put("auras", new JSONArray().put(_ar)).put("tInf", _x));
                    }
                } else {
                    if(_auras.addAura(this, _skills.get(i).auraID, _skills.get(i).aura.seconds, monsterid, type, "p", monsters.length)){
                        for (int z = 0; z < monsters.length; z++) {
                            if(type[z].equals("m")){
                                if (z != 0) {
                                    _x += ",";
                                }
                                _x += type[z] + ":" + monsterid[z];
                            }
                        }

                        JSONObject _ar = new JSONObject();
                        _ar.put("cat", _skills.get(i).aura.cat);
                        _ar.put("nam", _skills.get(i).aura.name);
                        _ar.put("t", "s");
                        _ar.put("s", "s");
                        _ar.put("dur", _skills.get(i).aura.seconds);
                        _ar.put("isNew", true);
                        _a.put(new JSONObject().put("cInf", "p:" + pID).put("cmd", "aura+").put("auras", new JSONArray().put(_ar)).put("tInf", _x));

                        addSarsa = true;
                    }
                }
                
                ct.put("a", _a);
            }

            
            ct.put("anims", anims);

            if(addSarsa){
                JSONArray sarsa = new JSONArray();
                JSONObject _sar = new JSONObject();
                _sar.put("cInf", "p:"+pID);

                JSONArray a = new JSONArray();
                /* Shows the damage for each monster or player ids */
                for (int l = 0; l < monsters.length; l++) {
                    JSONObject _a = new JSONObject();
                    if (type[l].equals("m")) {
                        /* Monster type is found set to true to add monster uotls */
                        addMonsters = true;
                    }
                    _a.put("hp", damage3[l]);
                    _a.put("tInf", type[l] + ":" + monsterid[l]);
                    _a.put("type", hit[l]);
                    a.put(_a);
                }
                _sar.put("a", a);
                _sar.put("actID", turn);
                _sar.put("iRes", 1);
                sarsa.put(_sar);
                ct.put("sarsa", sarsa);
            } else {
                JSONArray sarsa = new JSONArray();
                JSONArray a = new JSONArray().put(new JSONObject().put("hp", 0).put("tInf", "p:" + pID).put("type", "none"));
                sarsa.put(new JSONObject().put("cInf", "p:" + pID).put("a", a).put("actID", turn).put("iRes", 1));
                ct.put("sarsa", sarsa);
            }

            /* Refreshes all the player ids to their real current hp or level and etc... */
            JSONObject p = new JSONObject();
            JSONObject _self = new JSONObject();
            _self.put("intMP", mp);
            if (addState) {
                _self.put("intState", state);
            }
            p.put(username, _self);
            for (int b = 0; b < monsters.length; b++) {
                if (type[b].equals("p") && monsterid[b]!=pID) {
                    JSONObject _inf = new JSONObject();
                    ConnectionHandler uho = lobby.getHandler(monsterid[b]);
                    trace("Player " + uho.username + " hp:" + uho.hp);
                    _inf.put("intHP", uho.hp);
                    if (uho.hp <= 0) {
                        /* Player died, set its state to dead */
                        _inf.put("intState", 0);
                        uho.state = 0;
                    } else if (addState && uho.state == 1) {
                        /* Player is fighting, set its state to fighting */
                        _inf.put("intState", 2);
                        uho.state = 2;
                    }
                    p.put(uho.username, _inf);
                }
            }
            ct.put("p", p);

            /* Refreshes all the monster ids to their real current hp or level and etc... */
            if (addMonsters) {
                JSONObject m = new JSONObject();
                for (int b = 0; b < monsters.length; b++) {
                    if (type[b].equals("m")) {
                        JSONObject _minf = new JSONObject();
                        if (cRoom._monsters.get(monsterid2[b]).hp <= 0) {
                            _minf.put("intHP", 0);
                        } else {
                            _minf.put("intHP", cRoom._monsters.get(monsterid2[b]).hp);
                        }
                        if (cRoom._monsters.get(monsterid2[b]).hp <= 0 && cRoom._monsters.get(monsterid2[b]).state != 0) {
                            cRoom._monsters.get(monsterid2[b]).state = 0;
                            /* Monster died, set its state to dead */
                            _minf.put("intState", 0);
                        }
                        m.put(""+monsterid[b], _minf);
                    }
                }
                ct.put("m", m);
            }
            return ct;
            /* Skill Packet for v1.4 by Zeroskull (JSON Modified) */
        } catch (Exception e) {
            traceError("Error in formulating skill JSON: " + e.getMessage());
        }
        return new JSONObject();
    }

    private void autoRest() {
        if(state == 1 && !(hp >= hpmax && mp >= mpmax)){
            int rand = _gen.nextInt(50);
            while(rand < 35){
                rand = _gen.nextInt(50);
            }
            hp += hpmax / rand;
            if (hp > hpmax) {
                hp = hpmax;
            }
            mp += hpmax / rand;
            if (mp > mpmax) {
                mp = mpmax;
            }
            try {
                JSONObject _i = new JSONObject();

                _i.put("intMP", mp);
                _i.put("intHP", hp);

                JSONObject ct = new JSONObject().put("cmd", "ct").put("p", new JSONObject().put(username, _i));
                sendData(ct);
                autoRestTimer();
            } catch (JSONException e) {
            }
        }
    }

    private void restPlayer() {
        if(state == 1 && !(hp >= hpmax && mp >= mpmax)){
            hp += hpmax / 20;
            if (hp > hpmax) {
                hp = hpmax;
            }
            mp += hpmax / 20;
            if (mp > mpmax) {
                mp = mpmax;
            }

            sendUotls(true, false, true, false, false, false);
        }
    }

    private void autoRestTimer(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(state == 1 && (hp < hpmax || mp < mpmax)){
                    autoRest();
                }
            }
        }, 2500);
    }

    private void sendUotls(boolean addhp, boolean addhpmax, boolean addmp, boolean addmpmax, boolean addlevel, boolean addstate) {
        try {
            JSONObject uotls = new JSONObject();
            uotls.put("cmd", "uotls");
            JSONObject o = new JSONObject();
            if(addhp)
                o.put("intHP", hp);
            if(addhpmax)
                o.put("intHPMax", hpmax);
            if(addmp)
                o.put("intMP", mp);
            if(addmpmax)
                o.put("intMPMax", mpmax);
            if(addlevel)
                o.put("intLevel", level);
            if(addstate)
                o.put("intState", state);
            uotls.put("o", o);
            uotls.put("unm", username);
            lobby.sendDataToPlayerMap(username, uotls, false);
        } catch (JSONException e) {

        }
    }

    private void buyItem(int itemid) {
        try {
            JSONObject buy = new JSONObject();
            buy.put("cmd", "buyItem");
            ResultSet ch = _sql.query("SELECT id FROM hs_users_items WHERE sES='"+lobby._items.get(itemid).sES+"' AND itemId="+itemid+" AND userid="+uID);
            if (ch.next()) {
                if(!(lobby._items.get(itemid).iStk > 1)) {
                    buy.put("bitSuccess", 0);
                    buy.put("strMessage", "Item already exist!");
                    buy.put("CharItemID", -1);
                    sendData(buy);
                    return;
                }
            }
            if (lobby._items.containsKey(itemid)) {
                if (lobby._items.get(itemid).bUpg > upgrade) {
                    buy.put("bitSuccess", 0);
                    buy.put("strMessage", "This item is member only!");
                    buy.put("CharItemID", -1);
                    sendData(buy);
                } else if (lobby._items.get(itemid).isFounder > isFounder) {
                    buy.put("bitSuccess", 0);
                    buy.put("strMessage", "This item is founder only!");
                    buy.put("CharItemID", -1);
                    sendData(buy);
                } else if (lobby._items.get(itemid).iLvl > level) {
                    buy.put("bitSuccess", 0);
                    buy.put("strMessage", "Level requirement not met!");
                    buy.put("CharItemID", -1);
                    sendData(buy);
                } else {
                    int gold = 0;
                    int coins = 0;
                    ResultSet is = _sql.query("SELECT intGold,intCoins FROM hs_users_characters WHERE id="+uID);
                    if (is.next()) {
                        gold = is.getInt("intGold");
                        coins = is.getInt("intCoins");
                    }
                    is.close();
                    if ((gold - lobby._items.get(itemid).iCost) >= 0 || (lobby._items.get(itemid).bCoins == 1 && (coins - lobby._items.get(itemid).iCost) >= 0)) {
                        if (lobby._items.get(itemid).sES.equals("ar")) {
                            _sql.update("INSERT INTO hs_users_items (itemid, userid, sES, className, classXP , iLvl, EnhID) VALUES (" + itemid + ", " + uID + ", 'ar', '" + lobby._items.get(itemid).sName + "', '0', '" + lobby._items.get(itemid).iLvl + "','" + lobby._items.get(itemid).EnhID + "')");
                        } else if (lobby._items.get(itemid).sType.equals("Item") || lobby._items.get(itemid).sType.equals("Quest Item")) {
                            ResultSet es = _sql.query("SELECT id FROM hs_users_items WHERE itemid="+itemid+" AND userid="+uID);
                            if (es.next()) {
                                _sql.update("UPDATE hs_users_items SET iQty=iQty+" + lobby._items.get(itemid).iQty + " WHERE itemid=" + itemid + " AND userid=" + uID);
                            } else {
                                _sql.update("INSERT INTO hs_users_items (iQty ,itemid, userid, sES, iLvl, EnhID) VALUES (" + lobby._items.get(itemid).iQty + ", " + itemid + ", " + uID + ", '" + lobby._items.get(itemid).sES + "', '" + lobby._items.get(itemid).iLvl + "', '" + lobby._items.get(itemid).EnhID + "')");
                            }
                            es.close();
                        } else {
                            _sql.update("INSERT INTO hs_users_items (itemid, userid, sES, iLvl, EnhID) VALUES (" + itemid + ", " + uID + ", '" + lobby._items.get(itemid).sES + "', '" + lobby._items.get(itemid).iLvl + "', '" + lobby._items.get(itemid).EnhID + "')");
                        }
                        if (lobby._items.get(itemid).bCoins != 1) {
                            _sql.update("UPDATE hs_users_characters SET intGold=" + (gold - lobby._items.get(itemid).iCost) + " WHERE id=" + uID);
                        } else {
                            _sql.update("UPDATE hs_users_characters SET intCoins=intCoins-" + lobby._items.get(itemid).iCost + " WHERE id=" + uID);
                        }
                        ResultSet rs = _sql.query("SELECT id FROM hs_users_items WHERE sES='"+lobby._items.get(itemid).sES+"' AND itemId="+itemid+" AND userid="+uID);
                        if (rs.next()) {
                            buy.put("bitSuccess", 1);
                            buy.put("iQty", ""+lobby._items.get(itemid).iQty);
                            buy.put("CharItemID", rs.getInt("id"));
                            sendData(buy);
                        }
                        rs.close();
                    } else {
                        buy.put("bitSuccess", 0);
                        buy.put("strMessage", "Not Enough Gold!");
                        buy.put("CharItemID", -1);
                        sendData(buy);
                    }
                }
            } else {
                buy.put("bitSuccess", 0);
                buy.put("strMessage", "Item Does Not Exist");
                buy.put("CharItemID", -1);
                sendData(buy);
            }
        } catch (Exception e) {
            traceError("Error in buying item: " + e.getMessage());
        }
    }

    private void loadHairShop(int shopID) {
        try {
            JSONObject _hs = new JSONObject();
            _hs.put("HairShopID", shopID);
            _hs.put("cmd", "loadHairShop");
            JSONArray _hair = new JSONArray();
            ResultSet rs = _sql.query("SELECT hairsM,hairsF FROM hs_hairshop WHERE id="+shopID);
            if (rs.next()) {
                String[] items = rs.getString("hairs" + gender).split(",");
                int i = items.length;
                int e = 0;
                rs.close();
                while (e < i) {
                    ResultSet is = _sql.query("SELECT * FROM hs_hairs WHERE hairID="+Integer.parseInt(items[e]));
                    if (is.next()) {
                        JSONObject _h = new JSONObject();
                        _h.put("sFile", is.getString("sFile"));
                        _h.put("HairID", is.getInt("hairID"));
                        _h.put("sName", is.getString("sName"));
                        _h.put("sGen", is.getString("sGen"));
                        _hair.put(_h);
                    }
                    is.close();
                    e++;
                }
            }
            _hs.put("hair", _hair);
            sendData(_hs);
        } catch (Exception e) {
            traceError("Error in loading hair shop: " + e.getMessage());
        }
    }

    private void changeColor(int skincolor, int haircolor, int eyecolor, int hairid) {
        _sql.update("UPDATE hs_users_characters SET intColorSkin=" + skincolor + ",intColorHair=" + haircolor + ",intColorEye=" + eyecolor + " WHERE id=" + uID);
        try {
            JSONObject cc = new JSONObject();
            ResultSet rs = _sql.query("SELECT sName,sFile FROM hs_hairs WHERE hairID="+hairid);
            if (rs.next()) {
                String name = rs.getString("sName");
                String file = rs.getString("sFile");
                rs.close();
                cc.put("HairID", hairid);
                cc.put("strHairName", name);
                cc.put("strHairFilename",  file);
                _sql.update("UPDATE hs_users_characters SET hairID=" + hairid + ",hairName='" + name + "',hairFile='" + file + "' WHERE id=" + uID);
            }
            rs.close();
            cc.put("uid", pID);
            cc.put("cmd", "changeColor");
            cc.put("intColorSkin", skincolor);
            cc.put("intColorHair", haircolor);
            cc.put("intColorEye",  eyecolor);
            lobby.sendDataToPlayerMap(username, cc, false);
        } catch (Exception e) {
            traceError("Error in changing hair style and colors: " + e.getMessage());
        }
    }

    private void changeArmorColor(int iEyes, int iNose, int iMouth, int skin, int hair, int eye, int base, int trim, int accessory) {
        _sql.update("UPDATE hs_users_characters SET intColorBase=" + base + ", intColorAccessory=" + accessory + ", intColorTrim=" + trim +
                ", intColorSkin=" + skin + " , intColorHair=" + hair + ", intColorEye=" + eye +
                ", iEye=" + iEyes + ", iNose=" + iNose + ", iMouth=" + iMouth + "  WHERE id=" + uID);
        try {
            JSONObject cac = new JSONObject();
            cac.put("uid", pID);
            cac.put("cmd", "changeArmorColor");
            cac.put("iEyes", iEyes);
            cac.put("iNose", iNose);
            cac.put("iMouth", iMouth);
            cac.put("intColorBase", base);
            cac.put("intColorTrim", trim);
            cac.put("intColorAccessory", accessory);
            cac.put("intColorSkin", skin);
            cac.put("intColorHair", hair);
            cac.put("intColorEye",  eye);
            lobby.sendDataToPlayerMap(username, cac, false);
        } catch (JSONException e) {
        }
    }

    private void changeArmorColor(int base, int trim, int accessory) {
        _sql.update("UPDATE hs_users_characters SET intColorBase=" + base + ", intColorAccessory=" + accessory + ", intColorTrim=" + trim + " WHERE id=" + uID);
        try {
            JSONObject cac = new JSONObject();
            cac.put("uid", pID);
            cac.put("cmd", "changeArmorColor");
            cac.put("intColorBase", base);
            cac.put("intColorTrim", trim);
            cac.put("intColorAccessory", accessory);
            lobby.sendDataToPlayerMap(username, cac, false);
        } catch (JSONException e) {
        }
    }

    private boolean turnInItem(int[] itemid, int qty[]) {
        boolean doContinue = false;
        int itemsfound = 0;
        int total = 0;
        try {
            for (int i = 0; i < itemid.length; i++) {
                trace("Total Quantity: " + _tempItems.get(itemid[i]) + " Needed Quantity: " + qty[i]);
                ResultSet rs = _sql.query("SELECT iQty FROM hs_users_items WHERE itemid="+itemid[i]+" AND iQty>="+qty[i]+" AND userid="+uID);
                if (rs.next()) {
                    int ccqty = rs.getInt("iQty") - qty[i];
                    rs.close();
                    if (ccqty <= 0) {
                        _sql.update("DELETE FROM hs_users_items WHERE itemid=" + itemid[i] + " AND userid=" + uID);
                    } else {
                        _sql.update("UPDATE hs_users_items SET iQty=" + ccqty + " WHERE itemid=" + itemid[i] + " AND userid=" + uID);
                    }
                    
                    itemsfound += rs.getInt("iQty");
                    total += qty[i];
                } else if (_tempItems.get(itemid[i]) >= qty[i]) {
                    itemsfound += _tempItems.get(itemid[i]);
                    total += qty[i];
                } else {
                    return false;
                }
            }

            trace("Items Valid: " + itemsfound + " Total: " + total);
            if (itemsfound >= total) {
                doContinue = true;
            }

            if (doContinue) {
                JSONObject _ti = new JSONObject();
                _ti.put("cmd", "turnIn");
                String _items = "";
                for (int a = 0; a < itemid.length; a++) {
                    if (a != 0) {
                        _items += ",";
                    }
                    _items += itemid[a] + ":" + qty[a];

                    if(_tempItems.containsKey(itemid[a])) {
                        int what = _tempItems.get(itemid[a]);
                        _tempItems.put(itemid[a], (what -= qty[a]));
                    }
                }
                _ti.put("sItems", _items);

                sendData(_ti);
                return true;
            }
        } catch (Exception e) {
            traceError("Error in turning in item:" + e.getMessage());
        }
        return false;
    }

    private void ping() {
        final String msg = lobby.getRandomNotice();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //sendData("%xt%hi%-1%");
                //sendData("{\"t\":\"xt\",\"b\":{\"r\":-1,\"o\":{\"cmd\":\"umsg\",\"s\":\""+msg+"\"}}}");
                pkSend = 0;
                ping();
            }
        }, 30000);
    }

    private void enhanceItem(int itemid, int enhanceid, boolean islocal) {
        try {
            JSONObject _enh = new JSONObject();
            int iCost = lobby._items.get(enhanceid).iCost;
            int EnhLvl = lobby._items.get(enhanceid).iLvl;
            int EnhID = lobby._items.get(enhanceid).EnhID;
            _enh.put("ItemID", itemid);
            _enh.put("EnhID", enhanceid);
            _enh.put("EnhLvl", EnhLvl);
            _enh.put("EnhDPS", lobby._items.get(enhanceid).iDPS);
            _enh.put("EnhRty", lobby._items.get(enhanceid).iRty);
            _enh.put("EnhRng", lobby._items.get(enhanceid).iRng);
            _enh.put("EnhName", lobby._items.get(enhanceid).sName);
            _enh.put("EnhPID", EnhID);
            _enh.put("iCost", iCost);
            _enh.put("bSuccess", 1);
            if (!islocal) {
                _enh.put("cmd", "enhanceItemShop");
                _sql.update("UPDATE hs_users_characters SET gold=gold-" + iCost + " WHERE id=" + uID);
                sendData(_enh);
            } else {
                _enh.put("cmd", "enhanceItemLocal");
                ResultSet rs = _sql.query("SELECT id FROM hs_users_items WHERE itemid="+enhanceid+" AND userid=" + uID);
                if (rs.next()) {
                    int eid[] = {enhanceid};
                    int qty[] = {1};
                    turnInItem(eid, qty);
                }
            }

            if(lobby._items.get(itemid).sES.equals("Weapon")) {
                wepLvl = lobby._items.get(enhanceid).iLvl;
            }
            _sql.update("UPDATE hs_users_items SET iLvl=" + EnhLvl + ",EnhID=" + EnhID + " WHERE userid=" + uID + " AND itemid=" + itemid);


        } catch (Exception e) {
            traceError("Error in enhance item: " + e.getMessage() + ", itemid: " + itemid + ", enhanceid: " + enhanceid);
        }
    }

}
