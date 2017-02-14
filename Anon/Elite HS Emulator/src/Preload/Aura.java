package Preload;

import Sockets.SqlConnection;
import java.sql.*;

/**
 * Holds aura information.
 */
public class Aura {

    public String name, type, cat;
    public double reduction, damage;
    public int seconds;
    public boolean iscrit;

    public Aura(int auraID, SqlConnection _sql) {
        try {
            ResultSet is = _sql.query("SELECT * FROM hs_skills_auras WHERE id="+auraID);
            if (is.next()) {
                seconds = is.getInt("seconds");
                name = is.getString("name");
                iscrit = Boolean.parseBoolean(is.getString("iscrit"));
                damage = Double.parseDouble(is.getString("damage"));
                reduction = Double.parseDouble(is.getString("reduction"));
                type = is.getString("type");
                cat = is.getString("cat");
            }
            is.close();
        } catch (Exception e) {

        }
    }
}
