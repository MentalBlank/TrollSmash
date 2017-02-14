
package Sockets;

import java.io.*;
import java.util.*;
import java.sql.*;

/**
 * Handles all mySQL Connections.
 * @version 1.0b r6
 */
public class SqlConnection {
    protected Properties sqldata;
    protected static String ip, port, user, pass, database;
    protected static String dbVersion = "e80";
    protected Connection con;
    protected Statement st;

    public SqlConnection(){
        sqldata = new Properties();
    }

    public void close() {
        try {
            st.close();
            con.close();
        } catch (Throwable e) {
        }
    }

    private void loadConfigurations()
    {
        if(ip == null) {
            try
            {
                FileInputStream fin = new FileInputStream("configs/config.conf");
                sqldata.load(fin);
                fin.close();
            }
            catch (Exception ex)
            {
                Interface.writeLog("[mySQL] " + ex.getMessage(), 2);
            }
            Interface.serverIp = sqldata.getProperty("ServerIP");
            ip = sqldata.getProperty("mySQL_IP");
            port = sqldata.getProperty("mySQL_Port");
            user = sqldata.getProperty("mySQL_User");
            pass = sqldata.getProperty("mySQL_Pass");
            database = sqldata.getProperty("mySQL_Database");
        }
    }

    public boolean connect()
    {
        loadConfigurations();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://"+ip+":"+port+"/"+database, user,pass);
            st = con.createStatement();
            if (con != null) {
                return true;
            }
        }
        catch (Exception ex)
        {
            if(ex.getMessage().contains("Communications link failure")) {
                Interface.writeLog("[mySQL] Error connecting to mySQL Database! Please check if mySQL is Running!" +
                        "\n--------------------------------------------------------------------------------------" +
                        "\n" +ex.getMessage() +
                        "\n--------------------------------------------------------------------------------------"
                        , 2);
            } else
                Interface.writeLog("[mySQL] " + ex.getMessage(), 2);

            return false;
        }
        return false;
    }

    public void update(String query) {
        try {
            st.executeUpdate(query);
        } catch (Exception e) {
            Interface.writeLog("[mySQL] " + e.getMessage(), 2);
            if(e.getMessage().contains("Operation not allowed after statement closed")) {
                try {
                    st.close();
                    con.close();
                    connect();
                } catch (Exception ex) {
                }
            }
        }
    }

    public int getRowCount(String table) {
        int count = 0;
        ResultSet r;
        try {
            r = st.executeQuery("SELECT COUNT(*) AS rowcount FROM " + table);
            r.next();
            count = r.getInt("rowcount") ;
            r.close() ;
        } catch (Exception e) {
        }
        return count;
    }

    public ResultSet query(String query) {
        ResultSet _rs = null;
        try {
            _rs = st.executeQuery(query);
        } catch (Exception e) {
            Interface.writeLog("[mySQL] " + e.getMessage(), 2);
            if(e.getMessage().contains("Operation not allowed after statement closed")) {
                try {
                    st.close();
                    con.close();
                    connect();
                } catch (Exception ex) {
                }
            }
        }
        return _rs;
    }
}
