package Preload;

import Sockets.SqlConnection;
import java.sql.*;

/**
 * Holds a user's equipped skills
 */
public class Skill {

    public int mp, damage, auraID;
    public String anim, fx, str1;
    public boolean iscrit;

    public Aura aura;

    public Skill(int skillID, SqlConnection _sql) {
        try {
            ResultSet is = _sql.query("SELECT * FROM hs_skills WHERE id="+skillID);
            if (is.next()) {
                mp = is.getInt("mana");
                anim = is.getString("anim");
                iscrit = Boolean.parseBoolean(is.getString("iscrit"));
                damage = is.getInt("damage");
                auraID = is.getInt("aura");
                str1 = is.getString("str1");
                fx = is.getString("fx");
            }
            is.close();

            if(auraID != 0) {
                aura = new Aura(auraID, _sql);
            }

        } catch (Exception e) {

        }
    }
}
