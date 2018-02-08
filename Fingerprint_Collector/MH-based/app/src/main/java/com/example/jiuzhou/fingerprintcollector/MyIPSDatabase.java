package com.example.jiuzhou.fingerprintcollector;

import android.util.Log;

import java.sql.*;

public class MyIPSDatabase {

    Connection con = null;

    public MyIPSDatabase() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("loaded class");
            con = DriverManager.getConnection("jdbc:mysql://192.168.2.100:3306/myips", "root", "password");
            System.out.println("created con");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Exception in MyIPSDatabase(): " + e);
        }
    }

    public int getRSS(String dev_mac_addr, String ap_mac_addr) throws SQLException {

        PreparedStatement pre_statement = null;

        String query ="SELECT * FROM detected_devices where dev_mac_addr=? AND ap_mac_addr=?";

        try {

            pre_statement = con.prepareStatement(query);
            pre_statement.setString(1, dev_mac_addr);
            pre_statement.setString(2, ap_mac_addr);
            ResultSet rs= pre_statement.executeQuery();
            rs.next();
            Log.i("debug", rs.getString("dev_mac_addr") + rs.getString("ap_mac_addr") + rs.getInt("signal_strength") + rs.getString("time"));
            return rs.getInt("signal_strength");

        } catch (Exception e) {
            System.out.println("Exception in getRSS(): " + e);
            return -100;

        } finally {
            if (pre_statement != null) {
                pre_statement.close();
            }

        }
    }

    public void saveFingerprint(int xpos, int ypos,int AP2_1_rss,int AP2_2_rss,int AP2_3_rss,int AP2_4_rss,int AP2_5_rss,int AP2_6_rss,int AP2_7_rss,int AP2_8_rss) throws SQLException {

        PreparedStatement pre_statement = null;

        String query = "INSERT INTO fingerprints(position,ap2_1_rss,ap2_2_rss,ap2_3_rss,ap2_4_rss,ap2_5_rss,ap2_6_rss,ap2_7_rss,ap2_8_rss) "
                + "VALUES(POINT(?,?),?,?,?,?,?,?,?,?)";

        try {

            pre_statement = con.prepareStatement(query);

            try {
                pre_statement.setInt(1, xpos);
                pre_statement.setInt(2, ypos);

                pre_statement.setInt(3, AP2_1_rss);
                pre_statement.setInt(4, AP2_2_rss);
                pre_statement.setInt(5, AP2_3_rss);
                pre_statement.setInt(6, AP2_4_rss);
                pre_statement.setInt(7, AP2_5_rss);
                pre_statement.setInt(8, AP2_6_rss);
                pre_statement.setInt(9, AP2_7_rss);
                pre_statement.setInt(10, AP2_8_rss);


            } catch (NumberFormatException e) {

                pre_statement.setString(1, null);
                pre_statement.setString(2, null);
            }

            pre_statement.executeUpdate();
        } catch (Exception e) {
            System.out.println("Exception in saveFingerprint(): " + e);

        } finally {

            if (pre_statement != null) {
                pre_statement.close();
            }

        }
    }

    public void saveFingerprint2(int xpos, int ypos,int AP2_1_rss,int AP2_2_rss,int AP2_3_rss,int AP2_4_rss,int AP2_5_rss,int AP2_6_rss,int AP2_7_rss,int AP2_8_rss) throws SQLException {

        PreparedStatement pre_statement = null;

        String query = "INSERT INTO fingerprints2(position,ap2_1_rss,ap2_2_rss,ap2_3_rss,ap2_4_rss,ap2_5_rss,ap2_6_rss,ap2_7_rss,ap2_8_rss) "
                + "VALUES(POINT(?,?),?,?,?,?,?,?,?,?)";

        try {

            pre_statement = con.prepareStatement(query);

            try {
                pre_statement.setInt(1, xpos);
                pre_statement.setInt(2, ypos);

                pre_statement.setInt(3, AP2_1_rss);
                pre_statement.setInt(4, AP2_2_rss);
                pre_statement.setInt(5, AP2_3_rss);
                pre_statement.setInt(6, AP2_4_rss);
                pre_statement.setInt(7, AP2_5_rss);
                pre_statement.setInt(8, AP2_6_rss);
                pre_statement.setInt(9, AP2_7_rss);
                pre_statement.setInt(10, AP2_8_rss);


            } catch (NumberFormatException e) {

                pre_statement.setString(1, null);
                pre_statement.setString(2, null);
            }

            pre_statement.executeUpdate();
        } catch (Exception e) {
            System.out.println("Exception in saveFingerprint(): " + e);

        } finally {

            if (pre_statement != null) {
                pre_statement.close();
            }

        }
    }
}
