package Sockets;

import Json.JSONArray;

/**
 * Handles the game server's party system.
 * @version 1.0 r2
 */
public class Party {

    public String ptLeader;
    public int members = 1;
    public int ptID;
    public String[] _members;

    public Party(int id, String _ptLeader) {
        _members = new String[10];
        ptLeader = _ptLeader;
        ptID = id;
        for (int i = 1; i < 10; i++) {
            if(i == 1)
                _members[i] = ptLeader;
            else
                _members[i] = "";
        }
    }

    public void addMember(String _username) {
        for (int i = 1; i < 10; i++) {
            if(_members[i].equals("")) {
                _members[i] = _username;
                members++;
                break;
            }
        }
    }

    public void removeMember(String _username) {
        if (ptLeader.equals(_username)) {
            for (int i = 1; i < 10; i++) {
                if (!_members[i].equals(ptLeader)) {
                    if (!_members[i].equals("")) {
                        ptLeader = _members[i];
                        break;
                    }
                }
            }
        }
        for (int i = 1; i < 10; i++) {
            if(_members[i].equals(_username)) {
                _members[i] = "";
                members--;
                break;
            }
        }
    }

    public JSONArray getMembers() {
        JSONArray _temp = new JSONArray();
        for (int i = 1; i < 10; i++) {
            if (!_members[i].equals("")) {
                _temp.put(_members[i]);
            }
        }
        return _temp;
    }
}
