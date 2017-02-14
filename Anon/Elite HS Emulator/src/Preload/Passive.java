package Preload;

import Sockets.SqlConnection;
import java.sql.*;

/**
 * Holds passive skill information
 */
public class Passive {

    public int reduction, damage, seconds;
    public boolean iscrit;

    public Passive(int skillID, SqlConnection _sql) {
        try {
            ResultSet is = _sql.query("SELECT * FROM hs_passives WHERE id="+skillID);
            if (is.next()) {
                int auraID = is.getInt("auraID");
                is.close();
                ResultSet rs = _sql.query("SELECT * FROM hs_skills_auras WHERE id="+ auraID);
                if(rs.next()) {
                    iscrit = Boolean.parseBoolean(rs.getString("iscrit"));
                    damage = rs.getInt("damage");
                    seconds = rs.getInt("seconds");
                    reduction = rs.getInt("reduction");
                }
            }

        } catch (Exception e) {

        }
    }
}
