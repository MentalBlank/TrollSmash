package Preload;

import Sockets.SqlConnection;
import java.sql.*;

/**
 * Holds an item's info and etc...
 */
public class Item {

    public String sLink,sFile, sElmt, sReqQuests, sES, sType, sDesc, sName, sIcon, sFaction;
    public int id, bStaff, iRng, iDPS, bCoins, bUpg, isFounder, iCost, iRty, iLvl, iQty, iHrs,
            iStk, bTemp, iClass, FactionID, iReqRep, iReqCP, classID, EnhID;

    public Item(int itemID, SqlConnection _sql) {
        try {
            ResultSet rs = _sql.query("SELECT * FROM hs_items WHERE itemID=" + itemID);
            if(rs.next()) {
                sLink = rs.getString("sLink");
                sFile = rs.getString("sFile");
                sElmt = rs.getString("sElmt");
                sReqQuests = rs.getString("sReqQuests");
                sFaction = rs.getString("sFaction");
                sES = rs.getString("sES");
                sType = rs.getString("sType");
                sDesc = rs.getString("sDesc");
                sName = rs.getString("sName");
                sIcon = rs.getString("sIcon");
                id = rs.getInt("itemid");
                bStaff = rs.getInt("bStaff");
                iRng = rs.getInt("iRng");
                iDPS = rs.getInt("iDPS");
                bCoins = rs.getInt("bCoins");
                iCost = rs.getInt("iCost");
                iRty = rs.getInt("iRty");
                bUpg = rs.getInt("bUpg");
                iLvl = rs.getInt("iLvl");
                isFounder = rs.getInt("isFounder");
                iQty = rs.getInt("iQty");
                iHrs = rs.getInt("iHrs");
                iStk = rs.getInt("iStk");
                bTemp = rs.getInt("bTemp");
                iClass = rs.getInt("iClass");
                FactionID = rs.getInt("FactionID");
                iReqRep = rs.getInt("iReqRep");
                iReqCP = rs.getInt("iReqCP");
                classID = rs.getInt("classID");
                EnhID = rs.getInt("EnhID");
            }
            rs.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
